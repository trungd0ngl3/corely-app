import type { Metadata } from "next";
import Link from "next/link";
import { Suspense } from "react";
import { ArrowDownUp, ArrowRight, Check, SlidersHorizontal } from "lucide-react";
import { ProductCard } from "@/components/product-card";
import { products } from "@/lib/store-data";

export const metadata: Metadata = {
  title: "PC Gaming & Linh Kiện",
  description: "Chọn PC gaming và linh kiện máy tính chính hãng tại Corely.",
};

type SearchParams = {
  q?: string;
  category?: string;
  min?: string;
  max?: string;
  sort?: string;
};

export default function ProductsPage({
  searchParams,
}: {
  searchParams: Promise<SearchParams>;
}) {
  return (
    <Suspense fallback={<ProductsFallback />}>
      <ProductsPageContent searchParams={searchParams} />
    </Suspense>
  );
}

async function ProductsPageContent({
  searchParams,
}: {
  searchParams: Promise<SearchParams>;
}) {
  const params = await searchParams;
  const query = params.q?.trim().toLocaleLowerCase("vi");
  const category = params.category;
  const minPrice = Number(params.min) || undefined;
  const maxPrice = Number(params.max) || undefined;
  const sort = params.sort ?? "featured";
  let visibleProducts = products.filter((product) => {
    const matchesText =
      !query ||
      `${product.name} ${product.category} ${product.description} ${product.specs.join(" ")}`
        .toLocaleLowerCase("vi")
        .includes(query);
    const matchesCategory =
      !category || product.category.toLocaleLowerCase("vi") === category.toLocaleLowerCase("vi");
    const matchesPrice =
      (!minPrice || product.price >= minPrice) &&
      (!maxPrice || product.price <= maxPrice);
    return matchesText && matchesCategory && matchesPrice;
  });

  if (sort === "price-asc") {
    visibleProducts = [...visibleProducts].sort((a, b) => a.price - b.price);
  } else if (sort === "price-desc") {
    visibleProducts = [...visibleProducts].sort((a, b) => b.price - a.price);
  }

  const queryPart = params.q ? `q=${encodeURIComponent(params.q)}&` : "";
  const categoryPart = category ? `category=${encodeURIComponent(category)}&` : "";
  const pricePart = `${minPrice ? `min=${minPrice}&` : ""}${maxPrice ? `max=${maxPrice}&` : ""}`;
  const categories = [
    "PC Prebuilt",
    "Card đồ họa",
    "Bộ vi xử lý",
    "Bo mạch chủ",
    "RAM & SSD",
    "Màn hình & Gear",
    "Nguồn & vỏ máy",
  ];

  return (
    <div className="store-wrap catalog-page">
      <div className="breadcrumbs"><Link href="/">Trang chủ</Link><span>/</span><span>Sản phẩm</span></div>
      <div className="catalog-heading">
        <div><div className="eyebrow"><span className="eyebrow-line" /> CORELY HARDWARE ARCHIVE / 2025</div><h1 className="section-title">Danh sách linh kiện<br /><span>& dàn PC gaming Corely.</span></h1></div>
        <p className="section-copy">Tuyển chọn linh kiện chính hãng, cấu hình được kiểm tra kỹ lưỡng và sẵn sàng cho mọi nâng cấp.</p>
      </div>
      <div className="catalog-layout">
        <aside className="catalog-sidebar">
          <div className="sidebar-heading"><SlidersHorizontal size={17} /> BỘ LỌC PHẦN CỨNG</div>
          <div className="filter-group"><b>DANH MỤC</b>{categories.map((item) => <Link className={category === item ? "filter-link active" : "filter-link"} href={`/products?${queryPart}category=${encodeURIComponent(item)}`} key={item}><span>{item}</span>{category === item && <Check size={14} />}</Link>)}</div>
          <div className="filter-group"><b>NGÂN SÁCH</b>{[
            { label: "Dưới 15 triệu", max: 15000000 },
            { label: "15 - 30 triệu", min: 15000000, max: 30000000 },
            { label: "30 - 60 triệu", min: 30000000, max: 60000000 },
            { label: "Trên 60 triệu", min: 60000000 },
          ].map((range) => {
            const active = minPrice === range.min && maxPrice === range.max;
            const rangeQuery = `${range.min ? `min=${range.min}&` : ""}${range.max ? `max=${range.max}` : ""}`;
            return <Link className={active ? "filter-link active" : "filter-link"} href={`/products?${queryPart}${categoryPart}${rangeQuery}`} key={range.label}>{range.label}<span className="filter-radio" /></Link>;
          })}</div>
          <div className="filter-promo"><span>CORELY / SERVICE</span><b>Build máy theo yêu cầu?</b><p>Đội ngũ kỹ thuật giúp bạn chọn cấu hình tối ưu.</p><a href="mailto:support@corely.vn">Tư vấn miễn phí <ArrowRight size={14} /></a></div>
        </aside>
        <section className="catalog-results">
          <div className="catalog-toolbar"><span>Hiển thị <b>{visibleProducts.length}</b> sản phẩm{query ? <> cho “<b>{params.q}</b>”</> : null}</span><div className="sort-links"><ArrowDownUp size={15} /><span>Sắp xếp:</span><Link className={sort === "featured" ? "active" : ""} href={`/products?${queryPart}${categoryPart}${pricePart}sort=featured`}>Nổi bật</Link><Link className={sort === "price-asc" ? "active" : ""} href={`/products?${queryPart}${categoryPart}${pricePart}sort=price-asc`}>Giá thấp</Link><Link className={sort === "price-desc" ? "active" : ""} href={`/products?${queryPart}${categoryPart}${pricePart}sort=price-desc`}>Giá cao</Link></div></div>
          {visibleProducts.length ? (
            <div className="product-grid catalog-grid">{visibleProducts.map((product) => <ProductCard key={product.id} product={product} />)}</div>
          ) : (
            <div className="empty-results"><span>⌕</span><h2>Chưa tìm thấy sản phẩm phù hợp</h2><p>Thử một từ khóa khác hoặc xóa bớt bộ lọc để xem thêm sản phẩm.</p><Link className="button-primary" href="/products">Xóa bộ lọc <ArrowRight size={16} /></Link></div>
          )}
        </section>
      </div>
      <section className="catalog-help"><div><div className="eyebrow"><span className="eyebrow-line" /> BUILD SUPPORT / CORELY LAB</div><h2>Chưa biết chọn cấu hình<br /><span>tối ưu cho tựa game của bạn?</span></h2></div><Link className="button-primary" href="/#pc-estimator">Dùng bộ ước lượng cấu hình <ArrowRight size={16} /></Link></section>
    </div>
  );
}

function ProductsFallback() {
  return (
    <div aria-busy="true" className="store-wrap catalog-page">
      <div className="catalog-heading">
        <div className="section-title">Đang tải danh sách <span>sản phẩm...</span></div>
      </div>
    </div>
  );
}
