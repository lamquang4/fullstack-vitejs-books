import { VscTrash } from "react-icons/vsc";
import { LiaEdit } from "react-icons/lia";
import { IoMdAddCircle } from "react-icons/io";
import { FaRegEyeSlash } from "react-icons/fa";
import Image from "../../Image";
import Pagination from "../Pagination";
import FilterDropDownMenu from "../FilterDropDownMenu";
import { MdOutlineRemoveRedEye } from "react-icons/md";
import Loading from "../../Loading";
import InputSearch from "../InputSearch";
import toast from "react-hot-toast";
import { Link } from "react-router-dom";
import useGetBooks from "../../../hooks/admin/useGetBooks";
import useDeleteBook from "../../../hooks/admin/useDeleteBook";
import useUpdateStatusBook from "../../../hooks/admin/useUpdateStatusBook";
function Book() {
  const array = [
    {
      name: "All",
      value: null,
    },
    {
      name: "Show",
      value: 1,
    },
    {
      name: "Hidden",
      value: 0,
    },
  ];

  const {
    books,
    mutate,
    isLoading,
    totalPages,
    totalItems,
    currentPage,
    limit,
  } = useGetBooks();
  const { deleteBook, isLoading: isLoadingDelete } = useDeleteBook();
  const { updateStatusBook, isLoading: isLoadingUpdate } =
    useUpdateStatusBook();

  const handleDelete = async (id: string) => {
    if (!id) {
      return;
    }
    try {
      await deleteBook(id);
      mutate();
    } catch (err: any) {
      toast.error(err?.response?.data?.msg);
      mutate();
    }
  };

  const handleUpdateStatus = async (id: string, status: number) => {
    if (!id && !status) {
      return;
    }

    try {
      await updateStatusBook(id, status);
      mutate();
    } catch (err: any) {
      toast.error(err?.response?.data?.msg);
      mutate();
    }
  };

  return (
    <>
      <div className="py-[1.3rem] px-[1.2rem] bg-[#f1f4f9]">
        <div className="flex justify-between items-center">
          <h2 className="mb-[20px] text-[#74767d]">Book ({totalItems})</h2>

          <Link
            to={"/admin/add-book"}
            className="bg-[#daf4f0] border-0 cursor-pointer text-[0.9rem] font-medium w-[90px] !flex p-[10px_12px] items-center justify-center gap-[5px] text-[#0ab39c] hover:bg-[#0ab39c] hover:text-white"
          >
            <IoMdAddCircle size={22} /> Add
          </Link>
        </div>
      </div>

      <div className=" bg-white w-full overflow-auto">
        <div className="p-[1.2rem]">
          <InputSearch />
        </div>

        <table className="w-[350%] border-collapse sm:w-[220%] xl:w-full text-[0.9rem]">
          <thead>
            <tr className="bg-[#E9EDF2] text-left">
              <th className="p-[1rem]  ">Name</th>
              <th className="p-[1rem]  ">Price</th>
              <th className="p-[1rem]  ">Stock</th>
              <th className="p-[1rem]  ">Author</th>
              <th className="p-[1rem]  ">Publisher</th>
              <th className="p-[1rem]  ">Category</th>
              <th className="p-[1rem]   relative">
                <FilterDropDownMenu
                  title="Status"
                  array={array}
                  paramName="status"
                />
              </th>
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
                    <td className="p-[1rem]">
                      <div className="flex gap-[10px] items-center">
                        <div className="relative group">
                          {book.images?.[0]?.image && (
                            <Image
                              source={`${import.meta.env.VITE_BACKEND_URL}${
                                book.images[0].image
                              }`}
                              alt={book.title}
                              className={"w-[80px] z-1 relative"}
                              loading="lazy"
                            />
                          )}
                        </div>

                        <p className="text-[0.9rem] font-medium  text-[#444]">
                          {book.title}
                        </p>
                      </div>
                    </td>

                    <td className="p-[1rem]  ">
                      {book.discount > 0 ? (
                        <div className="flex gap-[12px]  ">
                          <del className="text-[#707072] text-[1rem]">
                            {book.price.toLocaleString("vi-VN")}₫
                          </del>

                          <p className="font-medium text-[#c00]">
                            {(book.price - book.discount).toLocaleString(
                              "vi-VN"
                            )}
                            ₫
                          </p>
                        </div>
                      ) : (
                        <p className="font-medium">
                          {book.price.toLocaleString("vi-VN")}₫
                        </p>
                      )}
                    </td>

                    <td className="p-[1rem]  ">{book.stock}</td>

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
                        <button
                          disabled={isLoadingUpdate}
                          onClick={() =>
                            handleUpdateStatus(
                              book.id || "",
                              book.status === 1 ? 0 : 1
                            )
                          }
                        >
                          {book.status === 1 ? (
                            <FaRegEyeSlash
                              size={22}
                              className="text-[#74767d]"
                            />
                          ) : (
                            <MdOutlineRemoveRedEye
                              size={22}
                              className="text-[#74767d]"
                            />
                          )}
                        </button>
                        <Link to={`/admin/edit-book/${book.id}`}>
                          <LiaEdit size={22} className="text-[#076ffe]" />
                        </Link>
                        <button
                          disabled={isLoadingDelete}
                          onClick={() => handleDelete(book.id!)}
                        >
                          <VscTrash size={22} className="text-[#d9534f]" />
                        </button>
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

      <Pagination
        totalPages={totalPages}
        currentPage={currentPage}
        limit={limit}
        totalItems={totalItems}
      />
    </>
  );
}

export default Book;
