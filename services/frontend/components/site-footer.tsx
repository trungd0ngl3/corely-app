import Link from "next/link";
import { AtSign, Camera, Cpu, Play } from "lucide-react";

export function SiteFooter() {
  return (
    <footer className="store-footer">
      <div className="store-wrap footer-grid">
        <div className="footer-brand">
          <Link aria-label="Corely - Trang chủ" className="brand" href="/">
            <span className="brand-mark"><Cpu size={22} /></span>
            <span>CORELY<span className="brand-dot">.</span><small>PRECISION HARDWARE</small></span>
          </Link>
          <p>Hiệu năng chuẩn xác. Cấu hình được tối ưu cho hành trình của bạn.</p>
          <div className="social-links">
            <a aria-label="Email" href="mailto:support@corely.vn"><AtSign size={17} /></a>
            <a aria-label="Instagram" href="https://instagram.com"><Camera size={17} /></a>
            <a aria-label="YouTube" href="https://youtube.com"><Play size={16} /></a>
          </div>
        </div>
        <div><h3>Khám phá</h3><Link href="/products">PC Gaming</Link><Link href="/products">Linh kiện PC</Link><Link href="/products">Màn hình & Gear</Link></div>
        <div><h3>Hỗ trợ khách hàng</h3><a href="mailto:support@corely.vn">Liên hệ hỗ trợ</a><a href="mailto:support@corely.vn">Chính sách bảo hành</a><a href="mailto:support@corely.vn">Giao hàng & thanh toán</a></div>
        <div><h3>Kết nối Corely</h3><p>Đội ngũ kỹ thuật sẵn sàng hỗ trợ bạn mỗi ngày.</p><span className="footer-contact">1900 636 789</span><span className="footer-contact">support@corely.vn</span></div>
      </div>
      <div className="store-wrap footer-bottom"><span>© 2025 CORELY. HIỆU NĂNG TRONG TẦM TAY.</span><span>Được thiết kế cho những người đam mê công nghệ.</span></div>
    </footer>
  );
}
