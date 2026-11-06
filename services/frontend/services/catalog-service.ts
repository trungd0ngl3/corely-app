export type StoreCategory = {
  id: string;
  name: string;
  slug: string;
};

export type ProductVariant = {
  id: string;
  name: string;
  sku?: string;
  price: number;
  stockQuantity?: number;
  image?: string;
  isActive?: boolean;
};

export type ProductSpecEntry = {
  label: string;
  value: string;
};

export type CatalogProduct = {
  id: string;
  name: string;
  slug?: string;
  sku?: string;
  category: string;
  categoryId?: string;
  brandName?: string;
  storeName?: string;
  price: number;
  oldPrice?: number;
  stockQuantity?: number;
  rating?: string;
  reviews?: number;
  badge?: string;
  description?: string;
  specs: string[];
  specEntries: ProductSpecEntry[];
  image?: string;
  images: string[];
  variants: ProductVariant[];
  href: string;
};

export type ProductReview = {
  id: string;
  rating: number;
  comment: string;
  userName: string;
  avatarUrl?: string;
  createdAt?: string;
};

export type ProductReviewsResult = {
  content: ProductReview[];
  totalElements: number;
  averageRating: number;
};

export type LandingCatalog = {
  products: CatalogProduct[];
  categories: StoreCategory[];
  totalProducts: number;
};

const API_BASE_URL =
  process.env.NEXT_PUBLIC_API_BASE_URL ?? "http://localhost:8080";

function isRecord(value: unknown): value is Record<string, unknown> {
  return typeof value === "object" && value !== null && !Array.isArray(value);
}

function readString(value: unknown): string | undefined {
  return typeof value === "string" && value.trim() ? value : undefined;
}

function readNumber(value: unknown): number | undefined {
  if (typeof value === "number" && Number.isFinite(value)) return value;
  if (typeof value === "string" && value.trim()) {
    const parsed = Number(value);
    if (Number.isFinite(parsed)) return parsed;
  }
  return undefined;
}

async function readResult(url: string, signal: AbortSignal): Promise<unknown> {
  const response = await fetch(url, { signal });
  if (!response.ok) {
    throw new Error(`Catalog request failed (${response.status} ${response.statusText}).`);
  }

  const payload: unknown = await response.json();
  if (!isRecord(payload) || !("result" in payload)) {
    throw new Error("The catalog API returned an unexpected response.");
  }
  return payload.result;
}

function resolveImageUrl(value: unknown): string | undefined {
  const image = readString(value);
  if (!image) return undefined;
  if (/^(https?:|data:|\/)/i.test(image)) return image;
  return `${API_BASE_URL.replace(/\/$/, "")}/${image.replace(/^\//, "")}`;
}
function toStoreCategory(value: unknown): StoreCategory | undefined {
  if (!isRecord(value)) return undefined;
  const id = readString(value.id);
  const name = readString(value.name);
  const slug = readString(value.slug);
  return id && name && slug ? { id, name, slug } : undefined;
}



export type CatalogProductPage = {
  content: CatalogProduct[];
  totalElements: number;
  totalPages: number;
  number: number; // current page index
  size: number; // page size
};

export async function fetchProductBySlug(slug: string, signal?: AbortSignal): Promise<CatalogProduct | undefined> {
  const url = `${API_BASE_URL}/api/v1/products/${encodeURIComponent(slug)}`;
  try {
    const result = await readResult(url, signal ?? new AbortController().signal);
    const product = toCatalogProduct(result);
    if (product) return product;
  } catch (err) {
    // Check fallback for apex-cyberpunk
  }

  if (slug === "apex-cyberpunk") {
    return getApexCyberpunkFallback();
  }

  return undefined;
}

export async function fetchProductReviews(
  productId: string,
  signal?: AbortSignal,
): Promise<ProductReviewsResult> {
  if (productId === "apex-cyberpunk") {
    return {
      content: [
        {
          id: "rev-1",
          rating: 5,
          comment: "Máy build rất chỉn chu, tản nhiệt nước và luồng gió tối ưu cực tốt. Test FurMark 4K nhiệt độ chỉ tầm 65°C.",
          userName: "Hoàng Nam",
          createdAt: "2025-02-14T10:00:00",
        },
        {
          id: "rev-2",
          rating: 5,
          comment: "Chiến Black Myth: Wukong và Cyberpunk 2077 max settings mượt mà không bị drop frame. Đóng gói 3 lớp xốp an tâm tuyệt đối.",
          userName: "Tuấn Anh",
          createdAt: "2025-02-01T15:30:00",
        },
      ],
      totalElements: 2,
      averageRating: 5.0,
    };
  }

  const url = `${API_BASE_URL}/api/v1/products/${encodeURIComponent(productId)}/reviews?page=0&size=20`;
  try {
    const result = await readResult(url, signal ?? new AbortController().signal);
    if (!isRecord(result) || !Array.isArray((result as any).content)) {
      return { content: [], totalElements: 0, averageRating: 5.0 };
    }
    const page = result as any;
    const pageMeta = isRecord(page.page) ? page.page : page;
    const totalElements = readNumber(pageMeta.totalElements ?? page.totalElements) ?? 0;
    const reviews: ProductReview[] = (page.content as unknown[])
      .filter(isRecord)
      .map((r) => ({
        id: readString(r.id) ?? "",
        rating: readNumber(r.rating) ?? 5,
        comment: readString(r.comment) ?? "",
        userName: readString(r.userName) ?? "Khách hàng Corely",
        avatarUrl: resolveImageUrl(r.avatarUrl),
        createdAt: readString(r.createdAt),
      }));

    const sumRating = reviews.reduce((acc, r) => acc + r.rating, 0);
    const averageRating = reviews.length > 0 ? Number((sumRating / reviews.length).toFixed(1)) : 5.0;

    return {
      content: reviews,
      totalElements: totalElements || reviews.length,
      averageRating,
    };
  } catch {
    return { content: [], totalElements: 0, averageRating: 5.0 };
  }
}

export async function fetchRelatedProducts(
  categoryId?: string,
  currentProductId?: string,
  signal?: AbortSignal,
): Promise<CatalogProduct[]> {
  try {
    const page = await fetchProductsPage(
      { categoryId, size: 8 },
      signal ?? new AbortController().signal,
    );
    return page.content
      .filter((p) => p.id !== currentProductId)
      .slice(0, 4);
  } catch {
    return [];
  }
}

export async function fetchProductsPage(
  params: {
    q?: string;
    categoryId?: string;
    brandId?: string;
    minPrice?: number;
    maxPrice?: number;
    page?: number;
    size?: number;
    sort?: string; // e.g., 'price,asc'
  },
  signal: AbortSignal,
): Promise<CatalogProductPage> {

  const query = new URLSearchParams();
  if (params.q) query.append('q', params.q);
  if (params.categoryId) query.append('categoryId', params.categoryId);
  if (params.brandId) query.append('brandId', params.brandId);
  if (params.minPrice !== undefined) query.append('minPrice', String(params.minPrice));
  if (params.maxPrice !== undefined) query.append('maxPrice', String(params.maxPrice));
  query.append('page', String(params.page ?? 0));
  query.append('size', String(params.size ?? 200)); // fetch large chunk
  if (params.sort) query.append('sort', params.sort);

  const url = `${API_BASE_URL}/api/v1/products?${query.toString()}`;
  const result = await readResult(url, signal);
  if (!isRecord(result) || !Array.isArray((result as any).content)) {
    throw new Error("The products API returned an unexpected response.");
  }
  const page = result as any;
  const pageMeta = isRecord(page.page) ? page.page : page;
  const totalElements = readNumber(pageMeta.totalElements ?? page.totalElements);
  const totalPages = readNumber(pageMeta.totalPages ?? page.totalPages);
  const number = readNumber(pageMeta.number ?? page.number) ?? 0;
  const size = readNumber(pageMeta.size ?? page.size) ?? 0;
  if (totalElements === undefined || totalPages === undefined) {
    throw new Error("The products API returned an unexpected pagination fields.");
  }
  const products = (page.content as unknown[])
    .map(toCatalogProduct)
    .filter((p): p is CatalogProduct => p !== undefined);
  return {
    content: products,
    totalElements,
    totalPages,
    number,
    size,
  };
}

export async function fetchCategories(signal: AbortSignal): Promise<StoreCategory[]> {
  const result = await readResult(`${API_BASE_URL}/api/v1/categories`, signal);
  if (!Array.isArray(result)) {
    throw new Error("The categories API returned an unexpected response.");
  }
  return (result as unknown[])
    .map(toStoreCategory)
    .filter((c): c is StoreCategory => c !== undefined);
}

export async function fetchLandingCatalog(
  signal: AbortSignal,
): Promise<LandingCatalog> {
  const [productsResult, categoriesResult] = await Promise.all([
    fetchProductsPage({ size: 200 }, signal),
    fetchCategories(signal),
  ]);

  return {
    products: productsResult.content,
    categories: categoriesResult,
    totalProducts: productsResult.totalElements,
  };
}

function toProductVariant(value: unknown): ProductVariant | undefined {
  if (!isRecord(value)) return undefined;
  const id = readString(value.id);
  const name = readString(value.name);
  const price = readNumber(value.price);
  if (!id || !name || price === undefined) return undefined;
  return {
    id,
    name,
    sku: readString(value.sku),
    price,
    stockQuantity: readNumber(value.stockQuantity),
    image: resolveImageUrl(value.imageUrl),
    isActive: typeof value.isActive === "boolean" ? value.isActive : true,
  };
}

// Updated href generation to use slug when available
function toCatalogProduct(value: unknown): CatalogProduct | undefined {
  if (!isRecord(value)) return undefined;
  const id = readString(value.id);
  const name = readString(value.name);
  const slug = readString(value.slug);
  const originalPrice = readNumber(value.price);
  if (!id || !name || originalPrice === undefined) return undefined;

  const discountPrice = readNumber(value.discountPrice);
  const hasDiscount =
    discountPrice !== undefined &&
    discountPrice > 0 &&
    discountPrice < originalPrice;

  const specEntries: ProductSpecEntry[] = isRecord(value.specs)
    ? Object.entries(value.specs).map(([key, spec]) => {
        const detail =
          typeof spec === "string" || typeof spec === "number" || typeof spec === "boolean"
            ? String(spec)
            : JSON.stringify(spec);
        return { label: key, value: detail };
      })
    : [];

  const specs = specEntries.map((e) => `${e.label}: ${e.value}`);

  const rawImageUrls = Array.isArray(value.imageUrls)
    ? value.imageUrls.filter((image): image is string => typeof image === "string")
    : [];

  const primaryImage = resolveImageUrl(value.thumbnailUrl ?? rawImageUrls[0]);
  const resolvedImages = rawImageUrls.map(resolveImageUrl).filter((img): img is string => Boolean(img));
  const images = Array.from(new Set([primaryImage, ...resolvedImages].filter((img): img is string => Boolean(img))));

  const variants: ProductVariant[] = Array.isArray(value.variants)
    ? (value.variants as unknown[])
        .map(toProductVariant)
        .filter((v): v is ProductVariant => v !== undefined && v.isActive !== false)
    : [];

  return {
    id,
    name,
    slug: slug ?? undefined,
    sku: readString(value.sku),
    category: readString(value.categoryName) ?? "Linh kiện",
    categoryId: readString(value.categoryId),
    brandName: readString(value.brandName),
    storeName: readString(value.storeName),
    price: hasDiscount ? discountPrice! : originalPrice,
    oldPrice: hasDiscount ? originalPrice : undefined,
    stockQuantity: readNumber(value.stockQuantity),
    description: readString(value.description),
    specs,
    specEntries,
    image: primaryImage,
    images: images.length > 0 ? images : primaryImage ? [primaryImage] : [],
    variants,
    href: slug ? `/products/${slug}` : "/products",
  };
}

function getApexCyberpunkFallback(): CatalogProduct {
  const fallbackImage =
    "https://lh3.googleusercontent.com/aida-public/AB6AXuA4xt_AaPUOQiBS62ZLG-gtIHeVxb8fDRwyGgtcRRZB3n4Sxp-eONiPVN9GtjtQkSLS3THtHNprRO_aJpNovfRkaKWkTWRchCES9xuMS7njq2ek0obLldbuwVHZ6QRiTIgTEAk9gA6qt8Yrw8dloD8NF5cBgRUurk5vIKFQA9KzaqrUmiqrJXbiBADe333OudDejZKuoBD3hC02dJFhfdBO-rr4_JT1Vpx3G7Tdhd70";

  return {
    id: "apex-cyberpunk",
    slug: "apex-cyberpunk",
    name: "PC Gaming Corely Apex Cyberpunk Edition",
    category: "PC Gaming",
    price: 52990000,
    oldPrice: 58990000,
    rating: "5.0",
    reviews: 64,
    badge: "CORELY SIGNATURE BUILD",
    description:
      "Cỗ máy gaming hiệu năng cao được tinh chỉnh để chinh phục game AAA, stream và sáng tạo nội dung. Từng linh kiện được kiểm tra tương thích trước khi lắp ráp.",
    specs: [
      "CPU: Intel Core i7-14700K · 20 nhân / 28 luồng",
      "GPU: GeForce RTX 4070 Ti SUPER · 16GB GDDR6X",
      "Bo mạch chủ: MSI MAG Z790 TOMAHAWK MAX WIFI",
      "RAM: 32GB DDR5 6000MHz · Dual channel",
      "Lưu trữ: SSD NVMe Gen 4 · 1TB",
      "Nguồn: 850W 80 Plus Gold · ATX 3.0",
      "Hệ điều hành: Windows 11 bản quyền tùy chọn",
    ],
    specEntries: [
      { label: "CPU", value: "Intel Core i7-14700K · 20 nhân / 28 luồng" },
      { label: "GPU", value: "GeForce RTX 4070 Ti SUPER · 16GB GDDR6X" },
      { label: "Bo mạch chủ", value: "MSI MAG Z790 TOMAHAWK MAX WIFI" },
      { label: "RAM", value: "32GB DDR5 6000MHz · Dual channel" },
      { label: "Lưu trữ", value: "SSD NVMe Gen 4 · 1TB" },
      { label: "Nguồn", value: "850W 80 Plus Gold · ATX 3.0" },
      { label: "Hệ điều hành", value: "Windows 11 bản quyền tùy chọn" },
    ],
    image: fallbackImage,
    images: [fallbackImage],
    variants: [
      {
        id: "apex-v1",
        name: "Cấu hình Tiêu chuẩn (i7-14700K · 32GB · 1TB)",
        price: 52990000,
        stockQuantity: 10,
      },
      {
        id: "apex-v2",
        name: "Cấu hình Nâng cấp (i9-14900K · 64GB · 2TB)",
        price: 62440000,
        stockQuantity: 5,
      },
    ],
    sku: "NX-APEX-147K",
    brandName: "Corely Signature",
    storeName: "Corely Flagship Store",
    stockQuantity: 15,
    href: "/products/apex-cyberpunk",
  };
}
