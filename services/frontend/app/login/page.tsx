import type { Metadata } from "next";
import AccountPage from "../account-page";

export const metadata: Metadata = {
  title: "Đăng nhập Corely ID",
  description: "Đăng nhập Corely Pilot để quản lý cấu hình và quyền lợi thành viên.",
};

export default function LoginPage() {
  return <AccountPage initialMode="login" />;
}
