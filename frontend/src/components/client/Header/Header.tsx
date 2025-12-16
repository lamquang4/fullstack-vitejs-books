import { Link } from "react-router-dom";
import Image from "../../Image";
import MenuDropDown from "./MenuDropDown";
import SearchDesktop from "./SearchDesktop";
import ProfileMenu from "./ProfileMenu";
import { CiShoppingCart } from "react-icons/ci";
import { CiUser, CiSearch } from "react-icons/ci";
import { AiOutlineMenu } from "react-icons/ai";
import SearchMobile from "./SearchMobile";
import MenuMobile from "./MenuMobile";
import Overplay from "../Overplay";
import { useCallback, useEffect, useMemo, useState } from "react";
import useGetCategories from "../../../hooks/client/useGetCategories";
import useGetCurrentUser from "../../../hooks/useGetCurrentUser";
import useGetCart from "../../../hooks/client/useGetCart";
function Header() {
  const { user } = useGetCurrentUser("client");
  const { cart } = useGetCart(user?.id || "");
  const { categories } = useGetCategories();

  const [openSearch, setOpenSearch] = useState<boolean>(false);
  const [menuMobileOpen, setMenuMobileOpen] = useState<boolean>(false);
  const [profileMenuOpen, setProfileMenuOpen] = useState<boolean>(false);

  const totalQuantity = useMemo(() => {
    return (
      cart?.items.reduce((sum, item) => {
        return sum + (item?.quantity || 0);
      }, 0) || 0
    );
  }, [cart?.items]);

  const toggleSearch = useCallback(() => {
    setOpenSearch((prev) => !prev);
    setMenuMobileOpen(false);
    setProfileMenuOpen(false);
  }, []);

  const toggleMobileMenu = useCallback(() => {
    setMenuMobileOpen((prev) => !prev);
    setOpenSearch(false);
    setProfileMenuOpen(false);
  }, []);

  const toggleProfileMenu = useCallback(() => {
    setProfileMenuOpen((prev) => !prev);
    setMenuMobileOpen(false);
    setOpenSearch(false);
  }, []);

  useEffect(() => {
    const handleResize = () => {
      if (window.innerWidth >= 1024) {
        setMenuMobileOpen(false);
        setOpenSearch(false);
      }
    };

    window.addEventListener("resize", handleResize);
    return () => window.removeEventListener("resize", handleResize);
  }, []);
  return (
    <>
      <header className="w-full bg-white sticky top-0 border-b border-gray-200 z-[15] text-black">
        <div className=" py-[20px] px-[15px] relative">
          <div className="w-full max-w-[1200px] mx-auto flex justify-between items-center">
            <Link to={"/"}>
              <Image
                source={"/assets/logo.png"}
                alt={"logo"}
                className={"w-[100px]"}
                loading="eager"
              />
            </Link>

            <nav className="hidden lg:block">
              <ul className="flex items-center gap-[30px] text-[0.9rem] font-semibold uppercase">
                <li className="relative group">
                  <span className="cursor-pointer relative after:content-[''] after:absolute after:-bottom-2.5 after:left-0 after:w-full after:h-[1.5px] after:bg-black after:origin-left after:scale-x-0 after:transition-transform after:duration-200 after:ease-in-out hover:after:scale-x-100">
                    Danh mục
                  </span>

                  <MenuDropDown
                    items={[
                      { name: "Tất cả", href: "/books/all" },
                      ...categories.map((c) => ({
                        name: c.name,
                        href: `/books/${c.slug}`,
                      })),
                    ]}
                  />
                </li>

                <li className="cursor-pointer relative after:content-[''] after:absolute after:-bottom-2.5 after:left-0 after:w-full after:h-[1.5px] after:bg-black after:origin-left after:scale-x-0 after:transition-transform after:duration-200 after:ease-in-out hover:after:scale-x-100">
                  <Link to={"/sale"}>Giảm giá</Link>
                </li>
              </ul>
            </nav>

            <div className="hidden lg:flex items-center gap-5">
              <SearchDesktop />

              <div
                className="relative cursor-pointer group"
                onMouseEnter={toggleProfileMenu}
                onMouseLeave={toggleProfileMenu}
              >
                <CiUser size={24} />
                <ProfileMenu isOpen={profileMenuOpen} />
              </div>

              <Link to={"/cart"} className="relative" data-testid="cart">
                <CiShoppingCart size={26} />

                <small
                  className="absolute flex items-center justify-center 
    top-[-9px] right-[-11px] 
    bg-[#C62028] text-white text-[0.7rem] font-medium leading-none 
    rounded-full w-[20px] h-[20px]"
                >
                  {totalQuantity}
                </small>
              </Link>
            </div>

            {/* Mobile Search */}
            <SearchMobile toggleSearch={toggleSearch} openSearch={openSearch} />

            {/* Mobile */}
            <div className="flex lg:hidden items-center gap-4 relative">
              <button onClick={toggleSearch} className="text-gray-800">
                <CiSearch size={24} />
              </button>

              <div
                className="relative cursor-pointer group"
                onMouseOver={toggleProfileMenu}
                onMouseOut={toggleProfileMenu}
              >
                <CiUser size={24} />
                <ProfileMenu isOpen={profileMenuOpen} />
              </div>

              <Link to={"/cart"} className="relative" data-testid="cart">
                <CiShoppingCart size={26} />

                <small
                  className="absolute flex items-center justify-center 
    top-[-9px] right-[-11px] 
    bg-[#C62028] text-white text-[0.7rem] font-medium leading-none 
    rounded-full w-[20px] h-[20px]"
                >
                  {totalQuantity}
                </small>
              </Link>

              <button onClick={toggleMobileMenu}>
                <AiOutlineMenu size={24} />
              </button>
            </div>
          </div>

          <MenuMobile isOpen={menuMobileOpen} toggleMenu={toggleMobileMenu} />
        </div>
      </header>

      {openSearch && <Overplay closeMenu={toggleSearch} IndexForZ={12} />}
    </>
  );
}

export default Header;
