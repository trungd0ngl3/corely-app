import type { Metadata } from "next";
import { notFound } from "next/navigation";
import {
  fetchProductBySlug,
  fetchProductReviews,
  fetchRelatedProducts,
} from "@/services/catalog-service";
import { ProductDetailView } from "./product-detail-view";

export const instant = false;

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
      `Thông tin chi tiết về ${product.name} tại Corely.`,
  };
}

export default async function ProductDetailPage({ params }: PageProps) {
  const { slug } = await params;
  const product = await fetchProductBySlug(slug);

  if (!product) {
    notFound();
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