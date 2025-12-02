import axios from "axios";
import useSWR from "swr";
import type { Publisher } from "../../types/type";

const fetcher = (url: string) => axios.get(url).then((res) => res.data);

export default function useGetPublisher(id: string) {
  const url = id
    ? `${import.meta.env.VITE_BACKEND_URL}/api/publisher/${id}`
    : null;
  const { data, error, isLoading, mutate } = useSWR<Publisher>(url, fetcher, {
    shouldRetryOnError: false,
    revalidateOnFocus: false,
  });

  return {
    publisher: data,
    isLoading,
    error,
    mutate,
  };
}
