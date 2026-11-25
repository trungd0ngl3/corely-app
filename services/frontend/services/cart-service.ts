import { getAuthSession } from "@/services/auth-service";

export type CartItem = { id: string; productId: string; variantId?: string; title: string; variant: string; price: number; quantity: number; image: string; inStock: boolean; availableStock?: number };
export type Cart = { items: CartItem[]; totalItems: number; totalAmount: number; discountAmount: number; finalAmount: number };
export class CartApiError extends Error { constructor(message: string, readonly status?: number) { super(message); this.name = "CartApiError"; } }

const API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL ?? "http://localhost:8080";
function isRecord(value: unknown): value is Record<string, unknown> { return typeof value === "object" && value !== null && !Array.isArray(value); }
function readString(value: unknown): string | undefined { return typeof value === "string" && value.trim() ? value : undefined; }
function readNumber(value: unknown): number | undefined { if (typeof value === "number" && Number.isFinite(value)) return value; if (typeof value === "string" && value.trim()) { const parsed = Number(value); return Number.isFinite(parsed) ? parsed : undefined; } return undefined; }
function resolveImageUrl(value: unknown): string { const image = readString(value); if (!image) return ""; if (/^(https?:|data:|\/)/i.test(image)) return image; return `${API_BASE_URL.replace(/\/$/, "")}/${image.replace(/^\//, "")}`; }

function readCartItem(value: unknown): CartItem {
  if (!isRecord(value)) throw new CartApiError("The cart API returned an invalid item.");
  const productId = readString(value.productId); const title = readString(value.productName); const price = readNumber(value.price); const quantity = readNumber(value.quantity);
  if (!productId || !title || price === undefined || quantity === undefined) throw new CartApiError("The cart API returned an invalid item.");
  const variantId = readString(value.variantId);
  return { id: `${productId}:${variantId ?? "0"}`, productId, variantId, title, variant: readString(value.variantName) ?? "Cấu hình tiêu chuẩn", price, quantity, image: resolveImageUrl(value.imageUrl), inStock: value.inStock !== false, availableStock: readNumber(value.availableStock) };
}
function readCart(value: unknown): Cart {
  if (!isRecord(value) || !Array.isArray(value.items)) throw new CartApiError("The cart API returned an unexpected response.");
  const items = value.items.map(readCartItem);
  return { items, totalItems: readNumber(value.totalItems) ?? items.reduce((sum, item) => sum + item.quantity, 0), totalAmount: readNumber(value.totalAmount) ?? items.reduce((sum, item) => sum + item.price * item.quantity, 0), discountAmount: readNumber(value.discountAmount) ?? 0, finalAmount: readNumber(value.finalAmount) ?? 0 };
}
async function requestCart<T>(path: string, init?: RequestInit): Promise<T> {
  const session = getAuthSession();
  if (!session) throw new CartApiError("Vui lòng đăng nhập để sử dụng giỏ hàng.", 401);
  const response = await fetch(`${API_BASE_URL}/api/v1/cart${path}`, { ...init, headers: { Authorization: `Bearer ${session.token}`, ...(init?.body ? { "Content-Type": "application/json" } : {}), ...init?.headers }, cache: "no-store" });
  let payload: unknown; try { payload = await response.json(); } catch { throw new CartApiError(`The cart service returned an invalid response (${response.status}).`, response.status); }
  if (!response.ok) { const message = isRecord(payload) && typeof payload.message === "string" ? payload.message : `Cart request failed (${response.status}).`; throw new CartApiError(message, response.status); }
  if (!isRecord(payload) || !("result" in payload)) throw new CartApiError("The cart API returned an unexpected response.", response.status);
  return payload.result as T;
}
export async function fetchCart(): Promise<Cart> { return readCart(await requestCart<unknown>("")); }
export async function addCartItem(input: { productId: string; variantId?: string; quantity: number }): Promise<void> { await requestCart<unknown>("/items", { method: "POST", body: JSON.stringify(input) }); }
export async function updateCartItem(input: { productId: string; variantId?: string; quantity: number }): Promise<void> { await requestCart<unknown>("/items", { method: "PUT", body: JSON.stringify(input) }); }
export async function removeCartItem(productId: string, variantId?: string): Promise<void> { const query = variantId ? `?variantId=${encodeURIComponent(variantId)}` : ""; await requestCart<unknown>(`/items/${encodeURIComponent(productId)}${query}`, { method: "DELETE" }); }
export async function clearCart(): Promise<void> { await requestCart<unknown>("", { method: "DELETE" }); }
