import axios from "axios";
import useSWR from "swr";
import type { Order } from "../../types/type";

const fetcher = (url: string) => axios.get(url).then((res) => res.data);

export default function useGetOrder(id: string) {
  const url = id ? `${import.meta.env.VITE_BACKEND_URL}/api/order/${id}` : null;
  const { data, error, isLoading, mutate } = useSWR<Order>(url, fetcher, {
    shouldRetryOnError: false,
    revalidateOnFocus: false,
  });

  return {
    order: data,
    isLoading,
    error,
    mutate,
  };
}
