"use client";

import type React from "react";
import { useState, Suspense } from "react";
import { useRouter } from "next/navigation";
import { motion } from "framer-motion";
import { ArrowRight, Eye, EyeOff } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";

export default function LoginPage() {
  const [username, setUsername] = useState<string>("");
  const [password, setPassword] = useState<string>("");
  const [error, setError] = useState<string | null>(null);
  const [showPassword, setShowPassword] = useState(false);
  const router = useRouter();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);

    try {
      const response = await fetch('/api/auth/login', {
        method: "POST",
        headers: {
          "Content-Type": "application/json", // Send data as JSON
        },
        body: JSON.stringify({ username, password }), // Serialize data to JSON
      });

      const data = await response.json();

      if (response.ok) {
        if (data.tempToken) {
          // Redirect to change password page with the token and reason as query parameters
          router.push(`/change-password?token=${data.tempToken}&reason=${data.reason}`);
        } else {
          router.push("/dashboard?fromLogin=true");
        }
      } else {
        setError(data.error || "Login fehlgeschlagen.");
      }
    } catch (error) {
      console.error('Login error:', error);
      setError("Netzwerkfehler. Bitte versuche es erneut.");
    }
  };

  return (
    <div className="flex min-h-screen flex-col bg-black text-white">
      {/* Logo Section */}
      <motion.div
        className="flex justify-center pt-16 pb-8"
        initial={{ opacity: 0, y: -20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.5 }}
      >
        <h1 className="text-3xl font-bold tracking-tight">LeanX</h1>
      </motion.div>

      {/* Login Form */}
      <motion.div
        className="flex flex-1 flex-col items-center justify-center px-6"
        initial={{ opacity: 0 }}
        animate={{ opacity: 1 }}
        transition={{ duration: 0.5, delay: 0.2 }}
      >
        <div className="w-full max-w-sm">
          <motion.h2
            className="mb-8 text-2xl font-medium"
            initial={{ opacity: 0, y: 10 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.3, delay: 0.3 }}
          >
            Anmelden
          </motion.h2>

          {error && (
            <motion.div
              className="mb-6 rounded-md bg-red-500/10 p-4 text-sm text-red-500"
              initial={{ opacity: 0, height: 0 }}
              animate={{ opacity: 1, height: "auto" }}
              exit={{ opacity: 0, height: 0 }}
            >
              {error}
            </motion.div>
          )}

          <form onSubmit={handleSubmit} className="space-y-6">
            <motion.div
              className="space-y-2"
              initial={{ opacity: 0, y: 10 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.3, delay: 0.4 }}
            >
              <Label htmlFor="username" className="text-sm font-normal text-gray-400">
                Benutzername
              </Label>
              <Input
                id="username"
                type="text"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                className="border-0 bg-white/5 text-white focus-visible:ring-1 focus-visible:ring-white/30"
                required
              />
            </motion.div>

            <motion.div
              className="space-y-2"
              initial={{ opacity: 0, y: 10 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.3, delay: 0.5 }}
            >
              <Label htmlFor="password" className="text-sm font-normal text-gray-400">
                Passwort
              </Label>
              <div className="relative">
                <Input
                  id="password"
                  type={showPassword ? "text" : "password"}
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  className="border-0 bg-white/5 text-white focus-visible:ring-1 focus-visible:ring-white/30 pr-10"
                  required
                />
                <button
                  type="button"
                  onClick={() => setShowPassword(!showPassword)}
                  className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400 hover:text-white focus:outline-none"
                >
                  {showPassword ? (
                    <EyeOff className="h-4 w-4" />
                  ) : (
                    <Eye className="h-4 w-4" />
                  )}
                </button>
              </div>
            </motion.div>

            <motion.div
              initial={{ opacity: 0, y: 10 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.3, delay: 0.6 }}
            >
              <Button
                type="submit"
                className="group relative w-full overflow-hidden rounded-md bg-white py-6 text-black transition-all hover:bg-white/90 disabled:opacity-70"
               
              >
                <span className="flex items-center justify-center gap-2">
                
                    
                    <>
                      Anmelden
                      <ArrowRight className="h-4 w-4 transition-transform group-hover:translate-x-1" />
                    </>
              
                </span>
              </Button>
            </motion.div>
          </form>

          <motion.p
            className="mt-8 text-center text-sm text-gray-500"
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            transition={{ duration: 0.3, delay: 0.7 }}
          >
            Passwort vergessen?{" "}
            <a href="#" className="text-white hover:underline">
              Zurücksetzen
            </a>
          </motion.p>
        </div>
      </motion.div>

      {/* Footer */}
      <motion.div
        className="py-6 text-center text-xs text-gray-600"
        initial={{ opacity: 0 }}
        animate={{ opacity: 1 }}
        transition={{ duration: 0.5, delay: 0.8 }}
      >
        <strong>Rechtlicher Hinweis (Legal Notice)</strong><br />
        Diese Webanwendung ist ein studentisches Projekt im Rahmen des Studiums<br />
        an der Hochschule für Wirtschaft und Recht (HWR) Berlin.<br />  
        <br />  
        Es handelt sich um eine nicht-kommerzielle Anwendung, die ausschließlich zu Lernzwecken entwickelt wurde.<br />
        Diese Webseite steht in keiner Verbindung zu kommerziellen Anbietern mit ähnlichen Namen oder Produkten.<br />
        Alle Markennamen und Logos sind Eigentum der jeweiligen Rechteinhaber.<br />
        <br />
        Bei Fragen oder Anliegen wenden Sie sich bitte an die Verantwortlichen dieses Projekts<br />
        (s_gruettmueller22@stud.hwr-berlin.de; s_kania22@stud.hwr-berlin.de).<br />
        <br />
        &copy; {new Date().getFullYear()} HWR-Berlin
      </motion.div>
    </div>
  )
}
