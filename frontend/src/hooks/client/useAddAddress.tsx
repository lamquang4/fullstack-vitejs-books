import axios from "axios";
import { useState } from "react";
import toast from "react-hot-toast";
import type { Address } from "../../types/type";

export default function useAddAddress() {
  const [isLoading, setIsLoading] = useState(false);
  const addAddress = async (data: Address) => {
    const loadingToast = toast.loading("Adding...");
    setIsLoading(true);
    try {
      const url = `${import.meta.env.VITE_BACKEND_URL}/api/address`;
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

  return { addAddress, isLoading };
}
