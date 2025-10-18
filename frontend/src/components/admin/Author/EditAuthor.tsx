import { useEffect, useState } from "react";
import toast from "react-hot-toast";
import { Link, useNavigate, useParams } from "react-router-dom";
import useGetAuthor from "../../../hooks/admin/useGetAuthor";
import useUpdateAuthor from "../../../hooks/admin/useUpdateAuthor";
function EditAuthor() {
  const navigate = useNavigate();
  const { id } = useParams();

  const { author, isLoading, mutate } = useGetAuthor(id as string);
  const { updateAuthor, isLoading: isLoadingUpdate } = useUpdateAuthor(
    id as string
  );

  const [data, setData] = useState({
    fullname: "",
  });

  useEffect(() => {
    if (isLoading) return;

    if (!author) {
      toast.error("Author not found");
      navigate("/admin/authors");
    }

    if (author) {
      setData({
        fullname: author.fullname || "",
      });
    }
  }, [isLoading, author, navigate]);

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
      await updateAuthor(data);
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
          <h2 className="text-[#74767d]">Edit author</h2>

          <div className="flex gap-[25px] w-full flex-col">
            <div className="md:p-[25px] p-[15px] bg-white rounded-md flex flex-col gap-[15px] w-full">
              <p className="font-bold text-[1rem] text-[#74767d]">
                General information
              </p>

              <div className="flex flex-col gap-1">
                <label htmlFor="" className="text-[0.9rem] font-medium">
                  Fullname
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
            </div>
          </div>

          <div className="flex justify-center gap-6">
            <button
              disabled={isLoadingUpdate}
              type="submit"
              className="p-[6px_10px] bg-teal-500 text-white text-[0.9rem] font-medium text-center hover:bg-teal-600 rounded-sm"
            >
              {isLoadingUpdate ? "Updating..." : "Update"}
            </button>
            <Link
              to="/admin/authors"
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

export default EditAuthor;
