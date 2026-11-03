import type { Metadata } from "next";
import AccountPage from "../account-page";

export const metadata: Metadata = {
  title: "Đăng ký Corely ID",
  description: "Tạo tài khoản Corely Pilot và mở khóa đặc quyền hội viên.",
};

export default function RegisterPage() {
  return <AccountPage initialMode="register" />;
}
