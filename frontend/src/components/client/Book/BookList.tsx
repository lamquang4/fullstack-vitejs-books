import { useCallback, useMemo, useState } from "react";
import Image from "../../Image";
import Loading from "../../Loading";
import type { Book } from "../../../types/type";
import { Link, useLocation, useNavigate } from "react-router-dom";

interface Props {
  category?: string;
  books: Book[];
  isLoading: boolean;
  total: number;
}
function BookList({ category, books, isLoading, total }: Props) {
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

  return (
    <>
      {!isLoading && category && (
        <h2 className="mb-[20px] text-black">
          {category} ({total})
        </h2>
      )}

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
      ) : books.length > 0 ? (
        <div
          className={`grid grid-cols-2 gap-x-[12px] gap-y-[35px] lg:grid-cols-3 2xl:grid-cols-4 sm:grid-cols-2 ${
            books.length <= 0 ? "h-[50vh]" : ""
          }`}
        >
          {books.map((book) => {
            return (
              <div key={book.id}>
                <div className="space-y-[20px] rounded-sm">
                  <div className="relative group">
                    <Link to={`/book/${book.slug}`}>
                      {book.images.length > 0 && (
                        <>
                          <div className="w-full flex items-center justify-center bg-gray-50 overflow-hidden">
                            <Image
                              source={`${import.meta.env.VITE_BACKEND_URL}${
                                book.images[0].image
                              }`}
                              alt={book.title}
                              className="w-full h-full object-contain"
                              loading="lazy"
                            />
                          </div>
                        </>
                      )}
                    </Link>

                    <div className="flex gap-2 flex-col absolute top-0 right-0 z-[3] font-semibold text-center  ">
                      {book.discount > 0 && (
                        <p className="text-white p-1.5 bg-[#C62028] rounded-sm">
                          -{Math.floor((book.discount / book.price) * 100)}%
                        </p>
                      )}
                    </div>
                  </div>
                  <div className="space-y-[6px]">
                    <h5 className="font-medium capitalize">{book.title}</h5>

                    <p className="font-medium">
                      {book.category.name} / {book.author.fullname}
                    </p>

                    {book.discount > 0 ? (
                      <div className="flex gap-[12px]">
                        <del className="text-[#707072] text-[1rem]">
                          {book.price.toLocaleString("vi-VN")}₫
                        </del>

                        <h5 className="font-semibold text-[#C62028]">
                          {(book.price - book.discount).toLocaleString("vi-VN")}
                          ₫
                        </h5>
                      </div>
                    ) : (
                      <h5 className="font-semibold text-[#C62028]">
                        {book.price.toLocaleString("vi-VN")}₫
                      </h5>
                    )}
                  </div>
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

            <h4 className="text-gray-600">No books found</h4>
          </div>
        </div>
      )}
    </>
  );
}

export default BookList;
