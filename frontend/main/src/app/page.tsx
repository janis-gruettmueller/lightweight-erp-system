import { redirect } from "next/navigation";

export default function Home() {
  redirect("/login"); // Automatisch zur Login-Seite weiterleiten
}
