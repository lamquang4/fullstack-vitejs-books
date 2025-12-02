import { useState } from "react";
import axios from "axios";
import toast from "react-hot-toast";

export default function useUpdateImagesBook() {
  const [isLoading, setIsLoading] = useState(false);

  const updateImagesBook = async (formData: FormData) => {
    if (!formData) {
      return;
    }
    const loadingToast = toast.loading("Đang cập nhật hình...");
    setIsLoading(true);

    try {
      const url = `${import.meta.env.VITE_BACKEND_URL}/api/book/image`;

      await axios.put(url, formData, {
        headers: { "Content-Type": "multipart/form-data" },
      });

      toast.success("Cập nhật hình thành công");
    } catch (err: any) {
      console.error("Lỗi:", err);
      throw err;
    } finally {
      setIsLoading(false);
      toast.dismiss(loadingToast);
    }
  };

  return { updateImagesBook, isLoading };
}
