import { Navigate } from "react-router-dom";
import Cookies from "js-cookie";
import { jwtDecode } from "jwt-decode";

interface JwtPayload {
  id: string;
  role: number;
  exp: number;
}

interface PublicRouteProps {
  children: React.ReactNode;
  redirectPath: string;
  type: "admin" | "client";
}

const PublicRoute = ({ children, redirectPath, type }: PublicRouteProps) => {
  const token =
    type === "admin" ? Cookies.get("token-admin") : Cookies.get("token-client");

  if (!token) return <>{children}</>;

  try {
    const decoded = jwtDecode<JwtPayload>(token);
    const role = Number(decoded.role);
    const isExpired = decoded.exp * 1000 < Date.now();
    if (isExpired) {
      Cookies.remove("token");
      return <>{children}</>;
    }

    // Nếu đã login redirect theo role
    if (type === "admin" && role >= 0 && role <= 2) {
      return <Navigate to={redirectPath} replace />;
    } else if (type === "client" && role === 3) {
      return <Navigate to="/" replace />;
    }
  } catch {
    Cookies.remove("token");
    return <>{children}</>;
  }

  return <>{children}</>;
};

export default PublicRoute;
