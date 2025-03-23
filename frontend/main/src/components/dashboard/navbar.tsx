"use client";

import { useState, useEffect } from "react";
import Link from "next/link";
import { BarChartIcon as ChartBarIcon, CreditCard, LogOut, Settings, User, Users } from "lucide-react";
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
  const [activeItem, setActiveItem] = useState("finance");
  const [loaded, setLoaded] = useState(false);

  useEffect(() => {
    setTimeout(() => setLoaded(true), 200); // Verzögerung für einen sanfteren Effekt
  }, []);

  const navItems = [
    { id: "overview", label: "Übersicht", icon: CreditCard },
    { id: "finance", label: "Finanzen", icon: CreditCard },
    { id: "hr", label: "Human Resources", icon: Users },
    { id: "sales", label: "Vertrieb", icon: ChartBarIcon },
  ];

  return (
    <header
      className={`sticky top-0 z-50 w-full border-b shadow-sm transition-colors duration-1000 ease-in-out ${
        loaded ? "bg-white" : "bg-black"
      }`}
    >
      <div className="container flex h-16 items-center justify-between px-4 mx-auto max-w-6xl">
        {/* Logo on the left */}
        <div className="mr-4 flex-shrink-0">
          <Link href="/dashboard" className="flex items-center space-x-2">
            <div
              className={`relative h-9 w-9 overflow-hidden rounded-full transition-colors duration-1000 ${
                loaded ? "bg-gradient-to-br from-primary to-primary/60" : "bg-white/20"
              }`}
            >
              <div className="absolute inset-0 flex items-center justify-center text-white font-bold text-lg">X</div>
            </div>
            <span
              className={`font-bold text-xl hidden sm:inline-block transition-colors duration-1000 ${
                loaded ? "text-gray-900" : "text-white"
              }`}
            >
              LeanX
            </span>
          </Link>
        </div>

        {/* Navigation items centered */}
        <div className="flex-1 flex justify-center">
          <nav className="flex items-center">
            <ul className="flex space-x-2 md:space-x-6">
              {navItems.map((item) => (
                <li key={item.id}>
                  <Button
                    variant={activeItem === item.id ? "default" : "ghost"}
                    className={cn(
                      "h-10 px-4 py-2 text-sm transition-all relative rounded-full",
                      activeItem === item.id
                        ? `${loaded ? "bg-primary/10 text-primary" : "bg-white/10 text-white"} font-medium`
                        : `${loaded ? "text-gray-900 hover:bg-gray-100" : "text-white/80 hover:bg-white/10"}`
                    )}
                    onClick={() => setActiveItem(item.id)}
                  >
                    <item.icon className="h-5 w-5 mr-2" />
                    <span className="md:inline-block">{item.label}</span>
                    {activeItem === item.id && (
                      <span
                        className={`absolute bottom-0 left-1/2 transform -translate-x-1/2 w-1/2 h-0.5 transition-colors duration-1000 ${
                          loaded ? "bg-primary" : "bg-white"
                        }`}
                      />
                    )}
                  </Button>
                </li>
              ))}
            </ul>
          </nav>
        </div>

        {/* Profile menu on the right */}
        <div className="ml-4 flex-shrink-0">
          <DropdownMenu>
            <DropdownMenuTrigger asChild>
              <Button variant="ghost" className="relative h-10 w-10 rounded-full">
                <Avatar
                  className={`h-9 w-9 transition-all duration-1000 ${
                    loaded ? "border-2 border-primary/20" : "border-2 border-white/20"
                  }`}
                >
                  <AvatarImage src="/placeholder.svg?height=36&width=36" alt="User" />
                  <AvatarFallback
                    className={`transition-colors duration-1000 ${
                      loaded ? "bg-muted text-gray-900" : "bg-white/10 text-white"
                    }`}
                  >
                    NK
                  </AvatarFallback>
                </Avatar>
              </Button>
            </DropdownMenuTrigger>
            <DropdownMenuContent className="w-56" align="end" forceMount>
              <DropdownMenuLabel>
                <div className="flex flex-col space-y-1">
                  <p className="text-sm font-medium leading-none">Noah K</p>
                  <p className="text-xs leading-none text-muted-foreground">FrontendMaster@example.com</p>
                </div>
              </DropdownMenuLabel>
              <DropdownMenuSeparator />
              <DropdownMenuGroup>
                <DropdownMenuItem>
                  <User className="mr-2 h-4 w-4" />
                  <span>Profil</span>
                </DropdownMenuItem>
                <DropdownMenuItem>
                  <Settings className="mr-2 h-4 w-4" />
                  <span>Einstellungen</span>
                </DropdownMenuItem>
              </DropdownMenuGroup>
              <DropdownMenuSeparator />
              <DropdownMenuItem>
                <LogOut className="mr-2 h-4 w-4" />
                <span>Log out</span>
              </DropdownMenuItem>
            </DropdownMenuContent>
          </DropdownMenu>
        </div>
      </div>

      {/* Creative separator between navbar and content */}
      <div
        className={`h-1 w-full transition-colors duration-1000 ${
          loaded ? "bg-gradient-to-r from-transparent via-primary/20 to-transparent" : "bg-gradient-to-r from-transparent via-white/10 to-transparent"
        }`}
      />
    </header>
  );
}
