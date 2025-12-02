import axios from "axios";
import { useState } from "react";

export default function usePaymentMomo() {
  const [isLoading, setIsLoading] = useState(false);
  const createPaymentMomo = async (orderCode: string) => {
    if (!orderCode) {
      return;
    }
    setIsLoading(true);
    try {
      const url = `${
        import.meta.env.VITE_BACKEND_URL
      }/api/payment/momo/qr/${orderCode}`;
      const res = await axios.post(url);
      return res.data;
    } catch (err) {
      console.error("Lỗi:", err);
      throw err;
    } finally {
      setIsLoading(false);
    }
  };

  return { createPaymentMomo, isLoading };
}
