export type StoreProduct = {
  id: string;
  name: string;
  category: string;
  price: number;
  oldPrice?: number;
  rating?: string;
  reviews?: number;
  badge?: string;
  description: string;
  specs: string[];
  image: string;
  href: string;
};

const apexImage =
  "https://lh3.googleusercontent.com/aida-public/AB6AXuA4xt_AaPUOQiBS62ZLG-gtIHeVxb8fDRwyGgtcRRZB3n4Sxp-eONiPVN9GtjtQkSLS3THtHNprRO_aJpNovfRkaKWkTWRchCES9xuMS7njq2ek0obLldbuwVHZ6QRiTIgTEAk9gA6qt8Yrw8dloD8NF5cBgRUurk5vIKFQA9KzaqrUmiqrJXbiBADe333OudDejZKuoBD3hC02dJFhfdBO-rr4_JT1Vpx3G7Tdhd70";
const gpuImage =
  "https://images.unsplash.com/photo-1591488320449-011701bb6704?auto=format&fit=crop&w=900&q=85";
const cpuImage =
  "https://images.unsplash.com/photo-1591799264318-7e6ef8ddb7ea?auto=format&fit=crop&w=900&q=85";
const memoryImage =
  "https://images.unsplash.com/photo-1541029071515-84cc54f84dc5?auto=format&fit=crop&w=900&q=85";
const monitorImage =
  "https://images.unsplash.com/photo-1527443224154-c4a3942d3acf?auto=format&fit=crop&w=900&q=85";
const keyboardImage =
  "https://images.unsplash.com/photo-1595225476474-87563907a212?auto=format&fit=crop&w=900&q=85";
const boardImage =
  "https://images.unsplash.com/photo-1562976540-1502c2145186?auto=format&fit=crop&w=900&q=85";

export const products: StoreProduct[] = [
  {
    id: "phantom-air",
    name: "Dàn PC Corely Phantom Air i5",
    category: "PC Prebuilt",
    price: 24990000,
    oldPrice: 27990000,
    rating: "4.9",
    reviews: 128,
    badge: "BÁN CHẠY",
    description: "Chiến game 1080p mượt mà, sẵn sàng nâng cấp.",
    specs: ["Core i5-14400F", "RTX 4060 8GB", "32GB DDR5", "SSD 1TB"],
    image: apexImage,
    href: "/products",
  },
  {
    id: "frost-valkyrie",
    name: "Dàn PC Corely Frost Valkyrie White",
    category: "PC Prebuilt",
    price: 38990000,
    oldPrice: 42990000,
    rating: "4.9",
    reviews: 86,
    badge: "TẶNG GEAR",
    description: "Hiệu năng 2K trong bộ khung trắng tinh tế.",
    specs: ["Core i7-14700F", "RTX 4070 SUPER", "32GB DDR5", "SSD 1TB"],
    image: apexImage,
    href: "/products",
  },
  {
    id: "apex-cyberpunk",
    name: "PC Gaming Corely Apex Cyberpunk Edition",
    category: "PC Prebuilt",
    price: 49080000,
    oldPrice: 52990000,
    rating: "5.0",
    reviews: 64,
    badge: "HIỆU NĂNG 2K",
    description:
      "Cỗ máy gaming hiệu năng cao, tối ưu cho game AAA và sáng tạo nội dung.",
    specs: ["Core i7-14700K", "RTX 4070 Ti SUPER 16GB", "32GB DDR5 6000MHz", "SSD NVMe 1TB"],
    image: apexImage,
    href: "/products/apex-cyberpunk",
  },
  {
    id: "overlord-extreme",
    name: "Corely Overlord Extreme",
    category: "PC Prebuilt",
    price: 89990000,
    rating: "4.8",
    reviews: 32,
    badge: "EXTREME",
    description: "Workstation mạnh mẽ cho render, AI và gaming 4K.",
    specs: ["Core i9", "GeForce RTX 4090", "64GB DDR5", "SSD NVMe 2TB"],
    image: apexImage,
    href: "/products",
  },
  {
    id: "rtx-4070-ti-super",
    name: "ASUS ROG Strix GeForce RTX 4070 Ti SUPER 16GB",
    category: "Card đồ họa",
    price: 25990000,
    rating: "4.9",
    reviews: 41,
    description: "Card đồ họa hiệu năng cao cho gaming 2K và sáng tạo.",
    specs: ["16GB GDDR6X", "PCIe 4.0", "Ray Tracing", "DLSS 3"],
    image: gpuImage,
    href: "/products",
  },
  {
    id: "ryzen-7800x3d",
    name: "AMD Ryzen 7 7800X3D",
    category: "Bộ vi xử lý",
    price: 9990000,
    rating: "4.9",
    reviews: 73,
    description: "CPU gaming với công nghệ AMD 3D V-Cache.",
    specs: ["8 nhân 16 luồng", "Up to 5.0GHz", "AM5", "TDP 120W"],
    image: cpuImage,
    href: "/products",
  },
  {
    id: "corsair-ddr5",
    name: "Corsair Vengeance RGB 32GB DDR5 6000MHz",
    category: "RAM & SSD",
    price: 3290000,
    rating: "4.8",
    reviews: 55,
    description: "Bộ nhớ DDR5 hiệu năng cao, tương thích Intel XMP.",
    specs: ["32GB (2x16GB)", "DDR5 6000MHz", "RGB", "Bảo hành 36 tháng"],
    image: memoryImage,
    href: "/products",
  },
  {
    id: "samsung-990-pro",
    name: "SSD Samsung 990 Pro 2TB NVMe",
    category: "RAM & SSD",
    price: 4990000,
    rating: "4.9",
    reviews: 38,
    description: "Lưu trữ siêu tốc PCIe Gen 4.0 cho mọi tác vụ.",
    specs: ["2TB", "PCIe Gen 4.0 x4", "Đọc 7450MB/s", "M.2 NVMe"],
    image: memoryImage,
    href: "/products",
  },
  {
    id: "rog-swift-oled",
    name: 'Màn hình ROG Swift OLED 27" 240Hz',
    category: "Màn hình & Gear",
    price: 21990000,
    rating: "4.9",
    reviews: 27,
    description: "Màn hình OLED QHD tốc độ cao cho game thủ chuyên nghiệp.",
    specs: ["27 inch OLED", "QHD 240Hz", "0.03ms", "G-SYNC"],
    image: monitorImage,
    href: "/products",
  },
  {
    id: "strike-75",
    name: "Bàn phím cơ Custom Corely Strike-75",
    category: "Màn hình & Gear",
    price: 3290000,
    rating: "4.8",
    reviews: 19,
    description: "Bàn phím custom layout 75%, gasket mount và switch linear.",
    specs: ["Layout 75%", "Gasket mount", "Linear switch", "Hot-swap"],
    image: keyboardImage,
    href: "/products",
  },
  {
    id: "msi-tomahawk",
    name: "Bo mạch chủ MSI MAG Z790 TOMAHAWK MAX WIFI",
    category: "Bo mạch chủ",
    price: 8990000,
    rating: "4.8",
    reviews: 22,
    description: "Bo mạch chủ Intel Z790 hỗ trợ DDR5 và Wi-Fi 7.",
    specs: ["Intel Z790", "DDR5", "Wi-Fi 7", "PCIe 5.0"],
    image: boardImage,
    href: "/products",
  },
  {
    id: "corsair-rm850e",
    name: "Nguồn Corsair RM850e 850W 80 Plus Gold",
    category: "Nguồn & vỏ máy",
    price: 3490000,
    rating: "4.8",
    reviews: 16,
    description: "Bộ nguồn modular ATX 3.0 sẵn sàng cho PCIe 5.0.",
    specs: ["850W", "80 Plus Gold", "ATX 3.0", "Fully Modular"],
    image: gpuImage,
    href: "/products",
  },
];

export const featuredProduct = products[2];

export { formatVnd } from "@/lib/format";
