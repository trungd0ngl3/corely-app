import type { Metadata } from "next";
import { UserProfilePage } from "./user-profile-page";

export const metadata: Metadata = {
  title: "Hồ sơ tài khoản",
  description: "Xem và cập nhật thông tin tài khoản Corely của bạn.",
};

export default function ProfilePage() {
  return <UserProfilePage />;
}
