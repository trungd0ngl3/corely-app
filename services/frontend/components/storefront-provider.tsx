"use client";

import { createContext, useCallback, useContext, useEffect, useMemo, useState, type ReactNode } from "react";
import { useRouter } from "next/navigation";
import Link from "next/link";
import { CheckCircle2, AlertCircle, ArrowRight, X } from "lucide-react";
import { AUTH_SESSION_CHANGED_EVENT, getAuthSession } from "@/services/auth-service";
import { addCartItem, clearCart as clearRemoteCart, fetchCart, removeCartItem, type CartItem, updateCartItem, CartApiError } from "@/services/cart-service";

export type CartLine = CartItem;

export type CartToast = {
  id: string;
  type: "success" | "error";
  title: string;
  message?: string;
  action?: { label: string; href: string };
};

type CartContextValue = {
  items: CartLine[];
  itemCount: number;
  subtotal: number;
  isLoading: boolean;
  error?: string;
  lastAddedAt: number | null;
  addItem: (line: { productId: string; variantId?: string; quantity?: number; title?: string }) => Promise<boolean>;
  setQuantity: (id: string, quantity: number) => Promise<boolean>;
  removeItem: (id: string) => Promise<boolean>;
  removeItems: (ids: string[]) => Promise<boolean>;
  clearCart: () => Promise<boolean>;
  reloadCart: () => Promise<void>;
  showToast: (toast: Omit<CartToast, "id">) => void;
};

const CartContext = createContext<CartContextValue | null>(null);
function toMessage(error: unknown): string { return error instanceof Error ? error.message : "Không thể cập nhật giỏ hàng. Vui lòng thử lại."; }

export function CartProvider({ children }: { children: ReactNode }) {
  const router = useRouter();
  const [items, setItems] = useState<CartLine[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string>();
  const [toast, setToast] = useState<CartToast | null>(null);
  const [lastAddedAt, setLastAddedAt] = useState<number | null>(null);

  const showToast = useCallback((newToast: Omit<CartToast, "id">) => {
    const id = Math.random().toString(36).substring(2, 9);
    setToast({ ...newToast, id });
  }, []);

  useEffect(() => {
    if (!toast) return;
    const timer = setTimeout(() => {
      setToast((current) => (current?.id === toast.id ? null : current));
    }, 3800);
    return () => clearTimeout(timer);
  }, [toast]);

  const reloadCart = useCallback(async () => {
    if (!getAuthSession()) { setItems([]); setError(undefined); setIsLoading(false); return; }
    setIsLoading(true);
    try { const cart = await fetchCart(); setItems(cart.items); setError(undefined); }
    catch (requestError) { setItems([]); setError(toMessage(requestError)); }
    finally { setIsLoading(false); }
  }, []);

  useEffect(() => {
    const initialLoad = window.setTimeout(() => { void reloadCart(); }, 0);
    window.addEventListener(AUTH_SESSION_CHANGED_EVENT, reloadCart);
    return () => {
      window.clearTimeout(initialLoad);
      window.removeEventListener(AUTH_SESSION_CHANGED_EVENT, reloadCart);
    };
  }, [reloadCart]);

  const runMutation = useCallback(async (operation: () => Promise<void>) => {
    try {
      await operation();
      await reloadCart();
      return true;
    } catch (requestError) {
      const isAuthError =
        (requestError instanceof CartApiError && requestError.status === 401) ||
        toMessage(requestError).toLowerCase().includes("đăng nhập");
      if (isAuthError) {
        // Redirect to login, preserving current page as redirect target
        const redirect = typeof window !== "undefined" ? window.location.pathname : "/";
        router.push(`/login?redirect=${encodeURIComponent(redirect)}`);
        return false;
      }
      const msg = toMessage(requestError);
      setError(msg);
      showToast({
        type: "error",
        title: "Không thể thêm vào giỏ",
        message: msg,
      });
      return false;
    }
  }, [reloadCart, router, showToast]);

  const value = useMemo<CartContextValue>(() => ({
    items,
    itemCount: items.reduce((count, item) => count + item.quantity, 0),
    subtotal: items.reduce((sum, item) => sum + item.price * item.quantity, 0),
    isLoading,
    error,
    lastAddedAt,
    showToast,
    reloadCart,
    addItem: async (line) => {
      const success = await runMutation(() => addCartItem({ ...line, quantity: line.quantity ?? 1 }));
      if (success) {
        setLastAddedAt(Date.now());
        showToast({
          type: "success",
          title: "Đã thêm vào giỏ hàng!",
          message: line.title ? `${line.title}${line.quantity && line.quantity > 1 ? ` (x${line.quantity})` : ""}` : undefined,
          action: { label: "Xem giỏ hàng", href: "/cart" },
        });
      }
      return success;
    },
    setQuantity: (id, quantity) => {
      const item = items.find((line) => line.id === id);
      return item ? runMutation(() => updateCartItem({ productId: item.productId, variantId: item.variantId, quantity })) : Promise.resolve(false);
    },
    removeItem: (id) => {
      const item = items.find((line) => line.id === id);
      return item ? runMutation(() => removeCartItem(item.productId, item.variantId)) : Promise.resolve(false);
    },
    removeItems: (ids) => runMutation(async () => {
      const selected = items.filter((item) => ids.includes(item.id));
      await Promise.all(selected.map((item) => removeCartItem(item.productId, item.variantId)));
    }),
    clearCart: () => runMutation(clearRemoteCart),
  }), [error, isLoading, items, lastAddedAt, reloadCart, runMutation, showToast]);

  return (
    <CartContext.Provider value={value}>
      {children}
      {toast && (
        <aside
          aria-live="polite"
          className={`store-toast store-toast-${toast.type}`}
          key={toast.id}
          role="status"
        >
          <div className="store-toast-icon">
            {toast.type === "success" ? <CheckCircle2 size={18} /> : <AlertCircle size={18} />}
          </div>
          <div className="store-toast-body">
            <strong className="store-toast-title">{toast.title}</strong>
            {toast.message && <span className="store-toast-message">{toast.message}</span>}
          </div>
          {toast.action && (
            <Link className="store-toast-action" href={toast.action.href} onClick={() => setToast(null)}>
              {toast.action.label} <ArrowRight size={13} />
            </Link>
          )}
          <button
            aria-label="Đóng thông báo"
            className="store-toast-close"
            onClick={() => setToast(null)}
            type="button"
          >
            <X size={15} />
          </button>
        </aside>
      )}
    </CartContext.Provider>
  );
}

export function useCart() {
  const context = useContext(CartContext);
  if (!context) throw new Error("useCart must be used within a CartProvider");
  return context;
}
