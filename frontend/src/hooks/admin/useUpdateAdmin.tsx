import axios from "axios";
import { useState } from "react";
import toast from "react-hot-toast";
import type { Admin } from "../../types/type";

export default function useUpdateAdmin(id: string) {
  const [isLoading, setIsLoading] = useState(false);
  const updateAdmin = async (data: Admin) => {
    if (!id) return;
    const loadingToast = toast.loading("Updating...");
    setIsLoading(true);
    try {
      const url = `${import.meta.env.VITE_BACKEND_URL}/api/user/admin/${id}`;
      await axios.put(url, data);
      toast.dismiss(loadingToast);
      toast.success("Updated successfully");
    } catch (err) {
      console.error("Error:", err);
      throw err;
    } finally {
      toast.dismiss(loadingToast);
      setIsLoading(false);
    }
  };

  return { updateAdmin, isLoading };
}
