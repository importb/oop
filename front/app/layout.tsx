import type { Metadata } from "next";
import { Inter } from "next/font/google";
import "./globals.css";

const inter = Inter({ subsets: ["latin"] });

import Header from "@/components/header";

export const metadata: Metadata = {
  title: "Programmeerimine II Hall of Fame"
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en">
      <body className={inter.className + " h-screen min-h-screen"}>
        <Header />
        {children}
      </body>
    </html>
  );
}
