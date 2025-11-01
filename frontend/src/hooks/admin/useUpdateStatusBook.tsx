import axios from "axios";
import { useState } from "react";
import toast from "react-hot-toast";
import Swal from "sweetalert2";

export default function useUpdateStatusBook() {
  const [isLoading, setIsLoading] = useState(false);
  const updateStatusBook = async (id: string, status: number) => {
    const action = status === 1 ? "hiện" : "ẩn";
    const result = await Swal.fire({
      title: `Xác nhận ${action}?`,
      text: `Bạn có chắc muốn ${action} sách này không?`,
      icon: "warning",
      showCancelButton: true,
      confirmButtonText: "Đồng ý",
      cancelButtonText: "Hủy",
    });

    if (!result.isConfirmed || !id) {
      return;
    }

    const loadingToast = toast.loading("Đang cập nhật...");

    setIsLoading(true);
    try {
      const url = `${import.meta.env.VITE_BACKEND_URL}/api/book/status/${id}`;
      await axios.patch(url, { status });

      toast.dismiss(loadingToast);
      toast.success("Cập nhật thành công");
    } catch (err) {
      console.error("Lỗi:", err);
      throw err;
    } finally {
      toast.dismiss(loadingToast);
      setIsLoading(false);
    }
  };

  return { updateStatusBook, isLoading };
}
