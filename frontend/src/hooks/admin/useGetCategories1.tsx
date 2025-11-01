import axios from "axios";
import useSWR from "swr";
import type { Category } from "../../types/type";

const fetcher = (url: string) => axios.get(url).then((res) => res.data);

export default function useGetCategories1() {
  const url = `${import.meta.env.VITE_BACKEND_URL}/api/category/all`;

  const { data, error, isLoading, mutate } = useSWR<Category[]>(url, fetcher, {
    shouldRetryOnError: false,
    revalidateOnFocus: false,
  });

  return {
    categories: data ?? [],
    isLoading,
    error,
    mutate,
  };
}
