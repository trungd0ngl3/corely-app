import type { Metadata } from "next";
import { Geist, Geist_Mono } from "next/font/google";
import { SiteFooter } from "@/components/site-footer";
import { SiteHeader } from "@/components/site-header";
import { CartProvider } from "@/components/storefront-provider";
import "./globals.css";

const geistSans = Geist({
  variable: "--font-geist-sans",
  subsets: ["latin"],
});

const geistMono = Geist_Mono({
  variable: "--font-geist-mono",
  subsets: ["latin"],
});

export const metadata: Metadata = {
  title: {
    default: "Corely | PC Gaming & Linh Kiện Chính Hãng",
    template: "%s | Corely",
  },
  description:
    "Khám phá dàn PC gaming, linh kiện máy tính chính hãng và cấu hình được tối ưu bởi chuyên gia Corely.",
};

export default function RootLayout({ children }: LayoutProps<"/">) {
  return (
    <html
      lang="vi"
      className={`${geistSans.variable} ${geistMono.variable} h-full antialiased`}
    >
      <body className="min-h-full flex flex-col">
        <CartProvider>
          <SiteHeader />
          <main className="store-main">{children}</main>
          <SiteFooter />
        </CartProvider>
      </body>
    </html>
  );
}
