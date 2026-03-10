import type { Metadata } from "next";
import "./globals.css";

export const metadata: Metadata = {
  title: "Essay Grading — Analytical Writing Assessment",
  description:
    "Write and grade essays with AI-powered analytical writing assessment, inspired by the GRE AWA.",
};

export default function RootLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <html lang="en">
      <body className="bg-slate-50 min-h-screen font-sans">{children}</body>
    </html>
  );
}
