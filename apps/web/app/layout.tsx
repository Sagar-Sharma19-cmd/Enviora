import type { Metadata } from "next";
import "./globals.css";
import { QueryProvider } from "@/providers/query-provider";

export const metadata: Metadata = {
  title: "Enviora — Developer Secrets Platform",
  description: "Secure, audit-ready secrets management platform for engineering teams across projects and environments.",
  keywords: ["secrets management", "developer tools", "security", "environment variables", "vault"],
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en" className="dark">
      <body className="min-h-screen bg-background font-sans antialiased text-foreground">
        <QueryProvider>{children}</QueryProvider>
      </body>
    </html>
  );
}
