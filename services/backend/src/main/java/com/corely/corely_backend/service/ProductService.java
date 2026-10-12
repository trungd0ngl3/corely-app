package com.corely.corely_backend.service;

import com.corely.corely_backend.dto.request.product.ProductCreationRequest;
import com.corely.corely_backend.dto.response.product.ProductResponse;
import com.corely.corely_backend.entity.Product;
import com.corely.corely_backend.entity.ProductImage;
import com.corely.corely_backend.entity.ProductVariant;
import com.corely.corely_backend.entity.Store;
import com.corely.corely_backend.exception.AppException;
import com.corely.corely_backend.exception.ErrorCode;
import com.corely.corely_backend.mapper.ProductMapper;
import com.corely.corely_backend.mapper.ProductVariantMapper;
import com.corely.corely_backend.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class ProductService {
    UserService userService;
    ProductRepository productRepository;
    StoreRepository storeRepository;
    CategoryRepository categoryRepository;
    BrandRepository brandRepository;
    ProductVariantRepository productVariantRepository;
    
    ProductMapper productMapper;
    ProductVariantMapper productVariantMapper;

    static String SYSTEM_STORE_SLUG = "corely";

    @Transactional
    public ProductResponse createProduct(ProductCreationRequest request) {
        if (productRepository.existsBySku(request.getSku()))
            throw new AppException(ErrorCode.PRODUCT_ALREADY_EXISTS);

        Store store = storeRepository.findBySlug(SYSTEM_STORE_SLUG)
                .orElseThrow(() -> new AppException(ErrorCode.STORE_NOT_FOUND));

        if (!store.getIsActive()) throw new AppException(ErrorCode.STORE_LOCKED);

        Product product = buildProduct(request, store);
        var saved = productRepository.save(product);
        log.info("Product {} created", saved.getId());
        return productMapper.toProductResponse(saved);
    }

    public ProductResponse getProductBySlug(String slug) {
        Product product = productRepository.findBySlugAndIsActiveTrue(slug)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        return productMapper.toProductResponse(product);
    }

    public Page<ProductResponse> getActiveProducts(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return productRepository.findByIsActiveTrue(pageable).map(productMapper::toProductResponse);
    }

    public Page<ProductResponse> getProducts(String q, UUID categoryId, UUID brandId, UUID storeId, Double minPrice, Double maxPrice, Pageable pageable) {
        return productRepository.findAll(
                com.corely.corely_backend.repository.ProductSpecification.filter(q, categoryId, brandId, storeId, minPrice, maxPrice),
                pageable
        ).map(productMapper::toProductResponse);
    }

        @Transactional
        public ProductResponse updateProduct(UUID id, ProductCreationRequest request) {
            Product product = productRepository.findById(id)
                    .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
            
            if (productRepository.existsBySkuAndIdNot(request.getSku(), id))
                throw new AppException(ErrorCode.PRODUCT_ALREADY_EXISTS);

            productMapper.updateProduct(product, request);
            product.setCategory(request.getCategoryId() != null ? categoryRepository.findById(request.getCategoryId()).orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND)) : null);
            product.setBrand(request.getBrandId() != null ? brandRepository.findById(request.getBrandId()).orElseThrow(() -> new AppException(ErrorCode.BRAND_NOT_FOUND)) : null);
            
            updateImages(product, request.getImageUrls());
            updateVariants(product, request.getVariants());

            var saved = productRepository.save(product);
            log.info("Product {} updated", saved.getId());
            return productMapper.toProductResponse(saved);
        }

        @Transactional
        public void deleteProduct(UUID id) {
            Product product = productRepository.findById(id)
                    .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
            
            if (!product.getIsActive()) throw new AppException(ErrorCode.PRODUCT_ALREADY_DELETED);
            
            product.setIsActive(false);
        }

        private String generateSlug(String name, UUID productId) {
            String slug = java.text.Normalizer.normalize(name, java.text.Normalizer.Form.NFD)
                    .replaceAll("\\p{M}", "")
                    .replace("đ", "d")
                    .replace("Đ", "D")
                    .toLowerCase()
                    .replaceAll("[^a-z0-9]+", "-")
                    .replaceAll("-{2,}", "-")
                    .replaceAll("(^-|-$)", "");
            String originalSlug = slug;
            int count = 1;
            while (productId == null ? productRepository.existsBySlug(slug) : productRepository.existsBySlugAndIdNot(slug, productId)) {
                slug = originalSlug + "-" + count++;
            }
            return slug;
        }

        private Product buildProduct(ProductCreationRequest request, Store store) {
            var category = (request.getCategoryId() != null) ? categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND)) : null;
            var brand = (request.getBrandId() != null) ? brandRepository.findById(request.getBrandId())
                    .orElseThrow(() -> new AppException(ErrorCode.BRAND_NOT_FOUND)) : null;

            Product product = productMapper.toProduct(request);
            product.setStore(store);
            product.setCategory(category);
            product.setBrand(brand);
            product.setSlug(generateSlug(request.getName(), null));

            if (request.getImageUrls() != null) {
                product.setImages(buildImages(request.getImageUrls(), product));
            }

            if (request.getVariants() != null) {
                Set<String> skus = new HashSet<>();
                product.setVariants(request.getVariants().stream().map(vr -> {
                    if (!skus.add(vr.getSku()) || productVariantRepository.existsBySku(vr.getSku()))
                        throw new AppException(ErrorCode.PRODUCT_ALREADY_EXISTS);
                    ProductVariant v = productVariantMapper.toProductVariant(vr);
                    v.setProduct(product);
                    return v;
                }).collect(Collectors.toList()));
            }
            return product;
        }

        private void updateImages(Product product, List<String> imageUrls) {
            if (product.getImages() == null) product.setImages(new java.util.ArrayList<>());
            product.getImages().clear();
            if (imageUrls != null) {
                product.getImages().addAll(buildImages(imageUrls, product));
            }
        }

        private void updateVariants(Product product, List<com.corely.corely_backend.dto.request.product.ProductVariantRequest> variantRequests) {
            if (variantRequests != null) {
                var existingVariants = product.getVariants().stream()
                        .collect(Collectors.toMap(ProductVariant::getId, v -> v));
                var newVariants = new java.util.ArrayList<ProductVariant>();
                java.util.Set<String> skus = new java.util.HashSet<>();

                for (var vr : variantRequests) {
                    if (!skus.add(vr.getSku())) throw new AppException(ErrorCode.PRODUCT_ALREADY_EXISTS);
                    ProductVariant v;
                    if (vr.getId() != null && existingVariants.containsKey(vr.getId())) {
                        v = existingVariants.get(vr.getId());
                        if (productVariantRepository.existsBySkuAndIdNot(vr.getSku(), v.getId()))
                            throw new AppException(ErrorCode.PRODUCT_ALREADY_EXISTS);
                        productVariantMapper.updateProductVariant(v, vr);
                    } else {
                        if (productVariantRepository.existsBySku(vr.getSku()))
                            throw new AppException(ErrorCode.PRODUCT_ALREADY_EXISTS);
                        v = productVariantMapper.toProductVariant(vr);
                        v.setProduct(product);
                    }
                    newVariants.add(v);
                }
                product.setVariants(newVariants);
            } else {
                product.getVariants().clear();
            }
        }

        private List<ProductImage> buildImages(List<String> urls, Product product) {
            return urls.stream().map(url -> {
                ProductImage img = new ProductImage();
                img.setImageUrl(url);
                img.setProduct(product);
                return img;
            }).collect(Collectors.toList());
        }

}
