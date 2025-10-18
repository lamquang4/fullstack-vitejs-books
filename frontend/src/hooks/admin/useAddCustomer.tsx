import axios from "axios";
import { useState } from "react";
import toast from "react-hot-toast";
import type { User } from "../../types/type";

export default function useAddCustomer() {
  const [isLoading, setIsLoading] = useState(false);
  const addCustomer = async (data: User) => {
    const loadingToast = toast.loading("Adding...");
    setIsLoading(true);
    try {
      const url = `${import.meta.env.VITE_BACKEND_URL}/api/user/customer`;
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

  return { addCustomer, isLoading };
}
