import InputImage from "../InputImage";
import { useCallback, useState } from "react";
import toast from "react-hot-toast";
import TextBoxEditor from "../TextBoxEditor/TextBoxEditor";
import { Link } from "react-router-dom";
import { useInputImage } from "../../../hooks/admin/useInputImage";
import useAddBook from "../../../hooks/admin/useAddBook";
import useGetCategories1 from "../../../hooks/admin/useGetCategories1";
import useGetAuthors1 from "../../../hooks/admin/useGetAuthors1";
import useGetPublishers1 from "../../../hooks/admin/useGetPublishers1";

function AddBook() {
  const { addBook, isLoading } = useAddBook();
  const { categories } = useGetCategories1();
  const { authors } = useGetAuthors1();
  const { publishers } = useGetPublishers1();

  const [data, setData] = useState({
    title: "",
    price: 1,
    discount: 0,
    description: "",
    publicationDate: "",
    numberOfPages: 1,
    weight: 1,
    width: 1,
    length: 1,
    thickness: 1,
    stock: 1,
    author: "",
    publisher: "",
    category: "",
    status: "",
  });

  const max = 10;

  const {
    previewImages,
    setPreviewImages,
    selectedFiles,
    setSelectedFiles,
    handlePreviewImage,
    handleRemovePreviewImage,
  } = useInputImage(max);

  const handleChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>
  ) => {
    const { name, value } = e.target;
    setData({
      ...data,
      [name]: value,
    });
  };

  const handleDescriptionChange = useCallback((val: string) => {
    setData((prev) => ({ ...prev, description: val }));
  }, []);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (Number(data.price) < Number(data.discount)) {
      toast.error("Số tiền giảm không được lớn hơn giá bán");
      return;
    }

    if (Number(data.discount) < 0) {
      toast.error("Số tiền giảm phải lớn hơn hoặc bằng 0");
      return;
    }

    if (Number(data.price) <= 0) {
      toast.error("Giá bán phải lớn hơn 0");
      return;
    }

    if (Number(data.stock) < 0) {
      toast.error("Số lượng hiện có phải lớn hơn hoặc bằng 0");
      return;
    }

    if (Number(data.numberOfPages) <= 0) {
      toast.error("Số trang phải lớn hơn 0");
      return;
    }

    if (Number(data.weight) <= 0) {
      toast.error("Khối lượng phải lớn hơn 0");
      return;
    }

    if (Number(data.width) <= 0) {
      toast.error("Chiều rộng phải lớn hơn 0");
      return;
    }

    if (Number(data.length) <= 0) {
      toast.error("Chiều dài phải lớn hơn 0");
      return;
    }

    if (Number(data.thickness) <= 0) {
      toast.error("Độ dày phải lớn hơn 0");
      return;
    }

    if (!selectedFiles) {
      toast.error("Vui lòng thêm ít nhất một hình sản phẩm");
      return;
    }

    try {
      const formData = new FormData();

      const bookData = {
        title: data.title,
        price: Number(data.price),
        discount: Number(data.discount),
        description: data.description,
        publicationDate: data.publicationDate,
        numberOfPages: Number(data.numberOfPages),
        weight: Number(data.weight),
        width: Number(data.width),
        length: Number(data.length),
        thickness: Number(data.thickness),
        stock: Number(data.stock),
        status: data.status,
        author: { id: data.author },
        publisher: { id: data.publisher },
        category: { id: data.category },
      };

      formData.append(
        "book",
        new Blob([JSON.stringify(bookData)], { type: "application/json" })
      );

      selectedFiles.forEach((file) => {
        formData.append("files", file);
      });

      await addBook(formData);
      setPreviewImages([]);
      setSelectedFiles([]);
      setData({
        title: "",
        price: 1,
        discount: 0,
        description: "",
        publicationDate: "",
        numberOfPages: 1,
        weight: 1,
        width: 1,
        length: 1,
        thickness: 1,
        stock: 1,
        author: "",
        publisher: "",
        category: "",
        status: "",
      });
    } catch (err: any) {
      toast.error(err?.response?.data?.message);
    }
  };

  return (
    <>
      <div className="py-[30px] sm:px-[25px] px-[15px] bg-[#F1F4F9] h-auto">
        <form className="flex flex-col gap-7 w-full" onSubmit={handleSubmit}>
          <h2 className="text-[#74767d]">Thêm sách</h2>

          <div className="flex gap-[25px] w-full flex-col">
            <div className="md:p-[25px] p-[15px] bg-white rounded-md w-full">
              <InputImage
                InputId="images-product"
                previewImages={previewImages}
                onPreviewImage={handlePreviewImage}
                onRemovePreviewImage={handleRemovePreviewImage}
                blockIndex={0}
              />
            </div>

            <div className="sm:p-[25px] p-[15px] bg-white rounded-md flex flex-col gap-[20px] w-full">
              <h5 className="font-bold text-[#74767d]">Thông tin chung</h5>

              <div className="flex flex-col gap-1 w-full">
                <label htmlFor="" className="text-[0.9rem] font-medium">
                  Tiêu đề
                </label>
                <input
                  type="text"
                  value={data.title}
                  onChange={handleChange}
                  name="title"
                  required
                  className="border border-gray-300 p-[6px_10px] text-[0.9rem] w-full outline-none focus:border-gray-400  "
                />
              </div>

              <div className="flex flex-wrap md:flex-nowrap gap-[15px]">
                <div className="flex flex-col gap-1 w-full">
                  <label htmlFor="" className="text-[0.9rem] font-medium">
                    Danh mục
                  </label>
                  <select
                    name="category"
                    required
                    onChange={handleChange}
                    value={data.category}
                    className="border border-gray-300 p-[6px_10px] text-[0.9rem] w-full outline-none focus:border-gray-400  "
                  >
                    <option value="">Chọn danh mục</option>
                    {categories.map((category) => (
                      <option value={category.id} key={category.id}>
                        {category.name}
                      </option>
                    ))}
                  </select>
                </div>

                <div className="flex flex-col gap-1 w-full">
                  <label htmlFor="" className="text-[0.9rem] font-medium">
                    Tác giả
                  </label>
                  <select
                    name="author"
                    required
                    onChange={handleChange}
                    value={data.author}
                    className="border border-gray-300 p-[6px_10px] text-[0.9rem] w-full outline-none focus:border-gray-400  "
                  >
                    <option value="">Chọn tác giả</option>
                    {authors.map((author) => (
                      <option value={author.id} key={author.id}>
                        {author.fullname}
                      </option>
                    ))}
                  </select>
                </div>
              </div>

              <div className="flex flex-wrap md:flex-nowrap gap-[15px]">
                <div className="flex flex-col gap-1 w-full">
                  <label htmlFor="" className="text-[0.9rem] font-medium">
                    Nhà xuất bản
                  </label>
                  <select
                    name="publisher"
                    required
                    onChange={handleChange}
                    value={data.publisher}
                    className="border border-gray-300 p-[6px_10px] text-[0.9rem] w-full outline-none focus:border-gray-400  "
                  >
                    <option value="">Chọn nhà xuất bản</option>
                    {publishers.map((publisher) => (
                      <option value={publisher.id} key={publisher.id}>
                        {publisher.name}
                      </option>
                    ))}
                  </select>
                </div>

                <div className="flex flex-col gap-1 w-full">
                  <label htmlFor="" className="text-[0.9rem] font-medium">
                    Tình trạng
                  </label>
                  <select
                    name="status"
                    required
                    onChange={handleChange}
                    value={data.status}
                    className="border border-gray-300 p-[6px_10px] text-[0.9rem] w-full outline-none focus:border-gray-400  "
                  >
                    <option value="">Chọn tình trạng</option>
                    <option value="0">Ẩn</option>
                    <option value="1">Hiện</option>
                  </select>
                </div>
              </div>

              <div className="flex flex-col gap-1">
                <label htmlFor="" className="text-[0.9rem] font-medium">
                  Mô tả
                </label>
                <TextBoxEditor
                  content={data.description}
                  onChange={handleDescriptionChange}
                />
              </div>
            </div>

            <div className="sm:p-[25px] p-[15px] bg-white rounded-md flex flex-col gap-[20px] w-full">
              <h5 className="font-bold text-[#74767d]">Giá cả</h5>

              <div className="flex flex-wrap md:flex-nowrap gap-[15px]">
                <div className="flex flex-col gap-1 w-full">
                  <label htmlFor="" className="text-[0.9rem] font-medium">
                    Giá bán
                  </label>
                  <input
                    type="number"
                    name="price"
                    inputMode="numeric"
                    value={data.price}
                    onChange={handleChange}
                    required
                    className="border border-gray-300 p-[6px_10px] text-[0.9rem] w-full outline-none focus:border-gray-400  "
                  />
                </div>

                <div className="flex flex-col gap-1 w-full">
                  <label htmlFor="" className="text-[0.9rem] font-medium">
                    Số tiền giảm (Giảm giá{" "}
                    {Math.floor((data.discount / data.price) * 100)}%)
                  </label>
                  <input
                    type="number"
                    name="discount"
                    inputMode="numeric"
                    value={data.discount}
                    onChange={handleChange}
                    required
                    className="border border-gray-300 p-[6px_10px] text-[0.9rem] w-full outline-none focus:border-gray-400  "
                  />
                </div>

                <div className="flex flex-col gap-1 w-full">
                  <label htmlFor="" className="text-[0.9rem] font-medium">
                    Số lượng hiện có
                  </label>
                  <input
                    type="number"
                    name="stock"
                    inputMode="numeric"
                    value={data.stock}
                    onChange={handleChange}
                    required
                    className="border border-gray-300 p-[6px_10px] text-[0.9rem] w-full outline-none focus:border-gray-400  "
                  />
                </div>
              </div>
            </div>

            <div className="sm:p-[25px] p-[15px] bg-white rounded-md flex flex-col gap-[20px] w-full">
              <h5 className="font-bold text-[#74767d]">Thông tin chi tiết</h5>

              <div className="flex gap-[15px]">
                <div className="flex flex-col gap-1 w-full">
                  <label htmlFor="" className="text-[0.9rem] font-medium">
                    Ngày xuất bản
                  </label>
                  <input
                    type="date"
                    value={data.publicationDate}
                    onChange={handleChange}
                    name="publicationDate"
                    required
                    className="border border-gray-300 p-[6px_10px] text-[0.9rem] w-full outline-none focus:border-gray-400  "
                  />
                </div>

                <div className="flex flex-col gap-1 w-full">
                  <label htmlFor="" className="text-[0.9rem] font-medium">
                    Số trang
                  </label>
                  <input
                    type="number"
                    inputMode="numeric"
                    value={data.numberOfPages}
                    onChange={handleChange}
                    name="numberOfPages"
                    required
                    className="border border-gray-300 p-[6px_10px] text-[0.9rem] w-full outline-none focus:border-gray-400  "
                  />
                </div>
              </div>

              <div className="flex gap-[15px]">
                <div className="flex flex-col gap-1 w-full">
                  <label htmlFor="" className="text-[0.9rem] font-medium">
                    Chiều rộng (cm)
                  </label>
                  <input
                    type="number"
                    name="width"
                    inputMode="numeric"
                    value={data.width}
                    onChange={handleChange}
                    required
                    className="border border-gray-300 p-[6px_10px] text-[0.9rem] w-full outline-none focus:border-gray-400  "
                  />
                </div>

                <div className="flex flex-col gap-1 w-full">
                  <label htmlFor="" className="text-[0.9rem] font-medium">
                    Chiều dài (cm)
                  </label>
                  <input
                    type="number"
                    name="length"
                    inputMode="numeric"
                    value={data.length}
                    onChange={handleChange}
                    required
                    className="border border-gray-300 p-[6px_10px] text-[0.9rem] w-full outline-none focus:border-gray-400  "
                  />
                </div>
              </div>

              <div className="flex gap-[15px]">
                <div className="flex flex-col gap-1 w-full">
                  <label htmlFor="" className="text-[0.9rem] font-medium">
                    Độ dày (cm)
                  </label>
                  <input
                    type="number"
                    name="thickness"
                    inputMode="numeric"
                    value={data.thickness}
                    onChange={handleChange}
                    required
                    className="border border-gray-300 p-[6px_10px] text-[0.9rem] w-full outline-none focus:border-gray-400  "
                  />
                </div>

                <div className="flex flex-col gap-1 w-full">
                  <label htmlFor="" className="text-[0.9rem] font-medium">
                    Khối lượng (gr)
                  </label>
                  <input
                    type="number"
                    name="weight"
                    inputMode="numeric"
                    value={data.weight}
                    onChange={handleChange}
                    required
                    className="border border-gray-300 p-[6px_10px] text-[0.9rem] w-full outline-none focus:border-gray-400  "
                  />
                </div>
              </div>
            </div>
          </div>

          <div className="flex justify-center gap-6">
            <button
              disabled={isLoading}
              type="submit"
              className="p-[6px_10px] bg-teal-500 text-white text-[0.9rem] font-medium text-center hover:bg-teal-600 rounded-sm"
            >
              {isLoading ? "Đang thêm..." : "Thêm"}
            </button>
            <Link
              to="/admin/books"
              className="p-[6px_10px] bg-red-500 text-white text-[0.9rem] text-center hover:bg-red-600 rounded-sm"
            >
              Trở vể
            </Link>
          </div>
        </form>
      </div>
    </>
  );
}

export default AddBook;
