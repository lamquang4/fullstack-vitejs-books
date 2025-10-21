import axios from "axios";
import useSWR from "swr";
import type { Cart } from "../../types/type";

const fetcher = (url: string) => axios.get(url).then((res) => res.data);

export default function useGetCart(userId: string) {
  const url = userId
    ? `${import.meta.env.VITE_BACKEND_URL}/api/cart/${userId}`
    : null;

  const { data, error, isLoading, mutate } = useSWR<Cart>(url, fetcher, {
    shouldRetryOnError: false,
    revalidateOnFocus: false,
  });

  return {
    cart: data,
    isLoading,
    error,
    mutate,
  };
}
