import axios from "axios";
import { useState } from "react";
import toast from "react-hot-toast";
import type { Publisher } from "../../types/type";

export default function useAddPublisher() {
  const [isLoading, setIsLoading] = useState(false);
  const addPublisher = async (data: Publisher) => {
    if (!data) {
      return;
    }
    const loadingToast = toast.loading("Đang thêm...");
    setIsLoading(true);
    try {
      const url = `${import.meta.env.VITE_BACKEND_URL}/api/publisher`;
      await axios.post(url, data);
      toast.dismiss(loadingToast);
      toast.success("Thêm thành công");
    } catch (err) {
      console.error("Lỗi:", err);
      throw err;
    } finally {
      toast.dismiss(loadingToast);
      setIsLoading(false);
    }
  };

  return { addPublisher, isLoading };
}
