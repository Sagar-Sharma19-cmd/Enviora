"use client";

import { useEffect, useState, Suspense } from "react";
import Link from "next/link";
import { useSearchParams } from "next/navigation";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { CheckCircle2, XCircle, Loader2, Mail, ArrowRight } from "lucide-react";
import { resendVerificationSchema, type ResendVerificationSchemaType } from "@/features/auth/schemas/verify-email-schema";
import { apiClient } from "@/lib/api/api-client";

function VerifyEmailContent() {
  const searchParams = useSearchParams();
  const token = searchParams.get("token");

  const [status, setStatus] = useState<"IDLE" | "LOADING" | "SUCCESS" | "ERROR">(() =>
    token ? "LOADING" : "ERROR"
  );
  const [message, setMessage] = useState<string>(() =>
    token ? "" : "Verification token is missing from URL."
  );
  const [resendStatus, setResendStatus] = useState<string | null>(null);
  const [isResending, setIsResending] = useState(false);

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<ResendVerificationSchemaType>({
    resolver: zodResolver(resendVerificationSchema),
  });

  useEffect(() => {
    if (!token) {
      return;
    }

    let isMounted = true;

    apiClient<{ message: string }>(`/auth/verify-email?token=${encodeURIComponent(token)}`)
      .then((res) => {
        if (isMounted) {
          setStatus("SUCCESS");
          setMessage(res.message || "Email address verified successfully!");
        }
      })
      .catch((err) => {
        if (isMounted) {
          setStatus("ERROR");
          setMessage(err instanceof Error ? err.message : "Failed to verify email token.");
        }
      });

    return () => {
      isMounted = false;
    };
  }, [token]);

  const onResend = async (data: ResendVerificationSchemaType) => {
    setIsResending(true);
    setResendStatus(null);
    try {
      const res = await apiClient<{ message: string }>("/auth/resend-verification", {
        method: "POST",
        body: JSON.stringify(data),
      });
      setResendStatus(res.message);
    } catch (err: unknown) {
      setResendStatus(err instanceof Error ? err.message : "Failed to send verification link.");
    } finally {
      setIsResending(false);
    }
  };

  return (
    <div className="w-full max-w-md glass-panel p-8 rounded-2xl shadow-2xl space-y-6">
      <div className="text-center space-y-2">
        <div className="inline-flex h-12 w-12 items-center justify-center rounded-xl bg-primary text-primary-foreground shadow-lg shadow-primary/30 mb-2">
          <Mail className="h-6 w-6" />
        </div>
        <h1 className="text-2xl font-bold tracking-tight">Email Verification</h1>
      </div>

      {status === "LOADING" && (
        <div className="text-center py-8 space-y-3">
          <Loader2 className="h-8 w-8 animate-spin mx-auto text-primary" />
          <p className="text-sm text-muted-foreground">Verifying your email address...</p>
        </div>
      )}

      {status === "SUCCESS" && (
        <div className="space-y-6 text-center">
          <div className="rounded-xl border border-emerald-500/30 bg-emerald-500/10 p-4 text-emerald-400 space-y-2">
            <CheckCircle2 className="h-10 w-10 mx-auto text-emerald-400" />
            <p className="font-semibold text-sm">{message}</p>
          </div>

          <Link
            href="/login"
            className="w-full rounded-lg bg-primary py-2.5 text-sm font-semibold text-primary-foreground shadow-md transition-all hover:bg-primary/90 flex items-center justify-center gap-2"
          >
            Proceed to Sign In <ArrowRight className="h-4 w-4" />
          </Link>
        </div>
      )}

      {status === "ERROR" && (
        <div className="space-y-6">
          <div className="rounded-xl border border-destructive/50 bg-destructive/10 p-4 text-red-400 space-y-2 text-center">
            <XCircle className="h-10 w-10 mx-auto text-destructive" />
            <p className="font-semibold text-sm">{message}</p>
          </div>

          <div className="border-t border-border/50 pt-4 space-y-4">
            <div className="text-center space-y-1">
              <h3 className="text-sm font-semibold">Need a new verification link?</h3>
              <p className="text-xs text-muted-foreground">Enter your email address below to resend</p>
            </div>

            {resendStatus && (
              <div className="rounded-lg bg-primary/10 border border-primary/20 p-3 text-xs text-primary text-center font-medium">
                {resendStatus}
              </div>
            )}

            <form onSubmit={handleSubmit(onResend)} className="space-y-3">
              <div>
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

              <button
                type="submit"
                disabled={isResending}
                className="w-full rounded-lg border border-input bg-background py-2 text-xs font-semibold hover:bg-accent hover:text-accent-foreground flex items-center justify-center gap-2 disabled:opacity-60"
              >
                {isResending ? <Loader2 className="h-3.5 w-3.5 animate-spin" /> : "Resend Verification Link"}
              </button>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}

export default function VerifyEmailPage() {
  return (
    <div className="flex min-h-screen items-center justify-center bg-background px-4">
      <Suspense
        fallback={
          <div className="text-center text-sm text-muted-foreground py-10">
            Loading verification details...
          </div>
        }
      >
        <VerifyEmailContent />
      </Suspense>
    </div>
  );
}
