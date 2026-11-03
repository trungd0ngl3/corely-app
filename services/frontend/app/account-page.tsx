"use client";

import Image from "next/image";
import Link from "next/link";
import { useState, type FormEvent } from "react";
import {
  ArrowRight,
  BadgeCheck,
  ChevronRight,
  CircleHelp,
  Eye,
  EyeOff,
  Gauge,
  Gift,
  Headphones,
  LockKeyhole,
  Mail,
  ShieldCheck,
  UserRound,
  Zap,
} from "lucide-react";

type AccountMode = "login" | "register";

const memberBenefits = [
  {
    icon: Gift,
    title: "Tích lũy Corely Points",
    detail:
      "Hoàn 2%–5% giá trị quy đổi trực tiếp cho từng đơn linh kiện, VGA và cấu hình custom.",
  },
  {
    icon: Headphones,
    title: "RMA 1 đổi 1 tận nhà 24/7",
    detail:
      "Xử lý linh kiện lỗi siêu tốc trong 2 giờ tại nội thành. Hỗ trợ kỹ thuật từ xa không giới hạn.",
  },
  {
    icon: Zap,
    title: "Ưu tiên đặt trước slot hiếm",
    detail:
      "Đặt trước Founders Edition, RTX 50/40 Series và CPU X3D flagship số lượng giới hạn.",
  },
  {
    icon: Gauge,
    title: "Lưu PC Builder không giới hạn",
    detail:
      "Đồng bộ cấu hình, điện năng và nhiệt độ dự toán trên tất cả thiết bị của bạn.",
  },
];

export default function AccountPage({
  initialMode,
}: {
  initialMode: AccountMode;
}) {
  const [mode, setMode] = useState(initialMode);
  const [showLoginPassword, setShowLoginPassword] = useState(false);
  const [showRegisterPassword, setShowRegisterPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);
  const [notice, setNotice] = useState("");

  function submitLogin(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setNotice(
      "Đăng nhập chưa khả dụng — Corely ID chưa được kết nối với dịch vụ xác thực.",
    );
  }

  function submitRegistration(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    const formData = new FormData(event.currentTarget);
    if (formData.get("password") !== formData.get("confirmPassword")) {
      setNotice("Mật khẩu xác nhận chưa khớp. Vui lòng kiểm tra lại.");
      return;
    }
    setNotice(
      "Đăng ký chưa khả dụng — Corely ID chưa được kết nối với dịch vụ xác thực.",
    );
  }

  return (
    <div className="store-wrap account-page">
      <div className="breadcrumbs">
        <Link href="/">Trang chủ</Link>
        <span>/</span>
        <span>Corely ID</span>
      </div>

      <div className="account-layout">
        <aside className="account-benefits">
          <div className="account-benefit-content">
            <div className="account-kicker">
              <span className="account-kicker-dot" />
              CORELY PILOT PROGRAM / EST. 2025
            </div>
            <h1>
              Đặc quyền
              <br />
              hội viên
              <br />
              <span>Corely Pilot.</span>
            </h1>
            <p className="account-lead">
              Mở khóa toàn bộ đặc quyền phần cứng đỉnh cao. Gia nhập cộng đồng
              ép xung và nhận hỗ trợ kỹ thuật chuyên biệt 1-1.
            </p>

            <div className="member-benefits">
              {memberBenefits.map(({ icon: Icon, title, detail }, index) => (
                <div className="member-benefit" key={title}>
                  <span className="member-benefit-icon">
                    <Icon aria-hidden="true" size={18} />
                  </span>
                  <div>
                    <span className="member-benefit-index">
                      0{index + 1} / PILOT PRIVILEGE
                    </span>
                    <h2>{title}</h2>
                    <p>{detail}</p>
                  </div>
                </div>
              ))}
            </div>

            <div className="pilot-membership">
              <div className="pilot-seal">
                <BadgeCheck aria-hidden="true" size={25} />
              </div>
              <div>
                <span>CORELY PILOT MEMBERSHIP</span>
                <b>Được thiết kế cho người đam mê phần cứng.</b>
              </div>
              <ShieldCheck aria-hidden="true" className="pilot-shield" size={22} />
            </div>
          </div>
          <Image
            alt=""
            className="account-hardware-image"
            height={700}
            priority
            src="https://lh3.googleusercontent.com/aida-public/AB6AXuDKQt6uwEJVvVmdFHV-QenBCiMtvguURU1eJeUlzemC6qYshN_DUXO6pPOc-Lydfs8xcwv-qvXR7DjTt32TSu3NP-JMtjZwnV7jyMkEZrqimOQ1nVQ6zx0_hYbZ2YuPHnwy4rPbW41e"
            unoptimized
            width={900}
          />
          <div className="account-visual-label">
            <span>CORELY LAB / CERTIFIED GEAR</span>
            <span>01 — 04</span>
          </div>
        </aside>

        <section aria-labelledby="account-form-heading" className="account-form-panel">
          <div className="account-form-topline">
            <span><LockKeyhole size={14} /> CORELY ID / SECURE ACCESS</span>
            <span>ENCRYPTED SESSION</span>
          </div>

          <div className="account-form-intro">
            <span className="account-form-icon">
              {mode === "login" ? (
                <UserRound aria-hidden="true" size={21} />
              ) : (
                <BadgeCheck aria-hidden="true" size={21} />
              )}
            </span>
            <span className="eyebrow"><span className="eyebrow-line" /> PILOT ACCESS PORTAL</span>
            <h2 id="account-form-heading">
              {mode === "login" ? "Chào mừng trở lại." : "Gia nhập Corely Pilot."}
            </h2>
            <p>
              {mode === "login"
                ? "Đăng nhập để tiếp tục hành trình hiệu năng của bạn."
                : "Tạo Corely ID và mở khóa đặc quyền dành riêng cho bạn."}
            </p>
          </div>

          <div aria-label="Chọn hình thức tài khoản" className="account-tabs" role="tablist">
            <Link
              aria-selected={mode === "login"}
              className={mode === "login" ? "account-tab active" : "account-tab"}
              href="/login"
              onClick={() => {
                setMode("login");
                setNotice("");
              }}
              role="tab"
            >
              Đăng nhập
            </Link>
            <Link
              aria-selected={mode === "register"}
              className={mode === "register" ? "account-tab active" : "account-tab"}
              href="/register"
              onClick={() => {
                setMode("register");
                setNotice("");
              }}
              role="tab"
            >
              Tạo tài khoản mới
            </Link>
          </div>

          {mode === "login" ? (
            <form className="account-form" onSubmit={submitLogin}>
              <label className="account-field">
                <span>Email / Tên đăng nhập</span>
                <span className="account-input-wrap">
                  <Mail aria-hidden="true" size={16} />
                  <input
                    autoComplete="username"
                    name="username"
                    placeholder="gamepilot@corely.vn"
                    required
                    type="text"
                  />
                </span>
              </label>
              <label className="account-field">
                <span>Mật khẩu bảo mật</span>
                <span className="account-input-wrap">
                  <LockKeyhole aria-hidden="true" size={16} />
                  <input
                    autoComplete="current-password"
                    minLength={8}
                    name="password"
                    placeholder="Nhập mật khẩu"
                    required
                    type={showLoginPassword ? "text" : "password"}
                  />
                  <button
                    aria-label={showLoginPassword ? "Ẩn mật khẩu" : "Hiện mật khẩu"}
                    className="password-visibility"
                    onClick={() => setShowLoginPassword((visible) => !visible)}
                    type="button"
                  >
                    {showLoginPassword ? <EyeOff size={16} /> : <Eye size={16} />}
                  </button>
                </span>
              </label>
              <div className="account-form-options">
                <label className="remember-session">
                  <input name="remember" type="checkbox" />
                  <span>Ghi nhớ phiên (30 ngày)</span>
                </label>
                <button
                  className="account-text-button"
                  onClick={() =>
                    setNotice(
                      "Khôi phục mật khẩu sẽ khả dụng khi Corely ID được kết nối.",
                    )
                  }
                  type="button"
                >
                  Quên mật khẩu?
                </button>
              </div>
              <button className="button-primary account-submit" type="submit">
                Đăng nhập vào Corely ID <ArrowRight size={17} />
              </button>
            </form>
          ) : (
            <form className="account-form register-form" onSubmit={submitRegistration}>
              <label className="account-field">
                <span>Họ và tên Pilot</span>
                <span className="account-input-wrap">
                  <UserRound aria-hidden="true" size={16} />
                  <input
                    autoComplete="name"
                    name="fullName"
                    placeholder="Nguyễn Văn A"
                    required
                  />
                </span>
              </label>
              <label className="account-field">
                <span>Số điện thoại</span>
                <span className="account-input-wrap">
                  <CircleHelp aria-hidden="true" size={16} />
                  <input
                    autoComplete="tel"
                    inputMode="tel"
                    name="phone"
                    placeholder="0901 234 567"
                    required
                    type="tel"
                  />
                </span>
              </label>
              <label className="account-field">
                <span>Địa chỉ email kích hoạt</span>
                <span className="account-input-wrap">
                  <Mail aria-hidden="true" size={16} />
                  <input
                    autoComplete="email"
                    name="email"
                    placeholder="pilot.name@domain.com"
                    required
                    type="email"
                  />
                </span>
              </label>
              <div className="account-password-grid">
                <label className="account-field">
                  <span>Mật khẩu (tối thiểu 8 ký tự)</span>
                  <span className="account-input-wrap">
                    <LockKeyhole aria-hidden="true" size={16} />
                    <input
                      autoComplete="new-password"
                      minLength={8}
                      name="password"
                      placeholder="Tạo mật khẩu"
                      required
                      type={showRegisterPassword ? "text" : "password"}
                    />
                    <button
                      aria-label={showRegisterPassword ? "Ẩn mật khẩu" : "Hiện mật khẩu"}
                      className="password-visibility"
                      onClick={() => setShowRegisterPassword((visible) => !visible)}
                      type="button"
                    >
                      {showRegisterPassword ? <EyeOff size={15} /> : <Eye size={15} />}
                    </button>
                  </span>
                </label>
                <label className="account-field">
                  <span>Nhập lại mật khẩu</span>
                  <span className="account-input-wrap">
                    <LockKeyhole aria-hidden="true" size={16} />
                    <input
                      autoComplete="new-password"
                      minLength={8}
                      name="confirmPassword"
                      placeholder="Xác nhận mật khẩu"
                      required
                      type={showConfirmPassword ? "text" : "password"}
                    />
                    <button
                      aria-label={showConfirmPassword ? "Ẩn mật khẩu" : "Hiện mật khẩu"}
                      className="password-visibility"
                      onClick={() => setShowConfirmPassword((visible) => !visible)}
                      type="button"
                    >
                      {showConfirmPassword ? <EyeOff size={15} /> : <Eye size={15} />}
                    </button>
                  </span>
                </label>
              </div>
              <label className="remember-session terms-agreement">
                <input name="terms" required type="checkbox" />
                <span>
                  Tôi đồng ý với Điều khoản dịch vụ, Chính sách bảo mật và tiếp
                  nhận thông tin bảo hành từ Corely Systems.
                </span>
              </label>
              <button className="button-primary account-submit" type="submit">
                Tạo tài khoản mới ngay <ArrowRight size={17} />
              </button>
            </form>
          )}

          {notice && <p className="account-notice" role="status">{notice}</p>}

          <div className="account-divider"><span /> HOẶC TIẾP TỤC VỚI <span /></div>
          <div className="account-socials">
            <button
              onClick={() =>
                setNotice("Đăng nhập Google chưa được kết nối với Corely ID.")
              }
              type="button"
            >
              <span className="google-mark">G</span> Google
            </button>
            <button
              onClick={() =>
                setNotice("Đăng nhập Facebook chưa được kết nối với Corely ID.")
              }
              type="button"
            >
              <span className="facebook-mark">f</span> Facebook
            </button>
          </div>

          <p className="account-security-note">
            <ShieldCheck aria-hidden="true" size={15} />
            Thông tin của bạn được mã hóa và bảo vệ an toàn.
          </p>
        </section>
      </div>

      <div className="account-help">
        <span><BadgeCheck size={17} /> THIẾT BỊ CHÍNH HÃNG · DỊCH VỤ CHUẨN CORELY</span>
        <Link href="/products">Khám phá Corely <ChevronRight size={15} /></Link>
      </div>
    </div>
  );
}
