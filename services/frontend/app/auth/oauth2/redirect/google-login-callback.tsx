"use client";

import { useRouter } from "next/navigation";
import { useEffect, useRef } from "react";
import { completeGoogleLogin } from "@/services/auth-service";

export default function GoogleLoginCallback() {
  const router = useRouter();
  const handled = useRef(false);

  useEffect(() => {
    if (handled.current) return;
    handled.current = true;

    const params = new URLSearchParams(window.location.hash.slice(1));
    window.history.replaceState(null, "", window.location.pathname);

    if (params.has("error")) {
      router.replace("/auth/oauth2/failed");
      return;
    }

    const token = params.get("token");
    const refreshToken = params.get("refreshToken");
    if (!token || !refreshToken) {
      router.replace("/auth/oauth2/failed");
      return;
    }

    try {
      completeGoogleLogin(token, refreshToken);
      router.replace("/");
    } catch {
      router.replace("/auth/oauth2/failed");
    }
  }, [router]);

  return (
    <div className="store-wrap">
      <section aria-live="polite" className="mx-auto max-w-lg py-16 text-center">
        <p role="status">Đang hoàn tất đăng nhập Google...</p>
      </section>
    </div>
  );
}
