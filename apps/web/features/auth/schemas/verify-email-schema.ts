import { z } from "zod";

export const resendVerificationSchema = z.object({
  email: z.string().min(1, { message: "Email is required" }).email({ message: "Invalid email address" }),
});

export type ResendVerificationSchemaType = z.infer<typeof resendVerificationSchema>;
