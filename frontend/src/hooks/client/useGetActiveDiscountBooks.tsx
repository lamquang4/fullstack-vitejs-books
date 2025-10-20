import { useLocation } from "react-router-dom";
import axios from "axios";
import useSWR from "swr";
import type { Book } from "../../types/type";

interface ResponseType {
  books: Book[];
  totalPages: number;
  total: number;
}

const fetcher = (url: string) => axios.get(url).then((res) => res.data);

export default function useGetActiveDiscountBooks() {
  const location = useLocation();
  const searchParams = new URLSearchParams(location.search);

  const page = parseInt(searchParams.get("page") || "1", 10);
  const q = searchParams.get("q");

  const query = new URLSearchParams();
  if (page) query.set("page", page.toString());
  if (q) query.set("q", q || "");

  const url = `${
    import.meta.env.VITE_BACKEND_URL
  }/api/book/active/discount?${query.toString()}`;

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
    totalPages: data?.totalPages || 1,
    totalItems: data?.total || 0,
    currentPage: page,
    isLoading,
    error,
    mutate,
  };
}
