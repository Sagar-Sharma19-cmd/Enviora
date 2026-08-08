import Link from "next/link";
import { ArrowLeft, KeyRound } from "lucide-react";

export default function ForgotPasswordPage() {
  return (
    <div className="flex min-h-screen items-center justify-center bg-background px-4">
      <div className="w-full max-w-md glass-panel p-8 rounded-2xl shadow-2xl space-y-6">
        <div className="text-center space-y-2">
          <div className="inline-flex h-12 w-12 items-center justify-center rounded-xl bg-primary/20 text-primary mb-2">
            <KeyRound className="h-6 w-6" />
          </div>
          <h1 className="text-2xl font-bold tracking-tight">Reset Password</h1>
          <p className="text-sm text-muted-foreground">
            Enter your work email address to receive password reset instructions
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

          <button
            type="button"
            className="w-full rounded-lg bg-primary py-2.5 text-sm font-semibold text-primary-foreground shadow-md transition-all hover:bg-primary/90 opacity-80 cursor-not-allowed"
            disabled
          >
            Send Instructions (Milestone 1 Preview)
          </button>
        </form>

        <div className="pt-4 border-t border-border/50 text-center text-xs text-muted-foreground">
          <Link href="/login" className="inline-flex items-center gap-1.5 text-primary font-semibold hover:underline">
            <ArrowLeft className="h-3.5 w-3.5" /> Back to sign in
          </Link>
        </div>
      </div>
    </div>
  );
}
