import axios from "axios";
import useSWR from "swr";

interface ResponseType {
  totalRevenue: number;
  todayRevenue: number;
  totalSoldQuantity: number;
  todaySoldQuantity: number;
}

const fetcher = (url: string) => axios.get(url).then((res) => res.data);

export default function useGetStatistic() {
  const url = `${import.meta.env.VITE_BACKEND_URL}/api/order/statistics`;
  const { data, error, isLoading, mutate } = useSWR<ResponseType>(url, fetcher, {
    shouldRetryOnError: false,
    revalidateOnFocus: false,
  });

  return {
    totalRevenue: data?.totalRevenue ?? 0,
    todayRevenue: data?.todayRevenue ?? 0,
    totalSoldQuantity: data?.totalSoldQuantity ?? 0,
    todaySoldQuantity: data?.todaySoldQuantity ?? 0,
    isLoading,
    error,
    mutate,
  };
}
