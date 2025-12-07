import axios from "axios";
import { useState } from "react";
import toast from "react-hot-toast";
import type { User } from "../../types/type";

export default function useRegister() {
  const [isLoading, setIsLoading] = useState(false);
  const handleRegister = async (data: User) => {
    if (!data) {
      return;
    }
    try {
      const url = `${import.meta.env.VITE_BACKEND_URL}/api/user`;
      await axios.post(url, data);
      toast.success("Đăng ký thành công");
    } catch (err) {
      console.error("Lỗi:", err);
      throw err;
    } finally {
      setIsLoading(false);
    }
  };

  return { handleRegister, isLoading };
}
