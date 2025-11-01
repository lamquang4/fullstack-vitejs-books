import { memo } from "react";
import Image from "../Image";
import { FaRegCircleUser } from "react-icons/fa6";
import { RiLogoutBoxLine } from "react-icons/ri";
import { Link } from "react-router-dom";
import useLogout from "../../hooks/useLogout";
import useCurrentUser from "../../hooks/useGetCurrentUser";

type Props = {
  menuOpen: boolean;
  toggleMenu: () => void;
};
function ProfileMenu({ menuOpen, toggleMenu }: Props) {
  const { user } = useCurrentUser("admin");
  const { handleLogout } = useLogout();
  return (
    <>
      {user && (
        <div
          className=" text-[0.9rem] relative"
          onMouseOver={toggleMenu}
          onMouseOut={toggleMenu}
        >
          <div className="flex cursor-pointer items-center gap-[6px] text-[0.9rem]">
            <div className="w-[30px] rounded-full border border-gray-300 p-1">
              <Image
                source={"/assets/owner.png"}
                alt={""}
                className="w-full"
                loading="eager"
              />
            </div>
            <p>{user.fullname}</p>
          </div>

          {menuOpen && (
            <div className="w-[185px] absolute top-full right-0 overflow-hidden z-20 duration-400 ease-in-out bg-white shadow-md rounded-md border border-gray-200 ">
              <p className="border-b p-2.5 border-gray-300 max-w-[210px] overflow-hidden text-ellipsis whitespace-nowrap text-center">
                Xin chào, {user.fullname}
              </p>

              <Link
                to={"/admin/account"}
                className="w-ful block hover:bg-gray-100 px-3 py-3.5"
              >
                <div className="flex items-center gap-[8px]">
                  <FaRegCircleUser size={18} />
                  <p>Tài khoản</p>
                </div>
              </Link>

              <button
                className="w-full block hover:bg-gray-100 px-3 py-3.5"
                onClick={() => handleLogout("admin")}
              >
                <div className="flex items-center gap-[8px] text-[#C62028] font-medium">
                  <RiLogoutBoxLine size={18} />
                  <p>Đăng xuất</p>
                </div>
              </button>
            </div>
          )}
        </div>
      )}
    </>
  );
}

export default memo(ProfileMenu);
