"use client";

import Link from "next/link";
import Image from "next/image";
import { useState } from "react";
import { ArrowUpRight, Check, Cpu, Heart, ShoppingCart, Star } from "lucide-react";
import { formatVnd } from "@/lib/format";
import type { StoreProduct } from "@/lib/store-data";
import type { CatalogProduct } from "@/services/catalog-service";
import { useCart } from "./storefront-provider";

export function ProductCard({
  product,
}: {
  product: StoreProduct | CatalogProduct;
}) {
  const { addItem } = useCart();
  const [favorite, setFavorite] = useState(false);
  const [isAdding, setIsAdding] = useState(false);
  const [justAdded, setJustAdded] = useState(false);
  const catalogProduct = "variants" in product;
  // Pick the first active variant automatically; fall back to first variant if none active
  const defaultVariant = catalogProduct
    ? (product.variants.find((v) => v.isActive !== false) ?? product.variants[0])
    : undefined;
  const canAddDirectly = catalogProduct;

  async function handleAddToCart(event: React.MouseEvent) {
    event.preventDefault();
    if (isAdding || !canAddDirectly) return;
    setIsAdding(true);
    const success = await addItem({
      productId: product.id,
      variantId: defaultVariant?.id,
      title: product.name,
    });
    setIsAdding(false);
    if (success) {
      setJustAdded(true);
      setTimeout(() => setJustAdded(false), 1500);
    }
  }

  return (
    <article className="product-card">
      <div className="product-card-image">
        <Link aria-label={`Xem ${product.name}`} className="product-image-link" href={product.href}>
          {product.image ? (
            <Image alt={product.name} height={450} loading="lazy" src={product.image} unoptimized width={600} />
          ) : (
            <span className="product-placeholder" aria-hidden="true"><Cpu size={48} strokeWidth={0.8} /></span>
          )}
          <span className="product-image-code">CORELY / {product.id.toUpperCase()}</span>
        </Link>
        {product.badge && <span className="product-badge">{product.badge}</span>}
        <button
          aria-label={favorite ? `Bỏ ${product.name} khỏi yêu thích` : `Lưu ${product.name} vào yêu thích`}
          aria-pressed={favorite}
          className={`favorite-button ${favorite ? "active" : ""}`}
          onClick={() => setFavorite((current) => !current)}
          type="button"
        >
          <Heart fill={favorite ? "currentColor" : "none"} size={16} />
        </button>
      </div>
      <div className="product-card-content">
        <span className="product-category">{product.category}</span>
        <Link className="product-name" href={product.href}>{product.name}</Link>
        {product.rating && product.reviews !== undefined && (
          <div className="product-rating"><Star aria-hidden="true" fill="currentColor" size={14} /><b>{product.rating}</b><span>({product.reviews} đánh giá)</span></div>
        )}
        <div className="product-price-row">
          <div><strong>{formatVnd(product.price)}</strong>{product.oldPrice && <del>{formatVnd(product.oldPrice)}</del>}</div>
          <button
            aria-label={justAdded ? `Đã thêm ${product.name} vào giỏ hàng` : `Thêm ${product.name} vào giỏ hàng`}
            className={`add-cart-icon ${justAdded ? "just-added" : ""}`}
            disabled={!canAddDirectly || isAdding}
            onClick={handleAddToCart}
            type="button"
          >
            {justAdded ? <Check size={16} /> : <ShoppingCart size={17} />}
          </button>
        </div>
      </div>
      <Link aria-label={`Xem chi tiết ${product.name}`} className="product-card-overlay-link" href={product.href}><ArrowUpRight size={17} /></Link>
    </article>
  );
}
