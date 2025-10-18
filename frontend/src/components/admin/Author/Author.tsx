import { VscTrash } from "react-icons/vsc";
import { LiaEdit } from "react-icons/lia";
import { IoMdAddCircle } from "react-icons/io";
import Pagination from "../Pagination";
import Image from "../../Image";
import Loading from "../../Loading";
import InputSearch from "../InputSearch";
import toast from "react-hot-toast";
import { Link } from "react-router-dom";
import useGetAuthors from "../../../hooks/admin/useGetAuthors";
import useDeleteAuthor from "../../../hooks/admin/useDeleteAuthor";
function Author() {
  const {
    authors,
    mutate,
    isLoading,
    totalPages,
    totalItems,
    currentPage,
    limit,
  } = useGetAuthors();
  const { deleteAuthor, isLoading: isLoadingDelete } = useDeleteAuthor();

  const handleDelete = async (id: string) => {
    if (!id) {
      return;
    }
    try {
      await deleteAuthor(id);
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
          <h2 className="mb-[20px] text-[#74767d]">Author ({totalItems})</h2>

          <Link
            to={"/admin/add-author"}
            className="bg-[#daf4f0] border-0 cursor-pointer text-[0.9rem] font-medium !flex p-[10px_12px] items-center justify-center gap-[5px] text-[#0ab39c] hover:bg-[#0ab39c] hover:text-white"
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
              <th className="p-[1rem]   ">Fullname</th>

              <th className="p-[1rem]  ">Number of products</th>

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
            ) : authors.length > 0 ? (
              authors.map((author) => (
                <tr key={author.id} className="hover:bg-[#f2f3f8]">
                  <td className="p-[1rem]">{author.fullname}</td>

                  <td className="p-[1rem]  ">chưa làm</td>

                  <td className="p-[1rem]  ">
                    <div className="flex items-center gap-[15px]">
                      <Link to={`/admin/edit-author/${author.id}`}>
                        <LiaEdit size={22} className="text-[#076ffe]" />
                      </Link>

                      <button
                        disabled={isLoadingDelete}
                        onClick={() => handleDelete(author.id!)}
                      >
                        <VscTrash size={22} className="text-[#d9534f]" />
                      </button>
                    </div>
                  </td>
                </tr>
              ))
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

export default Author;
