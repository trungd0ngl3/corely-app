"use client";

import Link from "next/link";
import Image from "next/image";
import { useState } from "react";
import { ArrowLeft, ArrowRight, Cpu, Heart, Minus, Plus, ShieldCheck, Trash2, Truck } from "lucide-react";
import { useCart } from "@/components/storefront-provider";
import { formatVnd, products } from "@/lib/store-data";
import { ProductCard } from "@/components/product-card";

export default function CartPage() {
  const { items, itemCount, removeItem, removeItems, setQuantity } = useCart();
  const [selectedIds, setSelectedIds] = useState<Set<string> | null>(null);

  const isSelected = (id: string) => selectedIds === null || selectedIds.has(id);
  const selectedItems = items.filter((item) => isSelected(item.id));
  const allSelected = items.length > 0 && selectedItems.length === items.length;
  const selectedTotal = items.reduce(
    (sum, item) => sum + (isSelected(item.id) ? item.price * item.quantity : 0),
    0,
  );
  const setAllSelected = (selected: boolean) =>
    setSelectedIds(selected ? null : new Set());

  return (
    <div className="store-wrap cart-page">
      <div className="breadcrumbs"><Link href="/">Trang chủ</Link><span>/</span><span>Giỏ hàng</span></div>
      <div className="cart-heading"><div><div className="eyebrow"><span className="eyebrow-line" /> YOUR BUILD / REVIEW</div><h1 className="section-title">Giỏ hàng <span>của bạn.</span></h1><p>{itemCount} sản phẩm đang chờ bạn hoàn thiện cấu hình.</p></div><Link className="button-quiet" href="/products"><ArrowLeft size={16} /> Tiếp tục mua sắm</Link></div>
      {items.length ? (
        <div className="cart-layout">
          <section className="cart-items-panel">
            <div className="cart-select-all"><label><input checked={allSelected} onChange={(event) => setAllSelected(event.target.checked)} type="checkbox" /> Chọn tất cả ({items.length} sản phẩm)</label><button disabled={!selectedItems.length} onClick={() => removeItems(selectedItems.map((item) => item.id))} type="button"><Trash2 size={15} /> Xóa mục đã chọn</button></div>
            <div className="cart-lines">{items.map((item) => (
              <article className="cart-line" key={item.id}>
                <input aria-label={`Chọn ${item.title}`} checked={isSelected(item.id)} onChange={(event) => setSelectedIds((current) => {
                  const next = new Set(current ?? items.map((line) => line.id));
                  if (event.target.checked) next.add(item.id);
                  else next.delete(item.id);
                  return next.size === items.length ? null : next;
                })} type="checkbox" />
                <Link className="cart-line-image" href={item.slug ? `/products/${item.slug}` : (item.productId === "apex-cyberpunk" ? "/products/apex-cyberpunk" : "/products")}>{item.image ? <Image alt={item.title} height={240} src={item.image} unoptimized width={260} /> : <Cpu aria-hidden="true" size={38} strokeWidth={0.8} />}</Link>
                <div className="cart-line-info"><span className="product-category">CORELY / {item.productId.toUpperCase()}</span><Link href={item.slug ? `/products/${item.slug}` : (item.productId === "apex-cyberpunk" ? "/products/apex-cyberpunk" : "/products")}>{item.title}</Link><small>{item.variant}</small><div className="cart-line-stock"><i /> CÒN HÀNG · SẴN SÀNG GIAO</div></div>
                <div className="cart-line-controls"><div className="quantity-control"><button aria-label={`Giảm số lượng ${item.title}`} disabled={item.quantity <= 1} onClick={() => setQuantity(item.id, item.quantity - 1)} type="button"><Minus size={13} /></button><span>{item.quantity}</span><button aria-label={`Tăng số lượng ${item.title}`} onClick={() => setQuantity(item.id, item.quantity + 1)} type="button"><Plus size={13} /></button></div><b>{formatVnd(item.price * item.quantity)}</b><div className="cart-line-actions"><button aria-label={`Lưu ${item.title} vào danh sách yêu thích`} type="button"><Heart size={15} /> Lưu để mua sau</button><button aria-label={`Xóa ${item.title}`} onClick={() => removeItem(item.id)} type="button"><Trash2 size={15} /> Xóa</button></div></div>
              </article>
            ))}</div>
            <div className="cart-assurance"><span><ShieldCheck size={17} /> Tất cả sản phẩm đều chính hãng và được bảo hành theo tiêu chuẩn Corely.</span><span><Truck size={17} /> Đóng gói chống sốc, giao hàng an tâm toàn quốc.</span></div>
          </section>
          <aside className="order-summary">
            <div className="summary-title"><span>ORDER SUMMARY</span><span>01 / 01</span></div>
            <h2>Tóm tắt đơn hàng</h2>
            <div className="summary-row"><span>Sản phẩm đã chọn</span><b>{selectedItems.length} / {items.length}</b></div>
            <div className="summary-row"><span>Tạm tính</span><b>{formatVnd(selectedTotal)}</b></div>
            <div className="summary-row"><span>Giao hàng</span><b className="success-text">MIỄN PHÍ</b></div>
            <div className="voucher-panel"><span>✦</span><div><b>Ưu đãi Corely</b><small>Nhận voucher 200.000₫ khi thanh toán online</small></div></div>
            <div className="summary-total"><span>TỔNG THANH TOÁN</span><b>{formatVnd(selectedTotal)}</b><small>Đã bao gồm VAT</small></div>
            <Link aria-disabled={!selectedItems.length} className={`button-primary summary-checkout ${!selectedItems.length ? "disabled" : ""}`} href={selectedItems.length ? `/checkout?items=${selectedItems.map((item) => encodeURIComponent(item.id)).join(",")}` : "#"}>Tiến hành thanh toán <ArrowRight size={17} /></Link>
            <p className="summary-safe"><ShieldCheck size={15} /> THANH TOÁN BẢO MẬT · HỖ TRỢ 24/7</p>
            <div className="summary-payments"><span>VISA</span><span>mastercard</span><span>NAPAS</span><span>VNPAY</span></div>
          </aside>
        </div>
      ) : (
        <div className="empty-cart"><span className="empty-cart-icon">⌑</span><h2>Giỏ hàng đang trống.</h2><p>Khám phá các cấu hình gaming và linh kiện được tuyển chọn bởi Corely.</p><Link className="button-primary" href="/products">Khám phá sản phẩm <ArrowRight size={17} /></Link></div>
      )}
      <section className="cart-recommendations"><div className="section-heading-row"><div><div className="eyebrow"><span className="eyebrow-line" /> COMPLETE YOUR SETUP</div><h2 className="section-title">Linh kiện & phụ kiện<br /><span>thường mua kèm.</span></h2></div><Link className="text-link" href="/products">Xem tất cả <ArrowRight size={16} /></Link></div><div className="product-grid">{[products[4], products[6], products[7]].map((product) => <ProductCard key={product.id} product={product} />)}</div></section>
    </div>
  );
}
