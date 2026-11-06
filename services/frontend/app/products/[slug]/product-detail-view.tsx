"use client";

import Image from "next/image";
import Link from "next/link";
import { useState } from "react";
import {
  ArrowLeft,
  ArrowRight,
  Check,
  ChevronRight,
  Cpu,
  Minus,
  Plus,
  ShieldCheck,
  ShoppingCart,
  Truck,
  Wrench,
} from "lucide-react";
import { ProductCard } from "@/components/product-card";
import { useCart } from "@/components/storefront-provider";
import { formatVnd } from "@/lib/format";
import type {
  CatalogProduct,
  ProductReviewsResult,
  ProductVariant,
} from "@/services/catalog-service";

export function ProductDetailView({
  product,
  reviewsResult,
  relatedProducts,
}: {
  product: CatalogProduct;
  reviewsResult: ProductReviewsResult;
  relatedProducts: CatalogProduct[];
}) {
  const { addItem } = useCart();
  const [activeImageIndex, setActiveImageIndex] = useState(0);
  const [selectedVariantId, setSelectedVariantId] = useState<string | null>(
    product.variants.length > 0 ? product.variants[0].id : null,
  );
  const [quantity, setQuantity] = useState(1);
  const [addedToast, setAddedToast] = useState(false);

  const selectedVariant: ProductVariant | undefined = product.variants.find(
    (v) => v.id === selectedVariantId,
  );

  const currentPrice = selectedVariant ? selectedVariant.price : product.price;
  const currentOldPrice = !selectedVariant ? product.oldPrice : undefined;
  const savings =
    currentOldPrice && currentOldPrice > currentPrice
      ? currentOldPrice - currentPrice
      : 0;

  const currentSku = selectedVariant?.sku || product.sku || product.id.slice(0, 8).toUpperCase();
  const currentStock = selectedVariant?.stockQuantity ?? product.stockQuantity;
  const isOutOfStock = currentStock !== undefined && currentStock <= 0;

  const allImages =
    product.images && product.images.length > 0
      ? product.images
      : product.image
      ? [product.image]
      : [];

  const activeImageUrl =
    selectedVariant?.image || allImages[activeImageIndex] || product.image;

  const isPcBuild =
    product.category.toLowerCase().includes("pc") ||
    product.name.toLowerCase().includes("pc gaming") ||
    product.slug === "apex-cyberpunk";

  function handleAddToCart() {
    if (isOutOfStock) return;
    const variantName = selectedVariant
      ? selectedVariant.name
      : "Cấu hình tiêu chuẩn";

    addItem({
      productId: product.id,
      slug: product.slug,
      title: product.name,
      variant: variantName,
      price: currentPrice,
      image: activeImageUrl || "",
      quantity,
    });

    setAddedToast(true);
    setTimeout(() => setAddedToast(false), 2500);
  }

  return (
    <div className="store-wrap detail-page">
      <div className="breadcrumbs">
        <Link href="/">Trang chủ</Link>
        <span>/</span>
        <Link href="/products">Sản phẩm</Link>
        <span>/</span>
        <Link href={`/products?category=${encodeURIComponent(product.category)}`}>
          {product.category}
        </Link>
        <span>/</span>
        <span>{product.name}</span>
      </div>

      <div className="detail-layout">
        <section className="detail-gallery">
          <div className="detail-main-image">
            {product.badge ? (
              <span className="product-badge">{product.badge}</span>
            ) : savings > 0 ? (
              <span className="product-badge">GIẢM GIÁ</span>
            ) : (
              <span className="product-badge">CHÍNH HÃNG</span>
            )}

            {allImages.length > 1 && (
              <span className="gallery-counter">
                0{activeImageIndex + 1} / 0{allImages.length}
              </span>
            )}

            {activeImageUrl ? (
              <Image
                alt={product.name}
                height={900}
                priority
                src={activeImageUrl}
                unoptimized
                width={1200}
              />
            ) : (
              <span className="product-placeholder" aria-hidden="true">
                <Cpu size={72} strokeWidth={0.8} />
              </span>
            )}

            <div className="gallery-overlay">
              <span>CORELY / {product.brandName?.toUpperCase() || product.category.toUpperCase()}</span>
              <b>{currentSku}</b>
            </div>
          </div>

          {allImages.length > 1 && (
            <div className="gallery-thumbs">
              {allImages.map((img, idx) => (
                <button
                  aria-label={`Xem ảnh ${idx + 1}`}
                  aria-pressed={activeImageIndex === idx}
                  className={
                    activeImageIndex === idx ? "gallery-thumb active" : "gallery-thumb"
                  }
                  key={`${img}-${idx}`}
                  onClick={() => setActiveImageIndex(idx)}
                  type="button"
                >
                  <Image alt="" height={300} src={img} unoptimized width={400} />
                  <span>Góc nhìn 0{idx + 1}</span>
                </button>
              ))}
            </div>
          )}

          {isPcBuild && (
            <div className="performance-card">
              <div className="performance-heading">
                <span>REAL-WORLD PERFORMANCE</span>
                <span>1440P / 4K ULTRA</span>
              </div>
              <h2>Hiệu năng đồ họa & Ray Tracing thực tế</h2>
              <div className="game-performance">
                {[
                  ["Cyberpunk 2077", "118", "FPS"],
                  ["Black Myth: Wukong", "86", "FPS"],
                  ["Warzone 3 / BO6", "164", "FPS"],
                  ["CS2 & Valorant", "320", "FPS"],
                ].map(([game, fps, unit]) => (
                  <div key={game}>
                    <span>{game}</span>
                    <b>
                      {fps}
                      <small> {unit}</small>
                    </b>
                    <div className="performance-meter">
                      <i style={{ width: `${Math.min(Number(fps) / 3.2, 100)}%` }} />
                    </div>
                  </div>
                ))}
              </div>
              <p>
                * FPS tham khảo ở thiết lập đồ họa tương ứng. Kết quả thực tế phụ
                thuộc vào độ phân giải và driver linh kiện.
              </p>
            </div>
          )}
        </section>

        <section className="detail-info">
          <div className="eyebrow">
            <span className="eyebrow-line" /> CORELY ARCHIVE /{" "}
            {product.brandName ? product.brandName.toUpperCase() : "ORIGINAL"} /{" "}
            {currentSku}
          </div>

          <h1>{product.name}</h1>

          <div className="detail-rating">
            <span>★★★★★</span> {reviewsResult.averageRating.toFixed(1)}{" "}
            <a href="#reviews">({reviewsResult.totalElements} đánh giá)</a>
            <i />
            <span>SKU: {currentSku}</span>
            <i />
            {isOutOfStock ? (
              <span className="detail-stock-badge out-of-stock">TẠM HẾT HÀNG</span>
            ) : (
              <span className="detail-stock-badge in-stock">
                CÒN HÀNG {currentStock !== undefined ? `(${currentStock})` : ""}
              </span>
            )}
          </div>

          <p className="detail-description">
            {product.description ||
              "Sản phẩm linh kiện và phần cứng máy tính chính hãng, được phân phối và bảo hành trực tiếp theo tiêu chuẩn Corely."}
          </p>

          <div className="detail-price">
            {formatVnd(currentPrice)}
            {currentOldPrice && <del>{formatVnd(currentOldPrice)}</del>}
            {savings > 0 && <span>TIẾT KIỆM {formatVnd(savings)}</span>}
          </div>

          <div className="installment-note">
            <span>↗</span> Trả góp 0% lãi suất: chỉ từ{" "}
            <b>{formatVnd(Math.ceil(currentPrice / 12))}/tháng</b> qua thẻ tín
            dụng.
          </div>

          {product.variants.length > 0 && (
            <div className="config-block">
              <div className="config-heading">
                <span>01 / PHIÊN BẢN</span>
                <span>CHỌN CẤU HÌNH</span>
              </div>
              <div className="config-options">
                {product.variants.map((v) => {
                  const isSelected = selectedVariantId === v.id;
                  const priceDiff = v.price - product.price;
                  return (
                    <button
                      aria-pressed={isSelected}
                      className={
                        isSelected ? "config-option selected" : "config-option"
                      }
                      key={v.id}
                      onClick={() => setSelectedVariantId(v.id)}
                      type="button"
                    >
                      <span className="config-check">
                        {isSelected && <Check size={13} />}
                      </span>
                      <span>{v.name}</span>
                      <b>
                        {priceDiff > 0
                          ? `+${formatVnd(priceDiff)}`
                          : priceDiff < 0
                          ? `-${formatVnd(Math.abs(priceDiff))}`
                          : "TIÊU CHUẨN"}
                      </b>
                    </button>
                  );
                })}
              </div>
            </div>
          )}

          <div className="detail-quantity-row">
            <span>Số lượng:</span>
            <div className="quantity-control">
              <button
                aria-label="Giảm số lượng"
                disabled={quantity <= 1 || isOutOfStock}
                onClick={() => setQuantity((q) => Math.max(1, q - 1))}
                type="button"
              >
                <Minus size={13} />
              </button>
              <span>{quantity}</span>
              <button
                aria-label="Tăng số lượng"
                disabled={
                  isOutOfStock ||
                  (currentStock !== undefined && quantity >= currentStock)
                }
                onClick={() => setQuantity((q) => q + 1)}
                type="button"
              >
                <Plus size={13} />
              </button>
            </div>
            {currentStock !== undefined && (
              <small style={{ color: "#7a878d", fontSize: "10px" }}>
                ({currentStock} sản phẩm có sẵn)
              </small>
            )}
          </div>

          <div className="detail-actions">
            <button
              className="button-primary"
              disabled={isOutOfStock}
              onClick={handleAddToCart}
              type="button"
            >
              <ShoppingCart size={17} />{" "}
              {isOutOfStock ? "Tạm hết hàng" : "Thêm vào giỏ hàng"}
            </button>
            <Link className="button-outline" href="/cart">
              Mở giỏ hàng <ChevronRight size={17} />
            </Link>
          </div>

          {addedToast && (
            <div className="detail-toast">
              <Check size={15} /> Đã thêm {quantity} sản phẩm vào giỏ hàng thành
              công!
            </div>
          )}

          <div className="detail-promises">
            <div>
              <ShieldCheck size={17} />
              <span>
                <b>Bảo hành chính hãng</b>
                <small>36 tháng toàn quốc</small>
              </span>
            </div>
            <div>
              <Wrench size={17} />
              <span>
                <b>Kiểm tra kỹ thuật</b>
                <small>Kiểm định trước khi giao</small>
              </span>
            </div>
            <div>
              <Truck size={17} />
              <span>
                <b>Giao hàng an tâm</b>
                <small>Đóng gói chống sốc 3 lớp</small>
              </span>
            </div>
          </div>

          <a className="detail-contact" href="mailto:support@corely.vn">
            Cần tư vấn cấu hình & tương thích? Liên hệ kỹ thuật viên Corely{" "}
            <ArrowRight size={15} />
          </a>
        </section>
      </div>

      <section className="content-section detail-specs">
        <div className="eyebrow">
          <span className="eyebrow-line" /> FULL SPECIFICATION
        </div>
        <h2 className="section-title">
          Thông số kỹ thuật <span>chi tiết.</span>
        </h2>
        <div className="spec-table">
          <div>
            <span>Tên sản phẩm</span>
            <b>{product.name}</b>
          </div>
          <div>
            <span>Mã sản phẩm (SKU)</span>
            <b>{currentSku}</b>
          </div>
          <div>
            <span>Danh mục</span>
            <b>{product.category}</b>
          </div>
          {product.brandName && (
            <div>
              <span>Thương hiệu</span>
              <b>{product.brandName}</b>
            </div>
          )}
          {product.storeName && (
            <div>
              <span>Cửa hàng cung cấp</span>
              <b>{product.storeName}</b>
            </div>
          )}
          <div>
            <span>Tình trạng kho</span>
            <b>
              {isOutOfStock
                ? "Hết hàng"
                : currentStock !== undefined
                ? `Còn hàng (${currentStock} sản phẩm)`
                : "Còn hàng"}
            </b>
          </div>
          <div>
            <span>Chế độ bảo hành</span>
            <b>36 tháng chính hãng tại Corely</b>
          </div>

          {product.specEntries &&
            product.specEntries.map((spec) => (
              <div key={spec.label}>
                <span>{spec.label}</span>
                <b>{spec.value}</b>
              </div>
            ))}
        </div>
      </section>

      <section className="content-section" id="reviews">
        <div className="eyebrow">
          <span className="eyebrow-line" /> VERIFIED REVIEWS
        </div>
        <h2 className="section-title">
          Đánh giá từ <span>khách hàng.</span>
        </h2>

        <div className="reviews-summary">
          <div className="reviews-score">
            {reviewsResult.averageRating.toFixed(1)}
          </div>
          <div className="reviews-score-info">
            <div className="reviews-stars">
              {"★".repeat(Math.round(reviewsResult.averageRating))}
              {"☆".repeat(5 - Math.round(reviewsResult.averageRating))}
            </div>
            <div className="reviews-count">
              Dựa trên {reviewsResult.totalElements} lượt đánh giá thực tế từ
              người dùng đã mua hàng
            </div>
          </div>
        </div>

        {reviewsResult.content.length > 0 ? (
          <div className="reviews-grid">
            {reviewsResult.content.map((rev) => (
              <article className="review-card" key={rev.id}>
                <div className="review-card-top">
                  <div className="review-user">
                    <span className="review-avatar">
                      {rev.userName ? rev.userName.charAt(0).toUpperCase() : "U"}
                    </span>
                    <div>
                      <div className="review-author">{rev.userName}</div>
                      <div className="review-date">
                        {rev.createdAt
                          ? new Date(rev.createdAt).toLocaleDateString("vi-VN")
                          : "Gần đây"}
                      </div>
                    </div>
                  </div>
                  <div className="reviews-stars">
                    {"★".repeat(rev.rating)}
                    {"☆".repeat(5 - rev.rating)}
                  </div>
                </div>
                <p className="review-comment">{rev.comment}</p>
              </article>
            ))}
          </div>
        ) : (
          <div className="reviews-empty">
            Chưa có đánh giá nào cho sản phẩm này. Hãy là người đầu tiên trải
            nghiệm và để lại nhận xét!
          </div>
        )}
      </section>

      {relatedProducts.length > 0 && (
        <section className="content-section">
          <div className="section-heading-row">
            <div>
              <div className="eyebrow">
                <span className="eyebrow-line" /> COMPLETE YOUR SETUP
              </div>
              <h2 className="section-title">
                Linh kiện & thiết bị
                <br />
                <span>thường mua cùng.</span>
              </h2>
            </div>
            <Link
              className="text-link"
              href={`/products?category=${encodeURIComponent(product.category)}`}
            >
              Xem danh mục {product.category} <ArrowRight size={16} />
            </Link>
          </div>
          <div className="product-grid">
            {relatedProducts.map((p) => (
              <ProductCard key={p.id} product={p} />
            ))}
          </div>
        </section>
      )}

      <div className="detail-back">
        <Link href="/products">
          <ArrowLeft size={15} /> Quay lại danh sách sản phẩm
        </Link>
      </div>
    </div>
  );
}