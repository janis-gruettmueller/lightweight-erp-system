"use client"

import React, { Suspense, useEffect, useState } from "react"
import DashboardLayout from "@/components/dashboard/dashboard-layout"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Button } from "@/components/ui/button"
import { Search, Bell, Bookmark, Filter, ChevronLeft, ChevronRight, ChevronUp, ChevronDown } from "lucide-react"
import { TenderCard } from "@/components/tenders/tender-card"
import { Tender, TenderFilters } from "@/types/tender"
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select"

export default function SalesPage() {
  const [tenders, setTenders] = useState<Tender[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [filters, setFilters] = useState<TenderFilters>({})
  const [searchQuery, setSearchQuery] = useState("")
  const [sortBy, setSortBy] = useState("publication_date")
  const [sortOrder, setSortOrder] = useState<"asc" | "desc">("desc")
  const [page, setPage] = useState(1)
  const [totalPages, setTotalPages] = useState(1)
  const [totalTenders, setTotalTenders] = useState(0)
  const [availableCategories, setAvailableCategories] = useState<string[]>([])
  const [availableRegions, setAvailableRegions] = useState<string[]>([])

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
      if (filters.region) queryParams.append('region', filters.region)
      if (filters.status) queryParams.append('status', filters.status)
      queryParams.append('sortBy', sortBy)
      queryParams.append('sortOrder', sortOrder)
      queryParams.append('page', page.toString())
      queryParams.append('limit', '20') // Increased limit

      const response = await fetch(`/api/tenders?${queryParams.toString()}`)
      if (!response.ok) {
        throw new Error('Failed to fetch tenders')
      }
      const data = await response.json()
      setTenders(data.tenders || [])
      setTotalPages(data.totalPages)
      setTotalTenders(data.total)
      if (data.filters) {
        setAvailableCategories(data.filters.categories)
        setAvailableRegions(data.filters.regions)
      }
    } catch (error) {
      console.error('Error fetching tenders:', error)
      setError('Failed to load tenders. Please try again later.')
    } finally {
      setLoading(false)
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
            <Card>
              <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                <CardTitle className="text-sm font-medium">Neue Ausschreibungen</CardTitle>
              </CardHeader>
              <CardContent>
                <div className="text-2xl font-bold">
                  {tenders.filter(t => t.status === 'new').length}
                </div>
              </CardContent>
            </Card>
            <Card>
              <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                <CardTitle className="text-sm font-medium">Aktive Ausschreibungen</CardTitle>
              </CardHeader>
              <CardContent>
                <div className="text-2xl font-bold">
                  {tenders.filter(t => t.status === 'active').length}
                </div>
              </CardContent>
            </Card>
            <Card>
              <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                <CardTitle className="text-sm font-medium">Gesamtvolumen</CardTitle>
              </CardHeader>
              <CardContent>
                <div className="text-2xl font-bold">
                  {tenders.reduce((sum, t) => sum + (t.estimatedValue || 0), 0).toLocaleString()}€
                </div>
              </CardContent>
            </Card>
            <Card>
              <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                <CardTitle className="text-sm font-medium">Durchschnittlicher Wert</CardTitle>
              </CardHeader>
              <CardContent>
                <div className="text-2xl font-bold">
                  {Math.round(tenders.reduce((sum, t) => sum + (t.estimatedValue || 0), 0) / (tenders.length || 1)).toLocaleString()}€
                </div>
              </CardContent>
            </Card>
          </div>

          <form onSubmit={handleSearch} className="flex items-center space-x-2">
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
              <SelectTrigger className="w-[180px]">
                <SelectValue placeholder="Kategorie" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="all">Alle Kategorien</SelectItem>
                {availableCategories.map((category) => (
                  <SelectItem key={category} value={category}>
                    {category}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
            <Select
              value={filters.region || "all"}
              onValueChange={(value) => setFilters(prev => ({ ...prev, region: value === "all" ? undefined : value }))}
            >
              <SelectTrigger className="w-[180px]">
                <SelectValue placeholder="Region" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="all">Alle Regionen</SelectItem>
                {availableRegions.map((region) => (
                  <SelectItem key={region} value={region}>
                    {region}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
            <Select
              value={filters.status || "all"}
              onValueChange={(value) => setFilters(prev => ({ ...prev, status: value === "all" ? undefined : value as Tender['status'] }))}
            >
              <SelectTrigger className="w-[180px]">
                <SelectValue placeholder="Status" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="all">Alle Status</SelectItem>
                <SelectItem value="new">Neu</SelectItem>
                <SelectItem value="active">Aktiv</SelectItem>
                <SelectItem value="closed">Geschlossen</SelectItem>
              </SelectContent>
            </Select>
            <Select
              value={sortBy}
              onValueChange={(value) => setSortBy(value)}
            >
              <SelectTrigger className="w-[180px]">
                <SelectValue placeholder="Sortieren nach" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="publication_date">Veröffentlichungsdatum</SelectItem>
                <SelectItem value="deadline">Frist</SelectItem>
                <SelectItem value="estimated_value">Geschätzter Wert</SelectItem>
              </SelectContent>
            </Select>
            <Button
              variant="outline"
              size="icon"
              onClick={() => setSortOrder(prev => prev === 'asc' ? 'desc' : 'asc')}
            >
              {sortOrder === 'asc' ? <ChevronUp className="h-4 w-4" /> : <ChevronDown className="h-4 w-4" />}
            </Button>
            <Button type="submit">
              <Filter className="mr-2 h-4 w-4" />
              Filter
            </Button>
            {(filters.search || filters.category || filters.region || filters.status) && (
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
            <div className="bg-red-50 text-red-500 p-4 rounded-md">
              {error}
            </div>
          )}

          {loading ? (
            <div className="flex items-center justify-center h-64">
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

              <div className="flex items-center justify-between mt-4">
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
}
