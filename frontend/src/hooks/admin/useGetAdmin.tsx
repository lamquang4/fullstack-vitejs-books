import axios from "axios";
import useSWR from "swr";
import type { Admin } from "../../types/type";

const fetcher = (url: string) => axios.get(url).then((res) => res.data);

export default function useGetAdmin(id: string) {
  const url = `${import.meta.env.VITE_BACKEND_URL}/api/user/admin/${id}`;
  const { data, error, isLoading, mutate } = useSWR<Admin>(url, fetcher, {
    shouldRetryOnError: false,
    revalidateOnFocus: false,
  });

  return {
    admin: data,
    isLoading,
    error,
    mutate,
  };
}
