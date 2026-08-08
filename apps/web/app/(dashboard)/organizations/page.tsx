import { Building2 } from "lucide-react";

export default function OrganizationsPage() {
  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold tracking-tight">Organizations</h1>
        <p className="text-sm text-muted-foreground mt-1">
          Multi-tenant organization management domain
        </p>
      </div>

      <div className="glass-panel p-8 rounded-2xl text-center space-y-4 max-w-lg mx-auto my-12">
        <div className="inline-flex h-12 w-12 items-center justify-center rounded-xl bg-purple-500/10 text-purple-400">
          <Building2 className="h-6 w-6" />
        </div>
        <h3 className="text-lg font-semibold">No Organizations Yet</h3>
        <p className="text-sm text-muted-foreground">
          Organization creation and member management will be implemented during the Authentication & Multi-tenancy milestone.
        </p>
      </div>
    </div>
  );
}
