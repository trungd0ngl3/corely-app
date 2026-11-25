"use client";

import Link from "next/link";
import Image from "next/image";
import { useEffect, useMemo, useState } from "react";
import {
  ArrowDownRight,
  ArrowRight,
  ArrowUpRight,
  Cpu,
} from "lucide-react";
import { ProductCard } from "@/components/product-card";
import { useCart } from "@/components/storefront-provider";
import {
  fetchLandingCatalog,
  type LandingCatalog,
  type StoreCategory,
} from "@/services/catalog-service";
import { formatVnd } from "@/lib/format";

function categoryIcon(category: StoreCategory) {
  if (category.slug.includes("cpu")) return "⌘";
  if (category.slug.includes("graphics") || category.slug.includes("gpu")) return "▧";
  if (category.slug.includes("motherboard")) return "▦";
  if (category.slug.includes("ram") || category.slug.includes("ssd")) return "◫";
  if (category.slug.includes("laptop")) return "▣";
  return "⬡";
}

export default function HomePage() {
  const [budget, setBudget] = useState(35);
  const [catalog, setCatalog] = useState<LandingCatalog>({
    products: [],
    categories: [],
    totalProducts: 0,
  });
  const [catalogError, setCatalogError] = useState<string>();
  const [isLoading, setIsLoading] = useState(true);
  const [retryCount, setRetryCount] = useState(0);
  const { addItem } = useCart();

  useEffect(() => {
    const controller = new AbortController();

    fetchLandingCatalog(controller.signal)
      .then((result) => {
        setCatalog(result);
        setIsLoading(false);
      })
      .catch((error: unknown) => {
        if (controller.signal.aborted) return;
        const message =
          error instanceof Error ? error.message : "Không thể tải dữ liệu sản phẩm.";
        console.error("Could not load the landing page catalog.", error);
        setCatalogError(message);
        setIsLoading(false);
      });

    return () => controller.abort();
  }, [retryCount]);

  const leadProduct = catalog.products[0];
  const estimate = useMemo(() => {
    const targetPrice = budget * 1_000_000;
    return [...catalog.products].sort((left, right) => {
      const leftPrice = left.price;
      const rightPrice = right.price;
      const leftWithinBudget = leftPrice <= targetPrice;
      const rightWithinBudget = rightPrice <= targetPrice;
      if (leftWithinBudget !== rightWithinBudget) return leftWithinBudget ? -1 : 1;
      return leftWithinBudget
        ? rightPrice - leftPrice
        : leftPrice - rightPrice;
    })[0];
  }, [budget, catalog.products]);
  function renderCatalogStatus() {
    if (isLoading) {
      return <p aria-live="polite" className="section-copy" role="status">Đang tải dữ liệu từ Corely...</p>;
    }
    return <p className="section-copy">Backend hiện chưa có dữ liệu cho mục này.</p>;
  }

  function retryCatalog() {
    setIsLoading(true);
    setCatalogError(undefined);
    setRetryCount((count) => count + 1);
  }

  return (
    <>
      <section className="store-wrap home-hero">
        <div className="hero-copy">
          <div className="eyebrow"><span className="eyebrow-line" /> CORELY PERFORMANCE SYSTEMS <span className="eyebrow-year">EST. 2025</span></div>
          <h1>Chiến game<br />đỉnh cao.<br /><span>Sáng tạo vô tận.</span></h1>
          <p>Khám phá linh kiện máy tính và thiết bị công nghệ từ danh mục được cập nhật trực tiếp bởi Corely.</p>
          <div className="hero-actions">
            <Link className="button-primary" href="/products">Khám phá cấu hình <ArrowRight size={17} /></Link>
            <Link className="button-quiet" href="#pc-estimator">Tìm cấu hình của bạn <ArrowDownRight size={17} /></Link>
          </div>
        </div>
        <div className="hero-visual">
          <div className="hero-orbit orbit-one" /><div className="hero-orbit orbit-two" />
          <div className="hero-glow" />
          {leadProduct?.image ? (
            <Image alt={leadProduct.name} height={900} loading="eager" src={leadProduct.image} unoptimized width={1200} />
          ) : (
            <div className="hero-product-placeholder" aria-label={leadProduct?.name ?? "Danh mục sản phẩm Corely"}>
              <Cpu aria-hidden="true" size={88} strokeWidth={0.7} />
              <span>{leadProduct?.name ?? "CORELY CATALOG"}</span>
            </div>
          )}
          <div className="hero-scan-label"><i /> CORELY HARDWARE <span>01 / 04</span></div>
          <div className="hero-spec-float"><span>LIVE CATALOG / PRODUCT</span><b>{leadProduct?.category.toUpperCase() ?? (isLoading ? "ĐANG TẢI" : "CORELY")} <i>AVAILABLE</i></b><small>{leadProduct?.description ?? leadProduct?.name ?? "DANH MỤC SẢN PHẨM"}</small></div>
          <div className="hero-price-float"><span>{leadProduct?.name ?? "CORELY CATALOG"}</span><b>{leadProduct ? formatVnd(leadProduct.price) : "CẬP NHẬT TRỰC TIẾP"}</b><Link href="/products">Khám phá sản phẩm <ArrowUpRight size={14} /></Link></div>
        </div>
        <div className="hero-index">01 <span>/</span> 04</div>
      </section>

      {catalogError && (
        <div className="store-wrap empty-results" role="alert">
          <h2>Không thể tải danh mục sản phẩm</h2>
          <p>{catalogError}</p>
          <button className="button-outline" onClick={retryCatalog} type="button">Thử lại</button>
        </div>
      )}

      <section className="store-wrap stats-strip" aria-label="Tóm tắt danh mục sản phẩm">
        <div><strong>{isLoading ? "—" : catalog.totalProducts}</strong><small>SẢN PHẨM TRONG DANH MỤC</small></div>
        <div><strong>{isLoading ? "—" : catalog.categories.length}</strong><small>DANH MỤC SẢN PHẨM</small></div>
        <div><strong>{estimate ? formatVnd(estimate.price) : "—"}</strong><small>GIÁ GỢI Ý THEO NGÂN SÁCH</small></div>
        <div className="stats-note"><i />{catalogError ? "KHÔNG THỂ KẾT NỐI API" : isLoading ? "ĐANG ĐỒNG BỘ DANH MỤC" : "DỮ LIỆU TỪ CORELY API"}</div>
      </section>

      <section className="estimator-section" id="pc-estimator">
        <div className="store-wrap estimator-layout">
          <div>
            <div className="eyebrow"><span className="eyebrow-line" /> BUDGET MATCH</div>
            <h2 className="section-title">Linh kiện PC.<br /><span>Chuẩn ngân sách.</span></h2>
            <p className="section-copy">Chọn ngân sách để tìm sản phẩm phù hợp trong danh mục Corely hiện tại.</p>
            <div className="budget-head"><span>01 / NGÂN SÁCH DỰ KIẾN</span><b>{budget} <small>TRIỆU VNĐ</small></b></div>
            <input aria-label="Ngân sách triệu đồng" className="budget-slider" max={120} min={15} onChange={(event) => setBudget(Number(event.target.value))} step={1} type="range" value={budget} />
            <div className="range-labels"><span>15 TRIỆU</span><span>120 TRIỆU +</span></div>
            <div className="budget-tiers">
              {[
                { label: "1080P ESPORTS", value: 18 },
                { label: "2K HI-END", value: 35 },
                { label: "4K / AI", value: 75 },
              ].map((tier) => <button key={tier.label} onClick={() => setBudget(tier.value)} type="button">{tier.label}<span>{tier.value}TR</span></button>)}
            </div>
          </div>
          <div className="estimate-card">
            <div className="estimate-topline"><span>CORELY CATALOG / BUDGET MATCH</span><span>02.01</span></div>
            {estimate ? (
              <>
                <div className="estimate-art"><span>PRODUCT FROM LIVE CATALOG</span>{estimate.image ? <Image alt={estimate.name} height={600} src={estimate.image} unoptimized width={800} /> : <div className="estimate-product-placeholder"><Cpu aria-hidden="true" size={64} strokeWidth={0.8} /><b>{estimate.name}</b></div>}<div className="estimate-crosshair">+</div></div>
                <div className="estimate-details"><div><span>GỢI Ý THEO NGÂN SÁCH</span><h3>{estimate.name}</h3></div><b>{formatVnd(estimate.price)}</b></div>
                <ul className="estimate-specs">{(estimate.specs.length ? estimate.specs : [estimate.category]).map((spec) => <li key={spec}><span>{spec}</span><i>CORELY</i></li>)}</ul>
                <div className="estimate-actions">
                  <button className="button-primary" onClick={() => addItem({ productId: estimate.id, title: estimate.name, variant: "Sản phẩm từ danh mục", price: estimate.price, image: estimate.image ?? "" })} type="button">Thêm vào giỏ <ArrowRight size={16} /></button>
                  <Link className="button-outline" href="/products">Xem sản phẩm</Link>
                </div>
                <div className="estimate-footnote">Gợi ý dựa trên giá sản phẩm hiện có trong danh mục Corely.</div>
              </>
            ) : (
              <div className="estimate-empty">{renderCatalogStatus()}</div>
            )}
          </div>
        </div>
      </section>

      <section className="store-wrap content-section">
        <div className="section-heading-row"><div><div className="eyebrow"><span className="eyebrow-line" /> CURATED BY CORELY</div><h2 className="section-title">Cỗ máy được<br /><span>chọn nhiều nhất.</span></h2></div><Link className="text-link" href="/products">Tất cả cấu hình <ArrowUpRight size={16} /></Link></div>
        {catalog.products.length ? (
          <div className="product-grid home-products">{catalog.products.slice(0, 4).map((product) => <ProductCard key={product.id} product={product} />)}</div>
        ) : renderCatalogStatus()}
      </section>

      <section className="category-section">
        <div className="store-wrap">
          <div className="section-heading-row"><div><div className="eyebrow"><span className="eyebrow-line" /> FIND YOUR COMPONENT</div><h2 className="section-title">Linh kiện<br /><span>chính hãng.</span></h2></div><p className="section-copy category-intro">Từ một linh kiện nâng cấp đến trọn bộ chiến mã, chọn đúng thứ bạn cần.</p></div>
          {catalog.categories.length ? (
            <div className="category-grid">{catalog.categories.map((category) => <Link className="category-tile" href="/products" key={category.id}><span className="category-symbol">{categoryIcon(category)}</span><span className="category-tag">{category.slug.replaceAll("-", " ").toUpperCase()}</span><b>{category.name}</b><ArrowUpRight className="category-arrow" size={17} /></Link>)}</div>
          ) : renderCatalogStatus()}
        </div>
      </section>

    </>
  );
}
