import { useSearchParams } from "react-router-dom";
import axios from "axios";
import useSWR from "swr";
import type { Author } from "../../types/type";

interface ResponseType {
  authors: Author[];
  totalPages: number;
  total: number;
}

const fetcher = (url: string) => axios.get(url).then((res) => res.data);

export default function useGetAuthors() {
  const [searchParams] = useSearchParams();

  const page = parseInt(searchParams.get("page") || "1", 10);
  const limit = parseInt(searchParams.get("limit") || "12", 10);
  const q = searchParams.get("q");

  const query = new URLSearchParams();
  if (page) query.set("page", page.toString());
  if (limit) query.set("limit", limit.toString());
  if (q) query.set("q", q || "");

  const url = `${
    import.meta.env.VITE_BACKEND_URL
  }/api/author?${query.toString()}`;

  const { data, error, isLoading, mutate } = useSWR<ResponseType>(
    url,
    fetcher,
    {
      shouldRetryOnError: false,
      revalidateOnFocus: false,
    }
  );

  return {
    authors: data?.authors ?? [],
    totalPages: data?.totalPages || 1,
    totalItems: data?.total || 0,
    currentPage: page,
    limit,
    isLoading,
    error,
    mutate,
  };
}
