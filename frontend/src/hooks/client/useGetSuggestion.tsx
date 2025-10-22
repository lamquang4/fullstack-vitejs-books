import axios from "axios";
import useSWR from "swr";
import type { Book } from "../../types/type";
import { useState } from "react";

interface ResponseType {
  books: Book[];
}

const fetcher = (url: string) => axios.get(url).then((res) => res.data);

export default function useGetSuggestion() {
  const query = new URLSearchParams();
  const [keyword, setKeyword] = useState("");
  if (keyword) query.set("q", keyword);
  const url = `${
    import.meta.env.VITE_BACKEND_URL
  }/api/book/active?${query.toString()}`;

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
    setKeyword,
    isLoading,
    error,
    mutate,
  };
}
