import { LiaEdit } from "react-icons/lia";
import { Link } from "react-router-dom";
import Loading from "../../Loading";
import Image from "../../Image";
import useGetBestsellerBooks from "../../../hooks/useGetBestSellerBooks";
function BestsellerBook() {
  const { books, isLoading } = useGetBestsellerBooks();
  return (
    <>
      <div className="py-[1.3rem] px-[1.2rem] bg-[#f1f4f9]">
        <div className="flex justify-between items-center">
          <h2 className=" text-[#74767d]">Top bestseller</h2>
        </div>
      </div>

      <div className=" bg-white w-full overflow-auto">
        <table className="w-[350%] border-collapse sm:w-[220%] xl:w-full text-[0.9rem]">
          <thead>
            <tr className="bg-[#E9EDF2] text-left">
              <th className="p-[1rem]  ">Name</th>
              <th className="p-[1rem]  ">Price</th>
              <th className="p-[1rem]  ">Stock</th>
              <th className="p-[1rem]  ">Author</th>
              <th className="p-[1rem]  ">Publisher</th>
              <th className="p-[1rem]  ">Category</th>

              <th className="p-[1rem]  ">Action</th>
            </tr>
          </thead>
          <tbody>
            {isLoading ? (
              <tr>
                <td colSpan={8} className="w-full">
                  <Loading height={60} size={50} color="black" thickness={2} />
                </td>
              </tr>
            ) : books.length > 0 ? (
              books.map((book) => {
                return (
                  <tr key={book.id} className="hover:bg-[#f2f3f8]">
                    <td className="p-[1rem] font-semibold">
                      <div className="flex gap-[10px] items-center">
                        <div className="relative group w-[80px] h-[80px] overflow-hidden">
                          {book.images?.[0]?.image && (
                            <Image
                              source={`${import.meta.env.VITE_BACKEND_URL}${
                                book.images[0].image
                              }`}
                              alt={book.title}
                              className={
                                "w-full h-full object-contain z-1 relative"
                              }
                              loading="lazy"
                            />
                          )}
                        </div>

                        <p>{book.title}</p>
                      </div>
                    </td>

                    <td className="p-[1rem]  ">
                      {book.discount > 0 ? (
                        <div className="flex gap-[12px]  ">
                          <del className="text-[#707072] text-[1rem]">
                            {book.price.toLocaleString("vi-VN")}₫
                          </del>

                          <p className="font-medium text-[#C62028]">
                            {(book.price - book.discount).toLocaleString(
                              "vi-VN"
                            )}
                            ₫
                          </p>
                        </div>
                      ) : (
                        <p className="font-medium text-[#C62028]">
                          {book.price.toLocaleString("vi-VN")}₫
                        </p>
                      )}
                    </td>

                    <td className="p-[1rem]  ">
                      <div className="flex flex-col gap-1.5">
                        <p>In stock: {book.stock}</p>
                        <p>Sold: {book.totalSold}</p>
                      </div>
                    </td>

                    <td className="p-[1rem]  ">{book.author.fullname}</td>
                    <td className="p-[1rem]  ">{book.publisher.name}</td>
                    <td className="p-[1rem]  ">{book.category.name}</td>

                    <td className="p-[1rem]  ">
                      {book.status === 1
                        ? "Show"
                        : book.status === 0
                        ? "Hidden"
                        : ""}
                    </td>

                    <td className="p-[1rem]  ">
                      <div className="flex items-center gap-[15px]">
                        <Link to={`/admin/edit-book/${book.id}`}>
                          <LiaEdit size={22} className="text-[#076ffe]" />
                        </Link>
                      </div>
                    </td>
                  </tr>
                );
              })
            ) : (
              <tr>
                <td colSpan={8} className="w-full h-[70vh]">
                  <div className="flex justify-center items-center">
                    <Image
                      source={"/assets/notfound1.png"}
                      alt={""}
                      className={"w-[135px]"}
                      loading="lazy"
                    />
                  </div>
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </>
  );
}

export default BestsellerBook;
