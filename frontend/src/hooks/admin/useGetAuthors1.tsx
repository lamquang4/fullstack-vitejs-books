import axios from "axios";
import useSWR from "swr";
import type { Author } from "../../types/type";

const fetcher = (url: string) => axios.get(url).then((res) => res.data);

export default function useGetAuthors1() {
  const url = `${import.meta.env.VITE_BACKEND_URL}/api/author/all`;

  const { data, error, isLoading, mutate } = useSWR<Author[]>(url, fetcher, {
    shouldRetryOnError: false,
    revalidateOnFocus: false,
  });

  return {
    authors: data ?? [],
    isLoading,
    error,
    mutate,
  };
}
