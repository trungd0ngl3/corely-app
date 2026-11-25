import Link from "next/link";

export default function GoogleLoginFailedPage() {
  return (
    <div className="store-wrap">
      <section aria-live="polite" className="mx-auto max-w-lg py-16 text-center">
        <p role="alert">Không thể đăng nhập bằng tài khoản mạng xã hội. Vui lòng thử lại.</p>
        <Link className="button-primary mt-6 inline-flex" href="/login">
          Quay lại đăng nhập
        </Link>
      </section>
    </div>
  );
}
