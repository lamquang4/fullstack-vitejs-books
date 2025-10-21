import axios from "axios";
import { useState } from "react";
import toast from "react-hot-toast";
import type { OrderAdd } from "../../types/type";

export default function useAddOrder(userId: string) {
  const [isLoading, setIsLoading] = useState(false);

  const addOrder = async (data: OrderAdd) => {
    setIsLoading(true);
    try {
      const url = `${
        import.meta.env.VITE_BACKEND_URL
      }/api/order/user/${userId}`;
      await axios.post(url, data);
      toast.success("Order successfully");
    } catch (err) {
      console.error("Error:", err);
      throw err;
    } finally {
      setIsLoading(false);
    }
  };

  return { addOrder, isLoading };
}
