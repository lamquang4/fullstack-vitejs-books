import axios from "axios";
import useSWR from "swr";
import type { Address } from "../../types/type";

const fetcher = (url: string) => axios.get(url).then((res) => res.data);

export default function useGetAddresses(userId: string) {
  const url = userId
    ? `${import.meta.env.VITE_BACKEND_URL}/api/address/${userId}`
    : null;

  const { data, error, isLoading, mutate } = useSWR<Address[]>(url, fetcher, {
    shouldRetryOnError: false,
    revalidateOnFocus: false,
  });

  return {
    addresses: data ?? [],
    isLoading,
    error,
    mutate,
  };
}
