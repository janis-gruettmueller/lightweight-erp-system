"use client"

import type { ReactNode } from "react"
import Navbar from "./navbar"

interface DashboardLayoutProps {
  children: ReactNode
  loaded?: boolean
}

export default function DashboardLayout({ children, loaded = false }: DashboardLayoutProps) {
  return (
    <div
      className={`flex min-h-screen flex-col transition-colors duration-1000 ${loaded ? "bg-gray-50/50" : "bg-black"}`}
    >
      <Navbar />
      <main className="flex-1 container mx-auto pt-4">
        <div
          className={`transition-colors duration-1000 ${loaded ? "bg-white" : "bg-black/40"} rounded-t-xl shadow-sm`}
        >
          {children}
        </div>
      </main>
    </div>
  )
}

