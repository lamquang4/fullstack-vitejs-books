import axios from "axios";
import { useState } from "react";
import toast from "react-hot-toast";
import type { Publisher } from "../../types/type";

export default function useUpdatePublisher(id: string) {
  const [isLoading, setIsLoading] = useState(false);
  const updatePublisher = async (data: Publisher) => {
    if (!id) return;
    const loadingToast = toast.loading("Updating...");
    setIsLoading(true);
    try {
      const url = `${import.meta.env.VITE_BACKEND_URL}/api/publisher/${id}`;
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

  return { updatePublisher, isLoading };
}
