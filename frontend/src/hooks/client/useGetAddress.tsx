import axios from "axios";
import useSWR from "swr";
import type { Address } from "../../types/type";

const fetcher = (url: string) => axios.get(url).then((res) => res.data);

export default function useGetAddress(id: string, userId: string) {
  const url = `${import.meta.env.VITE_BACKEND_URL}/api/address/${userId}/${id}`;
  const { data, error, isLoading, mutate } = useSWR<Address>(url, fetcher, {
    shouldRetryOnError: false,
    revalidateOnFocus: false,
  });

  return {
    address: data,
    isLoading,
    error,
    mutate,
  };
}
