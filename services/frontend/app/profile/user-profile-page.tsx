"use client";

import Image from "next/image";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { useEffect, useState, type FormEvent } from "react";
import {
  ArrowRight,
  BadgeCheck,
  CalendarDays,
  LogOut,
  Mail,
  Phone,
  Save,
  ShieldCheck,
  UserRound,
} from "lucide-react";
import { getAuthSession, logoutFromCurrentSession } from "@/services/auth-service";
import {
  fetchCurrentUser,
  updateCurrentUser,
  UserApiError,
  type UserProfile,
} from "@/services/user-service";

type ProfileForm = {
  fullName: string;
  phone: string;
  dateOfBirth: string;
};

function toProfileForm(profile: UserProfile): ProfileForm {
  return {
    fullName: profile.fullName,
    phone: profile.phone ?? "",
    dateOfBirth: profile.dateOfBirth ?? "",
  };
}

function getInitials(name: string) {
  return name
    .trim()
    .split(/\s+/)
    .slice(-2)
    .map((part) => part[0]?.toLocaleUpperCase("vi"))
    .join("");
}

export function UserProfilePage() {
  const router = useRouter();
  const [profile, setProfile] = useState<UserProfile>();
  const [form, setForm] = useState<ProfileForm>({
    fullName: "",
    phone: "",
    dateOfBirth: "",
  });
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [loggingOut, setLoggingOut] = useState(false);
  const [error, setError] = useState("");
  const [notice, setNotice] = useState("");
  const [signedIn, setSignedIn] = useState(false);

  useEffect(() => {
    const controller = new AbortController();

    async function loadProfile() {
      const session = await Promise.resolve().then(getAuthSession);
      if (controller.signal.aborted) return;
      if (!session) {
        setLoading(false);
        return;
      }

      setSignedIn(true);
      try {
        const user = await fetchCurrentUser(session.token, controller.signal);
        setProfile(user);
        setForm(toProfileForm(user));
      } catch (requestError) {
        if (controller.signal.aborted) return;
        if (requestError instanceof UserApiError && requestError.status === 401) {
          setSignedIn(false);
          setError("Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại.");
        } else {
          setError(
            requestError instanceof Error
              ? requestError.message
              : "Không thể tải hồ sơ tài khoản.",
          );
        }
      } finally {
        if (!controller.signal.aborted) setLoading(false);
      }
    }

    void loadProfile().catch((requestError: unknown) => {
      if (controller.signal.aborted) return;
      setError(
        requestError instanceof Error
          ? requestError.message
          : "Không thể tải hồ sơ tài khoản.",
      );
      setLoading(false);
    });

    return () => controller.abort();
  }, []);

  async function saveProfile(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    const session = getAuthSession();
    if (!session || !profile) {
      setSignedIn(false);
      setError("Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại.");
      return;
    }

    const phone = form.phone.replace(/\s/g, "");
    if (phone && !/^[0-9]{10,11}$/.test(phone)) {
      setError("Số điện thoại cần có 10 hoặc 11 chữ số.");
      return;
    }

    setSaving(true);
    setError("");
    setNotice("");
    try {
      const updatedProfile = await updateCurrentUser(session.token, {
        fullName: form.fullName.trim(),
        phone: phone || undefined,
        dateOfBirth: form.dateOfBirth || undefined,
        avatarUrl: profile.avatarUrl,
      });
      setProfile(updatedProfile);
      setForm(toProfileForm(updatedProfile));
      setNotice("Thông tin tài khoản đã được cập nhật.");
    } catch (requestError) {
      setError(
        requestError instanceof Error
          ? requestError.message
          : "Không thể cập nhật hồ sơ.",
      );
    } finally {
      setSaving(false);
    }
  }

  async function logout() {
    setLoggingOut(true);
    setError("");
    try {
      await logoutFromCurrentSession();
      router.replace("/login");
    } catch (requestError) {
      setError(
        requestError instanceof Error
          ? requestError.message
          : "Không thể đăng xuất. Vui lòng thử lại.",
      );
      setLoggingOut(false);
    }
  }

  return (
    <div className="store-wrap user-profile-page">
      <div className="breadcrumbs">
        <Link href="/">Trang chủ</Link>
        <span>/</span>
        <span>Tài khoản</span>
      </div>

      <div className="user-profile-heading">
        <div>
          <div className="eyebrow">
            <span className="eyebrow-line" /> CORELY ID / ACCOUNT CENTER
          </div>
          <h1 className="section-title">
            Hồ sơ <span>tài khoản.</span>
          </h1>
          <p>Quản lý thông tin cá nhân và trạng thái tài khoản của bạn.</p>
        </div>
        {signedIn && (
          <button
            className="button-outline"
            disabled={loggingOut}
            onClick={logout}
            type="button"
          >
            <LogOut aria-hidden="true" size={16} />
            {loggingOut ? "Đang đăng xuất..." : "Đăng xuất"}
          </button>
        )}
      </div>

      {loading ? (
        <div aria-busy="true" className="user-profile-state">
          Đang tải hồ sơ tài khoản...
        </div>
      ) : !signedIn ? (
        <section className="user-profile-state">
          <ShieldCheck aria-hidden="true" size={28} />
          <h2>Cần đăng nhập để xem hồ sơ</h2>
          <p>{error || "Đăng nhập Corely ID để quản lý thông tin của bạn."}</p>
          <Link className="button-primary" href="/login">
            Đăng nhập <ArrowRight aria-hidden="true" size={16} />
          </Link>
        </section>
      ) : error && !profile ? (
        <section className="user-profile-state" role="alert">
          <p>{error}</p>
          <button
            className="button-outline"
            onClick={() => window.location.reload()}
            type="button"
          >
            Tải lại trang
          </button>
        </section>
      ) : profile ? (
        <>
          <section className="user-profile-hero">
            <div className="user-profile-identity">
              <div className="user-profile-avatar">
                {profile.avatarUrl ? (
                  <Image
                    alt={`Ảnh đại diện của ${profile.fullName}`}
                    height={104}
                    src={profile.avatarUrl}
                    unoptimized
                    width={104}
                  />
                ) : (
                  <span>{getInitials(profile.fullName) || <UserRound size={36} />}</span>
                )}
              </div>
              <div>
                <span className="user-profile-kicker">CORELY MEMBER</span>
                <h2>{profile.fullName}</h2>
                <p>{profile.email}</p>
              </div>
            </div>
            <div className="user-profile-status">
              <span className={profile.isActive ? "is-active" : "is-inactive"} />
              {profile.isActive ? "Tài khoản đang hoạt động" : "Tài khoản chưa hoạt động"}
            </div>
            <div className="user-profile-roles">
              <BadgeCheck aria-hidden="true" size={16} />
              {profile.roles.length
                ? profile.roles.join(" · ")
                : "Chưa có vai trò"}
            </div>
          </section>

          <div className="user-profile-layout">
            <aside className="user-profile-aside">
              <div className="user-profile-nav-title">TÀI KHOẢN CỦA TÔI</div>
              <a aria-current="page" className="active" href="#personal-info">
                <UserRound aria-hidden="true" size={17} />
                Thông tin cá nhân
              </a>
              <Link href="/cart">
                <ArrowRight aria-hidden="true" size={17} />
                Giỏ hàng
              </Link>
              <Link href="/products">
                <BadgeCheck aria-hidden="true" size={17} />
                Tiếp tục mua sắm
              </Link>
            </aside>

            <section className="user-profile-panel" id="personal-info">
              <div className="user-profile-panel-heading">
                <div>
                  <span className="eyebrow">
                    <span className="eyebrow-line" /> ACCOUNT DETAILS
                  </span>
                  <h2>Thông tin cá nhân</h2>
                  <p>Cập nhật thông tin liên hệ gắn với Corely ID của bạn.</p>
                </div>
                <ShieldCheck aria-hidden="true" size={22} />
              </div>

              <form className="user-profile-form" onSubmit={saveProfile}>
                <label>
                  <span>Họ và tên</span>
                  <span className="user-profile-input">
                    <UserRound aria-hidden="true" size={17} />
                    <input
                      autoComplete="name"
                      maxLength={120}
                      name="fullName"
                      onChange={(event) =>
                        setForm({ ...form, fullName: event.target.value })
                      }
                      required
                      value={form.fullName}
                    />
                  </span>
                </label>
                <label>
                  <span>Email</span>
                  <span className="user-profile-input is-readonly">
                    <Mail aria-hidden="true" size={17} />
                    <input autoComplete="email" readOnly value={profile.email} />
                  </span>
                  <small>Email không thể thay đổi tại đây.</small>
                </label>
                <label>
                  <span>Số điện thoại</span>
                  <span className="user-profile-input">
                    <Phone aria-hidden="true" size={17} />
                    <input
                      autoComplete="tel"
                      inputMode="numeric"
                      maxLength={11}
                      name="phone"
                      onChange={(event) =>
                        setForm({ ...form, phone: event.target.value })
                      }
                      pattern="[0-9]{10,11}"
                      placeholder="10–11 chữ số"
                      title="Nhập số điện thoại gồm 10 hoặc 11 chữ số"
                      value={form.phone}
                    />
                  </span>
                </label>
                <label>
                  <span>Ngày sinh</span>
                  <span className="user-profile-input">
                    <CalendarDays aria-hidden="true" size={17} />
                    <input
                      autoComplete="bday"
                      max={new Date().toISOString().slice(0, 10)}
                      name="dateOfBirth"
                      onChange={(event) =>
                        setForm({ ...form, dateOfBirth: event.target.value })
                      }
                      type="date"
                      value={form.dateOfBirth}
                    />
                  </span>
                </label>

                {error && <p className="user-profile-message error" role="alert">{error}</p>}
                {notice && <p className="user-profile-message success" role="status">{notice}</p>}

                <div className="user-profile-form-footer">
                  <button className="button-primary" disabled={saving} type="submit">
                    <Save aria-hidden="true" size={16} />
                    {saving ? "Đang lưu..." : "Lưu thay đổi"}
                  </button>
                </div>
              </form>
            </section>
          </div>
        </>
      ) : null}
    </div>
  );
}
