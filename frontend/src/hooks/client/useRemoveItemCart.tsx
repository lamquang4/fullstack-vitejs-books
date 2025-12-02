import axios from "axios";
import { useState } from "react";

export function useRemoveItemCart() {
  const [isLoading, setIsLoading] = useState(false);

  const removeItem = async (id: string) => {
    if (!id) {
      return;
    }
    setIsLoading(true);
    try {
      const url = `${import.meta.env.VITE_BACKEND_URL}/api/cart/item/${id}`;
      await axios.delete(url);
    } catch (err: any) {
      console.error("Lỗi:", err);
      throw err;
    } finally {
      setIsLoading(false);
    }
  };

  return { removeItem, isLoading };
}
