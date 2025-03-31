"use client";

import { useState, useEffect, Suspense } from "react";
import { useRouter, useSearchParams } from "next/navigation";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";

// Create a separate client component that uses useSearchParams
function ChangePasswordClient() {
  const [newPassword, setNewPassword] = useState("");
  const [confirmNewPassword, setConfirmNewPassword] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [message, setMessage] = useState<string | null>(null);
  const router = useRouter();
  const searchParams = useSearchParams();
  const token = searchParams.get("token");
  const reason = searchParams.get("reason");

  useEffect(() => {
    if (!token) {
      router.push("/login");
    }
  }, [token, router]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setMessage(null);

    if (!token) {
      setError("Ungültiger Zugriff.");
      return;
    }

    if (newPassword !== confirmNewPassword) {
      setError("Die neuen Passwörter stimmen nicht überein.");
      return;
    }

    try {
      const response = await fetch("/api/auth/change-password", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ newPassword, confirmNewPassword, token }),
      });

      const data = await response.json();

      if (response.ok) {
        setMessage("Passwort erfolgreich geändert. Du wirst weitergeleitet...");
        setTimeout(() => {
          router.push("/dashboard");
        }, 1500);
      } else {
        setError(data.error || "Passwortänderung fehlgeschlagen.");
      }
    } catch (error) {
      console.error("Passwortänderungsfehler:", error);
      setError("Netzwerkfehler. Bitte versuche es erneut.");
    }
  };

  if (!token) {
    return (
      <div className="flex min-h-screen flex-col items-center justify-center bg-black text-white">
        <p className="text-lg">Du bist nicht autorisiert, diese Seite aufzurufen.</p>
        <Button onClick={() => router.push("/login")} className="mt-4">
          Zur Anmeldung
        </Button>
      </div>
    );
  }

  return (
    <div className="flex min-h-screen flex-col bg-black text-white">
      <div className="flex flex-1 flex-col items-center justify-center px-6">
        <div className="w-full max-w-sm">
          <h2 className="mb-8 text-2xl font-medium">Passwort ändern</h2>

          {reason && <p className="mb-4 text-gray-500">Grund: {reason}</p>}

          {error && (
            <div className="mb-6 rounded-md bg-red-500/10 p-4 text-sm text-red-500">
              {error}
            </div>
          )}

          {message && (
            <div className="mb-6 rounded-md bg-green-500/10 p-4 text-sm text-green-500">
              {message}
            </div>
          )}

          <form onSubmit={handleSubmit} className="space-y-6">
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
              <ul className="mt-1 text-xs italic text-gray-500">
                <li>Mindestpasswortlänge von 12 Zeichen.</li>
                <li>Muss mindestens einen Großbuchstaben enthalten.</li>
                <li>Muss mindestens eine Zahl enthalten.</li>
                <li>Muss mindestens ein Sonderzeichen (!@#$%&*. ) enthalten.</li>
              </ul>
            </div>

            <div className="space-y-2">
              <Label htmlFor="confirmNewPassword">Bestätige neues Passwort</Label>
              <Input
                id="confirmNewPassword"
                type="password"
                value={confirmNewPassword}
                onChange={(e) => setConfirmNewPassword(e.target.value)}
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

// Wrap the client component in a Suspense boundary
export default function ChangePasswordPage() {
  return (
    <Suspense fallback={<p>Loading...</p>}>
      <ChangePasswordClient />
    </Suspense>
  );
}