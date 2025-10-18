import axios from "axios";
import { useState } from "react";
import toast from "react-hot-toast";
import type { User } from "../../types/type";

export default function useUpdateCustomer(id: string) {
  const [isLoading, setIsLoading] = useState(false);
  const updateCustomer = async (data: User) => {
    if (!id) return;
    const loadingToast = toast.loading("Updating...");
    setIsLoading(true);
    try {
      const url = `${import.meta.env.VITE_BACKEND_URL}/api/user/customer/${id}`;
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

  return { updateCustomer, isLoading };
}
