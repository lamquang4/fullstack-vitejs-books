import axios from "axios";
import { useState } from "react";

export function useChangeQuantityItemCart() {
  const [isLoading, setIsLoading] = useState(false);

  const changeQuantity = async (id: string, quantity: number) => {
    setIsLoading(true);
    if (!id) {
      return;
    }
    try {
      const url = `${
        import.meta.env.VITE_BACKEND_URL
      }/api/cart/item/${id}?quantity=${quantity}`;
      await axios.put(url);
    } catch (err: any) {
      console.error("Error:", err);
      throw err;
    } finally {
      setIsLoading(false);
    }
  };

  return { changeQuantity, isLoading };
}
