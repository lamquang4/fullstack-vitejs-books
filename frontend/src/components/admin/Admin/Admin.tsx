import { VscTrash } from "react-icons/vsc";
import { LiaEdit } from "react-icons/lia";
import { IoMdAddCircle } from "react-icons/io";
import { TbLock, TbLockOpen } from "react-icons/tb";
import Pagination from "../Pagination";
import FilterDropDownMenu from "../FilterDropDownMenu";
import Image from "../../Image";
import Loading from "../../Loading";
import InputSearch from "../InputSearch";
import toast from "react-hot-toast";
import { Link } from "react-router-dom";
import useGetAdmins from "../../../hooks/admin/useGetAdmins";
import useDeleteUser from "../../../hooks/admin/useDeleteUser";
import useUpdateStatusUser from "../../../hooks/admin/useUpdateStatusUser";
import useCurrentUser from "../../../hooks/useGetCurrentUser";

function Admin() {
  const array = [
    { name: "Tất cả", value: null },
    { name: "Bình thường", value: 1 },
    { name: "Bị khóa", value: 0 },
  ];

  const { user } = useCurrentUser("admin");
  const {
    admins,
    mutate,
    isLoading,
    totalPages,
    totalItems,
    currentPage,
    limit,
  } = useGetAdmins();
  const { deleteUser, isLoading: isLoadingDelete } = useDeleteUser();
  const { updateStatusUser, isLoading: isLoadingUpdate } =
    useUpdateStatusUser();

  const handleDelete = async (id: string) => {
    if (!id) {
      return;
    }

    if (id === user?.id) {
      toast.error("Bạn không thể xóa chính tài khoản của mình");
      return;
    }

    try {
      await deleteUser(id);
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

    if (id === user?.id) {
      toast.error("Bạn không thể khóa chính tài khoản của mình");
      return;
    }

    try {
      await updateStatusUser(id, status);
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
          <h2 className=" text-[#74767d]">Quản trị viên ({totalItems})</h2>

          <Link
            to={"/admin/add-admin"}
            className="bg-[#C62028] border-0 cursor-pointer text-[0.9rem] font-medium w-[90px] !flex p-[10px_12px] items-center justify-center gap-[5px] text-white"
          >
            <IoMdAddCircle size={22} /> Thêm
          </Link>
        </div>
      </div>

      <div className=" bg-white w-full overflow-auto">
        <div className="p-[1.2rem]">
          <InputSearch />
        </div>

        <table className="w-[350%] badmin.address-collapse sm:w-[220%] xl:w-full text-[0.9rem]">
          <thead>
            <tr className="bg-[#E9EDF2] text-left">
              <th className="p-[1rem]">Họ tên</th>

              <th className="p-[1rem]">Email</th>

              <th className="p-[1rem]  ">Chức vụ</th>
              <th className="p-[1rem]  ">
                <FilterDropDownMenu
                  title="Tình trạng"
                  array={array}
                  paramName="status"
                />
              </th>
              <th className="p-[1rem]  ">Hành động</th>
            </tr>
          </thead>
          <tbody>
            {isLoading ? (
              <tr>
                <td colSpan={8} className="w-full">
                  <Loading height={60} size={50} color="black" thickness={2} />
                </td>
              </tr>
            ) : admins.length > 0 ? (
              admins.map((admin) => (
                <tr key={admin.id} className="hover:bg-[#f2f3f8]">
                  <td className="p-[1rem] text-[0.9rem] font-semibold">
                    {admin.fullname}
                  </td>
                  <td className="p-[1rem]  ">{admin.email}</td>

                  <td className="p-[1rem]  ">
                    {admin.role === 0
                      ? "Quản trị viên hệ thống"
                      : admin.role === 1
                      ? "Salesperson"
                      : ""}
                  </td>
                  <td className="p-[1rem]  ">
                    {admin.status === 1 ? "Bình thường" : "Bị khóa"}
                  </td>
                  <td className="p-[1rem]  ">
                    <div className="flex items-center gap-[15px]">
                      <button
                        disabled={isLoadingUpdate}
                        onClick={() =>
                          handleUpdateStatus(
                            admin.id || "",
                            admin.status === 1 ? 0 : 1
                          )
                        }
                      >
                        {admin.status === 1 ? (
                          <TbLock size={22} className="text-[#74767d]" />
                        ) : (
                          <TbLockOpen size={22} className="text-[#74767d]" />
                        )}
                      </button>

                      <Link to={`/admin/edit-admin/${admin.id}`}>
                        <LiaEdit size={22} className="text-[#076ffe]" />
                      </Link>

                      <button
                        disabled={isLoadingDelete}
                        onClick={() => handleDelete(admin.id!)}
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

export default Admin;
