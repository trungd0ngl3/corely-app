"use client";

import Link from "next/link";
import { useRouter } from "next/navigation";
import { useState, type FormEvent } from "react";
import {
  Cpu,
  Heart,
  Menu,
  Search,
  ShoppingCart,
  X,
} from "lucide-react";
import { useCart } from "./storefront-provider";

export function SiteHeader() {
  const router = useRouter();
  const { itemCount } = useCart();
  const [query, setQuery] = useState("");
  const [mobileOpen, setMobileOpen] = useState(false);

  function search(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    const value = query.trim();
    router.push(value ? `/products?q=${encodeURIComponent(value)}` : "/products");
    setMobileOpen(false);
  }

  return (
    <header className="store-header">
      <div className="store-topline">
        <div className="store-wrap topline-inner">
          <span>GIAO HÀNG TỐC ĐỘ CAO · BẢO HÀNH CHÍNH HÃNG 36 THÁNG</span>
          <span className="topline-status"><i /> HỆ THỐNG ĐANG HOẠT ĐỘNG</span>
        </div>
      </div>
      <div className="store-wrap header-main">
        <button
          aria-label={mobileOpen ? "Đóng menu" : "Mở menu"}
          className="icon-button mobile-menu-button"
          onClick={() => setMobileOpen((open) => !open)}
          type="button"
        >
          {mobileOpen ? <X size={20} /> : <Menu size={20} />}
        </button>
        <Link aria-label="Corely - Trang chủ" className="brand" href="/">
          <span className="brand-mark"><Cpu size={23} strokeWidth={2.2} /></span>
          <span>CORELY<span className="brand-dot">.</span><small>PRECISION HARDWARE</small></span>
        </Link>
        <form className="header-search" onSubmit={search} role="search">
          <Search aria-hidden="true" size={17} />
          <input
            aria-label="Tìm kiếm sản phẩm"
            onChange={(event) => setQuery(event.target.value)}
            placeholder="Tìm PC, linh kiện, cấu hình..."
            value={query}
          />
          <button type="submit">Tìm kiếm</button>
        </form>
        <div className="header-actions">
          <Link aria-label="Yêu thích" className="icon-button" href="/products">
            <Heart size={19} />
          </Link>
          <Link aria-label={`Giỏ hàng, ${itemCount} sản phẩm`} className="cart-button" href="/cart">
            <ShoppingCart size={19} />
            <span>Giỏ hàng</span>
            <b>{itemCount}</b>
          </Link>
        </div>
      </div>
      <nav aria-label="Điều hướng chính" className={`store-wrap main-nav ${mobileOpen ? "is-open" : ""}`}>
        <Link href="/products">PC Gaming & Workstation</Link>
        <Link href="/products?category=Card%20%C4%91%E1%BB%93%20h%E1%BB%8Da">Card đồ họa</Link>
        <Link href="/products?category=B%E1%BB%99%20vi%20x%E1%BB%AD%20l%C3%BD">CPU & Bo mạch chủ</Link>
        <Link href="/products?category=RAM%20%26%20SSD">RAM & SSD</Link>
        <Link href="/products">Màn hình & Gear</Link>
        <Link className="nav-deal" href="/products">Giờ vàng săn deal <span>↗</span></Link>
      </nav>
    </header>
  );
}
