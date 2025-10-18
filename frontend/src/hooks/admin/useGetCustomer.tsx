import axios from "axios";
import useSWR from "swr";
import type { User } from "../../types/type";

const fetcher = (url: string) => axios.get(url).then((res) => res.data);

export default function useGetCustomer(id: string) {
  const url = `${import.meta.env.VITE_BACKEND_URL}/api/user/customer/${id}`;
  const { data, error, isLoading, mutate } = useSWR<User>(url, fetcher, {
    shouldRetryOnError: false,
    revalidateOnFocus: false,
  });

  return {
    customer: data,
    isLoading,
    error,
    mutate,
  };
}
