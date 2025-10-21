import { memo } from "react";
import { Link } from "react-router-dom";
import useLogout from "../../../hooks/useLogout";
import useCurrentUser from "../../../hooks/useGetCurrentUser";

type ProfileMenuProp = {
  isOpen: boolean;
};

function ProfileMenu({ isOpen }: ProfileMenuProp) {
  if (!isOpen) return null;
  const { handleLogout } = useLogout();
  const { user } = useCurrentUser("client");

  return (
    <>
      {user ? (
        <div className="w-[180px] absolute top-[22px] right-[-40px] overflow-hidden z-20 bg-white shadow-md rounded-md font-normal">
          <p className="border-b p-2.5 border-gray-200 max-w-[210px] overflow-hidden text-ellipsis whitespace-nowrap text-center">
            Hello, {user.fullname}
          </p>

          <Link
            to="/account"
            className="hover:bg-gray-100 w-full block p-2.5 text-[0.95rem]  "
          >
            Account
          </Link>

          <Link
            to="/order"
            className="hover:bg-gray-100 w-full block p-2.5 text-[0.95rem]  "
          >
            Order history
          </Link>

          <Link
            to="/address"
            className="hover:bg-gray-100 w-full block p-2.5 text-[0.95rem]  "
          >
            Address book
          </Link>

          <button
            type="button"
            onClick={() => handleLogout("client")}
            className="hover:bg-gray-100 w-full block p-2.5 text-[0.95rem]   text-left"
          >
            Logout
          </button>
        </div>
      ) : (
        <div className="w-[120px] absolute top-[22px] right-[-40px] overflow-hidden z-20 bg-white shadow-md rounded-md font-normal">
          <Link
            to="/login"
            className="hover:bg-gray-100 w-full block p-2.5 text-[0.95rem]  "
          >
            Login
          </Link>

          <Link
            to="/register"
            className="hover:bg-gray-100 w-full block p-2.5 text-[0.95rem]  "
          >
            Register
          </Link>
        </div>
      )}
    </>
  );
}

export default memo(ProfileMenu);
