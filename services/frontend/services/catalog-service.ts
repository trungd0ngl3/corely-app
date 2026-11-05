export type StoreCategory = {
  id: string;
  name: string;
  slug: string;
};

export type CatalogProduct = {
  id: string;
  name: string;
  category: string;
  price: number;
  oldPrice?: number;
  rating?: string;
  reviews?: number;
  badge?: string;
  description?: string;
  specs: string[];
  image?: string;
  href: string;
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
  const totalElements = readNumber(page.totalElements);
  const totalPages = readNumber(page.totalPages);
  const number = readNumber(page.number) ?? 0;
  const size = readNumber(page.size) ?? 0;
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
  const specs = isRecord(value.specs)
    ? Object.entries(value.specs).map(([key, spec]) => {
        const detail =
          typeof spec === "string" || typeof spec === "number" || typeof spec === "boolean"
            ? String(spec)
            : JSON.stringify(spec);
        return `${key}: ${detail}`;
      })
    : [];
  const imageUrls = Array.isArray(value.imageUrls)
    ? value.imageUrls.filter((image): image is string => typeof image === "string")
    : [];

  return {
    id,
    name,
    category: readString(value.categoryName) ?? "Linh kiện",
    price: hasDiscount ? discountPrice! : originalPrice,
    oldPrice: hasDiscount ? originalPrice : undefined,
    description: readString(value.description),
    specs,
    image: resolveImageUrl(value.thumbnailUrl ?? imageUrls[0]),
    href: slug ? `/products/${slug}` : "/products",
  };
}
