"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";

export default function ChangePasswordPage() {
  const [oldPassword, setOldPassword] = useState("");
  const [newPassword, setNewPassword] = useState("");
  const [error, setError] = useState<string | null>(null);
  const router = useRouter();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);

    try {
      const response = await fetch("/api/auth/change-password", {
        method: "POST",
        headers: { "Content-Type": "application/x-www-form-urlencoded" },
        credentials: 'include',
        body: new URLSearchParams({ 
          oldPassword, 
          newPassword 
        }).toString(),
      });

      const data = await response.json();
      
      if (response.ok) {
        router.push("/dashboard");
      } else {
        setError(data.error || "Passwortänderung fehlgeschlagen.");
      }
    } catch (error) {
      setError("Netzwerkfehler. Bitte versuche es erneut.");
    }
  };

  return (
    <div className="flex min-h-screen flex-col bg-black text-white">
      <div className="flex flex-1 flex-col items-center justify-center px-6">
        <div className="w-full max-w-sm">
          <h2 className="mb-8 text-2xl font-medium">Passwort ändern</h2>
          
          {error && (
            <div className="mb-6 rounded-md bg-red-500/10 p-4 text-sm text-red-500">
              {error}
            </div>
          )}

          <form onSubmit={handleSubmit} className="space-y-6">
            <div className="space-y-2">
              <Label htmlFor="oldPassword">Altes Passwort</Label>
              <Input
                id="oldPassword"
                type="password"
                value={oldPassword}
                onChange={(e) => setOldPassword(e.target.value)}
                className="border-0 bg-white/5 text-white focus-visible:ring-1 focus-visible:ring-white/30"
                required
              />
            </div>

            <div className="space-y-2">
              <Label htmlFor="newPassword">Neues Passwort</Label>
              <Input
                id="newPassword"
                type="password"
                value={newPassword}
                onChange={(e) => setNewPassword(e.target.value)}
                className="border-0 bg-white/5 text-white focus-visible:ring-1 focus-visible:ring-white/30"
                required
              />
            </div>

            <Button
              type="submit"
              className="w-full bg-white text-black hover:bg-white/90"
            >
              Passwort ändern
            </Button>
          </form>
        </div>
      </div>
    </div>
  );
} 