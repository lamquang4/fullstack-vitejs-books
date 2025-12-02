import { Navigate } from "react-router-dom";
import { jwtDecode } from "jwt-decode";
import Cookies from "js-cookie";

interface JwtPayload {
  id: string;
  role: number;
  exp: number;
}

interface PrivateRouteProps {
  children: React.ReactNode;
  allowedRoles: number[];
  redirectPath: string;
  type: "admin" | "client";
}

const PrivateRoute = ({
  children,
  allowedRoles,
  redirectPath = "/login",
  type,
}: PrivateRouteProps) => {
  const token =
    type === "admin" ? Cookies.get("token-admin") : Cookies.get("token-client");

  if (!token) {
    return <Navigate to={redirectPath} replace />;
  }

  try {
    const decoded = jwtDecode<JwtPayload>(token);
    // kiểm tra token hết hạn
    const isExpired = decoded.exp * 1000 < Date.now();
    if (isExpired) {
      Cookies.remove("token");
      return <Navigate to={redirectPath} replace />;
    }

    // kiểm tra quyền truy cập
    const role = Number(decoded.role);
    if (!allowedRoles.includes(role)) {
      return <Navigate to={redirectPath} replace />;
    }

    return <>{children}</>;
  } catch {
    Cookies.remove("token");
    return <Navigate to={redirectPath} replace />;
  }
};

export default PrivateRoute;
