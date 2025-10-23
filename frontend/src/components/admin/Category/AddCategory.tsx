import { useState } from "react";
import toast from "react-hot-toast";
import { Link } from "react-router-dom";
import useAddCategory from "../../../hooks/admin/useAddCategory";

function AddCategory() {
  const [data, setData] = useState({
    name: "",
    status: "",
  });

  const { addCategory, isLoading } = useAddCategory();

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
      await addCategory({
        name: data.name.trim(),
        status: Number(data.status),
      });

      setData({
        name: "",
        status: "",
      });
    } catch (err: any) {
      toast.error(err?.response?.data?.message);
    }
  };

  return (
    <>
      <div className="py-[30px] sm:px-[25px] px-[15px] bg-[#F1F4F9] h-full">
        <form className="flex flex-col gap-7 w-full" onSubmit={handleSubmit}>
          <h2 className="text-[#74767d]">Add category</h2>

          <div className="flex gap-[25px] w-full flex-col">
            <div className="md:p-[25px] p-[15px] bg-white rounded-md flex flex-col gap-[15px] w-full">
              <h5 className="font-bold text-[#74767d]">General information</h5>

              <div className="flex flex-col gap-1">
                <label htmlFor="" className="text-[0.9rem] font-medium">
                  Name
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

              <div className="flex flex-col gap-1">
                <label htmlFor="" className="text-[0.9rem] font-medium">
                  Status
                </label>
                <select
                  name="status"
                  required
                  onChange={handleChange}
                  value={data.status}
                  className="border border-gray-300 p-[6px_10px] text-[0.9rem] w-full outline-none focus:border-gray-400  "
                >
                  <option value="">Select status</option>
                  <option value="0">Hidden</option>
                  <option value="1">Show</option>
                </select>
              </div>
            </div>
          </div>

          <div className="flex justify-center gap-6">
            <button
              disabled={isLoading}
              type="submit"
              className="p-[6px_10px] bg-teal-500 text-white text-[0.9rem] font-medium text-center hover:bg-teal-600 rounded-sm"
            >
              {isLoading ? "Adding..." : "Add"}
            </button>
            <Link
              to="/admin/categories"
              className="p-[6px_10px] bg-red-500 text-white text-[0.9rem] text-center hover:bg-red-600 rounded-sm"
            >
              Back
            </Link>
          </div>
        </form>
      </div>
    </>
  );
}

export default AddCategory;
