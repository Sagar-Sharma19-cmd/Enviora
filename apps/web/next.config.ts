import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  reactStrictMode: true,
  transpilePackages: ["@enviora/ui", "@enviora/types"],
};

export default nextConfig;
