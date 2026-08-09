"use client";

import { useEffect, useState, Suspense } from "react";
import Link from "next/link";
import { useRouter, useSearchParams } from "next/navigation";
import { Loader2, AlertCircle, ArrowRight } from "lucide-react";

function OAuthCallbackContent() {
  const router = useRouter();
  const searchParams = useSearchParams();
  const token = searchParams.get("token");
  const error = searchParams.get("error");

  const [errorMessage] = useState<string | null>(() => {
    if (error === "account_exists") {
      return "An account with this email address already exists. Please sign in using your password first before connecting Google.";
    }
    if (error === "oauth_denied") {
      return "Google authentication was canceled or denied by user.";
    }
    if (error) {
      return "Google authentication failed. Please try again.";
    }
    return null;
  });

  useEffect(() => {
    if (token) {
      if (typeof window !== "undefined") {
        localStorage.setItem("token", token);
      }
      router.push("/dashboard");
    }
  }, [token, router]);

  if (!token && !errorMessage) {
    return (
      <div className="w-full max-w-md glass-panel p-8 rounded-2xl shadow-2xl text-center space-y-4">
        <Loader2 className="h-8 w-8 animate-spin mx-auto text-primary" />
        <p className="text-sm text-muted-foreground">Completing Google authentication...</p>
      </div>
    );
  }

  if (errorMessage) {
    return (
      <div className="w-full max-w-md glass-panel p-8 rounded-2xl shadow-2xl space-y-6">
        <div className="text-center space-y-2">
          <div className="inline-flex h-12 w-12 items-center justify-center rounded-xl bg-destructive/20 text-destructive mb-2">
            <AlertCircle className="h-6 w-6" />
          </div>
          <h1 className="text-xl font-bold tracking-tight">Authentication Notice</h1>
        </div>

        <div className="rounded-xl border border-destructive/40 bg-destructive/10 p-4 text-xs text-red-400 leading-relaxed">
          {errorMessage}
        </div>

        <Link
          href="/login"
          className="w-full rounded-lg bg-primary py-2.5 text-sm font-semibold text-primary-foreground shadow-md transition-all hover:bg-primary/90 flex items-center justify-center gap-2"
        >
          Return to Sign In <ArrowRight className="h-4 w-4" />
        </Link>
      </div>
    );
  }

  return null;
}

export default function OAuthCallbackPage() {
  return (
    <div className="flex min-h-screen items-center justify-center bg-background px-4">
      <Suspense
        fallback={
          <div className="text-center text-sm text-muted-foreground py-10">
            Processing authentication...
          </div>
        }
      >
        <OAuthCallbackContent />
      </Suspense>
    </div>
  );
}
