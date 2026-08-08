import { Settings } from "lucide-react";

export default function SettingsPage() {
  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold tracking-tight">Organization Settings</h1>
        <p className="text-sm text-muted-foreground mt-1">
          Configure security settings, API tokens, and access policies
        </p>
      </div>

      <div className="glass-panel p-8 rounded-2xl text-center space-y-4 max-w-lg mx-auto my-12">
        <div className="inline-flex h-12 w-12 items-center justify-center rounded-xl bg-slate-500/10 text-slate-400">
          <Settings className="h-6 w-6" />
        </div>
        <h3 className="text-lg font-semibold">Settings Placeholder</h3>
        <p className="text-sm text-muted-foreground">
          Platform parameters and API key generation will be accessible here.
        </p>
      </div>
    </div>
  );
}
