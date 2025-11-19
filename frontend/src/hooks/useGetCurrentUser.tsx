import axios from "axios";
import useSWR from "swr";
import Cookies from "js-cookie";
import type { User } from "../types/type";

const fetcher =
  (type: "admin" | "client") =>
  async (url: string): Promise<User> => {
    const token =
      type === "admin"
        ? Cookies.get("token-admin")
        : Cookies.get("token-client");

    const res = await axios.get(url, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });

    return res.data;
  };

export default function useGetCurrentUser(type: "admin" | "client") {
  const url = `${import.meta.env.VITE_BACKEND_URL}/api/auth/me`;

  const { data, error, isLoading, mutate } = useSWR<User>(url, fetcher(type));

  return {
    user: data,
    isLoading,
    error,
    mutate,
  };
}
