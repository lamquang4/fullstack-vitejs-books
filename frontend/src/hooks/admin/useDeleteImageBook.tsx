import axios from "axios";
import { useState } from "react";
import toast from "react-hot-toast";
import Swal from "sweetalert2";

export default function useDeleteImageBox() {
  const [isLoading, setIsLoading] = useState(false);
  const deleteImageBook = async (imageId: string) => {
    const result = await Swal.fire({
      title: `Xác nhận xóa?`,
      text: `Bạn có chắc muốn xóa hình của sách này không?`,
      icon: "warning",
      showCancelButton: true,
      confirmButtonText: "Đồng ý",
      cancelButtonText: "Hủy",
    });

    if (!result.isConfirmed || !imageId) return;

    const loadingToast = toast.loading("Đang xóa...");

    setIsLoading(true);

    try {
      const url = `${
        import.meta.env.VITE_BACKEND_URL
      }/api/book/image/${imageId}`;
      await axios.delete(url);
      toast.dismiss(loadingToast);
      toast.success("Xóa thành công");
    } catch (err) {
      console.error("Lỗi:", err);
      throw err;
    } finally {
      toast.dismiss(loadingToast);
      setIsLoading(false);
    }
  };

  return { deleteImageBook, isLoading };
}
