import { VscTrash } from "react-icons/vsc";
import { LiaEdit } from "react-icons/lia";
import { IoMdAddCircle } from "react-icons/io";
import Pagination from "../Pagination";
import Image from "../../Image";
import Loading from "../../Loading";
import InputSearch from "../InputSearch";
import toast from "react-hot-toast";
import { Link } from "react-router-dom";
import useGetCategories from "../../../hooks/admin/useGetCategories";
import useDeleteCategory from "../../../hooks/admin/useDeleteCategory";
import FilterDropDownMenu from "../FilterDropDownMenu";
import useUpdateStatusCategory from "../../../hooks/admin/useUpdateStatusCategory";
import { FaRegEyeSlash } from "react-icons/fa6";
import { MdOutlineRemoveRedEye } from "react-icons/md";
function Category() {
  const {
    categories,
    mutate,
    isLoading,
    totalPages,
    totalItems,
    currentPage,
    limit,
  } = useGetCategories();
  const { deleteCategory, isLoading: isLoadingDelete } = useDeleteCategory();
  const { updateStatusCategory, isLoading: isLoadingUpdate } =
    useUpdateStatusCategory();

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

  const handleDelete = async (id: string) => {
    if (!id) {
      return;
    }
    try {
      await deleteCategory(id);
      mutate();
    } catch (err: any) {
      toast.error(err?.response?.data?.message);
      mutate();
    }
  };

  const handleUpdateStatus = async (id: string, status: number) => {
    if (!id && !status) {
      return;
    }

    try {
      await updateStatusCategory(id, status);
      mutate();
    } catch (err: any) {
      toast.error(err?.response?.data?.message);
      mutate();
    }
  };
  return (
    <>
      <div className="py-[1.3rem] px-[1.2rem] bg-[#f1f4f9]">
        <div className="flex justify-between items-center">
          <h2 className=" text-[#74767d]">Category ({totalItems})</h2>

          <Link
            to={"/admin/add-category"}
            className="bg-[#C62028] border-0 cursor-pointer text-[0.9rem] font-medium w-[90px] !flex p-[10px_12px] items-center justify-center gap-[5px] text-white"
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
              <th className="p-[1rem]   ">Name</th>
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
            ) : categories.length > 0 ? (
              categories.map((category) => (
                <tr key={category.id} className="hover:bg-[#f2f3f8]">
                  <td className="p-[1rem] font-semibold">{category.name}</td>

                  <td className="p-[1rem]  ">
                    {category.status === 1
                      ? "Show"
                      : category.status === 0
                      ? "Hidden"
                      : ""}
                  </td>

                  <td className="p-[1rem]  ">
                    <div className="flex items-center gap-[15px]">
                      <button
                        disabled={isLoadingUpdate}
                        onClick={() =>
                          handleUpdateStatus(
                            category.id || "",
                            category.status === 1 ? 0 : 1
                          )
                        }
                      >
                        {category.status === 1 ? (
                          <FaRegEyeSlash size={22} className="text-[#74767d]" />
                        ) : (
                          <MdOutlineRemoveRedEye
                            size={22}
                            className="text-[#74767d]"
                          />
                        )}
                      </button>

                      <Link to={`/admin/edit-category/${category.id}`}>
                        <LiaEdit size={22} className="text-[#076ffe]" />
                      </Link>

                      <button
                        disabled={isLoadingDelete}
                        onClick={() => handleDelete(category.id!)}
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

export default Category;
