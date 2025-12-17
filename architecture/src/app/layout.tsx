import type { Metadata } from "next";
import { Inter } from "next/font/google";
import "./globals.css";

const inter = Inter({
  subsets: ["latin"],
  variable: "--font-inter",
});

export const metadata: Metadata = {
  title: "Amazon EKS on AWS | Production Kubernetes Infrastructure",
  description: "Interactive architecture diagram for Amazon EKS deployment on AWS. Built with CDK, Karpenter, Grafana Cloud observability, and enterprise-grade addons.",
  keywords: ["Amazon EKS", "AWS", "Kubernetes", "Architecture", "CDK", "Karpenter", "Grafana", "Observability"],
  authors: [{ name: "Platform Engineering" }],
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en" className="dark">
      <body className={`${inter.variable} font-sans antialiased bg-slate-950 text-white`}>
        {children}
      </body>
    </html>
  );
}
