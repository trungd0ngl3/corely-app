"use client";

import { createContext, useCallback, useContext, useEffect, useMemo, useState, type ReactNode } from "react";
import { AUTH_SESSION_CHANGED_EVENT, getAuthSession } from "@/services/auth-service";
import { addCartItem, clearCart as clearRemoteCart, fetchCart, removeCartItem, type CartItem, updateCartItem } from "@/services/cart-service";

export type CartLine = CartItem;

type CartContextValue = {
  items: CartLine[];
  itemCount: number;
  subtotal: number;
  isLoading: boolean;
  error?: string;
  addItem: (line: { productId: string; variantId?: string; quantity?: number }) => Promise<boolean>;
  setQuantity: (id: string, quantity: number) => Promise<boolean>;
  removeItem: (id: string) => Promise<boolean>;
  removeItems: (ids: string[]) => Promise<boolean>;
  clearCart: () => Promise<boolean>;
  reloadCart: () => Promise<void>;
};

const CartContext = createContext<CartContextValue | null>(null);
function toMessage(error: unknown): string { return error instanceof Error ? error.message : "Không thể cập nhật giỏ hàng. Vui lòng thử lại."; }

export function CartProvider({ children }: { children: ReactNode }) {
  const [items, setItems] = useState<CartLine[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string>();

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
    try { await operation(); await reloadCart(); return true; }
    catch (requestError) { setError(toMessage(requestError)); return false; }
  }, [reloadCart]);

  const value = useMemo<CartContextValue>(() => ({
    items,
    itemCount: items.reduce((count, item) => count + item.quantity, 0),
    subtotal: items.reduce((sum, item) => sum + item.price * item.quantity, 0),
    isLoading,
    error,
    reloadCart,
    addItem: (line) => runMutation(() => addCartItem({ ...line, quantity: line.quantity ?? 1 })),
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
  }), [error, isLoading, items, reloadCart, runMutation]);

  return <CartContext.Provider value={value}>{children}</CartContext.Provider>;
}

export function useCart() {
  const context = useContext(CartContext);
  if (!context) throw new Error("useCart must be used within a CartProvider");
  return context;
}
