import axios from "axios";
import { useState } from "react";
import toast from "react-hot-toast";
import Swal from "sweetalert2";

export default function useUpdateStatusOrder() {
  const [isLoading, setIsLoading] = useState(false);
  const updateStatusOrder = async (id: string, status: number) => {
    const result = await Swal.fire({
      title: `Confirm change status`,
      text: `Are you want to change status this order?`,
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
      }/api/order/${id}?status=${status}`;
      await axios.put(url);

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

  return { updateStatusOrder, isLoading };
}
