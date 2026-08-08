import Link from "next/link";
import { Shield, KeyRound } from "lucide-react";

export default function LoginPage() {
  return (
    <div className="flex min-h-screen items-center justify-center bg-background px-4">
      <div className="w-full max-w-md glass-panel p-8 rounded-2xl shadow-2xl space-y-6">
        <div className="text-center space-y-2">
          <div className="inline-flex h-12 w-12 items-center justify-center rounded-xl bg-primary text-primary-foreground shadow-lg shadow-primary/30 mb-2">
            <KeyRound className="h-6 w-6" />
          </div>
          <h1 className="text-2xl font-bold tracking-tight">Sign in to Enviora</h1>
          <p className="text-sm text-muted-foreground">
            Enter your credentials to access your developer secrets vault
          </p>
        </div>

        <form className="space-y-4">
          <div>
            <label className="block text-xs font-semibold uppercase tracking-wider text-muted-foreground mb-1.5">
              Work Email
            </label>
            <input
              type="email"
              placeholder="alex@company.com"
              className="w-full rounded-lg border border-input bg-background/50 px-3.5 py-2.5 text-sm placeholder:text-muted-foreground/60 focus:outline-none focus:ring-2 focus:ring-primary"
              disabled
            />
          </div>

          <div>
            <label className="block text-xs font-semibold uppercase tracking-wider text-muted-foreground mb-1.5">
              Password
            </label>
            <input
              type="password"
              placeholder="••••••••••••"
              className="w-full rounded-lg border border-input bg-background/50 px-3.5 py-2.5 text-sm placeholder:text-muted-foreground/60 focus:outline-none focus:ring-2 focus:ring-primary"
              disabled
            />
          </div>

          <div className="flex items-center justify-between text-xs">
            <Link href="/forgot-password" className="text-primary hover:underline font-medium">
              Forgot password?
            </Link>
          </div>

          <button
            type="button"
            className="w-full rounded-lg bg-primary py-2.5 text-sm font-semibold text-primary-foreground shadow-md transition-all hover:bg-primary/90 opacity-80 cursor-not-allowed"
            disabled
          >
            Sign In (Milestone 1 Preview)
          </button>
        </form>

        <div className="pt-4 border-t border-border/50 text-center text-xs text-muted-foreground">
          Don't have an account?{" "}
          <Link href="/register" className="text-primary font-semibold hover:underline">
            Create an organization
          </Link>
        </div>
      </div>
    </div>
  );
}
