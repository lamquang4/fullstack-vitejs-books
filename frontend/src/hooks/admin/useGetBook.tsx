import axios from "axios";
import useSWR from "swr";
import type { Book } from "../../types/type";

const fetcher = (url: string) => axios.get(url).then((res) => res.data);

export default function useGetBook(id: string) {
  const url = `${import.meta.env.VITE_BACKEND_URL}/api/book/${id}`;
  const { data, error, isLoading, mutate } = useSWR<Book>(url, fetcher, {
    shouldRetryOnError: false,
    revalidateOnFocus: false,
  });

  return {
    book: data,
    isLoading,
    error,
    mutate,
  };
}
