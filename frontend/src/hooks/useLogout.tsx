import Cookies from "js-cookie";
import { useNavigate } from "react-router-dom";
import { useState } from "react";

export default function useLogout() {
  const navigate = useNavigate();
  const [isLoading, setIsLoading] = useState(false);

  const handleLogout = async (type: "admin" | "client") => {
    setIsLoading(true);
    try {
      if (type === "admin") {
        Cookies.remove("token-admin");
        navigate("/admin/login");
      } else if (type === "client") {
        Cookies.remove("token-client");
        navigate("/login");
      }
    } catch (err) {
      console.error("Logout error:", err);
    } finally {
      setIsLoading(false);
    }
  };

  return { handleLogout, isLoading };
}
