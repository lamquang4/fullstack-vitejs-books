import { useCallback, useMemo, useState } from "react";
import Image from "../../Image";
import Loading from "../../Loading";
import type { Book, Category } from "../../../types/type";
import { Link, useLocation, useNavigate } from "react-router-dom";

interface Props {
  category?: Category;
  products: Book[];
  isLoading: boolean;
  total: number;
}
function ProductList({ category, products, isLoading, total }: Props) {
  const location = useLocation();
  const navigate = useNavigate();
  const searchParams = new URLSearchParams(location.search);
  const search = searchParams.get("q");
  const pathname = location.pathname;

  const sortArray = [
    {
      name: "Hàng mới",
      sort: "newest",
    },
    {
      name: "Giá (thấp-cao)",
      sort: "price-asc",
    },
    {
      name: "Giá (cao-thấp)",
      sort: "price-desc",
    },
    {
      name: "Bán chạy nhất",
      sort: "bestseller",
    },
  ];

  const handleSortChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    const sort = e.target.value;
    const params = new URLSearchParams(searchParams.toString());

    if (sort) {
      params.set("sort", sort);
    } else {
      params.delete("sort");
    }
    params.set("page", "1");
    navigate(`${pathname}?${params.toString()}`);
  };

  const getTitle = () => {
    if (category) {
      return category.name;
    }
    if (pathname === "/search" && search) return search;
    if (pathname === "/sale") return "Giảm giá";
    return "";
  };

  return (
    <>
      <h2 className="mb-[20px]">
        {!isLoading && (
          <>
            {getTitle()} ({total})
          </>
        )}
      </h2>

      <div className="flex justify-between items-center flex-wrap mb-[35px]">
        <select
          onChange={handleSortChange}
          value={searchParams.get("sort") ?? ""}
          className="bg-gray-50 border border-gray-300 text-gray-900 text-[0.9rem] rounded-sm block p-2 outline-0"
        >
          {sortArray.map((item, index) => (
            <option value={item.sort} key={index}>
              {item.name}
            </option>
          ))}
        </select>
      </div>

      {isLoading ? (
        <Loading height={70} size={50} color="black" thickness={2} />
      ) : products.length > 0 ? (
        <div
          className={`grid grid-cols-2 gap-x-[12px] gap-y-[35px] lg:grid-cols-3 2xl:grid-cols-4 sm:grid-cols-2 ${
            products.length <= 0 ? "h-[50vh]" : ""
          }`}
        >
          {products.map((product) => {
            return (
              <div key={product._id}>
                <div className="relative group">
                  <Link to={`/product/${product.slug}`}>
                    <Image
                      source={product.images[0]}
                      alt={product.title}
                      className={"w-full z-1 relative"}
                      loading="lazy"
                    />

                    <Image
                      source={product.images[1]}
                      alt={product.title}
                      className={
                        "w-full absolute top-0 left-0 opacity-0 z-2 transition-opacity duration-300 group-hover:opacity-100"
                      }
                      loading="lazy"
                    />
                  </Link>

                  <div className="flex gap-2 flex-col absolute top-[12px] left-[12px] z-[3] font-semibold text-center  ">
                    {product.discount > 0 && (
                      <small className="uppercase text-[0.7rem] py-1 px-1.5 bg-white">
                        Sale{" "}
                        {Math.floor((product.discount / product.price) * 100)}%
                      </small>
                    )}
                  </div>
                </div>
                <div className="py-[12px] space-y-[6px]">
                  <h5 className="  font-medium capitalize">{product.name}</h5>

                  <p className="font-medium text-[#444]">
                    {product.category.name} / {product.author}
                  </p>

                  {product.discount > 0 ? (
                    <div className="flex gap-[12px]  ">
                      <del className="text-[#707072] text-[1rem]">
                        {product.price.toLocaleString("vi-VN")}₫
                      </del>

                      <h5 className="font-medium text-[#c00]">
                        {(product.price - product.discount).toLocaleString(
                          "vi-VN"
                        )}
                        ₫
                      </h5>
                    </div>
                  ) : (
                    <h5 className="font-medium">
                      {product.price.toLocaleString("vi-VN")}₫
                    </h5>
                  )}
                </div>
              </div>
            );
          })}
        </div>
      ) : (
        <div className="flex justify-center items-center h-[60vh]">
          <div className="flex flex-col justify-center items-center gap-[15px]">
            <Image
              source={"/assets/notfound1.png"}
              alt={""}
              className={"w-[150px]"}
              loading="eager"
            />

            <h4 className="text-gray-600">No products found</h4>
          </div>
        </div>
      )}
    </>
  );
}

export default ProductList;
