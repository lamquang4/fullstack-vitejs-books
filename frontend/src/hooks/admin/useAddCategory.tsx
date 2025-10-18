import axios from "axios";
import { useState } from "react";
import toast from "react-hot-toast";
import type { Category } from "../../types/type";

export default function useAddCategory() {
  const [isLoading, setIsLoading] = useState(false);
  const addCategory = async (data: Category) => {
    const loadingToast = toast.loading("Adding...");
    setIsLoading(true);
    try {
      const url = `${import.meta.env.VITE_BACKEND_URL}/api/category`;
      await axios.post(url, data);
      toast.dismiss(loadingToast);
      toast.success("Added successfully");
    } catch (err) {
      console.error("Error:", err);
      throw err;
    } finally {
      toast.dismiss(loadingToast);
      setIsLoading(false);
    }
  };

  return { addCategory, isLoading };
}
