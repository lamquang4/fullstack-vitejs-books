import { useLocation } from "react-router-dom";
import axios from "axios";
import useSWR from "swr";
import type { Order } from "../../types/type";

interface ResponseType {
  orders: Order[];
  totalPages: number;
  total: number;
  totalByStatus: { [status: number]: number };
}

const fetcher = (url: string) => axios.get(url).then((res) => res.data);

export default function useGetOrders() {
  const location = useLocation();
  const searchParams = new URLSearchParams(location.search);

  const page = parseInt(searchParams.get("page") || "1", 10);
  const limit = parseInt(searchParams.get("limit") || "12", 10);
  const q = searchParams.get("q");
  const status = searchParams.get("status");

  const query = new URLSearchParams();
  if (page) query.set("page", page.toString());
  if (limit) query.set("limit", limit.toString());
  if (q) query.set("q", q || "");
  if (status) query.set("status", status.toString());

  const url = `${
    import.meta.env.VITE_BACKEND_URL
  }/api/order?${query.toString()}`;

  const { data, error, isLoading, mutate } = useSWR<ResponseType>(
    url,
    fetcher,
    {
      shouldRetryOnError: false,
      revalidateOnFocus: false,
    }
  );

  return {
    orders: data?.orders ?? [],
    totalPages: data?.totalPages || 1,
    totalItems: data?.total || 0,
    totalByStatus: data?.totalByStatus,
    currentPage: page,
    limit,
    isLoading,
    error,
    mutate,
  };
}
