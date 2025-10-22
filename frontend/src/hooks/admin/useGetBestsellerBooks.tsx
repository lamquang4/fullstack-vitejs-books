import axios from "axios";
import useSWR from "swr";
import type { Book } from "../../types/type";

interface ResponseType {
  books: Book[];
}

const fetcher = (url: string) => axios.get(url).then((res) => res.data);

export default function useGetBestsellerBooks() {
  const url = `${import.meta.env.VITE_BACKEND_URL}/api/book/bestseller`;

  const { data, error, isLoading, mutate } = useSWR<ResponseType>(
    url,
    fetcher,
    {
      shouldRetryOnError: false,
      revalidateOnFocus: false,
    }
  );

  return {
    books: data?.books ?? [],
    isLoading,
    error,
    mutate,
  };
}
