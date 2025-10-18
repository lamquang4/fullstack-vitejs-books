import React from "react";
import Header from "./Header/Header";
import Footer from "./Footer";
import { useLocation } from "react-router-dom";

export default function LayoutPage({
  children,
}: {
  children: React.ReactNode;
}) {
  const location = useLocation();
  const pathname = location.pathname;
  const isPage = pathname === "/checkout";
  return (
    <>
      {!isPage && <Header />}
      {children}
      {!isPage && <Footer />}
    </>
  );
}
