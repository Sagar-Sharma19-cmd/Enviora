import { Shield, Key, FolderGit2, History } from "lucide-react";

export default function DashboardPage() {
  return (
    <div className="space-y-8">
      <div>
        <h1 className="text-2xl font-bold tracking-tight">Platform Overview</h1>
        <p className="text-sm text-muted-foreground mt-1">
          Enviora Developer Secrets Platform — Repository Bootstrap State
        </p>
      </div>

      {/* Metrics Cards */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-6">
        <div className="glass-panel p-5 rounded-xl space-y-2">
          <div className="flex items-center justify-between text-muted-foreground">
            <span className="text-xs font-semibold uppercase tracking-wider">Organizations</span>
            <Shield className="h-4 w-4 text-primary" />
          </div>
          <p className="text-2xl font-bold">0</p>
          <p className="text-xs text-muted-foreground">Ready for Milestone 1</p>
        </div>

        <div className="glass-panel p-5 rounded-xl space-y-2">
          <div className="flex items-center justify-between text-muted-foreground">
            <span className="text-xs font-semibold uppercase tracking-wider">Projects</span>
            <FolderGit2 className="h-4 w-4 text-indigo-400" />
          </div>
          <p className="text-2xl font-bold">0</p>
          <p className="text-xs text-muted-foreground">Ready for Milestone 2</p>
        </div>

        <div className="glass-panel p-5 rounded-xl space-y-2">
          <div className="flex items-center justify-between text-muted-foreground">
            <span className="text-xs font-semibold uppercase tracking-wider">Active Secrets</span>
            <Key className="h-4 w-4 text-purple-400" />
          </div>
          <p className="text-2xl font-bold">0</p>
          <p className="text-xs text-muted-foreground">Envelope Encryption Ready</p>
        </div>

        <div className="glass-panel p-5 rounded-xl space-y-2">
          <div className="flex items-center justify-between text-muted-foreground">
            <span className="text-xs font-semibold uppercase tracking-wider">Audit Events</span>
            <History className="h-4 w-4 text-emerald-400" />
          </div>
          <p className="text-2xl font-bold">0</p>
          <p className="text-xs text-muted-foreground">Immutable Ledger Ready</p>
        </div>
      </div>

      {/* Banner */}
      <div className="glass-panel p-6 rounded-2xl border border-primary/20 bg-primary/5 space-y-3">
        <h3 className="text-base font-semibold text-primary">Repository Architecture Bootstrap Complete</h3>
        <p className="text-sm text-muted-foreground leading-relaxed">
          The foundation of Enviora is successfully created. Next step is to start feature development beginning with Authentication.
        </p>
      </div>
    </div>
  );
}
