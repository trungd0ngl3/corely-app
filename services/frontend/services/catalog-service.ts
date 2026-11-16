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

async function readResult(
  url: string,
  signal: AbortSignal,
  allowNotFound = false,
): Promise<unknown | undefined> {
  const response = await fetch(url, { signal });
  if (allowNotFound && response.status === 404) return undefined;
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
  const result = await readResult(
    url,
    signal ?? new AbortController().signal,
    true,
  );
  if (result === undefined) return undefined;
  const product = toCatalogProduct(result);
  if (!product) throw new Error("The product API returned an unexpected response.");
  return product;
}

export async function fetchProductReviews(
  productId: string,
  signal?: AbortSignal,
): Promise<ProductReviewsResult> {
  const url = `${API_BASE_URL}/api/v1/products/${encodeURIComponent(productId)}/reviews?page=0&size=20`;
  const result = await readResult(url, signal ?? new AbortController().signal);
  if (!isRecord(result) || !Array.isArray(result.content)) {
    throw new Error("The reviews API returned an unexpected response.");
  }
  const pageMeta = isRecord(result.page) ? result.page : result;
  const totalElements = readNumber(pageMeta.totalElements ?? result.totalElements);
  if (totalElements === undefined) {
    throw new Error("The reviews API returned unexpected pagination fields.");
  }
  const reviews: ProductReview[] = result.content.map((value) => {
    if (!isRecord(value)) {
      throw new Error("The reviews API returned an invalid review.");
    }
    const review = value;
    const id = readString(review.id);
    const rating = readNumber(review.rating);
    const comment = readString(review.comment);
    if (
      !id ||
      rating === undefined ||
      !Number.isInteger(rating) ||
      rating < 1 ||
      rating > 5 ||
      !comment
    ) {
      throw new Error("The reviews API returned an invalid review.");
    }
    return {
      id,
      rating,
      comment,
      userName: readString(review.userName) ?? "Khách hàng",
      avatarUrl: resolveImageUrl(review.avatarUrl),
      createdAt: readString(review.createdAt),
    };
  });

  return { content: reviews, totalElements };
}

export async function fetchRelatedProducts(
  categoryId?: string,
  currentProductId?: string,
  signal?: AbortSignal,
): Promise<CatalogProduct[]> {
  const page = await fetchProductsPage(
    { categoryId, size: 8 },
    signal ?? new AbortController().signal,
  );
  return page.content
    .filter((p) => p.id !== currentProductId)
    .slice(0, 4);
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
  if (!isRecord(result) || !Array.isArray(result.content)) {
    throw new Error("The products API returned an unexpected response.");
  }
  const pageMeta = isRecord(result.page) ? result.page : result;
  const totalElements = readNumber(pageMeta.totalElements ?? result.totalElements);
  const totalPages = readNumber(pageMeta.totalPages ?? result.totalPages);
  const number = readNumber(pageMeta.number ?? result.number) ?? 0;
  const size = readNumber(pageMeta.size ?? result.size) ?? 0;
  if (totalElements === undefined || totalPages === undefined) {
    throw new Error("The products API returned an unexpected pagination fields.");
  }
  const products = result.content
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
