import { History } from "lucide-react";

export default function AuditPage() {
  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold tracking-tight">Audit Ledger</h1>
        <p className="text-sm text-muted-foreground mt-1">
          Immutable event log tracking all secret accesses and modifications
        </p>
      </div>

      <div className="glass-panel p-8 rounded-2xl text-center space-y-4 max-w-lg mx-auto my-12">
        <div className="inline-flex h-12 w-12 items-center justify-center rounded-xl bg-emerald-500/10 text-emerald-400">
          <History className="h-6 w-6" />
        </div>
        <h3 className="text-lg font-semibold">Audit Stream Ready</h3>
        <p className="text-sm text-muted-foreground">
          Audit event capture is configured for all future secret read/write endpoints.
        </p>
      </div>
    </div>
  );
}
