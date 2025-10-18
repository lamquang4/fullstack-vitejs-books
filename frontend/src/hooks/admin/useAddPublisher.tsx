import axios from "axios";
import { useState } from "react";
import toast from "react-hot-toast";
import type { Publisher } from "../../types/type";

export default function useAddPublisher() {
  const [isLoading, setIsLoading] = useState(false);
  const addPublisher = async (data: Publisher) => {
    const loadingToast = toast.loading("Adding...");
    setIsLoading(true);
    try {
      const url = `${import.meta.env.VITE_BACKEND_URL}/api/publisher`;
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

  return { addPublisher, isLoading };
}
