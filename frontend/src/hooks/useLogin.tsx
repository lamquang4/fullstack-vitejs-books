import axios from "axios";
import Cookies from "js-cookie";
import { useState } from "react";
import { toast } from "react-hot-toast";

export default function useLogin() {
  const [isLoading, setIsLoading] = useState(false);

  const handleLogin = async (email: string, password: string) => {
    setIsLoading(true);
    try {
      const url = `${import.meta.env.VITE_BACKEND_URL}/api/auth/login`;
      const res = await axios.post(url, { email, password });
      const { token, role } = res.data;

      const isAdminPage = window.location.pathname.startsWith("/admin");

      if (!isAdminPage && role >= 0 && role <= 2) {
        toast.error(
          "Bạn không thể đăng nhập bằng tài khoản quản trị viên vào trang khách hàng"
        );
        return;
      }

      if (isAdminPage && role === 3) {
        toast.error(
          "Bạn không thể đăng nhập bằng tài khoản khách hàng vào trang quản trị viên"
        );
        return;
      }

      toast.success("Đăng nhập thành công");

      if (role === 3) {
        Cookies.set("token-client", token, {
          expires: 1,
          sameSite: "strict",
          secure: import.meta.env.VITE_ENV === "production",
        });
        window.location.href = "/";
      } else if (role >= 0 && role <= 2) {
        Cookies.set("token-admin", token, {
          expires: 1,
          sameSite: "strict",
          secure: import.meta.env.VITE_ENV === "production",
        });
        window.location.href = "/admin/account";
      }
    } catch (err: any) {
      console.error("Lỗi:", err);
      throw err;
    } finally {
      setIsLoading(false);
    }
  };

  return { handleLogin, isLoading };
}
