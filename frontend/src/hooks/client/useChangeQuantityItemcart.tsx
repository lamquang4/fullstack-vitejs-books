import axios from "axios";
import { useState } from "react";

export function useChangeQuantityItemCart() {
  const [isLoading, setIsLoading] = useState(false);

  const changeQuantity = async (id: string, quantity: number) => {
    if (!id || !quantity) {
      return;
    }
    setIsLoading(true);
    try {
      const url = `${
        import.meta.env.VITE_BACKEND_URL
      }/api/cart/item/${id}?quantity=${quantity}`;
      await axios.put(url);
    } catch (err: any) {
      console.error("Lỗi:", err);
      throw err;
    } finally {
      setIsLoading(false);
    }
  };

  return { changeQuantity, isLoading };
}
