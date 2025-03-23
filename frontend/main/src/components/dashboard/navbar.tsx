"use client";

import { useState, useEffect } from "react";
import Link from "next/link";
import { usePathname, useSearchParams, useRouter } from "next/navigation";
import { BarChartIcon as ChartBarIcon, CreditCard, LogOut, Settings, User, Users, Loader2 } from "lucide-react";
import { cn } from "@/lib/utils";
import { Button } from "@/components/ui/button";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuGroup,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";

export default function Navbar() {
  const pathname = usePathname();
  const searchParams = useSearchParams();
  const router = useRouter();
  const [isFirstLoad, setIsFirstLoad] = useState(searchParams.get('fromLogin') === 'true');
  const [isLoggingOut, setIsLoggingOut] = useState(false);

  useEffect(() => {
    if (isFirstLoad) {
      const timer = setTimeout(() => {
        setIsFirstLoad(false);
      }, 200);
      return () => clearTimeout(timer);
    }
  }, [isFirstLoad]);

  const navItems = [
    { id: "overview", label: "Übersicht", icon: CreditCard, href: "/dashboard" },
    { id: "finance", label: "Finanzen", icon: CreditCard, href: "/finance" },
    { id: "hr", label: "Human Resources", icon: Users, href: "/hr" },
    { id: "sales", label: "Vertrieb", icon: ChartBarIcon, href: "/sales" },
  ];

  const handleLogout = async () => {
    try {
      setIsLoggingOut(true);

      // Always clear local storage first
      localStorage.removeItem('user');

      // Try to logout from backend
      await fetch('/api/auth/logout', {
        method: 'POST',
        credentials: 'include',
      });

      // Regardless of backend response, redirect to login
      router.push('/login');
    } catch (error) {
      console.error('Logout error:', error);
      // Even if there's an error, redirect to login
      router.push('/login');
    } finally {
      setIsLoggingOut(false);
    }
  };

  return (
    <header 
      className={`sticky top-0 z-50 w-full border-b shadow-sm bg-white ${
        isFirstLoad ? "bg-black transition-colors duration-1000" : ""
      }`}
    >
      <div className="container flex h-16 items-center justify-between px-4 mx-auto max-w-6xl">
        {/* Logo section */}
        <div className="mr-4 flex-shrink-0">
          <Link href="/dashboard" className="flex items-center space-x-2">
            <div className={`relative h-9 w-9 overflow-hidden rounded-full bg-gradient-to-br from-primary to-primary/60 ${
              isFirstLoad ? "bg-white/20" : ""
            }`}>
              <div className="absolute inset-0 flex items-center justify-center text-white font-bold text-lg">X</div>
            </div>
            <span className={`font-bold text-xl hidden sm:inline-block text-gray-900 ${
              isFirstLoad ? "text-white transition-colors duration-1000" : ""
            }`}>
              LeanX
            </span>
          </Link>
        </div>

        {/* Navigation items */}
        <div className="flex-1 flex justify-center">
          <nav className="flex items-center">
            <ul className="flex space-x-2 md:space-x-6">
              {navItems.map((item) => (
                <li key={item.id}>
                  <Link href={item.href}>
                    <Button
                      variant={pathname === item.href ? "default" : "ghost"}
                      className={cn(
                        "h-10 px-4 py-2 text-sm relative rounded-full",
                        pathname === item.href
                          ? "bg-primary/10 text-primary font-medium"
                          : isFirstLoad
                            ? "text-white/80 hover:bg-white/10"
                            : "text-gray-900 hover:bg-gray-100"
                      )}
                    >
                      <item.icon className="h-5 w-5 mr-2" />
                      <span className="md:inline-block">{item.label}</span>
                      {pathname === item.href && (
                        <span className={`absolute bottom-0 left-1/2 transform -translate-x-1/2 w-1/2 h-0.5 ${
                          isFirstLoad ? "bg-white" : "bg-primary"
                        }`} />
                      )}
                    </Button>
                  </Link>
                </li>
              ))}
            </ul>
          </nav>
        </div>

        {/* Profile menu */}
        <div className="ml-4 flex-shrink-0">
          <DropdownMenu>
            <DropdownMenuTrigger asChild>
              <Button variant="ghost" className="relative h-10 w-10 rounded-full">
                <Avatar className="h-9 w-9 border-2 border-primary/20">
                  <AvatarImage src="/placeholder.svg?height=36&width=36" alt="User" />
                  <AvatarFallback className="bg-muted text-gray-900">
                    NK
                  </AvatarFallback>
                </Avatar>
              </Button>
            </DropdownMenuTrigger>
            <DropdownMenuContent className="w-56" align="end" forceMount>
              <DropdownMenuLabel>
                <div className="flex flex-col space-y-1">
                  <p className="text-sm font-medium leading-none">Noah K</p>
                  <p className="text-xs leading-none text-muted-foreground">frontend@example.com</p>
                </div>
              </DropdownMenuLabel>
              <DropdownMenuSeparator />
              <DropdownMenuItem asChild>
                <Link href="/profile">
                  <User className="mr-2 h-4 w-4" />
                  <span>Profil</span>
                </Link>
              </DropdownMenuItem>
              <DropdownMenuSeparator />
              <DropdownMenuItem 
                onClick={handleLogout} 
                disabled={isLoggingOut}
                className="flex items-center cursor-pointer"
              >
                {isLoggingOut ? (
                  <>
                    <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                    <span>Abmelden...</span>
                  </>
                ) : (
                  <>
                    <LogOut className="mr-2 h-4 w-4" />
                    <span>Abmelden</span>
                  </>
                )}
              </DropdownMenuItem>
            </DropdownMenuContent>
          </DropdownMenu>
        </div>
      </div>

      {/* Separator */}
      <div className="h-1 w-full bg-gradient-to-r from-transparent via-primary/20 to-transparent" />
    </header>
  );
}
