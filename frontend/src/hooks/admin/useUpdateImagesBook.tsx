import { useState } from "react";
import axios from "axios";
import toast from "react-hot-toast";

export default function useUpdateImagesBook() {
  const [isLoading, setIsLoading] = useState(false);

  const updateImagesBook = async (formData: FormData) => {
    const loadingToast = toast.loading("Updating...");
    setIsLoading(true);

    try {
      const url = `${import.meta.env.VITE_BACKEND_URL}/api/book/image`;

      await axios.put(url, formData, {
        headers: { "Content-Type": "multipart/form-data" },
      });

      toast.success("Updated images successfully");
    } catch (err: any) {
      console.error("Error:", err);
      toast.error(err?.response?.data?.message);
      throw err;
    } finally {
      setIsLoading(false);
      toast.dismiss(loadingToast);
    }
  };

  return { updateImagesBook, isLoading };
}
