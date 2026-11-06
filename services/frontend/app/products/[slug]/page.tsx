import type { Metadata } from "next";
import Link from "next/link";
import { ArrowRight } from "lucide-react";
import {
  fetchProductBySlug,
  fetchProductReviews,
  fetchRelatedProducts,
} from "@/services/catalog-service";
import { ProductDetailView } from "./product-detail-view";

type PageProps = {
  params: Promise<{ slug: string }>;
};

export async function generateMetadata({
  params,
}: PageProps): Promise<Metadata> {
  const { slug } = await params;
  const product = await fetchProductBySlug(slug);

  if (!product) {
    return {
      title: "Sản phẩm không tìm thấy | Corely",
      description: "Không tìm thấy sản phẩm trong hệ thống phần cứng Corely.",
    };
  }

  return {
    title: `${product.name} | Corely`,
    description:
      product.description ||
      `Mua ${product.name} chính hãng giá tốt tại Corely. Bảo hành 36 tháng.`,
  };
}

export default async function ProductDetailPage({ params }: PageProps) {
  const { slug } = await params;
  const product = await fetchProductBySlug(slug);

  if (!product) {
    return (
      <div className="store-wrap catalog-page">
        <div className="breadcrumbs">
          <Link href="/">Trang chủ</Link>
          <span>/</span>
          <Link href="/products">Sản phẩm</Link>
          <span>/</span>
          <span>Không tìm thấy</span>
        </div>
        <div className="empty-results" style={{ margin: "60px auto", maxWidth: "600px" }}>
          <span>⌕</span>
          <h2>Không tìm thấy sản phẩm</h2>
          <p>
            Sản phẩm với đường dẫn “{slug}” không tồn tại hoặc đã ngừng kinh doanh. Vui lòng kiểm tra lại đường dẫn hoặc duyệt qua danh mục phần cứng.
          </p>
          <Link className="button-primary" href="/products">
            Khám phá danh sách sản phẩm <ArrowRight size={16} />
          </Link>
        </div>
      </div>
    );
  }

  const [reviewsResult, relatedProducts] = await Promise.all([
    fetchProductReviews(product.id),
    fetchRelatedProducts(product.categoryId, product.id),
  ]);

  return (
    <ProductDetailView
      product={product}
      relatedProducts={relatedProducts}
      reviewsResult={reviewsResult}
    />
  );
}