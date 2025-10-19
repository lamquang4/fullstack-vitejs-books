import axios from "axios";
import { useState } from "react";
import toast from "react-hot-toast";
import Swal from "sweetalert2";

export default function useDeleteImageBox() {
  const [isLoading, setIsLoading] = useState(false);
  const deleteImageBook = async (imageId: string) => {
    const result = await Swal.fire({
      title: `Confirm deletion?`,
      text: `Are you sure you want to delete this book image?`,
      icon: "warning",
      showCancelButton: true,
      confirmButtonText: "Yes",
      cancelButtonText: "Cancel",
    });

    if (!result.isConfirmed || !imageId) return;

    const loadingToast = toast.loading("Deleting...");

    setIsLoading(true);

    try {
      const url = `${
        import.meta.env.VITE_BACKEND_URL
      }/api/book/image/${imageId}`;
      await axios.delete(url);
      toast.dismiss(loadingToast);
      toast.success("Deleted successfully");
    } catch (err) {
      console.error("Error:", err);
      throw err;
    } finally {
      toast.dismiss(loadingToast);
      setIsLoading(false);
    }
  };

  return { deleteImageBook, isLoading };
}
