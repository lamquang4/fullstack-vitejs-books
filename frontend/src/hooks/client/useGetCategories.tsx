import axios from "axios";
import useSWR from "swr";
import type { Category } from "../../types/type";

interface ResponseType {
  categories: Category[];
}

const fetcher = (url: string) => axios.get(url).then((res) => res.data);

export default function useGetCategories() {
  const url = `${import.meta.env.VITE_BACKEND_URL}/api/category/active`;

  const { data, error, isLoading, mutate } = useSWR<ResponseType>(
    url,
    fetcher,
    {
      shouldRetryOnError: false,
      revalidateOnFocus: false,
    }
  );

  return {
    categories: data?.categories ?? [],
    isLoading,
    error,
    mutate,
  };
}
