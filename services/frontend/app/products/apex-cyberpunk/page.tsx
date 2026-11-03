"use client";

import Link from "next/link";
import Image from "next/image";
import { useState } from "react";
import { ArrowLeft, ArrowRight, Check, ChevronRight, ShieldCheck, ShoppingCart, Truck, Wrench } from "lucide-react";
import { ProductCard } from "@/components/product-card";
import { useCart } from "@/components/storefront-provider";
import { featuredProduct, formatVnd, products } from "@/lib/store-data";

const componentOptions = {
  cpu: [
    { name: "Core i7-14700K · 20 nhân / 28 luồng", price: 0 },
    { name: "Core i9-14900K · 24 nhân / 32 luồng", price: 3800000 },
  ],
  memory: [
    { name: "32GB DDR5 · 2×16GB · 6000MHz", price: 0 },
    { name: "64GB DDR5 · 2×32GB · 6000MHz", price: 2450000 },
  ],
  storage: [
    { name: "SSD NVMe Gen 4 · 1TB", price: 0 },
    { name: "Samsung 990 Pro · 2TB", price: 3200000 },
  ],
};

export default function ProductDetailPage() {
  const [selected, setSelected] = useState({ cpu: 0, memory: 0, storage: 0 });
  const [activeView, setActiveView] = useState(0);
  const { addItem } = useCart();
  const extra =
    componentOptions.cpu[selected.cpu].price +
    componentOptions.memory[selected.memory].price +
    componentOptions.storage[selected.storage].price;
  const price = featuredProduct.price + extra;

  function addConfiguredBuild() {
    addItem({
      productId: featuredProduct.id,
      title: featuredProduct.name,
      variant: [
        componentOptions.cpu[selected.cpu].name,
        componentOptions.memory[selected.memory].name,
        componentOptions.storage[selected.storage].name,
      ].join(" · "),
      price,
      image: featuredProduct.image,
    });
  }

  return (
    <div className="store-wrap detail-page">
      <div className="breadcrumbs"><Link href="/">Trang chủ</Link><span>/</span><Link href="/products">PC Gaming</Link><span>/</span><span>Apex Cyberpunk</span></div>
      <div className="detail-layout">
        <section className="detail-gallery">
          <div className="detail-main-image"><span className="product-badge">CORELY SIGNATURE BUILD</span><span className="gallery-counter">0{activeView + 1} / 04</span><Image alt={featuredProduct.name} height={900} src={featuredProduct.image} unoptimized width={1200} /><div className="gallery-overlay"><span>APEX / SERIES 04</span><b>ENGINEERED TO PERFORM.</b></div></div>
          <div className="gallery-thumbs">{["Mặt hông", "Mặt trước", "LCD 2.8″", "Góc setup"].map((label, index) => <button aria-pressed={activeView === index} className={activeView === index ? "gallery-thumb active" : "gallery-thumb"} key={label} onClick={() => setActiveView(index)} type="button"><Image alt="" height={300} src={featuredProduct.image} unoptimized width={400} /><span>{label}</span></button>)}</div>
          <div className="performance-card"><div className="performance-heading"><span>REAL-WORLD PERFORMANCE</span><span>1440P / ULTRA</span></div><h2>Hiệu năng thực tế: 4K & 2K Ray Tracing</h2><div className="game-performance">{[
            ["Cyberpunk 2077", "118", "FPS"],
            ["Black Myth: Wukong", "86", "FPS"],
            ["Warzone 3 / BO6", "164", "FPS"],
            ["CS2 & Valorant", "320", "FPS"],
          ].map(([game, fps, unit]) => <div key={game}><span>{game}</span><b>{fps}<small> {unit}</small></b><div className="performance-meter"><i style={{ width: `${Math.min(Number(fps) / 3.2, 100)}%` }} /></div></div>)}</div><p>* FPS tham khảo ở thiết lập tương ứng; kết quả thực tế phụ thuộc game và phiên bản driver.</p></div>
        </section>
        <section className="detail-info">
          <div className="eyebrow"><span className="eyebrow-line" /> CORELY / APEX SERIES / 2025</div>
          <h1>PC Gaming Corely<br /><span>Apex Cyberpunk Edition</span></h1>
          <div className="detail-rating"><span>★★★★★</span> 5.0 <a href="#reviews">(64 đánh giá)</a><i /> SKU: NX-APEX-147K</div>
          <p className="detail-description">Cỗ máy gaming hiệu năng cao được tinh chỉnh để chinh phục game AAA, stream và sáng tạo nội dung. Từng linh kiện được kiểm tra tương thích trước khi lắp ráp.</p>
          <div className="detail-price">{formatVnd(price)}<del>{formatVnd(featuredProduct.oldPrice ?? price)}</del><span>TIẾT KIỆM {formatVnd(Math.max(0, (featuredProduct.oldPrice ?? price) - price))}</span></div>
          <div className="installment-note"><span>↗</span> Trả góp 0% lãi suất: chỉ từ <b>{formatVnd(Math.ceil(price / 10))}/tháng</b> qua thẻ tín dụng.</div>
          <div className="config-block"><div className="config-heading"><span>01 / PROCESSOR</span><span>CHỌN CPU</span></div><div className="config-options">{componentOptions.cpu.map((option, index) => <button aria-pressed={selected.cpu === index} className={selected.cpu === index ? "config-option selected" : "config-option"} key={option.name} onClick={() => setSelected((current) => ({ ...current, cpu: index }))} type="button"><span className="config-check">{selected.cpu === index && <Check size={13} />}</span><span>{option.name}</span><b>{option.price ? `+${formatVnd(option.price)}` : "MẶC ĐỊNH"}</b></button>)}</div></div>
          <div className="config-block"><div className="config-heading"><span>02 / MEMORY</span><span>CHỌN RAM</span></div><div className="config-options">{componentOptions.memory.map((option, index) => <button aria-pressed={selected.memory === index} className={selected.memory === index ? "config-option selected" : "config-option"} key={option.name} onClick={() => setSelected((current) => ({ ...current, memory: index }))} type="button"><span className="config-check">{selected.memory === index && <Check size={13} />}</span><span>{option.name}</span><b>{option.price ? `+${formatVnd(option.price)}` : "MẶC ĐỊNH"}</b></button>)}</div></div>
          <div className="config-block"><div className="config-heading"><span>03 / STORAGE</span><span>CHỌN SSD</span></div><div className="config-options">{componentOptions.storage.map((option, index) => <button aria-pressed={selected.storage === index} className={selected.storage === index ? "config-option selected" : "config-option"} key={option.name} onClick={() => setSelected((current) => ({ ...current, storage: index }))} type="button"><span className="config-check">{selected.storage === index && <Check size={13} />}</span><span>{option.name}</span><b>{option.price ? `+${formatVnd(option.price)}` : "MẶC ĐỊNH"}</b></button>)}</div></div>
          <div className="detail-actions"><button className="button-primary" onClick={addConfiguredBuild} type="button"><ShoppingCart size={17} /> Thêm vào giỏ hàng</button><Link className="button-outline" href="/cart">Mở giỏ hàng <ChevronRight size={17} /></Link></div>
          <div className="detail-promises"><div><ShieldCheck size={17} /><span><b>Bảo hành chính hãng</b><small>36 tháng toàn quốc</small></span></div><div><Wrench size={17} /><span><b>Kiểm tra kỹ thuật</b><small>Stress test trước khi giao</small></span></div><div><Truck size={17} /><span><b>Giao hàng an tâm</b><small>Đóng gói chống sốc</small></span></div></div>
          <a className="detail-contact" href="mailto:support@corely.vn">Cần tư vấn cấu hình? Liên hệ chuyên gia Corely <ArrowRight size={15} /></a>
        </section>
      </div>
      <section className="content-section detail-specs"><div className="eyebrow"><span className="eyebrow-line" /> FULL SPECIFICATION</div><h2 className="section-title">Thông số kỹ thuật <span>chi tiết.</span></h2><div className="spec-table">{[
        ["CPU", "Intel Core i7-14700K · 20 nhân / 28 luồng"],
        ["GPU", "GeForce RTX 4070 Ti SUPER · 16GB GDDR6X"],
        ["Bo mạch chủ", "MSI MAG Z790 TOMAHAWK MAX WIFI"],
        ["RAM", "32GB DDR5 6000MHz · Dual channel"],
        ["Lưu trữ", "SSD NVMe Gen 4 · 1TB"],
        ["Nguồn", "850W 80 Plus Gold · ATX 3.0"],
        ["Hệ điều hành", "Windows 11 bản quyền tùy chọn"],
      ].map(([label, value]) => <div key={label}><span>{label}</span><b>{value}</b></div>)}</div></section>
      <section className="content-section" id="reviews"><div className="section-heading-row"><div><div className="eyebrow"><span className="eyebrow-line" /> COMPLETE YOUR SETUP</div><h2 className="section-title">Linh kiện & gear<br /><span>thường mua cùng.</span></h2></div><Link className="text-link" href="/products">Xem tất cả <ArrowRight size={16} /></Link></div><div className="product-grid">{[products[4], products[6], products[7]].map((product) => <ProductCard key={product.id} product={product} />)}</div></section>
      <div className="detail-back"><Link href="/products"><ArrowLeft size={15} /> Quay lại danh sách PC gaming</Link></div>
    </div>
  );
}
