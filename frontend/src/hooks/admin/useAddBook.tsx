import axios from "axios";
import { useState } from "react";
import toast from "react-hot-toast";

export default function useAddBook() {
  const [isLoading, setIsLoading] = useState(false);
  const addBook = async (formData: FormData) => {
    const loadingToast = toast.loading("Adding...");
    setIsLoading(true);
    try {
      const url = `${import.meta.env.VITE_BACKEND_URL}/api/book`;
      await axios.post(url, formData, {
        headers: {
          "Content-Type": "multipart/form-data",
        },
      });
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

  return { addBook, isLoading };
}
