"use client";

import { useState } from "react";
import Link from "next/link";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { UserPlus, Loader2, CheckCircle2, AlertCircle, Mail } from "lucide-react";
import { registerSchema, type RegisterSchemaType } from "@/features/auth/schemas/register-schema";
import { apiClient } from "@/lib/api/api-client";

interface RegisteredUser {
  id: string;
  name: string;
  email: string;
  status: string;
}

export default function RegisterPage() {
  const [serverError, setServerError] = useState<string | null>(null);
  const [successUser, setSuccessUser] = useState<RegisteredUser | null>(null);
  const [isLoading, setIsLoading] = useState(false);

  const apiBaseUrl = process.env.NEXT_PUBLIC_API_URL || "http://localhost:8080/api/v1";

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<RegisterSchemaType>({
    resolver: zodResolver(registerSchema),
  });

  const onSubmit = async (data: RegisterSchemaType) => {
    setIsLoading(true);
    setServerError(null);
    setSuccessUser(null);

    try {
      const response = await apiClient<RegisteredUser>("/auth/register", {
        method: "POST",
        body: JSON.stringify(data),
      });
      setSuccessUser(response);
    } catch (err: unknown) {
      if (err instanceof Error) {
        setServerError(err.message);
      } else {
        setServerError("An unexpected registration error occurred.");
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
            <UserPlus className="h-6 w-6" />
          </div>
          <h1 className="text-2xl font-bold tracking-tight">Create your account</h1>
          <p className="text-sm text-muted-foreground">
            Sign up to start managing developer secrets with Enviora
          </p>
        </div>

        {successUser ? (
          <div className="rounded-xl border border-emerald-500/30 bg-emerald-500/10 p-5 text-center space-y-3">
            <div className="inline-flex h-10 w-10 items-center justify-center rounded-full bg-emerald-500/20 text-emerald-400">
              <CheckCircle2 className="h-6 w-6" />
            </div>
            <h3 className="text-base font-semibold text-emerald-300">Account Created Successfully!</h3>
            <p className="text-xs text-muted-foreground">
              Welcome, <span className="font-semibold text-foreground">{successUser.name}</span>. A verification link has been sent to <span className="font-semibold text-foreground">{successUser.email}</span>.
            </p>
            <div className="p-3 bg-background/50 rounded-lg border border-border/50 text-xs text-muted-foreground space-y-1">
              <p className="font-medium text-foreground">Next Step:</p>
              <p>Please check your inbox and click the link to verify your email address before signing in.</p>
            </div>
            <div className="pt-2 flex flex-col sm:flex-row items-center justify-center gap-2">
              <a
                href="http://localhost:8025"
                target="_blank"
                rel="noreferrer"
                className="w-full sm:w-auto inline-flex items-center justify-center gap-1.5 rounded-lg bg-secondary border border-border px-3.5 py-2 text-xs font-semibold text-foreground hover:bg-accent transition-colors"
              >
                <Mail className="h-3.5 w-3.5" /> Open Dev Mailpit Inbox
              </a>
              <Link
                href="/login"
                className="w-full sm:w-auto inline-flex items-center justify-center rounded-lg bg-emerald-600 px-4 py-2 text-xs font-semibold text-white hover:bg-emerald-500 transition-colors"
              >
                Proceed to Sign In
              </Link>
            </div>
          </div>
        ) : (
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
                Or register with email
              </span>
              <div className="w-full border-t border-border/50" />
            </div>

            <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
              {serverError && (
                <div className="rounded-lg border border-destructive/50 bg-destructive/10 p-3.5 flex items-start gap-2.5 text-xs text-red-400">
                  <AlertCircle className="h-4 w-4 shrink-0 text-destructive mt-0.5" />
                  <span>{serverError}</span>
                </div>
              )}

              <div>
                <label className="block text-xs font-semibold uppercase tracking-wider text-muted-foreground mb-1.5">
                  Full Name
                </label>
                <input
                  {...register("name")}
                  type="text"
                  placeholder="Alex Rivera"
                  className="w-full rounded-lg border border-input bg-background/50 px-3.5 py-2.5 text-sm placeholder:text-muted-foreground/60 focus:outline-none focus:ring-2 focus:ring-primary"
                />
                {errors.name && (
                  <p className="text-[11px] text-red-400 mt-1">{errors.name.message}</p>
                )}
              </div>

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

              <button
                type="submit"
                disabled={isLoading}
                className="w-full rounded-lg bg-primary py-2.5 text-sm font-semibold text-primary-foreground shadow-md transition-all hover:bg-primary/90 flex items-center justify-center gap-2 disabled:opacity-60"
              >
                {isLoading ? (
                  <>
                    <Loader2 className="h-4 w-4 animate-spin" /> Registering...
                  </>
                ) : (
                  "Create Account"
                )}
              </button>
            </form>
          </div>
        )}

        <div className="pt-4 border-t border-border/50 text-center text-xs text-muted-foreground">
          Already have an account?{" "}
          <Link href="/login" className="text-primary font-semibold hover:underline">
            Sign in
          </Link>
        </div>
      </div>
    </div>
  );
}
