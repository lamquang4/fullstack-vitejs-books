import { memo } from "react";
import { Link } from "react-router-dom";
import useLogout from "../../../hooks/useLogout";
import useGetCurrentUser from "../../../hooks/useGetCurrentUser";

type ProfileMenuProp = {
  isOpen: boolean;
};

function ProfileMenu({ isOpen }: ProfileMenuProp) {
  const { handleLogout } = useLogout();
  const { user } = useGetCurrentUser("client");

  if (!isOpen) return null;
  return (
    <>
      {user ? (
        <div className="w-[180px] absolute top-[22px] right-[-40px] overflow-hidden z-20 bg-white shadow-md rounded-md font-normal">
          <p className="border-b p-2.5 border-gray-200 max-w-[210px] overflow-hidden text-ellipsis whitespace-nowrap text-center">
            Xin chào, {user.fullname}
          </p>

          <Link
            to="/account"
            className="hover:bg-gray-100 w-full block p-2.5 text-[0.95rem]  "
          >
            Thông tin tài khoản
          </Link>

          <Link
            to="/order"
            className="hover:bg-gray-100 w-full block p-2.5 text-[0.95rem]  "
          >
            Đơn hàng
          </Link>

          <Link
            to="/address"
            className="hover:bg-gray-100 w-full block p-2.5 text-[0.95rem]  "
          >
            Sổ địa chỉ
          </Link>

          <button
            type="button"
            onClick={() => handleLogout("client")}
            className="hover:bg-gray-100 w-full block p-2.5 text-[0.95rem]   text-left"
          >
            Đăng xuất
          </button>
        </div>
      ) : (
        <div className="w-[120px] absolute top-[22px] right-[-40px] overflow-hidden z-20 bg-white shadow-md rounded-md font-normal">
          <Link
            to="/login"
            className="hover:bg-gray-100 w-full block p-2.5 text-[0.95rem]  "
          >
            Đăng nhập
          </Link>

          <Link
            to="/register"
            className="hover:bg-gray-100 w-full block p-2.5 text-[0.95rem]  "
          >
            Đăng ký
          </Link>
        </div>
      )}
    </>
  );
}

export default memo(ProfileMenu);
