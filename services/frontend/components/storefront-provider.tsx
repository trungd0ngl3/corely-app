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
const initialItems: CartLine[] = [
  {
    id: "apex-cyberpunk-standard",
    productId: "apex-cyberpunk",
    title: "Dàn PC Gaming Corely Apex Cyberpunk Edition",
    variant: "Core i7-14700K · 32GB DDR5 · RTX 4070 Ti SUPER",
    price: 49080000,
    quantity: 1,
    image:
      "https://lh3.googleusercontent.com/aida-public/AB6AXuA4xt_AaPUOQiBS62ZLG-gtIHeVxb8fDRwyGgtcRRZB3n4Sxp-eONiPVN9GtjtQkSLS3THtHNprRO_aJpNovfRkaKWkTWRchCES9xuMS7njq2ek0obLldbuwVHZ6QRiTIgTEAk9gA6qt8Yrw8dloD8NF5cBgRUurk5vIKFQA9KzaqrUmiqrJXbiBADe333OudDejZKuoBD3hC02dJFhfdBO-rr4_JT1Vpx3G7Tdhd70",
  },
  {
    id: "rog-monitor",
    productId: "rog-monitor",
    title: 'Màn hình ROG Swift OLED 27" 240Hz',
    variant: "PG27AQDM · OLED · QHD",
    price: 21990000,
    quantity: 1,
    image:
      "https://images.unsplash.com/photo-1527443224154-c4a3942d3acf?auto=format&fit=crop&w=900&q=85",
  },
  {
    id: "strike-keyboard",
    productId: "strike-keyboard",
    title: "Bàn phím cơ Custom Corely Strike-75",
    variant: "Gasket Mount · Switch Linear",
    price: 3290000,
    quantity: 1,
    image:
      "https://images.unsplash.com/photo-1595225476474-87563907a212?auto=format&fit=crop&w=900&q=85",
  },
];

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
      cartSnapshot = parsed;
    } else {
      console.error("Saved cart data is invalid; the demo cart was restored.");
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
