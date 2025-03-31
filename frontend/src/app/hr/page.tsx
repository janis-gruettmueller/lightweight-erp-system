"use client"

import withAuth from '@/utils/withAuth';
import React, { useState, Suspense } from 'react'
import { 
  Users,
  UserPlus,
  GraduationCap,
  Calendar,
  Search,
  Plus,
  Clock,
  ChevronRight,
  Mail,
  UserCheck,
  Award,
  Building2
} from "lucide-react"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Button } from "@/components/ui/button"
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar"
import DashboardLayout from "@/components/dashboard/dashboard-layout"

export default withAuth(function HRPage() {
  const [searchQuery, setSearchQuery] = useState("")

  return (
    <Suspense fallback={<div>Loading...</div>}>
      <DashboardLayout>
        <div className="flex-1 space-y-8 p-4 md:p-8 pt-6">
          {/* Header */}
          <div className="flex items-center justify-between">
            <div>
              <h2 className="text-3xl font-bold tracking-tight">Human Resources</h2>
              <p className="text-muted-foreground">Personalverwaltung und Entwicklung</p>
            </div>
            <div className="flex items-center space-x-2">
              <div className="relative">
                <Search className="absolute left-2 top-2.5 h-4 w-4 text-muted-foreground" />
                <Input
                  placeholder="Mitarbeiter suchen..."
                  className="pl-8 w-[200px]"
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                />
              </div>
              <Button>
                <UserPlus className="mr-2 h-4 w-4" /> Mitarbeiter hinzufügen
              </Button>
            </div>
          </div>

          {/* Employee Overview Section */}
          <div className="space-y-4">
            <div className="border-b-2 border-black pb-2">
              <h3 className="text-2xl font-semibold flex items-center">
                <Users className="mr-2 h-5 w-5" />
                Mitarbeiterübersicht
              </h3>
            </div>
            
            <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
              <Card className="border-2 border-black">
                <CardHeader>
                  <CardTitle>Mitarbeiterstatistik</CardTitle>
                  <CardDescription>Aktuelle Zahlen</CardDescription>
                </CardHeader>
                <CardContent>
                  <div className="space-y-4">
                    <div className="flex items-center justify-between">
                      <div>
                        <p className="text-sm text-muted-foreground">Gesamt</p>
                        <p className="text-2xl font-bold">12</p>
                      </div>
                      <Users className="h-8 w-8 text-blue-500" />
                    </div>
                    <div className="grid grid-cols-2 gap-4">
                      <div>
                        <p className="text-sm text-muted-foreground">Vollzeit</p>
                        <p className="text-xl font-semibold">11</p>
                      </div>
                      <div>
                        <p className="text-sm text-muted-foreground">Teilzeit</p>
                        <p className="text-xl font-semibold">1</p>
                      </div>
                    </div>
                  </div>
                </CardContent>
              </Card>

              <Card className="border-2 border-black">
                <CardHeader>
                  <CardTitle>Abteilungsverteilung</CardTitle>
                  <CardDescription>Nach Bereichen</CardDescription>
                </CardHeader>
                <CardContent>
                  {[
                    { dept: 'Beratung', count: 10, percent: 80 },
                    { dept: 'Finanzen', count: 1, percent: 10 },
                    { dept: 'Vertrieb', count: 1, percent: 10 }
                  ].map((dept, i) => (
                    <div key={i} className="mb-4">
                      <div className="flex justify-between text-sm mb-1">
                        <span>{dept.dept}</span>
                        <span className="font-medium">{dept.count} MA</span>
                      </div>
                      <div className="h-2 bg-gray-100 rounded-full">
                        <div 
                          className="h-full bg-blue-500 rounded-full"
                          style={{ width: `${dept.percent}%` }}
                        />
                      </div>
                    </div>
                  ))}
                </CardContent>
              </Card>

              <Card className="border-2 border-black">
                <CardHeader>
                  <CardTitle>Neue Mitarbeiter</CardTitle>
                  <CardDescription>Letzte 30 Tage</CardDescription>
                </CardHeader>
                <CardContent>
                  <div className="space-y-4">
                    {[
                      { name: 'Max Mustermann', role: 'Student', dept: 'Vertrieb' },
                      { name: 'Anna Schmidt', role: 'Marketing Manager', dept: 'Marketing' },
                      { name: 'Sven Drescher', role: 'Praktikant', dept: 'Unterhaltung' }
                    ].map((emp, i) => (
                      <div key={i} className="flex items-center space-x-4 p-2 hover:bg-gray-50 rounded-md">
                        <Avatar>
                          <AvatarFallback>{emp.name.split(' ').map(n => n[0]).join('')}</AvatarFallback>
                        </Avatar>
                        <div className="flex-1">
                          <p className="font-medium">{emp.name}</p>
                          <p className="text-sm text-muted-foreground">{emp.role}</p>
                        </div>
                        <ChevronRight className="h-4 w-4 text-muted-foreground" />
                      </div>
                    ))}
                  </div>
                </CardContent>
              </Card>
            </div>
          </div>

          {/* Recruiting Section */}
          <div className="space-y-4">
            <div className="border-b-2 border-black pb-2">
              <h3 className="text-2xl font-semibold flex items-center">
                <UserPlus className="mr-2 h-5 w-5" />
                Recruiting
              </h3>
            </div>
            
            <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
              <Card className="border-2 border-black">
                <CardHeader>
                  <CardTitle>Offene Positionen</CardTitle>
                  <CardDescription>Aktuelle Ausschreibungen</CardDescription>
                </CardHeader>
                <CardContent>
                  <div className="space-y-4">
                    {[
                      { position: 'Senior Berater', applications: 12, status: 'Aktiv' },
                      { position: 'UX Designer', applications: 8, status: 'Aktiv' },
                      { position: 'Projekt Manager', applications: 15, status: 'Review' }
                    ].map((pos, i) => (
                      <div key={i} className="flex items-center justify-between p-2 hover:bg-gray-50 rounded-md">
                        <div>
                          <p className="font-medium">{pos.position}</p>
                          <p className="text-sm text-muted-foreground">{pos.applications} Bewerbungen</p>
                        </div>
                        <span className={`text-sm ${
                          pos.status === 'Aktiv' ? 'text-green-500' : 'text-orange-500'
                        }`}>
                          {pos.status}
                        </span>
                      </div>
                    ))}
                  </div>
                </CardContent>
              </Card>

              <Card className="border-2 border-black">
                <CardHeader>
                  <CardTitle>Bewerbungsprozess</CardTitle>
                  <CardDescription>Aktuelle Phase</CardDescription>
                </CardHeader>
                <CardContent>
                  <div className="space-y-4">
                    <div className="flex items-center justify-between">
                      <div>
                        <p className="text-sm text-muted-foreground">Neue Bewerbungen</p>
                        <p className="text-2xl font-bold">24</p>
                      </div>
                      <Mail className="h-8 w-8 text-blue-500" />
                    </div>
                    <div className="grid grid-cols-2 gap-4">
                      <div>
                        <p className="text-sm text-muted-foreground">Interviews</p>
                        <p className="text-xl font-semibold">8</p>
                      </div>
                      <div>
                        <p className="text-sm text-muted-foreground">Angebote</p>
                        <p className="text-xl font-semibold">3</p>
                      </div>
                    </div>
                  </div>
                </CardContent>
              </Card>
            </div>
          </div>

          {/* Training & Development Section */}
          <div className="space-y-4">
            <div className="border-b-2 border-black pb-2">
              <h3 className="text-2xl font-semibold flex items-center">
                <GraduationCap className="mr-2 h-5 w-5" />
                Weiterbildung
              </h3>
            </div>
            
            <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
              <Card className="border-2 border-black">
                <CardHeader>
                  <CardTitle>Aktuelle Kurse</CardTitle>
                  <CardDescription>Laufende Weiterbildungen</CardDescription>
                </CardHeader>
                <CardContent>
                  <div className="space-y-4">
                    {[
                      { course: 'Führungskräfte Training', participants: 1, progress: 5 },
                      { course: 'Agile Methodiken', participants: 8, progress: 70 },
                      { course: 'Datenschutz Basics', participants: 3, progress: 25 }
                    ].map((course, i) => (
                      <div key={i} className="space-y-2">
                        <div className="flex justify-between">
                          <p className="font-medium">{course.course}</p>
                          <span className="text-sm text-muted-foreground">
                            {course.participants} TN
                          </span>
                        </div>
                        <div className="h-2 bg-gray-100 rounded-full">
                          <div 
                            className="h-full bg-green-500 rounded-full"
                            style={{ width: `${course.progress}%` }}
                          />
                        </div>
                      </div>
                    ))}
                  </div>
                </CardContent>
              </Card>

              <Card className="border-2 border-black">
                <CardHeader>
                  <CardTitle>Zertifizierungen</CardTitle>
                  <CardDescription>Team Qualifikationen</CardDescription>
                </CardHeader>
                <CardContent>
                  <div className="space-y-4">
                    {[
                      { cert: 'Scrum Master', count: 10, target: 12 },
                      { cert: 'AWS Certified', count: 8, target: 8 },
                      { cert: 'ISO 27001', count: 2, target: 3 }
                    ].map((cert, i) => (
                      <div key={i} className="flex items-center justify-between p-2">
                        <div>
                          <p className="font-medium">{cert.cert}</p>
                          <p className="text-sm text-muted-foreground">
                            {cert.count}/{cert.target} zertifiziert
                          </p>
                        </div>
                        <Award className={`h-5 w-5 ${
                          cert.count >= cert.target ? 'text-green-500' : 'text-orange-500'
                        }`} />
                      </div>
                    ))}
                  </div>
                </CardContent>
              </Card>
            </div>
          </div>
        </div>
      </DashboardLayout>
    </Suspense>
  )
})