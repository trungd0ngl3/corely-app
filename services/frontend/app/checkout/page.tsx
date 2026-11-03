"use client";

import Link from "next/link";
import Image from "next/image";
import { Suspense, useState, type FormEvent } from "react";
import { useSearchParams } from "next/navigation";
import { ArrowLeft, ArrowRight, Check, ShieldCheck, Truck } from "lucide-react";
import { useCart } from "@/components/storefront-provider";
import { formatVnd } from "@/lib/store-data";

export default function CheckoutPage() {
  return (
    <Suspense fallback={<div aria-busy="true" className="store-wrap checkout-page">Đang tải thanh toán...</div>}>
      <CheckoutContent />
    </Suspense>
  );
}

function CheckoutContent() {
  const searchParams = useSearchParams();
  const itemIds = searchParams.get("items");
  const selectedIds = itemIds === null ? null : new Set(itemIds.split(","));
  const cart = useCart();
  const items = selectedIds
    ? cart.items.filter((item) => selectedIds.has(item.id))
    : cart.items;
  const subtotal = items.reduce((sum, item) => sum + item.price * item.quantity, 0);
  const [paymentMethod, setPaymentMethod] = useState("vietqr");
  const [shippingSlot, setShippingSlot] = useState("standard");
  const [invoice, setInvoice] = useState(false);
  const [submitted, setSubmitted] = useState(false);
  const shipping = shippingSlot === "express" ? 35000 : 0;
  const discount = paymentMethod === "cod" ? 0 : 200000;
  const total = Math.max(0, subtotal + shipping - discount);

  function submitOrder(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setSubmitted(true);
  }

  return (
    <div className="store-wrap checkout-page">
      <div className="breadcrumbs"><Link href="/">Trang chủ</Link><span>/</span><Link href="/cart">Giỏ hàng</Link><span>/</span><span>Thanh toán</span></div>
      <div className="checkout-heading"><div className="eyebrow"><span className="eyebrow-line" /> SECURE CHECKOUT / CORELY</div><h1 className="section-title">Hoàn tất <span>đơn hàng.</span></h1><div className="checkout-steps"><span className="complete"><Check size={13} /> Giỏ hàng</span><i /><span className="active">02 / Thanh toán</span><i /><span>03 / Xác nhận</span></div></div>
      {submitted && <div className="checkout-demo-notice" role="status"><Check size={18} /><div><b>Thông tin đã được kiểm tra.</b><span>Đây là giao diện demo; chưa có thanh toán hoặc đơn hàng thật nào được tạo.</span></div></div>}
      {items.length ? (
        <form className="checkout-layout" onSubmit={submitOrder}>
          <div className="checkout-fields">
            <section className="checkout-panel">
              <div className="checkout-panel-heading"><span>01</span><div><h2>Thông tin nhận hàng</h2><p>Thông tin của bạn được bảo mật và chỉ dùng cho đơn hàng này.</p></div></div>
              <div className="form-grid">
                <label>Họ và tên<input autoComplete="name" name="name" placeholder="Nguyễn Văn A" required /></label>
                <label>Số điện thoại<input autoComplete="tel" inputMode="tel" name="phone" placeholder="090 000 0000" required /></label>
                <label className="form-wide">Email<input autoComplete="email" name="email" placeholder="you@example.com" required type="email" /></label>
                <label>Tỉnh / Thành phố<select defaultValue="TP. Hồ Chí Minh" name="city"><option>TP. Hồ Chí Minh</option><option>Hà Nội</option><option>Đà Nẵng</option><option>Cần Thơ</option></select></label>
                <label>Quận / Huyện<input autoComplete="address-level2" name="district" placeholder="Chọn quận / huyện" required /></label>
                <label className="form-wide">Địa chỉ cụ thể<input autoComplete="street-address" name="address" placeholder="Số nhà, tên đường, phường / xã" required /></label>
                <label className="form-wide">Ghi chú đơn hàng <span className="optional">(không bắt buộc)</span><textarea name="note" placeholder="Hướng dẫn giao hàng hoặc yêu cầu lắp đặt..." rows={3} /></label>
              </div>
              <label className="invoice-toggle"><input checked={invoice} onChange={(event) => setInvoice(event.target.checked)} type="checkbox" /> Tôi cần xuất hóa đơn VAT cho đơn hàng này</label>
              {invoice && <label className="invoice-company">Tên công ty / Mã số thuế<input name="taxInfo" placeholder="Nhập tên công ty và mã số thuế" required /></label>}
            </section>
            <section className="checkout-panel">
              <div className="checkout-panel-heading"><span>02</span><div><h2>Thời gian giao hàng</h2><p>Chọn khung giờ phù hợp với lịch trình của bạn.</p></div></div>
              <div className="shipping-options">
                <label className={shippingSlot === "standard" ? "shipping-option selected" : "shipping-option"}><input checked={shippingSlot === "standard"} name="shipping" onChange={() => setShippingSlot("standard")} type="radio" /><span><b>Giao hàng tiêu chuẩn</b><small>1–3 ngày làm việc · Đóng gói chống sốc</small></span><strong>MIỄN PHÍ</strong></label>
                <label className={shippingSlot === "express" ? "shipping-option selected" : "shipping-option"}><input checked={shippingSlot === "express"} name="shipping" onChange={() => setShippingSlot("express")} type="radio" /><span><b>Giao hàng nhanh</b><small>Ưu tiên xử lý · Dự kiến trong ngày tại nội thành</small></span><strong>35.000₫</strong></label>
              </div>
              <div className="shipping-support"><Truck size={16} /> Nhân viên Corely sẽ gọi xác nhận trước khi giao hàng.</div>
            </section>
            <section className="checkout-panel">
              <div className="checkout-panel-heading"><span>03</span><div><h2>Phương thức thanh toán bảo mật</h2><p>Thông tin thanh toán được mã hóa an toàn.</p></div></div>
              <div className="payment-options">
                {[
                  ["vietqr", "Chuyển khoản VietQR", "Quét mã QR nhanh chóng, xác nhận tự động", "VQR"],
                  ["cod", "Thanh toán khi nhận hàng", "Thanh toán trực tiếp cho nhân viên giao hàng", "COD"],
                  ["card", "Thẻ tín dụng / ghi nợ", "Visa, Mastercard, Napas", "CARD"],
                ].map(([value, name, description, mark]) => <label className={paymentMethod === value ? "payment-option selected" : "payment-option"} key={value}><input checked={paymentMethod === value} name="payment" onChange={() => setPaymentMethod(value)} type="radio" /><span className="payment-mark">{mark}</span><span><b>{name}</b><small>{description}</small></span>{paymentMethod === value && <Check className="payment-check" size={16} />}</label>)}
              </div>
              <p className="terms-copy">Bằng việc bấm xác nhận, quý khách đồng ý với <a href="mailto:support@corely.vn">Điều khoản dịch vụ</a> và <a href="mailto:support@corely.vn">Chính sách RMA 36 tháng</a> của Corely.</p>
            </section>
          </div>
          <aside className="order-summary checkout-summary">
            <div className="summary-title"><span>ORDER SUMMARY</span><span>{String(items.length).padStart(2, "0")} ITEMS</span></div>
            <h2>Tóm tắt đơn hàng</h2>
            <div className="checkout-order-items">{items.map((item) => <div className="checkout-order-item" key={item.id}><Image alt="" height={90} src={item.image} unoptimized width={100} /><div><b>{item.title}</b><small>{item.variant}</small><span>Số lượng: {item.quantity}</span></div><strong>{formatVnd(item.price * item.quantity)}</strong></div>)}</div>
            <div className="summary-row"><span>Tạm tính</span><b>{formatVnd(subtotal)}</b></div>
            <div className="summary-row"><span>Giao hàng</span><b>{shipping ? formatVnd(shipping) : <span className="success-text">MIỄN PHÍ</span>}</b></div>
            <div className="checkout-voucher"><span>✦</span><span>Ưu đãi thanh toán online</span><b>{discount ? `-${formatVnd(discount)}` : "Không áp dụng khi COD"}</b></div>
            <div className="summary-total"><span>TỔNG THANH TOÁN</span><b>{formatVnd(total)}</b><small>Đã bao gồm VAT</small></div>
            <button className="button-primary summary-checkout" type="submit">Xác nhận đặt hàng <ArrowRight size={17} /></button>
            <p className="summary-safe"><ShieldCheck size={15} /> GIAO DỊCH BẢO MẬT · HỖ TRỢ 24/7</p>
            <Link className="back-to-cart" href="/cart"><ArrowLeft size={14} /> Quay lại giỏ hàng</Link>
          </aside>
        </form>
      ) : (
        <div className="empty-cart"><h2>Chưa có sản phẩm để thanh toán.</h2><p>Thêm sản phẩm vào giỏ hàng trước khi tiếp tục.</p><Link className="button-primary" href="/products">Khám phá sản phẩm <ArrowRight size={16} /></Link></div>
      )}
    </div>
  );
}
