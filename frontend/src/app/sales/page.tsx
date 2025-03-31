"use client"

import withAuth from '@/utils/withAuth';
import React, { Suspense, useEffect, useState } from "react"
import DashboardLayout from "@/components/dashboard/dashboard-layout"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Button } from "@/components/ui/button"
import { Search, Bell, Bookmark, Filter, ChevronLeft, ChevronRight, ChevronUp, ChevronDown, RefreshCw } from "lucide-react"
import { TenderCard } from "@/components/tenders/tender-card"
import { Tender, TenderFilters } from "@/types/tender"
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select"

export default withAuth(function SalesPage() {
  const [tenders, setTenders] = useState<Tender[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [filters, setFilters] = useState<TenderFilters>({})
  const [searchQuery, setSearchQuery] = useState("")
  const [sortBy, setSortBy] = useState("deadline")
  const [sortOrder, setSortOrder] = useState<"asc" | "desc">("asc")
  const [page, setPage] = useState(1)
  const [totalPages, setTotalPages] = useState(1)
  const [totalTenders, setTotalTenders] = useState(0)
  const [totalITTenders, setTotalITTenders] = useState(0)
  const [availableCategories, setAvailableCategories] = useState<string[]>([])
  const [isSyncing, setIsSyncing] = useState(false)

  // Update default sort value to use deadline
  const defaultSortValue = "deadline_asc"

  useEffect(() => {
    fetchTenders()
  }, [filters, sortBy, sortOrder, page])

  const fetchTenders = async () => {
    try {
      setLoading(true)
      setError(null)
      const queryParams = new URLSearchParams()
      if (filters.search) queryParams.append('search', filters.search)
      if (filters.category) queryParams.append('category', filters.category)
      queryParams.append('sortBy', sortBy || 'deadline')
      queryParams.append('sortOrder', sortOrder || 'asc')
      queryParams.append('page', page.toString())
      queryParams.append('limit', '20')

      const response = await fetch(`/api/tenders?${queryParams.toString()}`)
      if (!response.ok) {
        const errorData = await response.json()
        throw new Error(errorData.error || 'Failed to fetch tenders')
      }
      const data = await response.json()
      setTenders(data.tenders || [])
      setTotalPages(data.totalPages)
      setTotalTenders(data.total)
      setTotalITTenders(data.totalITTenders || 0)
      if (data.filters) {
        setAvailableCategories(data.filters.categories)
      }
    } catch (error) {
      console.error('Error fetching tenders:', error)
      setError('Failed to load tenders. Please try again later.')
    } finally {
      setLoading(false)
    }
  }

  const handleSync = async () => {
    try {
      setIsSyncing(true)
      await fetchTenders()
    } finally {
      setIsSyncing(false)
    }
  }

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault()
    setFilters(prev => ({ ...prev, search: searchQuery }))
    setPage(1)
  }

  const handleBookmark = async (tenderId: string) => {
    // TODO: Implement bookmark functionality
    console.log('Bookmarking tender:', tenderId)
  }

  const clearFilters = () => {
    setFilters({})
    setSearchQuery("")
    setPage(1)
  }

  return (
    <Suspense fallback={<div>Loading...</div>}>
      <DashboardLayout>
        <div className="flex-1 space-y-4 p-4 md:p-8 pt-6">
          <div className="flex items-center justify-between">
            <h2 className="text-3xl font-bold tracking-tight">Ausschreibungs-Dashboard</h2>
            <div className="flex items-center space-x-2">
              <Button variant="outline" size="icon">
                <Bell className="h-4 w-4" />
              </Button>
              <Button variant="outline" size="icon">
                <Bookmark className="h-4 w-4" />
              </Button>
            </div>
          </div>

          <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
            <Card className="border border-black">
              <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                <CardTitle className="text-sm font-medium">Gesamtausschreibungen</CardTitle>
              </CardHeader>
              <CardContent>
                <div className="text-2xl font-bold">
                  {totalTenders}
                </div>
              </CardContent>
            </Card>
            <Card className="border border-black">
              <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                <CardTitle className="text-sm font-medium">Neue Ausschreibungen</CardTitle>
              </CardHeader>
              <CardContent>
                <div className="text-2xl font-bold">
                  {tenders.filter(t => t.status === 'new').length}
                </div>
              </CardContent>
            </Card>
            <Card className="border border-black">
              <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                <CardTitle className="text-sm font-medium">IT-Ausschreibungen</CardTitle>
              </CardHeader>
              <CardContent>
                <div className="text-2xl font-bold">
                  {totalITTenders}
                </div>
              </CardContent>
            </Card>
            <Card className="border border-black bg-gray-50">
              <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                <CardTitle className="text-sm font-medium text-gray-600">Bestandskunden Ausschreibungen</CardTitle>
              </CardHeader>
              <CardContent>
                <div className="text-2xl font-bold text-gray-700">
                  4
                </div>
                
              </CardContent>
            </Card>
          </div>

          <form onSubmit={handleSearch} className="flex items-center space-x-2 border border-black p-4 rounded-lg">
            <div className="relative flex-1">
              <Search className="absolute left-2 top-2.5 h-4 w-4 text-muted-foreground" />
              <Input 
                placeholder="Ausschreibungen durchsuchen..." 
                className="pl-8"
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
              />
            </div>
            <Select
              value={filters.category || "all"}
              onValueChange={(value) => setFilters(prev => ({ ...prev, category: value === "all" ? undefined : value }))}
            >
              <SelectTrigger className="min-w-[180px]">
                <div className="flex flex-col items-start">
                  <span className="text-xs text-muted-foreground">Kategorien</span>
                  <SelectValue defaultValue="all">Alle Kategorien</SelectValue>
                </div>
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="all">Alle Kategorien</SelectItem>
                <SelectItem value="bauarbeiten">Bauarbeiten</SelectItem>
                <SelectItem value="dienstleistungen">Dienstleistungen</SelectItem>
                <SelectItem value="facility_management">Facility Management</SelectItem>
                <SelectItem value="infrastruktur">Infrastruktur</SelectItem>
                <SelectItem value="it">IT</SelectItem>
                <SelectItem value="it_digitalisierung">IT & Digitalisierung</SelectItem>
                <SelectItem value="lieferungen">Lieferungen</SelectItem>
                <SelectItem value="planung_beratung">Planung & Beratung</SelectItem>
              </SelectContent>
            </Select>
            <Select
              value={`${sortBy}_${sortOrder}`}
              onValueChange={(value) => {
                const [field, order] = value.split('_')
                setSortBy(field)
                setSortOrder(order as 'asc' | 'desc')
              }}
              defaultValue={defaultSortValue}
            >
              <SelectTrigger className="min-w-[180px]">
                <div className="flex flex-col items-start">
                  <span className="text-xs text-muted-foreground">Sortierung</span>
                  <SelectValue>Sortieren</SelectValue>
                </div>
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="deadline_asc">Frist (Nächste)</SelectItem>
                <SelectItem value="deadline_desc">Frist (Späteste)</SelectItem>
                <SelectItem value="estimated_value_desc" disabled className="text-gray-400 cursor-not-allowed">
                  Geschätzter Wert (Höchste) 
                </SelectItem>
                <SelectItem value="estimated_value_asc" disabled className="text-gray-400 cursor-not-allowed">
                  Geschätzter Wert (Niedrigste) 
                </SelectItem>
              </SelectContent>
            </Select>
            <Button 
              type="button" 
              className="bg-black text-white"
              onClick={handleSync}
              disabled={isSyncing}
            >
              <RefreshCw className={`mr-2 h-4 w-4 ${isSyncing ? 'animate-spin' : ''}`} />
              {isSyncing ? 'Synchronisiere...' : 'Synchronisieren'}
            </Button>
            {(filters.search || filters.category) && (
              <Button
                type="button"
                variant="outline"
                onClick={clearFilters}
              >
                Filter zurücksetzen
              </Button>
            )}
          </form>

          {error && (
            <div className="bg-red-50 text-red-500 p-4 rounded-md border border-black">
              {error}
            </div>
          )}

          {loading ? (
            <div className="flex items-center justify-center h-64 border border-black rounded-lg">
              <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary"></div>
            </div>
          ) : (
            <>
              <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
                {tenders.map((tender) => (
                  <TenderCard
                    key={tender.id}
                    tender={tender}
                    onBookmark={handleBookmark}
                  />
                ))}
              </div>

              <div className="flex items-center justify-between mt-4 border border-black p-4 rounded-lg">
                <div className="text-sm text-muted-foreground">
                  Zeige {tenders.length} von {totalTenders} Ausschreibungen
                </div>
                <div className="flex items-center space-x-2">
                  <Button
                    variant="outline"
                    size="sm"
                    onClick={() => setPage(prev => Math.max(1, prev - 1))}
                    disabled={page === 1}
                  >
                    <ChevronLeft className="h-4 w-4" />
                  </Button>
                  <div className="text-sm">
                    Seite {page} von {totalPages}
                  </div>
                  <Button
                    variant="outline"
                    size="sm"
                    onClick={() => setPage(prev => Math.min(totalPages, prev + 1))}
                    disabled={page === totalPages}
                  >
                    <ChevronRight className="h-4 w-4" />
                  </Button>
                </div>
              </div>
            </>
          )}
        </div>
      </DashboardLayout>
    </Suspense>
  )
})
