import axios from "axios";
import { useState } from "react";
import type { OrderAdd } from "../../types/type";

export default function useAddOrder(userId: string) {
  const [isLoading, setIsLoading] = useState(false);

  const addOrder = async (data: OrderAdd) => {
    setIsLoading(true);
    try {
      const url = `${
        import.meta.env.VITE_BACKEND_URL
      }/api/order/user/${userId}`;
      const res = await axios.post(url, data);
      return res.data;
    } catch (err) {
      console.error("Lỗi:", err);
      throw err;
    } finally {
      setIsLoading(false);
    }
  };

  return { addOrder, isLoading };
}
