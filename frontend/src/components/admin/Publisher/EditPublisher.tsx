import { useEffect, useState } from "react";
import toast from "react-hot-toast";
import { Link, useNavigate, useParams } from "react-router-dom";
import useGetPublisher from "../../../hooks/admin/useGetPublisher";
import useUpdatePublisher from "../../../hooks/admin/useUpdatePublisher";
function EditPublisher() {
  const navigate = useNavigate();
  const { id } = useParams();

  const [data, setData] = useState({
    name: "",
  });

  const { publisher, isLoading, mutate } = useGetPublisher(id as string);
  const { updatePublisher, isLoading: isLoadingUpdate } = useUpdatePublisher(
    id as string
  );

  useEffect(() => {
    if (isLoading) return;

    if (!publisher) {
      toast.error("Nhà xuất bản không tìm thấy");
      navigate("/admin/publishers");
    }
  }, [isLoading, publisher, navigate]);

  useEffect(() => {
    if (publisher) {
      setData({
        name: publisher.name || "",
      });
    }
  }, [publisher]);

  const handleChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>
  ) => {
    const { name, value } = e.target;
    setData({
      ...data,
      [name]: value,
    });
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    try {
      await updatePublisher(data);
      mutate();
    } catch (err: any) {
      toast.error(err?.response?.data?.message);
      mutate();
    }
  };

  return (
    <>
      <div className="py-[30px] sm:px-[25px] px-[15px] bg-[#F1F4F9] h-full">
        <form className="flex flex-col gap-7 w-full" onSubmit={handleSubmit}>
          <h2 className="text-[#74767d]">Chỉnh sửa nhà xuất bản</h2>

          <div className="flex gap-[25px] w-full flex-col">
            <div className="md:p-[25px] p-[15px] bg-white rounded-md flex flex-col gap-[15px] w-full">
              <p className="font-bold text-[1rem] text-[#74767d]">
                Thông tin chung
              </p>

              <div className="flex flex-col gap-1">
                <label htmlFor="" className="text-[0.9rem] font-medium">
                  Tên
                </label>
                <input
                  type="text"
                  name="name"
                  value={data.name}
                  onChange={handleChange}
                  required
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
              to="/admin/publishers"
              className="p-[6px_10px] bg-red-500 text-white text-[0.9rem] text-center hover:bg-red-600 rounded-sm"
            >
              Trở về
            </Link>
          </div>
        </form>
      </div>
    </>
  );
}

export default EditPublisher;
