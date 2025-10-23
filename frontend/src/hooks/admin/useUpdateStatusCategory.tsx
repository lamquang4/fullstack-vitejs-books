import axios from "axios";
import { useState } from "react";
import toast from "react-hot-toast";
import Swal from "sweetalert2";

export default function useUpdateStatusCategory() {
  const [isLoading, setIsLoading] = useState(false);
  const updateStatusCategory = async (id: string, status: number) => {
    const action = status === 1 ? "show" : "hide";
    const result = await Swal.fire({
      title: `Confirm ${action}?`,
      text: `Are you want to ${action} this category?`,
      icon: "warning",
      showCancelButton: true,
      confirmButtonText: "Yes",
      cancelButtonText: "Cancel",
    });

    if (!result.isConfirmed || !id) {
      return;
    }

    const loadingToast = toast.loading("Updating...");

    setIsLoading(true);
    try {
      const url = `${
        import.meta.env.VITE_BACKEND_URL
      }/api/category/status/${id}`;
      await axios.patch(url, { status });

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

  return { updateStatusCategory, isLoading };
}
