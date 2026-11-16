import Link from "next/link";
import { ArrowRight } from "lucide-react";

export default function ProductNotFound() {
  return (
    <div className="store-wrap catalog-page">
      <div className="breadcrumbs">
        <Link href="/">Trang chủ</Link>
        <span>/</span>
        <Link href="/products">Sản phẩm</Link>
        <span>/</span>
        <span>Không tìm thấy</span>
      </div>
      <div
        className="empty-results"
        style={{ margin: "60px auto", maxWidth: "600px" }}
      >
        <span>⌕</span>
        <h2>Không tìm thấy sản phẩm</h2>
        <p>Sản phẩm không tồn tại hoặc đã ngừng kinh doanh.</p>
        <Link className="button-primary" href="/products">
          Khám phá danh sách sản phẩm <ArrowRight size={16} />
        </Link>
      </div>
    </div>
  );
}
