"use client";

import {
  createContext,
  useContext,
  useMemo,
  useSyncExternalStore,
  type ReactNode,
} from "react";

export type CartLine = {
  id: string;
  productId: string;
  title: string;
  variant: string;
  price: number;
  quantity: number;
  image: string;
};

type CartContextValue = {
  items: CartLine[];
  itemCount: number;
  subtotal: number;
  addItem: (line: Omit<CartLine, "id" | "quantity">) => void;
  setQuantity: (id: string, quantity: number) => void;
  removeItem: (id: string) => void;
  removeItems: (ids: string[]) => void;
  clearCart: () => void;
};

const STORAGE_KEY = "corely-cart-v1";
const initialItems: CartLine[] = [];
const prototypeCartItemIds = new Set([
  "apex-cyberpunk-standard",
  "rog-monitor",
  "strike-keyboard",
]);

function isCartLine(value: unknown): value is CartLine {
  if (!value || typeof value !== "object") return false;
  const line = value as Record<string, unknown>;
  return (
    typeof line.id === "string" &&
    typeof line.productId === "string" &&
    typeof line.title === "string" &&
    typeof line.variant === "string" &&
    typeof line.price === "number" &&
    typeof line.quantity === "number" &&
    typeof line.image === "string"
  );
}

let cartSnapshot = initialItems;
let cartLoaded = false;
const cartSubscribers = new Set<() => void>();

function loadCart() {
  if (cartLoaded || typeof window === "undefined") return;
  cartLoaded = true;
  try {
    const saved = window.localStorage.getItem(STORAGE_KEY);
    if (saved === null) return;
    const parsed: unknown = JSON.parse(saved);
    if (Array.isArray(parsed) && parsed.every(isCartLine)) {
      cartSnapshot = parsed.filter((item) => !prototypeCartItemIds.has(item.id));
      if (cartSnapshot.length !== parsed.length) {
        window.localStorage.setItem(STORAGE_KEY, JSON.stringify(cartSnapshot));
      }
    } else {
      console.error("Saved cart data is invalid; ignoring the saved cart.");
    }
  } catch (error) {
    console.error("Could not restore the saved cart.", error);
  }
}

function getCartSnapshot() {
  loadCart();
  return cartSnapshot;
}

function getServerCartSnapshot() {
  return initialItems;
}

function subscribeToCart(listener: () => void) {
  cartSubscribers.add(listener);
  return () => cartSubscribers.delete(listener);
}

function updateCart(updater: (items: CartLine[]) => CartLine[]) {
  cartSnapshot = updater(cartSnapshot);
  try {
    window.localStorage.setItem(STORAGE_KEY, JSON.stringify(cartSnapshot));
  } catch (error) {
    console.error("Could not save the cart in this browser.", error);
  }
  cartSubscribers.forEach((listener) => listener());
}

const CartContext = createContext<CartContextValue | null>(null);

export function CartProvider({ children }: { children: ReactNode }) {
  const items = useSyncExternalStore(
    subscribeToCart,
    getCartSnapshot,
    getServerCartSnapshot,
  );

  const value = useMemo<CartContextValue>(() => {
    const addItem: CartContextValue["addItem"] = (line) => {
      updateCart((current) => {
        const existing = current.find(
          (item) => item.productId === line.productId && item.variant === line.variant,
        );
        if (existing) {
          return current.map((item) =>
            item.id === existing.id
              ? { ...item, quantity: item.quantity + 1 }
              : item,
          );
        }
        return [...current, { ...line, id: `${line.productId}-${Date.now()}`, quantity: 1 }];
      });
    };

    return {
      items,
      itemCount: items.reduce((count, item) => count + item.quantity, 0),
      subtotal: items.reduce((sum, item) => sum + item.price * item.quantity, 0),
      addItem,
      setQuantity: (id, quantity) =>
        updateCart((current) =>
          current.map((item) =>
            item.id === id
              ? { ...item, quantity: Math.max(1, Math.floor(quantity)) }
              : item,
          ),
        ),
      removeItem: (id) =>
        updateCart((current) => current.filter((item) => item.id !== id)),
      removeItems: (ids) =>
        updateCart((current) => current.filter((item) => !ids.includes(item.id))),
      clearCart: () => updateCart(() => []),
    };
  }, [items]);

  return <CartContext.Provider value={value}>{children}</CartContext.Provider>;
}

export function useCart() {
  const context = useContext(CartContext);
  if (!context) {
    throw new Error("useCart must be used within a CartProvider");
  }
  return context;
}
