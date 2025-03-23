"use client"

import type { ReactNode } from "react"
import Navbar from "./navbar"

interface DashboardLayoutProps {
  children: ReactNode
}

export default function DashboardLayout({ children }: DashboardLayoutProps) {
  return (
    <div className="flex min-h-screen flex-col bg-gray-50/50">
      <Navbar />
      <main className="flex-1 container mx-auto pt-4">
        <div className="bg-white rounded-t-xl shadow-sm">
          {children}
        </div>
      </main>
    </div>
  )
}

