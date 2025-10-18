import axios from "axios";
import { useState } from "react";
import toast from "react-hot-toast";
import type { Admin } from "../../types/type";

export default function useAddAdmin() {
  const [isLoading, setIsLoading] = useState(false);
  const addAdmin = async (data: Admin) => {
    const loadingToast = toast.loading("Adding...");
    setIsLoading(true);
    try {
      const url = `${import.meta.env.VITE_BACKEND_URL}/api/user/admin`;
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

  return { addAdmin, isLoading };
}
