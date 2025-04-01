"use client"

import withAuth from '@/utils/withAuth';
import React, { useEffect, useState, Suspense } from 'react'
import { useSearchParams } from 'next/navigation'
import { BarChart, LineChart, PieChart, CheckCircle2, AlertCircle, XCircle } from "lucide-react"
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
  Area,
  Legend
} from 'recharts'
import DashboardLayout from "@/components/dashboard/dashboard-layout"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { Progress } from "@/components/ui/progress"
import { Badge } from "@/components/ui/badge"

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
    { department: 'Beratung', salary: 65000, employees: 10 },
    { department: 'Marketing', salary: 55000, employees: 1 },
    { department: 'Vertrieb', salary: 60000, employees: 1 },
    { department: 'HR', salary: 52000, employees: 2 },
    { department: 'Finanzen', salary: 63000, employees: 1 },
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
    { month: 'Jan', revenue: 90000, expenses: 67123 },
    { month: 'Feb', revenue: 110000, expenses: 72123 },
    { month: 'Mar', revenue: 85000, expenses: 88000 },
    { month: 'Apr', revenue: 130000, expenses: 55000 },
    { month: 'May', revenue: 125000, expenses: 66000 },
    { month: 'Jun', revenue: 154000, expenses: 80000 },
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
                      <div className="text-2xl font-bold">155,231.89€</div>
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
                      <div className="text-2xl font-bold">15</div>
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

                <Card className="border-2 border-black">
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

                <div className="space-y-4">
                  <h2 className="text-2xl font-bold tracking-tight">Business Intelligence</h2>
                  
                  <Card className="border-2 border-black">
                    <CardHeader>
                      <CardTitle>Cloud Migration GmbH - Projektübersicht</CardTitle>
                      <CardDescription>Detaillierte Analyse des aktuellen Projekts</CardDescription>
                    </CardHeader>
                    <CardContent>
                      <div className="grid gap-8 md:grid-cols-2">
                        <div className="space-y-8">
                          <div>
                            <h3 className="text-lg font-semibold mb-4">Finanzielle Kennzahlen</h3>
                            <div className="grid grid-cols-2 gap-4">
                              <div className="space-y-2 p-4 border rounded-lg bg-green-50">
                                <div className="text-sm font-medium text-muted-foreground">Gesamtumsatz</div>
                                <div className="text-3xl font-bold text-green-600">150,000€</div>
                              </div>
                              <div className="space-y-2 p-4 border rounded-lg bg-blue-50">
                                <div className="text-sm font-medium text-muted-foreground">Gewinn</div>
                                <div className="text-3xl font-bold text-blue-600">45,000€</div>
                                <div className="text-sm font-medium text-blue-600">30% Marge</div>
                              </div>
                            </div>
                          </div>

                          <div>
                            <h3 className="text-lg font-semibold mb-4">Projektstatus</h3>
                            <div className="space-y-4">
                              <div className="space-y-2">
                                <div className="flex justify-between items-center">
                                  <span className="text-sm font-medium">Gewinnwahrscheinlichkeit</span>
                                  <span className="text-2xl font-bold text-green-600">92%</span>
                                </div>
                                <Progress value={92} className="w-full h-4 bg-gray-200" />
                              </div>
                            </div>
                          </div>

                          <div>
                            <h3 className="text-lg font-semibold mb-4">Ressourcenverteilung</h3>
                            <div className="h-[200px]">
                              <ResponsiveContainer width="100%" height="100%">
                                <RechartsPieChart>
                                  <Pie
                                    data={[
                                      { name: 'Cloud Engineering', value: 40 },
                                      { name: 'DevOps', value: 30 },
                                      { name: 'Security', value: 30 }
                                    ]}
                                    cx="50%"
                                    cy="50%"
                                    innerRadius={60}
                                    outerRadius={80}
                                    fill="#8884d8"
                                    dataKey="value"
                                  >
                                    {COLORS.map((color, index) => (
                                      <Cell key={`cell-${index}`} fill={color} />
                                    ))}
                                  </Pie>
                                  <Tooltip />
                                </RechartsPieChart>
                              </ResponsiveContainer>
                            </div>
                          </div>
                        </div>

                        <div className="space-y-8">
                          <div>
                            <h3 className="text-lg font-semibold mb-4">Projektteam</h3>
                            <div className="space-y-4">
                              {[
                                {
                                  name: "Dr. Sarah Weber",
                                  role: "Projektleiter",
                                  certifications: ["PMP", "AWS Solutions Architect"],
                                  status: "compliant",
                                  dailyRate: "1,500€"
                                },
                                {
                                  name: "Michael Schmidt",
                                  role: "Cloud Engineer",
                                  certifications: ["AWS Certified Developer", "Azure Administrator"],
                                  status: "compliant",
                                  dailyRate: "1,200€"
                                },
                                {
                                  name: "Lisa Müller",
                                  role: "DevOps Engineer",
                                  certifications: ["Kubernetes Administrator"],
                                  status: "warning",
                                  dailyRate: "1,100€",
                                  note: "21% unter Standard-Tagessatz"
                                },
                                {
                                  name: "Thomas Wagner",
                                  role: "Security Engineer",
                                  certifications: ["CISSP"],
                                  status: "non-compliant",
                                  dailyRate: "1,300€",
                                  note: "CISSP Zertifikat fehlt"
                                }
                              ].map((employee) => (
                                <div key={employee.name} className="flex items-center justify-between p-3 border rounded-lg">
                                  <div className="space-y-1">
                                    <div className="flex items-center space-x-2">
                                      <span className="font-medium">{employee.name}</span>
                                      <Badge variant="outline">{employee.role}</Badge>
                                    </div>
                                    <div className="flex items-center space-x-2">
                                      {employee.certifications.map((cert) => (
                                        <Badge key={cert} variant="secondary">{cert}</Badge>
                                      ))}
                                    </div>
                                    {employee.note && (
                                      <div className="flex items-center gap-2 mt-2 text-xs">
                                        {employee.status === "warning" ? (
                                          <AlertCircle className="h-4 w-4 text-yellow-500" />
                                        ) : (
                                          <XCircle className="h-4 w-4 text-red-500" />
                                        )}
                                        <span className={`${
                                          employee.status === "warning" ? "text-yellow-600" : "text-red-600"
                                        } font-medium`}>
                                          {employee.note}
                                        </span>
                                      </div>
                                    )}
                                  </div>
                                  <div className="flex items-center space-x-4">
                                    <span className="text-sm font-medium">{employee.dailyRate}</span>
                                    {employee.status === "compliant" && (
                                      <CheckCircle2 className="h-5 w-5 text-green-500" />
                                    )}
                                    {employee.status === "warning" && (
                                      <AlertCircle className="h-5 w-5 text-yellow-500" />
                                    )}
                                    {employee.status === "non-compliant" && (
                                      <XCircle className="h-5 w-5 text-red-500" />
                                    )}
                                  </div>
                                </div>
                              ))}
                            </div>
                          </div>

                          <div>
                            <h3 className="text-lg font-semibold mb-4">Projektkosten</h3>
                            <div className="space-y-4 p-4 border rounded-lg">
                              <div className="flex justify-between items-center">
                                <span className="text-sm font-medium">Projektlaufzeit</span>
                                <span className="text-sm">6 Monate</span>
                              </div>
                              <div className="flex justify-between items-center">
                                <span className="text-sm font-medium">Teamgröße</span>
                                <span className="text-sm">4 Mitarbeiter</span>
                              </div>
                              <div className="flex justify-between items-center">
                                <span className="text-sm font-medium">Gesamtkosten</span>
                                <span className="text-sm font-bold">105,000€</span>
                              </div>
                              <div className="flex justify-between items-center">
                                <span className="text-sm font-medium">Durchschnittliche Auslastung</span>
                                <span className="text-sm">85%</span>
                              </div>
                            </div>
                          </div>
                        </div>
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
                      <CardTitle>Projekt Metriken</CardTitle>
                      <CardDescription>Durchschnittliche Projektgrößen und -dauern</CardDescription>
                    </CardHeader>
                    <CardContent>
                      <div className="grid gap-8 md:grid-cols-2">
                        <div className="space-y-8">
                          <div>
                            <h3 className="text-lg font-semibold mb-4">Projektgrößen</h3>
                            <div className="grid grid-cols-2 gap-4">
                              <div className="space-y-2 p-4 border rounded-lg">
                                <div className="text-sm font-medium text-muted-foreground">Durchschnittliches Budget</div>
                                <div className="text-2xl font-bold">285,000€</div>
                                <div className="text-xs text-muted-foreground">Pro Projekt</div>
                              </div>
                              <div className="space-y-2 p-4 border rounded-lg">
                                <div className="text-sm font-medium text-muted-foreground">Durchschnittliche Dauer</div>
                                <div className="text-2xl font-bold">8.5 Monate</div>
                                <div className="text-xs text-muted-foreground">Pro Projekt</div>
                              </div>
                            </div>
                          </div>

                          <div>
                            <h3 className="text-lg font-semibold mb-4">Budgetentwicklung</h3>
                            <div className="h-[200px]">
                              <ResponsiveContainer width="100%" height="100%">
                                <RechartsLineChart data={[
                                  { year: '2019', budget: 220000, duration: 7.2, teamSize: 3.5 },
                                  { year: '2020', budget: 245000, duration: 7.8, teamSize: 4.0 },
                                  { year: '2021', budget: 265000, duration: 8.2, teamSize: 4.2 },
                                  { year: '2022', budget: 285000, duration: 8.5, teamSize: 4.5 },
                                  { year: '2023', budget: 310000, duration: 9.0, teamSize: 5.0 }
                                ]}>
                                  <CartesianGrid strokeDasharray="3 3" />
                                  <XAxis dataKey="year" />
                                  <YAxis 
                                    yAxisId="left"
                                    tickFormatter={(value) => `${(value / 1000).toFixed(0)}k€`}
                                  />
                                  <YAxis 
                                    yAxisId="right"
                                    orientation="right"
                                    tickFormatter={(value) => `${value} Monate`}
                                  />
                                  <Tooltip 
                                    formatter={(value: number, name: string) => {
                                      if (name === 'Budget') return `${(value / 1000).toFixed(0)}k€`;
                                      if (name === 'Dauer') return `${value} Monate`;
                                      return value;
                                    }}
                                  />
                                  <Line 
                                    yAxisId="left"
                                    type="monotone" 
                                    dataKey="budget" 
                                    stroke="#0088FE" 
                                    name="Budget"
                                    strokeWidth={2}
                                  />
                                  <Line 
                                    yAxisId="right"
                                    type="monotone" 
                                    dataKey="duration" 
                                    stroke="#00C49F" 
                                    name="Dauer"
                                    strokeWidth={2}
                                  />
                                </RechartsLineChart>
                              </ResponsiveContainer>
                            </div>
                          </div>
                        </div>

                        <div className="space-y-8">
                          <div>
                            <h3 className="text-lg font-semibold mb-4">Teamgrößen</h3>
                            <div className="h-[200px]">
                              <ResponsiveContainer width="100%" height="100%">
                                <RechartsBarChart data={[
                                  { year: '2019', teamSize: 3.5 },
                                  { year: '2020', teamSize: 4.0 },
                                  { year: '2021', teamSize: 4.2 },
                                  { year: '2022', teamSize: 4.5 },
                                  { year: '2023', teamSize: 5.0 }
                                ]}>
                                  <CartesianGrid strokeDasharray="3 3" />
                                  <XAxis dataKey="year" />
                                  <YAxis />
                                  <Tooltip />
                                  <Bar 
                                    dataKey="teamSize" 
                                    fill="#FFBB28" 
                                    name="Teamgröße"
                                    radius={[4, 4, 0, 0]}
                                  />
                                </RechartsBarChart>
                              </ResponsiveContainer>
                            </div>
                          </div>

                          <div>
                            <h3 className="text-lg font-semibold mb-4">Projektverteilung</h3>
                            <div className="h-[200px]">
                              <ResponsiveContainer width="100%" height="100%">
                                <RechartsPieChart>
                                  <Pie
                                    data={[
                                      { name: 'Kleine Projekte', value: 35, description: '< 100k€' },
                                      { name: 'Mittlere Projekte', value: 45, description: '100k€ - 500k€' },
                                      { name: 'Große Projekte', value: 20, description: '> 500k€' }
                                    ]}
                                    cx="50%"
                                    cy="50%"
                                    innerRadius={60}
                                    outerRadius={80}
                                    fill="#8884d8"
                                    dataKey="value"
                                  >
                                    {COLORS.map((color, index) => (
                                      <Cell key={`cell-${index}`} fill={color} />
                                    ))}
                                  </Pie>
                                  <Tooltip 
                                    formatter={(value: number, name: string, props: any) => [
                                      `${value}%`,
                                      `${name} (${props.payload.description})`
                                    ]}
                                  />
                                </RechartsPieChart>
                              </ResponsiveContainer>
                            </div>
                          </div>
                        </div>
                      </div>
                    </CardContent>
                  </Card>

                  <Card className="border-2 border-black">
                    <CardHeader>
                      <CardTitle>Projekt Effizienz</CardTitle>
                      <CardDescription>Kennzahlen zur Projekteffizienz</CardDescription>
                    </CardHeader>
                    <CardContent>
                      <div className="grid gap-4">
                        <div className="space-y-4">
                          <div className="flex justify-between items-center">
                            <span className="text-sm font-medium">Durchschnittliche Auslastung</span>
                            <span className="text-sm font-bold">92%</span>
                          </div>
                          <Progress value={92} className="w-full" />
                        </div>
                        <div className="space-y-4">
                          <div className="flex justify-between items-center">
                            <span className="text-sm font-medium">Durchschnittliche Marge</span>
                            <span className="text-sm font-bold">28%</span>
                          </div>
                          <Progress value={28} className="w-full" />
                        </div>
                        <div className="space-y-4">
                          <div className="flex justify-between items-center">
                            <span className="text-sm font-medium">Durchschnittliche Kundenbewertung</span>
                            <span className="text-sm font-bold">4.8/5.0</span>
                          </div>
                          <Progress value={96} className="w-full" />
                        </div>
                      </div>
                    </CardContent>
                  </Card>
                </div>

                <Card className="border-2 border-black">
                  <CardHeader>
                    <CardTitle>Vergabeportale Vergleich</CardTitle>
                    <CardDescription>Verteilung der Cloud-Ausschreibungen nach Portalen</CardDescription>
                  </CardHeader>
                  <CardContent className="h-[400px]">
                    <ResponsiveContainer width="100%" height="100%">
                      <RechartsBarChart data={[
                        {
                          year: '2019',
                          'E-Vergabe': 15,
                          'IT.NRW': 8,
                          'Vergabe.NRW': 6,
                          'Bund.de': 10,
                          'Bayern-Portal': 4,
                          'Hamburg.de': 3,
                          'Berlin.de': 5,
                          'Baden-Württemberg': 4
                        },
                        {
                          year: '2020',
                          'E-Vergabe': 18,
                          'IT.NRW': 10,
                          'Vergabe.NRW': 8,
                          'Bund.de': 12,
                          'Bayern-Portal': 6,
                          'Hamburg.de': 4,
                          'Berlin.de': 6,
                          'Baden-Württemberg': 5
                        },
                        {
                          year: '2021',
                          'E-Vergabe': 22,
                          'IT.NRW': 12,
                          'Vergabe.NRW': 10,
                          'Bund.de': 15,
                          'Bayern-Portal': 8,
                          'Hamburg.de': 5,
                          'Berlin.de': 7,
                          'Baden-Württemberg': 6
                        },
                        {
                          year: '2022',
                          'E-Vergabe': 25,
                          'IT.NRW': 15,
                          'Vergabe.NRW': 12,
                          'Bund.de': 18,
                          'Bayern-Portal': 10,
                          'Hamburg.de': 6,
                          'Berlin.de': 8,
                          'Baden-Württemberg': 7
                        },
                        {
                          year: '2023',
                          'E-Vergabe': 30,
                          'IT.NRW': 18,
                          'Vergabe.NRW': 15,
                          'Bund.de': 22,
                          'Bayern-Portal': 12,
                          'Hamburg.de': 7,
                          'Berlin.de': 10,
                          'Baden-Württemberg': 8
                        }
                      ]}>
                        <CartesianGrid strokeDasharray="3 3" />
                        <XAxis dataKey="year" />
                        <YAxis />
                        <Tooltip />
                        <Legend />
                        <Bar dataKey="E-Vergabe" stackId="a" fill="#0088FE" name="E-Vergabe" />
                        <Bar dataKey="IT.NRW" stackId="a" fill="#00C49F" name="IT.NRW" />
                        <Bar dataKey="Vergabe.NRW" stackId="a" fill="#FFBB28" name="Vergabe.NRW" />
                        <Bar dataKey="Bund.de" stackId="a" fill="#FF8042" name="Bund.de" />
                        <Bar dataKey="Bayern-Portal" stackId="a" fill="#8884d8" name="Bayern-Portal" />
                        <Bar dataKey="Hamburg.de" stackId="a" fill="#82ca9d" name="Hamburg.de" />
                        <Bar dataKey="Berlin.de" stackId="a" fill="#ffc658" name="Berlin.de" />
                        <Bar dataKey="Baden-Württemberg" stackId="a" fill="#ff7c43" name="Baden-Württemberg" />
                      </RechartsBarChart>
                    </ResponsiveContainer>
                  </CardContent>
                </Card>

               

                <Card className="border-2 border-black">
                  <CardHeader>
                    <CardTitle>Erfolgsrate bei Ausschreibungen</CardTitle>
                    <CardDescription>Analyse der gewonnenen vs. verlorenen Ausschreibungen</CardDescription>
                  </CardHeader>
                  <CardContent>
                    <div className="grid gap-8 md:grid-cols-2">
                      <div className="space-y-8">
                        <div>
                          <h3 className="text-lg font-semibold mb-4">Gesamtübersicht</h3>
                          <div className="grid grid-cols-2 gap-4">
                            <div className="space-y-2 p-4 border rounded-lg">
                              <div className="text-sm font-medium text-muted-foreground">Gesamtanzahl</div>
                              <div className="text-2xl font-bold">35</div>
                            </div>
                            <div className="space-y-2 p-4 border rounded-lg">
                              <div className="text-sm font-medium text-muted-foreground">Erfolgsrate</div>
                              <div className="text-2xl font-bold">68%</div>
                              <div className="text-xs text-muted-foreground">21 gewonnen</div>
                            </div>
                          </div>
                        </div>

                        <div>
                          <h3 className="text-lg font-semibold mb-4">Erfolgsrate nach Jahr</h3>
                          <div className="h-[200px]">
                            <ResponsiveContainer width="100%" height="100%">
                              <RechartsLineChart data={[
                                { year: '2019', success: 65, total: 45 },
                                { year: '2020', success: 68, total: 62 },
                                { year: '2021', success: 72, total: 78 },
                                { year: '2022', success: 70, total: 95 },
                                { year: '2023', success: 68, total: 120 }
                              ]}>
                                <CartesianGrid strokeDasharray="3 3" />
                                <XAxis dataKey="year" />
                                <YAxis />
                                <Tooltip />
                                <Line 
                                  type="monotone" 
                                  dataKey="success" 
                                  stroke="#00C49F" 
                                  name="Gewonnene Ausschreibungen"
                                  strokeWidth={2}
                                />
                                <Line 
                                  type="monotone" 
                                  dataKey="total" 
                                  stroke="#FF8042" 
                                  name="Gesamt Ausschreibungen"
                                  strokeWidth={2}
                                />
                              </RechartsLineChart>
                            </ResponsiveContainer>
                          </div>
                        </div>
                      </div>

                      <div className="space-y-8">
                        <div>
                          <h3 className="text-lg font-semibold mb-4">Erfolgsrate nach Portal</h3>
                          <div className="h-[200px]">
                            <ResponsiveContainer width="100%" height="100%">
                              <RechartsBarChart data={[
                                { portal: 'E-Vergabe', success: 5, total: 10 },
                                { portal: 'IT.NRW', success: 0, total: 5 },
                                { portal: 'Bund.de', success: 12, total: 12 },
                                { portal: 'Bayern-Portal', success: 0, total: 0 },
                                { portal: 'Hamburg.de', success: 1, total: 1 },
                                { portal: 'Berlin.de', success: 3, total: 7 }
                              ]}>
                                <CartesianGrid strokeDasharray="3 3" />
                                <XAxis dataKey="portal" />
                                <YAxis />
                                <Tooltip />
                                <Bar dataKey="success" fill="#00C49F" name="Gewonnene" />
                                <Bar dataKey="total" fill="#FF8042" name="Gesamt" />
                              </RechartsBarChart>
                            </ResponsiveContainer>
                          </div>
                        </div>

                        <div>
                          <h3 className="text-lg font-semibold mb-4">Erfolgsrate nach Volumen</h3>
                          <div className="h-[200px]">
                            <ResponsiveContainer width="100%" height="100%">
                              <RechartsPieChart>
                                <Pie
                                  data={[
                                    { name: '0-50k€', value: 75, total: 40 },
                                    { name: '50k-200k€', value: 70, total: 45 },
                                    { name: '200k-500k€', value: 65, total: 35 },
                                    { name: '500k€+', value: 60, total: 20 }
                                  ]}
                                  cx="50%"
                                  cy="50%"
                                  innerRadius={60}
                                  outerRadius={80}
                                  fill="#8884d8"
                                  dataKey="value"
                                >
                                  {COLORS.map((color, index) => (
                                    <Cell key={`cell-${index}`} fill={color} />
                                  ))}
                                </Pie>
                                <Tooltip />
                              </RechartsPieChart>
                            </ResponsiveContainer>
                          </div>
                        </div>
                      </div>
                    </div>
                  </CardContent>
                </Card>
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