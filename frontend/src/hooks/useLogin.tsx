import axios from "axios";
import Cookies from "js-cookie";
import { useNavigate } from "react-router-dom";
import { useState } from "react";
import { toast } from "react-hot-toast";

export default function useLogin() {
  const navigate = useNavigate();
  const [isLoading, setIsLoading] = useState(false);

  const handleLogin = async (email: string, password: string) => {
    setIsLoading(true);
    try {
      const url = `${import.meta.env.VITE_BACKEND_URL}/api/auth/login`;
      const res = await axios.post(url, { email, password });
      const { token, role } = res.data;

      toast.success("Login successfully");

      if (role === 3) {
        navigate("/");

        Cookies.set("token-client", token, {
          expires: 1, // 1 ngày
          sameSite: "strict",
          secure: import.meta.env.VITE_ENV === "production",
        });
      } else if (role >= 0 && role <= 2) {
        navigate("/admin/account");

        Cookies.set("token-admin", token, {
          expires: 1, // 1 ngày
          sameSite: "strict",
          secure: import.meta.env.VITE_ENV === "production",
        });
      } else {
        navigate("/");
      }
    } catch (err: any) {
      console.error("Login error:", err);
      throw err;
    } finally {
      setIsLoading(false);
    }
  };

  return { handleLogin, isLoading };
}
