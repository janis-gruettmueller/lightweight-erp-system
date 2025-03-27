"use client"

import withAuth from '@/utils/withAuth';
import React, { useEffect, useState, Suspense } from 'react'
import { useSearchParams } from 'next/navigation'
import { BarChart, LineChart, PieChart } from "lucide-react"
import { 
  ResponsiveContainer, 
  LineChart as RechartsLineChart,
  Line,
  BarChart as RechartsBarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  PieChart as RechartsPieChart,
  Pie,
  Cell,
  AreaChart,
  Area
} from 'recharts'
import DashboardLayout from "@/components/dashboard/dashboard-layout"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"

// Create SearchParamsComponent as a top-level component
function SearchParamsComponent() {
  const searchParams = useSearchParams();
  const fromLogin = searchParams.get('fromLogin');

  const [loaded, setLoaded] = useState(false);

  useEffect(() => {
    if (fromLogin) {
      const timer = setTimeout(() => {
        setLoaded(true);
      }, 100);
      return () => clearTimeout(timer);
    } else {
      setLoaded(true);
    }
  }, [fromLogin]);

  // Add all sample data arrays here (MOVE YOUR DATA HERE)
  const revenueData = [
    { name: 'Mo', value: 40 },
    { name: 'Di', value: 30 },
    { name: 'Mi', value: 45 },
    { name: 'Do', value: 25 },
    { name: 'Fr', value: 55 },
    { name: 'Sa', value: 35 },
    { name: 'So', value: 40 },
  ]

  const employeeData = [
    { name: 'Jan', value: 6 },
    { name: 'Feb', value: 8 },
    { name: 'Mar', value: 7 },
    { name: 'Apr', value: 12 },
  ]

  const tenderData = [
    { status: 'Gewonnen', value: 8 },
    { status: 'In Bearbeitung', value: 12 },
    { status: 'Neu', value: 4 },
  ]

  const salaryData = [
    { department: 'Beratung', salary: 65000, employees: 42 },
    { department: 'Marketing', salary: 55000, employees: 28 },
    { department: 'Vertrieb', salary: 60000, employees: 35 },
    { department: 'HR', salary: 52000, employees: 15 },
    { department: 'Finanzen', salary: 63000, employees: 20 },
  ]

  const salesData = [
    { project: 'ERP System XYZ GmbH', value: 250000, status: 'Gewonnen' },
    { project: 'CRM Update AG', value: 180000, status: 'In Verhandlung' },
    { project: 'IT Infrastructure KG', value: 320000, status: 'Gewonnen' },
    { project: 'Cloud Migration GmbH', value: 150000, status: 'In Verhandlung' },
    { project: 'Security Update AG', value: 90000, status: 'Gewonnen' },
  ]

  // Add the missing financeData
  const financeData = [
    { month: 'Jan', revenue: 4000, expenses: 2400 },
    { month: 'Feb', revenue: 3000, expenses: 1398 },
    { month: 'Mar', revenue: 2000, expenses: 9800 },
    { month: 'Apr', revenue: 2780, expenses: 3908 },
    { month: 'May', revenue: 1890, expenses: 4800 },
    { month: 'Jun', revenue: 2390, expenses: 3800 },
  ]

  const hrData = [
    { department: 'Vertrieb', employees: 100, turnover: 5 },
    { department: 'Beratung', employees: 80, turnover: 8 },
    { department: 'Marketing', employees: 40, turnover: 3 },
    { department: 'HR', employees: 20, turnover: 2 },
    { department: 'Finanzen', employees: 30, turnover: 4 },
  ]

  const COLORS = ['#0088FE', '#00C49F', '#FFBB28', '#FF8042']
  
  return (
    <div className={`relative min-h-screen w-full transition-colors duration-1000 ease-in-out ${
      loaded ? "bg-background" : "bg-black"}`}
    >
      <div className={`absolute inset-0 transition-opacity duration-1000 ease-in-out ${
        loaded ? "opacity-0" : "opacity-100"} bg-black pointer-events-none z-40`}
      />
  
        <DashboardLayout>
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
                  <Card className="border-2 border-black">
                    <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                      <CardTitle className="text-sm font-medium">Umsatz</CardTitle>
                      <LineChart className="h-4 w-4 text-muted-foreground" />
                    </CardHeader>
                    <CardContent>
                      <div className="text-2xl font-bold">45,231.89€</div>
                      <p className="text-xs text-muted-foreground">+20.1% mehr als letzten Monat</p>
                      <div className="h-[80px] w-full mt-4">
                        <ResponsiveContainer width="100%" height="100%">
                          <AreaChart data={revenueData}>
                            <defs>
                              <linearGradient id="colorRevenue" x1="0" y1="0" x2="0" y2="1">
                                <stop offset="5%" stopColor="#0088FE" stopOpacity={0.8}/>
                                <stop offset="95%" stopColor="#0088FE" stopOpacity={0}/>
                              </linearGradient>
                            </defs>
                            <Area type="monotone" dataKey="value" stroke="#0088FE" fillOpacity={1} fill="url(#colorRevenue)" />
                            <Tooltip />
                          </AreaChart>
                        </ResponsiveContainer>
                      </div>
                    </CardContent>
                  </Card>

                  <Card className="border-2 border-black">
                    <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                      <CardTitle className="text-sm font-medium">Mitarbeiter</CardTitle>
                      <BarChart className="h-4 w-4 text-muted-foreground" />
                    </CardHeader>
                    <CardContent>
                      <div className="text-2xl font-bold">12</div>
                      <p className="text-xs text-muted-foreground">+180 mehr als letzten Monat</p>
                      <div className="h-[80px] w-full mt-4">
                        <ResponsiveContainer width="100%" height="100%">
                          <RechartsBarChart data={employeeData}>
                            <Bar dataKey="value" fill="#00C49F" />
                            <Tooltip />
                          </RechartsBarChart>
                        </ResponsiveContainer>
                      </div>
                    </CardContent>
                  </Card>

                  <Card className="border-2 border-black">
                    <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                      <CardTitle className="text-sm font-medium">Passende Ausschreibungen</CardTitle>
                      <PieChart className="h-4 w-4 text-muted-foreground" />
                    </CardHeader>
                    <CardContent>
                      <div className="flex items-center justify-between">
                        <div className="text-2xl font-bold">9</div>
                        
                      </div>
                      <p className="text-xs text-muted-foreground">5 weniger als letzten Monat</p>
                      <div className="h-[80px] w-full mt-4">
                        <ResponsiveContainer width="100%" height="100%">
                          <RechartsPieChart>
                            <Pie
                              data={tenderData}
                              cx="50%"
                              cy="50%"
                              innerRadius={25}
                              outerRadius={35}
                              fill="#8884d8"
                              dataKey="value"
                            >
                              {tenderData.map((entry, index) => (
                                <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                              ))}
                            </Pie>
                            <Tooltip />
                          </RechartsPieChart>
                        </ResponsiveContainer>
                      </div>
                    </CardContent>
                  </Card>
                </div>

                <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-7">
                  <Card className="col-span-4 border-2 border-black">
                    <CardHeader>
                      <CardTitle>Monatliche Gehälter</CardTitle>
                      <CardDescription>Alle Gehälter über die Abteilungen</CardDescription>
                    </CardHeader>
                    <CardContent>
                      <div className="space-y-4">
                        {salaryData.map((dept, index) => (
                          <div key={dept.department} className="flex items-center">
                            <div className="w-1/3 text-sm font-medium">{dept.department}</div>
                            <div className="w-2/3 flex items-center gap-2">
                              <div className="flex-1 h-2 bg-gray-100 rounded-full overflow-hidden">
                                <div 
                                  className="h-full bg-blue-500" 
                                  style={{ width: `${(dept.salary / 70000) * 100}%` }} 
                                />
                              </div>
                              <span className="text-sm font-medium">{dept.salary.toLocaleString()}€</span>
                              <span className="text-xs text-gray-500">({dept.employees})</span>
                            </div>
                          </div>
                        ))}
                      </div>
                    </CardContent>
                  </Card>

                  <Card className="col-span-3 border-2 border-black">
                    <CardHeader>
                      <CardTitle>Vertriebs Verkäufe</CardTitle>
                      <CardDescription>Alle gewonnenen Ausschreibungen</CardDescription>
                    </CardHeader>
                    <CardContent>
                      <div className="space-y-4">
                        {salesData.map((sale, index) => (
                          <div key={index} className="flex items-center gap-4 p-2 rounded-lg hover:bg-gray-50">
                            <div className={`w-2 h-2 rounded-full ${
                              sale.status === 'Gewonnen' ? 'bg-green-500' : 'bg-yellow-500'
                            }`} />
                            <div className="flex-1">
                              <div className="text-sm font-medium">{sale.project}</div>
                              <div className="text-xs text-gray-500">{sale.status}</div>
                            </div>
                            <div className="text-sm font-medium">
                              {sale.value.toLocaleString()}€
                            </div>
                          </div>
                        ))}
                      </div>
                    </CardContent>
                  </Card>
                </div>
              </TabsContent>

              <TabsContent value="finance" className="space-y-4">
                <div className="grid gap-4 grid-cols-1 md:grid-cols-2">
                  <Card className="border-2 border-black">
                    <CardHeader>
                      <CardTitle>Umsatz vs. Ausgaben</CardTitle>
                      <CardDescription>Monatliche Finanzübersicht</CardDescription>
                    </CardHeader>
                    <CardContent className="h-[400px]">
                      <ResponsiveContainer width="100%" height="100%">
                        <RechartsLineChart data={financeData}>
                          <CartesianGrid strokeDasharray="3 3" />
                          <XAxis dataKey="month" />
                          <YAxis />
                          <Tooltip />
                          <Line type="monotone" dataKey="revenue" stroke="#8884d8" name="Umsatz" />
                          <Line type="monotone" dataKey="expenses" stroke="#82ca9d" name="Ausgaben" />
                        </RechartsLineChart>
                      </ResponsiveContainer>
                    </CardContent>
                  </Card>

                  <Card className="border-2 border-black">
                    <CardHeader>
                      <CardTitle>Quartalsvergleich</CardTitle>
                      <CardDescription>Finanzielle Entwicklung</CardDescription>
                    </CardHeader>
                    <CardContent className="h-[400px]">
                      <ResponsiveContainer width="100%" height="100%">
                        <RechartsBarChart data={financeData}>
                          <CartesianGrid strokeDasharray="3 3" />
                          <XAxis dataKey="month" />
                          <YAxis />
                          <Tooltip />
                          <Bar dataKey="revenue" fill="#8884d8" name="Umsatz" />
                        </RechartsBarChart>
                      </ResponsiveContainer>
                    </CardContent>
                  </Card>
                </div>
              </TabsContent>

              <TabsContent value="hr" className="space-y-4">
                <div className="grid gap-4 grid-cols-1 md:grid-cols-2">
                  <Card className="border-2 border-black">
                    <CardHeader>
                      <CardTitle>Mitarbeiterverteilung</CardTitle>
                      <CardDescription>Nach Abteilungen</CardDescription>
                    </CardHeader>
                    <CardContent className="h-[400px]">
                      <ResponsiveContainer width="100%" height="100%">
                        <RechartsBarChart data={hrData} layout="vertical">
                          <CartesianGrid strokeDasharray="3 3" />
                          <XAxis type="number" />
                          <YAxis dataKey="department" type="category" />
                          <Tooltip />
                          <Bar dataKey="employees" fill="#8884d8" name="Mitarbeiter" />
                        </RechartsBarChart>
                      </ResponsiveContainer>
                    </CardContent>
                  </Card>

                  <Card className="border-2 border-black">
                    <CardHeader>
                      <CardTitle>Fluktuation</CardTitle>
                      <CardDescription>Nach Abteilungen</CardDescription>
                    </CardHeader>
                    <CardContent className="h-[400px]">
                      <ResponsiveContainer width="100%" height="100%">
                        <RechartsLineChart data={hrData}>
                          <CartesianGrid strokeDasharray="3 3" />
                          <XAxis dataKey="department" />
                          <YAxis />
                          <Tooltip />
                          <Line type="monotone" dataKey="turnover" stroke="#82ca9d" name="Fluktuation" />
                        </RechartsLineChart>
                      </ResponsiveContainer>
                    </CardContent>
                  </Card>
                </div>
              </TabsContent>

              <TabsContent value="sales" className="space-y-4">
                <div className="grid gap-4 grid-cols-1 md:grid-cols-2">
                  <Card className="border-2 border-black">
                    <CardHeader>
                      <CardTitle>Verkaufsstatus</CardTitle>
                      <CardDescription>Verteilung der Verkaufschancen</CardDescription>
                    </CardHeader>
                    <CardContent className="h-[400px]">
                      <ResponsiveContainer width="100%" height="100%">
                        <RechartsPieChart>
                          <Pie
                            data={salesData}
                            cx="50%"
                            cy="50%"
                            labelLine={false}
                            label={({ name, percent }) => `${name} ${(percent * 100).toFixed(0)}%`}
                            outerRadius={150}
                            fill="#8884d8"
                            dataKey="value"
                          >
                            {salesData.map((entry, index) => (
                              <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                            ))}
                          </Pie>
                          <Tooltip />
                        </RechartsPieChart>
                      </ResponsiveContainer>
                    </CardContent>
                  </Card>

                  <Card className="border-2 border-black">
                    <CardHeader>
                      <CardTitle>Verkaufstrend</CardTitle>
                      <CardDescription>Monatliche Entwicklung</CardDescription>
                    </CardHeader>
                    <CardContent className="h-[400px]">
                      <ResponsiveContainer width="100%" height="100%">
                        <RechartsLineChart data={financeData}>
                          <CartesianGrid strokeDasharray="3 3" />
                          <XAxis dataKey="month" />
                          <YAxis />
                          <Tooltip />
                          <Line type="monotone" dataKey="revenue" stroke="#8884d8" name="Verkäufe" />
                        </RechartsLineChart>
                      </ResponsiveContainer>
                    </CardContent>
                  </Card>
                </div>
              </TabsContent>
            </Tabs>
          </div>
        </DashboardLayout>
    </div>
  );
}

export default withAuth(function DashboardPage() {
  return (
    <Suspense fallback={<div>Loading...</div>}>
      <SearchParamsComponent />
    </Suspense>
  );
})
