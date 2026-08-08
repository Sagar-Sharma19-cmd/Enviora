import { FolderGit2 } from "lucide-react";

export default function ProjectsPage() {
  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold tracking-tight">Projects</h1>
        <p className="text-sm text-muted-foreground mt-1">
          Manage application projects and deployment environment scopes
        </p>
      </div>

      <div className="glass-panel p-8 rounded-2xl text-center space-y-4 max-w-lg mx-auto my-12">
        <div className="inline-flex h-12 w-12 items-center justify-center rounded-xl bg-indigo-500/10 text-indigo-400">
          <FolderGit2 className="h-6 w-6" />
        </div>
        <h3 className="text-lg font-semibold">No Projects Found</h3>
        <p className="text-sm text-muted-foreground">
          Project scoping and environment secret mapping will be enabled following authentication.
        </p>
      </div>
    </div>
  );
}
