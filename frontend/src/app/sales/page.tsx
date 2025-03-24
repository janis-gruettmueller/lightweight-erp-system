"use client"

import React, { Suspense } from "react"
import DashboardLayout from "@/components/dashboard/dashboard-layout"

export default function SalesPage() {
  return (
    <Suspense fallback={<div>Loading...</div>}>
      <DashboardLayout>
        <div className="flex-1 space-y-4 p-4 md:p-8 pt-6">
          <h2 className="text-3xl font-bold tracking-tight">Vertrieb</h2>
          {/* Sales content */}
        </div>
      </DashboardLayout>
    </Suspense>
  )
} 