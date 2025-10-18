import axios from "axios";
import { useState } from "react";
import toast from "react-hot-toast";

export default function useUpdateBook(id: string) {
  const [isLoading, setIsLoading] = useState(false);
  const updateBook = async (formData: FormData) => {
    if (!id) return;
    const loadingToast = toast.loading("Updating...");
    setIsLoading(true);
    try {
      const url = `${import.meta.env.VITE_BACKEND_URL}/api/book/${id}`;
      await axios.put(url, formData, {
        headers: {
          "Content-Type": "multipart/form-data",
        },
      });
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

  return { updateBook, isLoading };
}
