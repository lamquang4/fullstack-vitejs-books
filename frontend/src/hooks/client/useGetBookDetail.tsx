import axios from "axios";
import useSWR from "swr";
import type { Book } from "../../types/type";

const fetcher = (url: string) => axios.get(url).then((res) => res.data);

export default function useGetBookDetail(slug: string) {
  const url = slug
    ? `${import.meta.env.VITE_BACKEND_URL}/api/book/slug/${slug}`
    : null;
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
