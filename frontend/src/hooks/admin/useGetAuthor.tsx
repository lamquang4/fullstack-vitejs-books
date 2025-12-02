import axios from "axios";
import useSWR from "swr";
import type { Author } from "../../types/type";

const fetcher = (url: string) => axios.get(url).then((res) => res.data);

export default function useGetAuthor(id: string) {
  const url = id
    ? `${import.meta.env.VITE_BACKEND_URL}/api/author/${id}`
    : null;
  const { data, error, isLoading, mutate } = useSWR<Author>(url, fetcher, {
    shouldRetryOnError: false,
    revalidateOnFocus: false,
  });

  return {
    author: data,
    isLoading,
    error,
    mutate,
  };
}
