import { useEffect, useState } from "react";
import toast from "react-hot-toast";
import { Link, useNavigate, useParams } from "react-router-dom";
import { validateEmail } from "../../../utils/validateEmail";
import useGetUser from "../../../hooks/admin/useGetUser";
import useUpdateUser from "../../../hooks/admin/useUpdateUser";
import useCurrentUser from "../../../hooks/useGetCurrentUser";

function EditAdmin() {
  const navigate = useNavigate();
  const { id } = useParams();

  const { user: admin } = useCurrentUser("admin");
  const { user, isLoading, mutate } = useGetUser(id as string);
  const { updateUser, isLoading: isLoadingUpdate } = useUpdateUser(
    id as string
  );

  const [data, setData] = useState({
    email: "",
    password: "",
    fullname: "",
    role: "",
    status: "",
  });

  useEffect(() => {
    if (isLoading) return;

    if (!user) {
      toast.error("Quản trị viên không tìm thấy");
      navigate("/admin/admins");
      return;
    }

    if (user.role === 3) {
      toast.error("Quản trị viên không tìm thấy");
      navigate("/admin/admins");
      return;
    }

    setData({
      fullname: user.fullname || "",
      email: user.email || "",
      password: "",
      role: user.role?.toString() || "",
      status: user.status?.toString() || "",
    });
  }, [isLoading, user, navigate]);

  const handleChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>
  ) => {
    const { name, value } = e.target;
    setData((prev) => ({
      ...prev,
      [name]: name === "email" ? value.toLowerCase() : value,
    }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!validateEmail(data.email)) {
      toast.error("Email không hợp lệ");
      mutate(undefined, true);
      return;
    }

    if (data.password.trim().length < 6 && data.password) {
      toast.error("Mật khẩu phải có ít nhất 6 ký tự");
      return;
    }

    if (user?.id === admin?.id) {
      toast.error("Bạn không thể khóa chính tài khoản của mình");
      return;
    }

    try {
      await updateUser({
        fullname: data.fullname.trim(),
        email: data.email.trim(),
        password: data.password.trim(),
        role: Number(data.role),
        status: Number(data.status),
      });

      setData((prev) => ({
        ...prev,
        password: "",
      }));
      mutate();
    } catch (err: any) {
      toast.error(err?.response?.data?.message);
      mutate();
    }
  };

  return (
    <div className="py-[30px] sm:px-[25px] px-[15px] bg-[#F1F4F9] h-full">
      <form className="flex flex-col gap-7 w-full" onSubmit={handleSubmit}>
        <h2 className="text-[#74767d]">Chỉnh sửa quản trị viên</h2>

        <div className="flex gap-[25px] w-full flex-col">
          <div className="md:p-[25px] p-[15px] bg-white rounded-md flex flex-col gap-[20px] w-full">
            <h5 className="font-bold text-[#74767d]">Thông tin tài khoản</h5>

            <div className="flex flex-col gap-1">
              <label htmlFor="" className="text-[0.9rem] font-medium">
                Họ tên
              </label>
              <input
                type="text"
                name="fullname"
                value={data.fullname}
                onChange={handleChange}
                required
                className="border border-gray-300 p-[6px_10px] text-[0.9rem] w-full outline-none focus:border-gray-400  "
              />
            </div>

            <div className="flex flex-col gap-1">
              <label htmlFor="" className="text-[0.9rem] font-medium">
                Email
              </label>
              <input
                type="email"
                name="email"
                value={data.email}
                onChange={handleChange}
                required
                className="lowercase border border-gray-300 p-[6px_10px] text-[0.9rem] w-full outline-none focus:border-gray-400  "
              />
            </div>

            <div className="flex flex-wrap md:flex-nowrap gap-[15px]">
              <div className="flex flex-col gap-1 w-full">
                <label htmlFor="" className="text-[0.9rem] font-medium">
                  Chức vụ
                </label>
                <select
                  name="role"
                  value={data.role}
                  onChange={handleChange}
                  required
                  className="border border-gray-300 p-[6px_10px] text-[0.9rem] w-full outline-none focus:border-gray-400  "
                >
                  <option value="">Chọn chức vụ</option>
                  <option value="0">Quản trị viên hệ thống</option>
                  <option value="1">Nhân viên bán hàng</option>
                </select>
              </div>

              <div className="flex flex-col gap-1 w-full">
                <label htmlFor="" className="text-[0.9rem] font-medium">
                 Tình trạng
                </label>
                <select
                  name="status"
                  value={data.status}
                  onChange={handleChange}
                  required
                  className="border border-gray-300 p-[6px_10px] text-[0.9rem] w-full outline-none focus:border-gray-400  "
                >
                  <option value="">Chọn tình trạng</option>
                  <option value="1">Bình thường</option>
                  <option value="0">Bị chặn</option>
                </select>
              </div>
            </div>

            <div className="flex flex-col gap-1">
              <label htmlFor="" className="text-[0.9rem] font-medium">
               Mật khẩu mới
              </label>
              <input
                type="password"
                name="password"
                value={data.password}
                onChange={handleChange}
                className="border border-gray-300 p-[6px_10px] text-[0.9rem] w-full outline-none focus:border-gray-400  "
              />
            </div>
          </div>
        </div>

        <div className="flex justify-center gap-6">
          <button
            type="submit"
            className="p-[6px_10px] bg-teal-500 text-white text-[0.9rem] font-medium text-center hover:bg-teal-600 rounded-sm"
          >
            {isLoadingUpdate ? "Đang cập nhật..." : "Cập nhật"}
          </button>
          <Link
            to="/admin/admins"
            className="p-[6px_10px] bg-red-500 text-white text-[0.9rem] text-center hover:bg-red-600 rounded-sm"
          >
            Trờ về
          </Link>
        </div>
      </form>
    </div>
  );
}

export default EditAdmin;
