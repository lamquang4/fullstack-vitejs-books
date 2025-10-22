import { useState } from "react";
import Overplay from "./Overplay";
import Image from "../Image";
import { IoIosArrowDown } from "react-icons/io";
import { IoIosArrowUp } from "react-icons/io";
import { TbCategoryPlus } from "react-icons/tb";
import { FaRegAddressCard } from "react-icons/fa";
import { LuChartNoAxesCombined, LuHousePlus, LuUserRoundPen } from "react-icons/lu";
import { FaRegCircleUser } from "react-icons/fa6";
import { RiShoppingBag4Line } from "react-icons/ri";
import { Link, useLocation } from "react-router-dom";
import { GrBook } from "react-icons/gr";
type Props = {
  menuOpen: boolean;
  toggleMenu: () => void;
};

function MenuSide({ menuOpen, toggleMenu }: Props) {
  const location = useLocation();
  const pathname = location.pathname;
  const [openMenus, setOpenMenus] = useState<Record<string, boolean>>({});

  const menuData = [
    {
      title: "Goods",
      items: [
        {
          icon: <LuChartNoAxesCombined size={20} />,
          label: "Thống kê",
          path: "/dashboard",
        },
        {
          icon: <GrBook size={20} />,
          label: "Books",
          key: "2a",
          children: [
            { label: "List books", path: "/admin/books" },
            { label: "Add book", path: "/admin/add-book" },
          ],
        },
        {
          icon: <RiShoppingBag4Line size={20} />,
          label: "Orders",
          path: "/admin/orders",
        },
      ],
    },
    {
      title: "Users",
      items: [
        {
          icon: <FaRegAddressCard size={20} />,
          label: "Administrators",
          key: "5a",
          children: [
            { label: "List administrators", path: "/admin/admins" },
            { label: "Add administrator", path: "/admin/add-admin" },
          ],
        },
        {
          icon: <FaRegCircleUser size={20} />,
          label: "Customers",
          key: "6a",
          children: [
            { label: "List customers", path: "/admin/customers" },
            { label: "Add customer", path: "/admin/add-customer" },
          ],
        },
      ],
    },
    {
      title: "Other",
      items: [
        {
          icon: <TbCategoryPlus size={20} />,
          label: "Categories",
          key: "3a",
          children: [
            { label: "List categories", path: "/admin/categories" },
            { label: "Add category", path: "/admin/add-category" },
          ],
        },
        {
          icon: <LuUserRoundPen size={20} />,
          label: "Authors",
          key: "14a",
          children: [
            { label: "List authors", path: "/admin/authors" },
            { label: "Add author", path: "/admin/add-author" },
          ],
        },
        {
          icon: <LuHousePlus size={20} />,
          label: "Publishers",
          key: "28a",
          children: [
            { label: "List publishers", path: "/admin/publishers" },
            { label: "Add publisher", path: "/admin/add-publisher" },
          ],
        },
      ],
    },
  ];

  const toggleOpen = (menu: string) => {
    setOpenMenus((prev) => ({
      ...prev,
      [menu]: !prev[menu],
    }));
  };

  return (
    <>
      <nav
        className={` ${menuOpen ? "left-0" : "left-[-100%]"} 
        ${
          menuOpen ? "xl:translate-x-[-100%] xl:p-0 xl:w-0" : "xl:translate-x-0"
        } custom-scroll fixed border top-0 h-screen w-[320px] pb-5 bg-white transition-all duration-350 ease-in-out z-100 xl:sticky overflow-y-auto border-b border-gray-200`}
      >
        <div className="mb-[20px] flex justify-center sticky top-0 bg-white px-3.5 py-4.5">
          <Image
            source={"/assets/logo.png"}
            alt=""
            className="w-[100px]"
            loading="eager"
          />
        </div>
        <ul className="flex flex-col gap-[15px] font-semibold px-3.5">
          {menuData.map((group, groupIndex) => (
            <div key={groupIndex} className="flex flex-col gap-[10px]">
              <p className="  uppercase">{group.title}</p>
              {group.items.map((item, index) => (
                <li key={index}>
                  {item.children ? (
                    <>
                      <div
                        onClick={() => toggleOpen(item.key)}
                        className={`${
                          openMenus[item.key] ||
                          item.children.some((child) => pathname === child.path)
                            ? "text-[#C62028]"
                            : "hover:bg-gray-100"
                        } rounded-lg p-3 w-full cursor-pointer flex justify-between items-center`}
                      >
                        <p className="font-medium flex items-center gap-[10px]">
                          {item.icon} {item.label}
                        </p>
                        <button>
                          {openMenus[item.key] ||
                          item.children.some(
                            (child) => pathname === child.path
                          ) ? (
                            <IoIosArrowDown size={18} />
                          ) : (
                            <IoIosArrowUp size={18} />
                          )}
                        </button>
                      </div>
                      <ul
                        className={`max-h-0 overflow-hidden invisible transition-all duration-600 ease-in-out pl-[25px] ${
                          openMenus[item.key] ||
                          item.children.some((child) => pathname === child.path)
                            ? "max-h-fit visible"
                            : ""
                        }`}
                      >
                        {item.children.map((child, childIndex) => (
                          <li
                            key={childIndex}
                            className={`rounded-lg w-full cursor-pointer my-[5px] ${
                              pathname === child.path
                                ? "text-white bg-[#C62028]"
                                : "hover:bg-gray-100"
                            }`}
                          >
                            <Link
                              to={child.path}
                              className="text-[0.9rem] font-medium p-3"
                            >
                              {child.label}
                            </Link>
                          </li>
                        ))}
                      </ul>
                    </>
                  ) : (
                    <Link
                      to={item.path}
                      className={`${
                        pathname === item.path
                          ? "text-white bg-[#C62028]"
                          : "hover:bg-gray-100"
                      } rounded-lg p-3 w-full cursor-pointer flex justify-between items-center`}
                    >
                      <p className="font-medium flex items-center gap-[10px]">
                        {item.icon} {item.label}
                      </p>
                    </Link>
                  )}
                </li>
              ))}
            </div>
          ))}
        </ul>
      </nav>

      {menuOpen && <Overplay closeMenu={toggleMenu} />}
    </>
  );
}

export default MenuSide;
