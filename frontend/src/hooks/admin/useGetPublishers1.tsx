import axios from "axios";
import useSWR from "swr";
import type { Publisher } from "../../types/type";

const fetcher = (url: string) => axios.get(url).then((res) => res.data);

export default function useGetPublishers1() {
  const url = `${import.meta.env.VITE_BACKEND_URL}/api/publisher/all`;

  const { data, error, isLoading, mutate } = useSWR<Publisher[]>(url, fetcher, {
    shouldRetryOnError: false,
    revalidateOnFocus: false,
  });

  return {
    publishers: data ?? [],
    isLoading,
    error,
    mutate,
  };
}
