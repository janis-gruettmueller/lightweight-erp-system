import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Bookmark, ExternalLink } from "lucide-react"
import { Tender } from "@/types/tender"

interface TenderCardProps {
  tender: Tender;
  onBookmark: (tenderId: string) => void;
}

export function TenderCard({ tender, onBookmark }: TenderCardProps) {
  return (
    <Card className="hover:shadow-lg transition-shadow border border-black">
      <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
        <CardTitle className="text-lg">{tender.title}</CardTitle>
        <Button
          variant="ghost"
          size="icon"
          onClick={() => onBookmark(tender.id)}
          className={tender.isBookmarked ? "text-primary" : "text-muted-foreground"}
        >
          <Bookmark className="h-4 w-4" />
        </Button>
      </CardHeader>
      <CardContent>
        <div className="space-y-2">
          <p className="text-sm text-muted-foreground line-clamp-2">
            {tender.description}
          </p>
          <div className="flex items-center justify-between text-sm">
            <span className="text-muted-foreground">
              {tender.source}
            </span>
            <span>
              Frist: {new Date(tender.deadline).toLocaleDateString('de-DE')}
            </span>
          </div>
          {tender.category && (
            <div className="flex items-center space-x-2">
              <span className="text-xs bg-primary/10 text-primary px-2 py-1 rounded">
                {tender.category}
              </span>
              {tender.region && (
                <span className="text-xs bg-secondary/10 text-secondary px-2 py-1 rounded">
                  {tender.region}
                </span>
              )}
            </div>
          )}
          <div className="flex justify-end pt-2">
            <Button
              variant="outline"
              size="sm"
              asChild
            >
              <a href={tender.tenderUrl} target="_blank" rel="noopener noreferrer">
                <ExternalLink className="h-4 w-4 mr-2" />
                Details
              </a>
            </Button>
          </div>
        </div>
      </CardContent>
    </Card>
  )
} 