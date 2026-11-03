"use client";

import Link from "next/link";
import Image from "next/image";
import { useState } from "react";
import {
  ArrowDownRight,
  ArrowRight,
  ArrowUpRight,
  BadgeCheck,
  Cpu,
  Gauge,
  Headphones,
  ShieldCheck,
  Truck,
  Wrench,
} from "lucide-react";
import { ProductCard } from "@/components/product-card";
import { useCart } from "@/components/storefront-provider";
import { featuredProduct, formatVnd, products } from "@/lib/store-data";

const budgetRigs = [
  { ceiling: 25, title: "Phantom Air i5", product: products[0] },
  { ceiling: 45, title: "Frost Valkyrie White", product: products[1] },
  { ceiling: 120, title: "Apex Cyberpunk Edition", product: featuredProduct },
];

const categories = [
  ["Card đồ họa", "GRAPHICS / GPU", "▧"],
  ["Bộ vi xử lý", "PROCESSORS / CPU", "⌘"],
  ["Bo mạch chủ", "MOTHERBOARDS", "▦"],
  ["RAM & SSD", "MEMORY / STORAGE", "◫"],
  ["Nguồn & vỏ máy", "POWER / CASE", "⬡"],
  ["Màn hình & Gear", "DISPLAY / GEAR", "▣"],
];

export default function HomePage() {
  const [budget, setBudget] = useState(35);
  const [purpose, setPurpose] = useState("Gaming eSports / AAA");
  const estimate = budgetRigs.find((rig) => budget <= rig.ceiling) ?? budgetRigs[2];
  const { addItem } = useCart();

  return (
    <>
      <section className="store-wrap home-hero">
        <div className="hero-copy">
          <div className="eyebrow"><span className="eyebrow-line" /> CORELY PERFORMANCE SYSTEMS <span className="eyebrow-year">EST. 2025</span></div>
          <h1>Chiến game<br />đỉnh cao.<br /><span>Sáng tạo vô tận.</span></h1>
          <p>Hiệu năng chuẩn xác cho mọi cuộc chơi. Khám phá những cấu hình PC được tuyển chọn và tinh chỉnh bởi đội ngũ chuyên gia Corely.</p>
          <div className="hero-actions">
            <Link className="button-primary" href="/products">Khám phá cấu hình <ArrowRight size={17} /></Link>
            <Link className="button-quiet" href="#pc-estimator">Tìm cấu hình của bạn <ArrowDownRight size={17} /></Link>
          </div>
          <div className="hero-proof"><span><BadgeCheck size={16} /> Linh kiện chính hãng</span><span><ShieldCheck size={16} /> Bảo hành 36 tháng</span></div>
        </div>
        <div className="hero-visual">
          <div className="hero-orbit orbit-one" /><div className="hero-orbit orbit-two" />
          <div className="hero-glow" />
          <Image alt="Dàn PC Gaming Corely Apex Cyberpunk Edition" height={900} priority src={featuredProduct.image} unoptimized width={1200} />
          <div className="hero-scan-label"><i /> PERFORMANCE SERIES <span>01 / 04</span></div>
          <div className="hero-spec-float"><span>GPU PERFORMANCE</span><b>1440p <i>ULTRA</i></b><small>RAY TRACING READY</small></div>
          <div className="hero-price-float"><span>APEX CYBERPUNK EDITION</span><b>{formatVnd(featuredProduct.price)}</b><Link href={featuredProduct.href}>Xem cấu hình <ArrowUpRight size={14} /></Link></div>
        </div>
        <div className="hero-index">01 <span>/</span> 04</div>
      </section>

      <section className="store-wrap stats-strip" aria-label="Cam kết Corely">
        <div><strong>15K<span>+</span></strong><small>GAME THỦ TIN CHỌN</small></div>
        <div><strong>36<span>TH</span></strong><small>BẢO HÀNH CHÍNH HÃNG</small></div>
        <div><strong>24<span>/7</span></strong><small>HỖ TRỢ KỸ THUẬT</small></div>
        <div className="stats-note"><i /> BUILT FOR PERFORMANCE <span>— NO COMPROMISE</span></div>
      </section>

      <section className="estimator-section" id="pc-estimator">
        <div className="store-wrap estimator-layout">
          <div>
            <div className="eyebrow"><span className="eyebrow-line" /> CONFIGURATION ENGINE</div>
            <h2 className="section-title">Cấu hình PC.<br /><span>Chuẩn nhu cầu.</span></h2>
            <p className="section-copy">Chọn mục đích sử dụng và ngân sách. Corely sẽ gợi ý cỗ máy cân bằng nhất cho bạn.</p>
            <div className="purpose-label">01 / BẠN SẼ DÙNG PC CHO VIỆC GÌ?</div>
            <div className="purpose-options">
              {["Gaming eSports / AAA", "Stream & sáng tạo", "Đồ họa 3D / AI Render"].map((item) => (
                <button aria-pressed={purpose === item} className={purpose === item ? "purpose-option active" : "purpose-option"} key={item} onClick={() => setPurpose(item)} type="button">
                  <span>{item === "Gaming eSports / AAA" ? "◈" : item === "Stream & sáng tạo" ? "◉" : "⌘"}</span>{item}
                </button>
              ))}
            </div>
            <div className="budget-head"><span>02 / NGÂN SÁCH DỰ KIẾN</span><b>{budget} <small>TRIỆU VNĐ</small></b></div>
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
            <div className="estimate-topline"><span>CORELY CONFIGURATION / SUGGESTED BUILD</span><span>02.01</span></div>
            <div className="estimate-art"><span>OPTIMIZED FOR {purpose.toUpperCase()}</span><Image alt="" height={600} src={estimate.product.image} unoptimized width={800} /><div className="estimate-crosshair">+</div></div>
            <div className="estimate-details"><div><span>GỢI Ý CẤU HÌNH</span><h3>{estimate.title}</h3></div><b>{formatVnd(estimate.product.price)}</b></div>
            <ul className="estimate-specs">{estimate.product.specs.map((spec) => <li key={spec}><span>{spec}</span><i>INCLUDED</i></li>)}</ul>
            <div className="estimate-actions">
              <button className="button-primary" onClick={() => addItem({ productId: estimate.product.id, title: estimate.product.name, variant: `Gợi ý theo nhu cầu: ${purpose}`, price: estimate.product.price, image: estimate.product.image })} type="button">Thêm vào giỏ <ArrowRight size={16} /></button>
              <Link className="button-outline" href={estimate.product.href}>Xem chi tiết</Link>
            </div>
            <div className="estimate-footnote">* Giá tham khảo cho cấu hình tiêu chuẩn. Có thể tùy chỉnh linh kiện.</div>
          </div>
        </div>
      </section>

      <section className="store-wrap content-section">
        <div className="section-heading-row"><div><div className="eyebrow"><span className="eyebrow-line" /> CURATED BY CORELY</div><h2 className="section-title">Cỗ máy được<br /><span>chọn nhiều nhất.</span></h2></div><Link className="text-link" href="/products">Tất cả cấu hình <ArrowUpRight size={16} /></Link></div>
        <div className="product-grid home-products">{products.slice(0, 4).map((product) => <ProductCard key={product.id} product={product} />)}</div>
      </section>

      <section className="category-section">
        <div className="store-wrap">
          <div className="section-heading-row"><div><div className="eyebrow"><span className="eyebrow-line" /> FIND YOUR COMPONENT</div><h2 className="section-title">Linh kiện<br /><span>chính hãng.</span></h2></div><p className="section-copy category-intro">Từ một linh kiện nâng cấp đến trọn bộ chiến mã, chọn đúng thứ bạn cần.</p></div>
          <div className="category-grid">{categories.map(([name, tag, icon]) => <Link className="category-tile" href={`/products?category=${encodeURIComponent(name)}`} key={name}><span className="category-symbol">{icon}</span><span className="category-tag">{tag}</span><b>{name}</b><ArrowUpRight className="category-arrow" size={17} /></Link>)}</div>
        </div>
      </section>

      <section className="store-wrap benefits-section">
        <div className="eyebrow"><span className="eyebrow-line" /> CORELY SERVICE STANDARD</div>
        <h2 className="section-title">Không chỉ là phần cứng.<br /><span>Là sự an tâm.</span></h2>
        <div className="benefit-grid">
          <div><span><ShieldCheck /></span><b>Linh kiện new seal</b><p>Cam kết sản phẩm chính hãng, nguyên seal và đầy đủ hóa đơn.</p></div>
          <div><span><Wrench /></span><b>Lắp đặt chuẩn chỉnh</b><p>Build gọn gàng, tối ưu luồng khí và kiểm tra kỹ trước khi giao.</p></div>
          <div><span><Truck /></span><b>Giao hàng an tâm</b><p>Đóng gói chống sốc nhiều lớp, hỗ trợ giao hàng toàn quốc.</p></div>
          <div><span><Headphones /></span><b>Hỗ trợ 24/7</b><p>Đội ngũ kỹ thuật luôn sẵn sàng đồng hành cùng bạn.</p></div>
        </div>
        <div className="benefit-tail"><span><Gauge size={17} /> THỰC HIỆN BÀI KIỂM TRA HIỆU NĂNG TRƯỚC KHI BÀN GIAO</span><Link href="/products">Tìm hiểu thêm <ArrowRight size={15} /></Link></div>
      </section>

      <section className="newsletter">
        <div className="store-wrap newsletter-inner"><div><div className="eyebrow"><span className="eyebrow-line" /> CORELY DISPATCH / 001</div><h2>Nhận tín hiệu<br /><span>từ thế giới công nghệ.</span></h2><p>Cập nhật cấu hình mới, deal linh kiện và những bài test hiệu năng mới nhất.</p></div><form action="/products" className="newsletter-form"><label className="sr-only" htmlFor="newsletter-email">Email của bạn</label><input id="newsletter-email" name="q" placeholder="Email của bạn" type="email" /><button aria-label="Đăng ký nhận tin" type="submit"><ArrowRight size={19} /></button><small>KHÔNG SPAM. CHỈ CÓ THÔNG TIN ĐÁNG ĐỌC.</small></form><Cpu className="newsletter-art" size={200} strokeWidth={0.45} /></div>
      </section>
    </>
  );
}
