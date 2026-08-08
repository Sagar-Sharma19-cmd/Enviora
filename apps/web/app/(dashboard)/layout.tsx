import Link from "next/link";
import { Shield, LayoutDashboard, Building2, FolderGit2, History, Settings, User } from "lucide-react";

export default function DashboardLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <div className="flex min-h-screen bg-background text-foreground">
      {/* Sidebar Navigation */}
      <aside className="w-64 border-r border-border/50 glass-panel flex flex-col justify-between">
        <div>
          {/* Logo */}
          <div className="h-16 flex items-center gap-3 px-6 border-b border-border/50">
            <div className="flex h-8 w-8 items-center justify-center rounded-lg bg-primary text-primary-foreground font-bold shadow-md shadow-primary/25">
              <Shield className="h-4 w-4" />
            </div>
            <span className="text-lg font-bold tracking-tight bg-gradient-to-r from-white to-slate-400 bg-clip-text text-transparent">
              Enviora
            </span>
          </div>

          {/* Navigation Items */}
          <nav className="p-4 space-y-1">
            <Link
              href="/dashboard"
              className="flex items-center gap-3 rounded-lg px-3 py-2 text-sm font-medium text-muted-foreground transition-colors hover:bg-muted hover:text-foreground"
            >
              <LayoutDashboard className="h-4 w-4 text-blue-400" /> Dashboard
            </Link>
            <Link
              href="/organizations"
              className="flex items-center gap-3 rounded-lg px-3 py-2 text-sm font-medium text-muted-foreground transition-colors hover:bg-muted hover:text-foreground"
            >
              <Building2 className="h-4 w-4 text-purple-400" /> Organizations
            </Link>
            <Link
              href="/projects"
              className="flex items-center gap-3 rounded-lg px-3 py-2 text-sm font-medium text-muted-foreground transition-colors hover:bg-muted hover:text-foreground"
            >
              <FolderGit2 className="h-4 w-4 text-indigo-400" /> Projects
            </Link>
            <Link
              href="/audit"
              className="flex items-center gap-3 rounded-lg px-3 py-2 text-sm font-medium text-muted-foreground transition-colors hover:bg-muted hover:text-foreground"
            >
              <History className="h-4 w-4 text-emerald-400" /> Audit Logs
            </Link>
            <Link
              href="/settings"
              className="flex items-center gap-3 rounded-lg px-3 py-2 text-sm font-medium text-muted-foreground transition-colors hover:bg-muted hover:text-foreground"
            >
              <Settings className="h-4 w-4 text-slate-400" /> Settings
            </Link>
          </nav>
        </div>

        {/* User Footer */}
        <div className="p-4 border-t border-border/50">
          <div className="flex items-center gap-3 rounded-lg p-2 bg-muted/40">
            <div className="flex h-8 w-8 items-center justify-center rounded-full bg-primary/20 text-primary">
              <User className="h-4 w-4" />
            </div>
            <div className="flex-1 min-w-0">
              <p className="text-xs font-semibold truncate">Dev Bootstrap</p>
              <p className="text-[11px] text-muted-foreground truncate">Early Dev Mode</p>
            </div>
          </div>
        </div>
      </aside>

      {/* Main Content Area */}
      <main className="flex-1 flex flex-col min-w-0">
        <header className="h-16 border-b border-border/50 glass-panel px-8 flex items-center justify-between">
          <h2 className="text-sm font-medium text-muted-foreground">
            Environment: <span className="text-foreground font-semibold">Development Bootstrap</span>
          </h2>
          <div className="text-xs font-mono bg-primary/10 border border-primary/20 text-primary px-3 py-1 rounded-full">
            Milestone 0: Architecture Ready
          </div>
        </header>

        <div className="flex-1 p-8 overflow-y-auto">
          {children}
        </div>
      </main>
    </div>
  );
}
