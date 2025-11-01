import axios from "axios";
import { useState } from "react";
import toast from "react-hot-toast";
import type { Category } from "../../types/type";

export default function useUpdateCategory(id: string) {
  const [isLoading, setIsLoading] = useState(false);
  const updateCategory = async (data: Category) => {
    if (!id) return;
    const loadingToast = toast.loading("Đang cập nhật...");
    setIsLoading(true);
    try {
      const url = `${import.meta.env.VITE_BACKEND_URL}/api/category/${id}`;
      await axios.put(url, data);
      toast.dismiss(loadingToast);
      toast.success("Cập nhật thành công");
    } catch (err) {
      console.error("Lỗi:", err);
      throw err;
    } finally {
      toast.dismiss(loadingToast);
      setIsLoading(false);
    }
  };

  return { updateCategory, isLoading };
}
