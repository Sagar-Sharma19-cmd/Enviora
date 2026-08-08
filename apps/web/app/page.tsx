import Link from "next/link";
import { Shield, Layers, Lock, Terminal, ArrowRight } from "lucide-react";

export default function Home() {
  return (
    <div className="relative flex flex-col min-h-screen overflow-hidden bg-background">
      {/* Glow Orbs */}
      <div className="absolute top-1/4 left-1/2 -translate-x-1/2 -translate-y-1/2 w-[600px] h-[400px] bg-primary/15 rounded-full blur-[140px] pointer-events-none" />
      <div className="absolute top-2/3 right-10 w-[400px] h-[300px] bg-purple-500/10 rounded-full blur-[120px] pointer-events-none" />

      {/* Header Navigation */}
      <header className="sticky top-0 z-50 w-full border-b border-border/50 glass-panel">
        <div className="container mx-auto flex h-16 items-center justify-between px-6">
          <div className="flex items-center gap-2">
            <div className="flex h-9 w-9 items-center justify-center rounded-lg bg-primary text-primary-foreground font-bold shadow-lg shadow-primary/25">
              <Shield className="h-5 w-5" />
            </div>
            <span className="text-xl font-bold tracking-tight bg-gradient-to-r from-white via-slate-200 to-slate-400 bg-clip-text text-transparent">
              Enviora
            </span>
          </div>

          <div className="flex items-center gap-4">
            <Link
              href="/login"
              className="text-sm font-medium text-muted-foreground transition-colors hover:text-foreground"
            >
              Sign In
            </Link>
            <Link
              href="/register"
              className="inline-flex items-center justify-center rounded-lg bg-primary px-4 py-2 text-sm font-medium text-primary-foreground shadow-md transition-all hover:bg-primary/90 hover:shadow-primary/20"
            >
              Get Started
            </Link>
          </div>
        </div>
      </header>

      {/* Main Hero Section */}
      <main className="flex-1 container mx-auto px-6 py-20 flex flex-col items-center justify-center text-center">
        {/* Status Badge */}
        <div className="inline-flex items-center gap-2 rounded-full border border-primary/30 bg-primary/10 px-4 py-1.5 text-xs font-semibold text-primary mb-8 backdrop-blur-md">
          <span className="relative flex h-2 w-2">
            <span className="animate-ping absolute inline-flex h-full w-full rounded-full bg-primary opacity-75"></span>
            <span className="relative inline-flex rounded-full h-2 w-2 bg-primary"></span>
          </span>
          Early Development Bootstrap Phase
        </div>

        {/* Hero Title */}
        <h1 className="text-4xl md:text-6xl font-extrabold tracking-tight max-w-4xl leading-tight">
          Developer Secrets Management for{" "}
          <span className="bg-gradient-to-r from-blue-400 via-indigo-400 to-purple-400 bg-clip-text text-transparent">
            Modern Engineering Teams
          </span>
        </h1>

        {/* Subtitle */}
        <p className="mt-6 text-lg md:text-xl text-muted-foreground max-w-2xl font-normal leading-relaxed">
          Securely store, organize, audit, and sync application environment variables across your entire stack with zero plaintext compromise.
        </p>

        {/* CTA Buttons */}
        <div className="mt-10 flex flex-col sm:flex-row items-center gap-4 w-full sm:w-auto">
          <Link
            href="/dashboard"
            className="w-full sm:w-auto inline-flex items-center justify-center gap-2 rounded-xl bg-primary px-6 py-3.5 text-base font-semibold text-primary-foreground shadow-lg shadow-primary/25 transition-all hover:bg-primary/90 hover:scale-[1.02]"
          >
            Launch Dashboard <ArrowRight className="h-4 w-4" />
          </Link>
          <Link
            href="/register"
            className="w-full sm:w-auto inline-flex items-center justify-center gap-2 rounded-xl border border-border bg-card/60 px-6 py-3.5 text-base font-semibold text-foreground backdrop-blur-sm transition-all hover:bg-card hover:border-muted-foreground/40"
          >
            Create Organization
          </Link>
        </div>

        {/* Feature Cards Grid */}
        <div className="mt-24 grid grid-cols-1 md:grid-cols-3 gap-6 w-full max-w-5xl text-left">
          <div className="glass-panel p-6 rounded-2xl glow-effect">
            <div className="h-10 w-10 rounded-lg bg-blue-500/10 border border-blue-500/20 flex items-center justify-center text-blue-400 mb-4">
              <Layers className="h-5 w-5" />
            </div>
            <h3 className="text-lg font-semibold mb-2">Hierarchical Domain</h3>
            <p className="text-sm text-muted-foreground leading-relaxed">
              Organize variables seamlessly from Organization → Projects → Environments → Secrets with granular RBAC scoping.
            </p>
          </div>

          <div className="glass-panel p-6 rounded-2xl glow-effect">
            <div className="h-10 w-10 rounded-lg bg-indigo-500/10 border border-indigo-500/20 flex items-center justify-center text-indigo-400 mb-4">
              <Lock className="h-5 w-5" />
            </div>
            <h3 className="text-lg font-semibold mb-2">Envelope Encryption</h3>
            <p className="text-sm text-muted-foreground leading-relaxed">
              Secrets are encrypted at rest with AES-256 envelope keys. Zero plaintext payloads stored or logged anywhere.
            </p>
          </div>

          <div className="glass-panel p-6 rounded-2xl glow-effect">
            <div className="h-10 w-10 rounded-lg bg-purple-500/10 border border-purple-500/20 flex items-center justify-center text-purple-400 mb-4">
              <Terminal className="h-5 w-5" />
            </div>
            <h3 className="text-lg font-semibold mb-2">Audit-Ready Ledger</h3>
            <p className="text-sm text-muted-foreground leading-relaxed">
              Every access, read, modification, and environment export is recorded in an immutable audit stream for SOC2 compliance.
            </p>
          </div>
        </div>
      </main>

      {/* Footer */}
      <footer className="border-t border-border/40 py-8 text-center text-sm text-muted-foreground">
        <div className="container mx-auto px-6 flex flex-col sm:flex-row items-center justify-between gap-4">
          <p>© 2026 Enviora. All rights reserved.</p>
          <div className="flex items-center gap-6">
            <span className="text-xs font-mono bg-muted/50 px-2.5 py-1 rounded-md">
              Target Milestone: Auth System
            </span>
          </div>
        </div>
      </footer>
    </div>
  );
}
