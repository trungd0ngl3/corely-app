package com.corely.corely_backend.seed;

import com.corely.corely_backend.entity.*;
import com.corely.corely_backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@Order(7)
@RequiredArgsConstructor
public class ProductSeeder implements CommandLineRunner {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final StoreRepository storeRepository;

    @Override
    public void run(String... args) {
        if (productRepository.count() == 0) {
            var store = storeRepository.findTopBy().orElseThrow();

            var cpu = categoryRepository.findBySlug("cpu").orElseThrow();
            var gpu = categoryRepository.findBySlug("graphics-card").orElseThrow();
            var motherboard = categoryRepository.findBySlug("motherboard").orElseThrow();
            var ram = categoryRepository.findBySlug("ram").orElseThrow();
            var ssd = categoryRepository.findBySlug("ssd").orElseThrow();
            var laptop = categoryRepository.findBySlug("laptop").orElseThrow();

            var intel = brandRepository.findBySlug("intel").orElseThrow();
            var amd = brandRepository.findBySlug("amd").orElseThrow();
            var nvidia = brandRepository.findBySlug("nvidia").orElseThrow();
            var asus = brandRepository.findBySlug("asus").orElseThrow();
            var msi = brandRepository.findBySlug("msi").orElseThrow();
            var corsair = brandRepository.findBySlug("corsair").orElseThrow();
            var samsung = brandRepository.findBySlug("samsung").orElseThrow();

            List<Product> products = new ArrayList<>();
            products.add(Product.builder()
                .name("Intel Core i5-14600K")
                .slug("intel-core-i5-14600k")
                .sku("CPU001")
                .price(new BigDecimal("8490000"))
                .discountPrice(new BigDecimal("8065500"))
                .stockQuantity(30)
                .category(cpu)
                .brand(intel)
                .store(store)
                .specs(Map.of("Cores", 14, "Threads", 20, "Socket", "LGA1700"))
                .isActive(true)
                .build());
            products.add(Product.builder()
                    .name("Intel Core i9-14900K")
                    .slug("intel-core-i9-14900k")
                    .sku("CPU002")
                    .price(new BigDecimal("15990000"))
                    .discountPrice(new BigDecimal("14990000"))
                    .stockQuantity(15)
                    .category(cpu)
                    .brand(intel)
                    .store(store)
                    .specs(Map.of("Cores",24,"Threads",32,"Socket","LGA1700"))
                    .isActive(true)
                    .build());

            products.add(Product.builder()
                    .name("AMD Ryzen 7 9800X3D")
                    .slug("amd-ryzen-7-9800x3d")
                    .sku("CPU003")
                    .price(new BigDecimal("13990000"))
                    .discountPrice(new BigDecimal("13290000"))
                    .stockQuantity(20)
                    .category(cpu)
                    .brand(amd)
                    .store(store)
                    .specs(Map.of("Cores",8,"Threads",16,"Socket","AM5"))
                    .isActive(true)
                    .build());

            products.add(Product.builder()
                    .name("NVIDIA RTX 4060 Ti")
                    .slug("rtx-4060-ti")
                    .sku("GPU001")
                    .price(new BigDecimal("12990000"))
                    .discountPrice(new BigDecimal("12390000"))
                    .stockQuantity(18)
                    .category(gpu)
                    .brand(nvidia)
                    .store(store)
                    .specs(Map.of("VRAM","8GB GDDR6"))
                    .isActive(true)
                    .build());

            products.add(Product.builder()
                    .name("NVIDIA RTX 4070 Super")
                    .slug("rtx-4070-super")
                    .sku("GPU002")
                    .price(new BigDecimal("19990000"))
                    .discountPrice(new BigDecimal("18990000"))
                    .stockQuantity(10)
                    .category(gpu)
                    .brand(nvidia)
                    .store(store)
                    .specs(Map.of("VRAM","12GB GDDR6X"))
                    .isActive(true)
                    .build());

            products.add(Product.builder()
                    .name("NVIDIA RTX 5090")
                    .slug("rtx-5090")
                    .sku("GPU003")
                    .price(new BigDecimal("68990000"))
                    .discountPrice(new BigDecimal("66990000"))
                    .stockQuantity(5)
                    .category(gpu)
                    .brand(nvidia)
                    .store(store)
                    .specs(Map.of("VRAM","32GB GDDR7"))
                    .isActive(true)
                    .build());

            products.add(Product.builder()
                    .name("ASUS ROG STRIX Z790-E")
                    .slug("asus-z790-e")
                    .sku("MB001")
                    .price(new BigDecimal("11990000"))
                    .discountPrice(new BigDecimal("11290000"))
                    .stockQuantity(15)
                    .category(motherboard)
                    .brand(asus)
                    .store(store)
                    .specs(Map.of("Socket","LGA1700","Chipset","Z790"))
                    .isActive(true)
                    .build());

            products.add(Product.builder()
                    .name("MSI MAG B650 Tomahawk")
                    .slug("msi-b650-tomahawk")
                    .sku("MB002")
                    .price(new BigDecimal("6390000"))
                    .discountPrice(new BigDecimal("5990000"))
                    .stockQuantity(18)
                    .category(motherboard)
                    .brand(msi)
                    .store(store)
                    .specs(Map.of("Socket","AM5","Chipset","B650"))
                    .isActive(true)
                    .build());

            products.add(Product.builder()
                    .name("Corsair Vengeance DDR5 32GB")
                    .slug("corsair-ddr5-32gb")
                    .sku("RAM001")
                    .price(new BigDecimal("3590000"))
                    .discountPrice(new BigDecimal("3390000"))
                    .stockQuantity(50)
                    .category(ram)
                    .brand(corsair)
                    .store(store)
                    .specs(Map.of("Capacity","32GB","Speed","6000MHz"))
                    .isActive(true)
                    .build());

            products.add(Product.builder()
                    .name("Samsung 990 Pro 1TB")
                    .slug("samsung-990-pro-1tb")
                    .sku("SSD001")
                    .price(new BigDecimal("3190000"))
                    .discountPrice(new BigDecimal("2990000"))
                    .stockQuantity(40)
                    .category(ssd)
                    .brand(samsung)
                    .store(store)
                    .specs(Map.of("Capacity","1TB","Interface","PCIe 4.0"))
                    .isActive(true)
                    .build());

            products.add(Product.builder()
                    .name("ASUS ROG Zephyrus G16")
                    .slug("rog-zephyrus-g16")
                    .sku("LAP001")
                    .price(new BigDecimal("52990000"))
                    .discountPrice(new BigDecimal("50990000"))
                    .stockQuantity(8)
                    .category(laptop)
                    .brand(asus)
                    .store(store)
                    .specs(Map.of(
                            "CPU","Intel Core Ultra 9",
                            "GPU","RTX 4080",
                            "RAM","32GB"))
                    .isActive(true)
                    .build());

            products.add(Product.builder()
                    .name("MSI Raider GE78 HX")
                    .slug("msi-raider-ge78")
                    .sku("LAP002")
                    .price(new BigDecimal("68990000"))
                    .discountPrice(new BigDecimal("65990000"))
                    .stockQuantity(6)
                    .category(laptop)
                    .brand(msi)
                    .store(store)
                    .specs(Map.of(
                            "CPU","Intel Core i9",
                            "GPU","RTX 4090",
                            "RAM","32GB"))
                    .isActive(true)
                    .build());
            // ================= CPU =================
            products.add(Product.builder()
                    .name("AMD Ryzen 5 9600X")
                    .slug("amd-ryzen-5-9600x")
                    .sku("CPU004")
                    .price(new BigDecimal("7490000"))
                    .discountPrice(new BigDecimal("6990000"))
                    .stockQuantity(25)
                    .category(cpu)
                    .brand(amd)
                    .store(store)
                    .specs(Map.of(
                            "Cores",6,
                            "Threads",12,
                            "Socket","AM5"))
                    .isActive(true)
                    .build());

// ================= GPU =================
            products.add(Product.builder()
                    .name("ASUS Dual RTX 4060")
                    .slug("asus-dual-rtx-4060")
                    .sku("GPU004")
                    .price(new BigDecimal("9990000"))
                    .discountPrice(new BigDecimal("9490000"))
                    .stockQuantity(22)
                    .category(gpu)
                    .brand(asus)
                    .store(store)
                    .specs(Map.of(
                            "VRAM","8GB GDDR6"))
                    .isActive(true)
                    .build());

            products.add(Product.builder()
                    .name("MSI RTX 4080 Super")
                    .slug("msi-rtx-4080-super")
                    .sku("GPU005")
                    .price(new BigDecimal("32990000"))
                    .discountPrice(new BigDecimal("31990000"))
                    .stockQuantity(8)
                    .category(gpu)
                    .brand(msi)
                    .store(store)
                    .specs(Map.of(
                            "VRAM","16GB GDDR6X"))
                    .isActive(true)
                    .build());

// ================= Motherboard =================
            products.add(Product.builder()
                    .name("ASUS Prime B760M-A")
                    .slug("asus-prime-b760m-a")
                    .sku("MB003")
                    .price(new BigDecimal("4290000"))
                    .discountPrice(new BigDecimal("3990000"))
                    .stockQuantity(20)
                    .category(motherboard)
                    .brand(asus)
                    .store(store)
                    .specs(Map.of(
                            "Socket","LGA1700",
                            "Chipset","B760"))
                    .isActive(true)
                    .build());

            products.add(Product.builder()
                    .name("MSI PRO Z790-P WiFi")
                    .slug("msi-pro-z790-p")
                    .sku("MB004")
                    .price(new BigDecimal("7290000"))
                    .discountPrice(new BigDecimal("6890000"))
                    .stockQuantity(15)
                    .category(motherboard)
                    .brand(msi)
                    .store(store)
                    .specs(Map.of(
                            "Socket","LGA1700",
                            "Chipset","Z790"))
                    .isActive(true)
                    .build());

// ================= RAM =================
            products.add(Product.builder()
                    .name("Corsair Vengeance DDR5 64GB")
                    .slug("corsair-ddr5-64gb")
                    .sku("RAM002")
                    .price(new BigDecimal("6490000"))
                    .discountPrice(new BigDecimal("6190000"))
                    .stockQuantity(20)
                    .category(ram)
                    .brand(corsair)
                    .store(store)
                    .specs(Map.of(
                            "Capacity","64GB",
                            "Speed","6400MHz"))
                    .isActive(true)
                    .build());

            products.add(Product.builder()
                    .name("Corsair Vengeance DDR5 16GB")
                    .slug("corsair-ddr5-16gb")
                    .sku("RAM003")
                    .price(new BigDecimal("1990000"))
                    .discountPrice(new BigDecimal("1850000"))
                    .stockQuantity(60)
                    .category(ram)
                    .brand(corsair)
                    .store(store)
                    .specs(Map.of(
                            "Capacity","16GB",
                            "Speed","5600MHz"))
                    .isActive(true)
                    .build());

// ================= SSD =================
            products.add(Product.builder()
                    .name("Samsung 980 Pro 2TB")
                    .slug("samsung-980-pro-2tb")
                    .sku("SSD002")
                    .price(new BigDecimal("4990000"))
                    .discountPrice(new BigDecimal("4690000"))
                    .stockQuantity(25)
                    .category(ssd)
                    .brand(samsung)
                    .store(store)
                    .specs(Map.of(
                            "Capacity","2TB",
                            "Interface","PCIe 4.0"))
                    .isActive(true)
                    .build());

            products.add(Product.builder()
                    .name("Samsung 970 EVO Plus 1TB")
                    .slug("samsung-970-evo-plus")
                    .sku("SSD003")
                    .price(new BigDecimal("2390000"))
                    .discountPrice(new BigDecimal("2190000"))
                    .stockQuantity(35)
                    .category(ssd)
                    .brand(samsung)
                    .store(store)
                    .specs(Map.of(
                            "Capacity","1TB",
                            "Interface","PCIe 3.0"))
                    .isActive(true)
                    .build());

// ================= Laptop =================
            products.add(Product.builder()
                    .name("ASUS TUF Gaming A15")
                    .slug("asus-tuf-a15")
                    .sku("LAP003")
                    .price(new BigDecimal("28990000"))
                    .discountPrice(new BigDecimal("26990000"))
                    .stockQuantity(12)
                    .category(laptop)
                    .brand(asus)
                    .store(store)
                    .specs(Map.of(
                            "CPU","Ryzen 7",
                            "GPU","RTX 4060",
                            "RAM","16GB"))
                    .isActive(true)
                    .build());

            products.add(Product.builder()
                    .name("MSI Katana 15")
                    .slug("msi-katana-15")
                    .sku("LAP004")
                    .price(new BigDecimal("31990000"))
                    .discountPrice(new BigDecimal("29990000"))
                    .stockQuantity(10)
                    .category(laptop)
                    .brand(msi)
                    .store(store)
                    .specs(Map.of(
                            "CPU","Intel Core i7",
                            "GPU","RTX 4070",
                            "RAM","16GB"))
                    .isActive(true)
                    .build());
            productRepository.saveAll(products);
        }
    }
}
