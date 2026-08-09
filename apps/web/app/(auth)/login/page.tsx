"use client";

import { useState } from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { KeyRound, Loader2, AlertCircle, MailWarning } from "lucide-react";
import { loginSchema, type LoginSchemaType } from "@/features/auth/schemas/login-schema";
import { apiClient } from "@/lib/api/api-client";

interface LoginResponseData {
  token: string;
  user: {
    id: string;
    name: string;
    email: string;
    status: string;
  };
}

export default function LoginPage() {
  const router = useRouter();
  const [serverError, setServerError] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(false);
  const [isUnverified, setIsUnverified] = useState(false);
  const [unverifiedEmail, setUnverifiedEmail] = useState<string>("");

  const apiBaseUrl = process.env.NEXT_PUBLIC_API_URL || "http://localhost:8080/api/v1";

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<LoginSchemaType>({
    resolver: zodResolver(loginSchema),
  });

  const onSubmit = async (data: LoginSchemaType) => {
    setIsLoading(true);
    setServerError(null);
    setIsUnverified(false);

    try {
      const response = await apiClient<LoginResponseData>("/auth/login", {
        method: "POST",
        body: JSON.stringify(data),
      });

      // Save token in localStorage for frontend session
      if (typeof window !== "undefined") {
        localStorage.setItem("token", response.token);
        localStorage.setItem("user", JSON.stringify(response.user));
      }

      router.push("/dashboard");
    } catch (err: unknown) {
      if (err instanceof Error) {
        setServerError(err.message);
        if (err.message.includes("Email verification is required")) {
          setIsUnverified(true);
          setUnverifiedEmail(data.email);
        }
      } else {
        setServerError("An unexpected login error occurred.");
      }
    } finally {
      setIsLoading(false);
    }
  };

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

        <div className="space-y-4">
          <a
            href={`${apiBaseUrl}/auth/oauth2/google`}
            className="w-full rounded-lg border border-input bg-background/50 hover:bg-accent hover:text-accent-foreground py-2.5 text-xs font-semibold shadow-sm transition-all flex items-center justify-center gap-2.5"
          >
            <svg className="h-4 w-4" viewBox="0 0 24 24">
              <path
                fill="#4285F4"
                d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z"
              />
              <path
                fill="#34A853"
                d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"
              />
              <path
                fill="#FBBC05"
                d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.06H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.94l2.85-2.22.81-.63z"
              />
              <path
                fill="#EA4335"
                d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.06l3.66 2.84c.87-2.6 3.3-4.52 6.16-4.52z"
              />
            </svg>
            <span>Continue with Google</span>
          </a>

          <div className="relative flex items-center justify-center">
            <div className="w-full border-t border-border/50" />
            <span className="bg-background px-3 text-[10px] uppercase font-semibold text-muted-foreground shrink-0">
              Or sign in with email
            </span>
            <div className="w-full border-t border-border/50" />
          </div>
        </div>

        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
          {serverError && (
            <div className="rounded-lg border border-destructive/50 bg-destructive/10 p-3.5 space-y-2 text-xs text-red-400">
              <div className="flex items-start gap-2.5">
                {isUnverified ? (
                  <MailWarning className="h-4 w-4 shrink-0 text-amber-400 mt-0.5" />
                ) : (
                  <AlertCircle className="h-4 w-4 shrink-0 text-destructive mt-0.5" />
                )}
                <span>{serverError}</span>
              </div>
              {isUnverified && (
                <div className="pt-2 border-t border-destructive/20">
                  <Link
                    href={`/verify-email`}
                    className="inline-flex items-center gap-1.5 font-semibold text-primary hover:underline text-xs"
                  >
                    Need to resend verification link for {unverifiedEmail}?
                  </Link>
                </div>
              )}
            </div>
          )}

          <div>
            <label className="block text-xs font-semibold uppercase tracking-wider text-muted-foreground mb-1.5">
              Work Email
            </label>
            <input
              {...register("email")}
              type="email"
              placeholder="alex@company.com"
              className="w-full rounded-lg border border-input bg-background/50 px-3.5 py-2.5 text-sm placeholder:text-muted-foreground/60 focus:outline-none focus:ring-2 focus:ring-primary"
            />
            {errors.email && (
              <p className="text-[11px] text-red-400 mt-1">{errors.email.message}</p>
            )}
          </div>

          <div>
            <label className="block text-xs font-semibold uppercase tracking-wider text-muted-foreground mb-1.5">
              Password
            </label>
            <input
              {...register("password")}
              type="password"
              placeholder="••••••••••••"
              className="w-full rounded-lg border border-input bg-background/50 px-3.5 py-2.5 text-sm placeholder:text-muted-foreground/60 focus:outline-none focus:ring-2 focus:ring-primary"
            />
            {errors.password && (
              <p className="text-[11px] text-red-400 mt-1">{errors.password.message}</p>
            )}
          </div>

          <div className="flex items-center justify-between text-xs">
            <Link href="/forgot-password" className="text-primary hover:underline font-medium">
              Forgot password?
            </Link>
          </div>

          <button
            type="submit"
            disabled={isLoading}
            className="w-full rounded-lg bg-primary py-2.5 text-sm font-semibold text-primary-foreground shadow-md transition-all hover:bg-primary/90 flex items-center justify-center gap-2 disabled:opacity-60"
          >
            {isLoading ? (
              <>
                <Loader2 className="h-4 w-4 animate-spin" /> Signing In...
              </>
            ) : (
              "Sign In"
            )}
          </button>
        </form>

        <div className="pt-4 border-t border-border/50 text-center text-xs text-muted-foreground">
          Don&apos;t have an account?{" "}
          <Link href="/register" className="text-primary font-semibold hover:underline">
            Create an account
          </Link>
        </div>
      </div>
    </div>
  );
}
