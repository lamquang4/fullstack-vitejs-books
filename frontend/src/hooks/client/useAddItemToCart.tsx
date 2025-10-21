import axios from "axios";
import { useState } from "react";
import toast from "react-hot-toast";

export function useAddItemToCart() {
  const [isLoading, setIsLoading] = useState(false);

  const addItem = async (userId: string, bookId: string, quantity: number) => {
    setIsLoading(true);
    try {
      const url = `${
        import.meta.env.VITE_BACKEND_URL
      }/api/cart?userId=${userId}&bookId=${bookId}&quantity=${quantity}`;
      await axios.post(url);
      toast.success("Add book to cart successfully");
    } catch (err: any) {
      console.error("Error:", err);
      throw err;
    } finally {
      setIsLoading(false);
    }
  };

  return { addItem, isLoading };
}
