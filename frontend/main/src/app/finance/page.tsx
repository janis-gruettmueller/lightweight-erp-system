"use client"

import { useState } from "react"
import { 
  Calculator, 
  Receipt, 
  PiggyBank, 
  FileText, 
  BadgeDollarSign,
  Search,
  Plus,
  ArrowUpRight,
  ArrowDownRight,
  Clock,
  ChevronRight
} from "lucide-react"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Button } from "@/components/ui/button"
import DashboardLayout from "@/components/dashboard/dashboard-layout"

export default function FinancePage() {
  const [searchQuery, setSearchQuery] = useState("")

  return (
    <DashboardLayout>
      <div className="flex-1 space-y-8 p-4 md:p-8 pt-6">
        {/* Header with search and actions */}
        <div className="flex items-center justify-between">
          <div>
            <h2 className="text-3xl font-bold tracking-tight">Finanzen</h2>
            <p className="text-muted-foreground">Verwalten Sie Ihre Finanzen und Budgets</p>
          </div>
          <div className="flex items-center space-x-2">
            <div className="relative">
              <Search className="absolute left-2 top-2.5 h-4 w-4 text-muted-foreground" />
              <Input
                placeholder="Suchen..."
                className="pl-8 w-[200px]"
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
              />
            </div>
            <Button>
              <Plus className="mr-2 h-4 w-4" /> Neue Transaktion
            </Button>
          </div>
        </div>

        {/* Rechnungen Section */}
        <div className="space-y-4">
          <div className="border-b-2 border-black pb-2">
            <h3 className="text-2xl font-semibold flex items-center">
              <Receipt className="mr-2 h-5 w-5" />
              Rechnungen
            </h3>
          </div>
          
          <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
            <Card className="border-2 border-black">
              <CardHeader>
                <CardTitle>Offene Rechnungen</CardTitle>
                <CardDescription>12 ausstehend</CardDescription>
              </CardHeader>
              <CardContent>
                <div className="space-y-4">
                  {/* Recent Invoices List */}
                  {['INV-2024-089', 'INV-2024-088', 'INV-2024-087'].map((inv, i) => (
                    <div key={i} className="flex items-center justify-between p-2 hover:bg-gray-50 rounded-md">
                      <div>
                        <p className="font-medium">{inv}</p>
                        <p className="text-sm text-muted-foreground">21. März 2024</p>
                      </div>
                      <div className="text-right">
                        <p className="font-medium">8,320€</p>
                        <p className="text-sm text-orange-500">Ausstehend</p>
                      </div>
                      <ChevronRight className="h-4 w-4 text-muted-foreground" />
                    </div>
                  ))}
                </div>
              </CardContent>
            </Card>

            <Card className="border-2 border-black">
              <CardHeader>
                <CardTitle>Rechnungsstatus</CardTitle>
                <CardDescription>Übersicht März 2024</CardDescription>
              </CardHeader>
              <CardContent>
                <div className="space-y-4">
                  <div className="flex items-center justify-between">
                    <div>
                      <p className="text-sm text-muted-foreground">Bezahlt</p>
                      <p className="text-2xl font-bold text-green-600">32,450€</p>
                    </div>
                    <ArrowUpRight className="h-8 w-8 text-green-600" />
                  </div>
                  <div className="flex items-center justify-between">
                    <div>
                      <p className="text-sm text-muted-foreground">Ausstehend</p>
                      <p className="text-2xl font-bold text-orange-500">12,800€</p>
                    </div>
                    <Clock className="h-8 w-8 text-orange-500" />
                  </div>
                </div>
              </CardContent>
            </Card>

            <Card className="border-2 border-black">
              <CardHeader>
                <CardTitle>Schnellaktionen</CardTitle>
                <CardDescription>Rechnungsverwaltung</CardDescription>
              </CardHeader>
              <CardContent>
                <div className="space-y-2">
                  <Button className="w-full justify-start" variant="default">
                    <Plus className="mr-2 h-4 w-4" /> Neue Rechnung
                  </Button>
                  <Button className="w-full justify-start" variant="outline">
                    <FileText className="mr-2 h-4 w-4" /> Rechnungsübersicht
                  </Button>
                  <Button className="w-full justify-start" variant="outline">
                    <BadgeDollarSign className="mr-2 h-4 w-4" /> Zahlungen prüfen
                  </Button>
                </div>
              </CardContent>
            </Card>
          </div>
        </div>

        {/* Budget Section */}
        <div className="space-y-4">
          <div className="border-b-2 border-black pb-2">
            <h3 className="text-2xl font-semibold flex items-center">
              <PiggyBank className="mr-2 h-5 w-5" />
              Budgetierung
            </h3>
          </div>
          
          <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
            {/* Budget Cards Here */}
            <Card className="border-2 border-black">
              <CardHeader>
                <CardTitle>Budgetauslastung</CardTitle>
                <CardDescription>Aktuelles Quartal</CardDescription>
              </CardHeader>
              <CardContent>
                {['Entwicklung', 'Marketing', 'Vertrieb'].map((dept, i) => (
                  <div key={i} className="mb-4">
                    <div className="flex justify-between text-sm mb-1">
                      <span>{dept}</span>
                      <span className="font-medium">
                        {['65%', '48%', '72%'][i]}
                      </span>
                    </div>
                    <div className="h-2 bg-gray-100 rounded-full">
                      <div 
                        className="h-full bg-blue-500 rounded-full"
                        style={{ width: ['65%', '48%', '72%'][i] }}
                      />
                    </div>
                  </div>
                ))}
              </CardContent>
            </Card>
            {/* More Budget Cards */}
          </div>
        </div>

        {/* Reports Section */}
        <div className="space-y-4">
          <div className="border-b-2 border-black pb-2">
            <h3 className="text-2xl font-semibold flex items-center">
              <FileText className="mr-2 h-5 w-5" />
              Berichte
            </h3>
          </div>
          
          <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
            {/* Report Cards Here */}
            <Card className="border-2 border-black">
              <CardHeader>
                <CardTitle>Aktuelle Berichte</CardTitle>
                <CardDescription>März 2024</CardDescription>
              </CardHeader>
              <CardContent>
                <div className="space-y-2">
                  {[
                    { name: 'Quartalsbericht Q1', date: '31. März 2024', status: 'In Bearbeitung' },
                    { name: 'Umsatzanalyse', date: '15. März 2024', status: 'Abgeschlossen' },
                    { name: 'Kostenübersicht', date: '1. März 2024', status: 'Abgeschlossen' }
                  ].map((report, i) => (
                    <div key={i} className="flex items-center justify-between p-2 hover:bg-gray-50 rounded-md">
                      <div>
                        <p className="font-medium">{report.name}</p>
                        <p className="text-sm text-muted-foreground">{report.date}</p>
                      </div>
                      <span className={`text-sm ${
                        report.status === 'Abgeschlossen' ? 'text-green-500' : 'text-orange-500'
                      }`}>
                        {report.status}
                      </span>
                    </div>
                  ))}
                </div>
              </CardContent>
            </Card>
            {/* More Report Cards */}
          </div>
        </div>
      </div>
    </DashboardLayout>
  )
} 