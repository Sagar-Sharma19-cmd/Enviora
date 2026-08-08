# Frontend Architecture Overview

- **Framework**: Next.js 16 (App Router).
- **Styling**: Tailwind CSS + custom glassmorphism design system in `app/globals.css`.
- **State & Data Fetching**: TanStack Query for server state management, custom React hooks for local state.
- **Form Management**: React Hook Form with Zod schemas.
- **Directory Layout**: Feature-first (`features/<feature>/components`) with shared components in `components/ui/`.
