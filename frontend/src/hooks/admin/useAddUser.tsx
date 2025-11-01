import axios from "axios";
import { useState } from "react";
import toast from "react-hot-toast";
import type { User } from "../../types/type";

export default function useAddUser() {
  const [isLoading, setIsLoading] = useState(false);
  const addUser = async (data: User) => {
    const loadingToast = toast.loading("Đang thêm...");
    setIsLoading(true);
    try {
      const url = `${import.meta.env.VITE_BACKEND_URL}/api/user`;
      await axios.post(url, data);
      toast.dismiss(loadingToast);
      toast.success("Thêm thành công");
    } catch (err) {
      console.error("Lỗi:", err);
      throw err;
    } finally {
      toast.dismiss(loadingToast);
      setIsLoading(false);
    }
  };

  return { addUser, isLoading };
}
