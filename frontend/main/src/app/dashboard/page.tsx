"use client"

import { useEffect, useState } from "react"
import { BarChart, LineChart, PieChart } from "lucide-react"
import DashboardLayout from "@/components/dashboard/dashboard-layout"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"

export default function DashboardPage() {
  const [loaded, setLoaded] = useState(false)

  useEffect(() => {
    // Small delay to ensure the initial black state is visible
    const timer = setTimeout(() => {
      setLoaded(true)
    }, 100)

    return () => clearTimeout(timer)
  }, [])

  return (
    <div
      className={`relative min-h-screen w-full transition-colors duration-1000 ease-in-out ${loaded ? "bg-background" : "bg-black"}`}
    >
      <div
        className={`absolute inset-0 transition-opacity duration-1000 ease-in-out ${loaded ? "opacity-0" : "opacity-100"} bg-black pointer-events-none z-40`}
      />

      <DashboardLayout loaded={loaded}>
        <div className="flex-1 space-y-4 p-4 md:p-8 pt-8 bg-white">
          <div className="flex items-center justify-between">
            <h2 className="text-3xl text-black font-bold tracking-tight">Dashboard</h2>
          </div>

          <Tabs defaultValue="overview" className="space-y-4">
            <TabsList>
              <TabsTrigger value="overview">Overview</TabsTrigger>
              <TabsTrigger value="finance">Finance</TabsTrigger>
              <TabsTrigger value="hr">Human Resources</TabsTrigger>
              <TabsTrigger value="sales">Sales</TabsTrigger>
            </TabsList>

            <TabsContent value="overview" className="space-y-4">
              <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
                <Card>
                  <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                    <CardTitle className="text-sm font-medium">Umsatz</CardTitle>
                    <LineChart className="h-4 w-4 text-muted-foreground" />
                  </CardHeader>
                  <CardContent>
                    <div className="text-2xl font-bold">45,231.89€</div>
                    <p className="text-xs text-muted-foreground">+20.1% mehr als letzten Monat</p>
                    <div className="mt-4 h-[80px] w-full bg-muted/30 rounded-md flex items-center justify-center text-muted-foreground text-xs">
                      Chart Platzhalter
                    </div>
                  </CardContent>
                </Card>

                <Card>
                  <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                    <CardTitle className="text-sm font-medium">Mitarbeiter</CardTitle>
                    <BarChart className="h-4 w-4 text-muted-foreground" />
                  </CardHeader>
                  <CardContent>
                    <div className="text-2xl font-bold">2350</div>
                    <p className="text-xs text-muted-foreground">+180 mehr als letzten Monat</p>
                    <div className="mt-4 h-[80px] w-full bg-muted/30 rounded-md flex items-center justify-center text-muted-foreground text-xs">
                    Chart Platzhalter
                    </div>
                  </CardContent>
                </Card>

                <Card>
                  <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                    <CardTitle className="text-sm font-medium">Passende Ausschreibungen</CardTitle>
                    <PieChart className="h-4 w-4 text-muted-foreground" />
                  </CardHeader>
                  <CardContent>
                    <div className="text-2xl font-bold">12</div>
                    <p className="text-xs text-muted-foreground">-5 weniger als letzte Woche</p>
                    <div className="mt-4 h-[80px] w-full bg-muted/30 rounded-md flex items-center justify-center text-muted-foreground text-xs">
                    Chart Platzhalter
                    </div>
                  </CardContent>
                </Card>
              </div>

              <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-7">
                <Card className="col-span-4">
                  <CardHeader>
                    <CardTitle>Monatliche Gehälter</CardTitle>
                    <CardDescription>Alle Gehälter über die Abteilungen</CardDescription>
                  </CardHeader>
                  <CardContent className="h-[300px] bg-muted/30 rounded-md flex items-center justify-center text-muted-foreground">
                  Chart Platzhalter
                  </CardContent>
                </Card>

                <Card className="col-span-3">
                  <CardHeader>
                    <CardTitle>Vertriebs Verkäufe</CardTitle>
                    <CardDescription>Alle gewonnenne Ausschreibungen</CardDescription>
                  </CardHeader>
                  <CardContent className="h-[300px] bg-muted/30 rounded-md flex items-center justify-center text-muted-foreground">
                  Chart Platzhalter
                  </CardContent>
                </Card>
              </div>
            </TabsContent>

            <TabsContent value="finance" className="space-y-4">
              <Card>
                <CardHeader>
                  <CardTitle>Finanzielle Überischt</CardTitle>
                  <CardDescription>Detaillierte Finanzielle KPIs</CardDescription>
                </CardHeader>
                <CardContent className="h-[400px] bg-muted/30 rounded-md flex items-center justify-center text-muted-foreground">
                  Finanzen Dashboard 
                </CardContent>
              </Card>
            </TabsContent>

            <TabsContent value="hr" className="space-y-4">
              <Card>
                <CardHeader>
                  <CardTitle>Human Resources</CardTitle>
                  <CardDescription>Mitarbeiter KPIs und Abteilungs Statistiken</CardDescription>
                </CardHeader>
                <CardContent className="h-[400px] bg-muted/30 rounded-md flex items-center justify-center text-muted-foreground">
                  HR Dashboard 
                </CardContent>
              </Card>
            </TabsContent>

            <TabsContent value="sales" className="space-y-4">
              <Card>
                <CardHeader>
                  <CardTitle>Sales Performance</CardTitle>
                  <CardDescription>Vertriebs Kunden und Erfolge </CardDescription>
                </CardHeader>
                <CardContent className="h-[400px] bg-muted/30 rounded-md flex items-center justify-center text-muted-foreground">
                  Vertrieb Dashboard 
                </CardContent>
              </Card>
            </TabsContent>
          </Tabs>
        </div>
      </DashboardLayout>
    </div>
  )
}

