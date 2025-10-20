import InputImage from "../InputImage";
import { useCallback, useState } from "react";
import toast from "react-hot-toast";
import TextBoxEditor from "../TextBoxEditor/TextBoxEditor";
import { Link } from "react-router-dom";
import { useInputImage } from "../../../hooks/admin/useInputImage";
import useAddBook from "../../../hooks/admin/useAddBook";
import useGetCategories from "../../../hooks/admin/useGetCategories";
import useGetAuthors from "../../../hooks/admin/useGetAuthors";
import useGetPublishers from "../../../hooks/admin/useGetPublishers";

function AddBook() {
  const { addBook, isLoading } = useAddBook();
  const { categories } = useGetCategories();
  const { authors } = useGetAuthors();
  const { publishers } = useGetPublishers();

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

    if (data.discount > data.price) {
      toast.error("Discount cannot be greater than price");
      return;
    }

    if (data.discount < 0) {
      toast.error("Discount amount must be greater than or equal 0");
      return;
    }

    if (data.price <= 0) {
      toast.error("Price must be greater than 0");
      return;
    }

    if (data.stock < 0) {
      toast.error("Stock must be greater than or equal 0");
      return;
    }

    if (data.numberOfPages <= 0) {
      toast.error("Number of pages must be greater than 0");
      return;
    }

    if (data.weight <= 0) {
      toast.error("Weight must be greater than 0");
      return;
    }

    if (data.width <= 0) {
      toast.error("Width must be greater than 0");
      return;
    }

    if (data.length <= 0) {
      toast.error("Length must be greater than 0");
      return;
    }

    if (data.thickness <= 0) {
      toast.error("Thickness must be greater than 0");
      return;
    }

    if (!selectedFiles) {
      toast.error("Please select at least one image");
      return;
    }

    try {
      const formData = new FormData();

      const bookData = {
        title: data.title,
        price: data.price,
        discount: data.discount,
        description: data.description,
        publicationDate: data.publicationDate,
        numberOfPages: data.numberOfPages,
        weight: data.weight,
        width: data.width,
        length: data.length,
        thickness: data.thickness,
        stock: data.stock,
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
          <h2 className="text-[#74767d]">Add book</h2>

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
              <h5 className="font-bold text-[#74767d]">General information</h5>

              <div className="flex flex-col gap-1 w-full">
                <label htmlFor="" className="text-[0.9rem] font-medium">
                  Title
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
                    Category
                  </label>
                  <select
                    name="category"
                    required
                    onChange={handleChange}
                    value={data.category}
                    className="border border-gray-300 p-[6px_10px] text-[0.9rem] w-full outline-none focus:border-gray-400  "
                  >
                    <option value="">Select category</option>
                    {categories.map((category) => (
                      <option value={category.id} key={category.id}>
                        {category.name}
                      </option>
                    ))}
                  </select>
                </div>

                <div className="flex flex-col gap-1 w-full">
                  <label htmlFor="" className="text-[0.9rem] font-medium">
                    Author
                  </label>
                  <select
                    name="author"
                    required
                    onChange={handleChange}
                    value={data.author}
                    className="border border-gray-300 p-[6px_10px] text-[0.9rem] w-full outline-none focus:border-gray-400  "
                  >
                    <option value="">Select author</option>
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
                    Publisher
                  </label>
                  <select
                    name="publisher"
                    required
                    onChange={handleChange}
                    value={data.publisher}
                    className="border border-gray-300 p-[6px_10px] text-[0.9rem] w-full outline-none focus:border-gray-400  "
                  >
                    <option value="">Select publisher</option>
                    {publishers.map((publisher) => (
                      <option value={publisher.id} key={publisher.id}>
                        {publisher.name}
                      </option>
                    ))}
                  </select>
                </div>

                <div className="flex flex-col gap-1 w-full">
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

              <div className="flex flex-col gap-1">
                <label htmlFor="" className="text-[0.9rem] font-medium">
                  Description
                </label>
                <TextBoxEditor
                  content={data.description}
                  onChange={handleDescriptionChange}
                />
              </div>
            </div>

            <div className="sm:p-[25px] p-[15px] bg-white rounded-md flex flex-col gap-[20px] w-full">
              <h5 className="font-bold text-[#74767d]">Pricing</h5>

              <div className="flex flex-wrap md:flex-nowrap gap-[15px]">
                <div className="flex flex-col gap-1 w-full">
                  <label htmlFor="" className="text-[0.9rem] font-medium">
                    Price
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
                    Discount amount (Discount{" "}
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
                    Stock
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
              <h5 className="font-bold text-[#74767d]">
                Book detail information
              </h5>

              <div className="flex gap-[15px]">
                <div className="flex flex-col gap-1 w-full">
                  <label htmlFor="" className="text-[0.9rem] font-medium">
                    Publication date
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
                    Number of pages
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
                    Width (cm)
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
                    Length (cm)
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
                    Thickness (cm)
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
                    Weight (gr)
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
              {isLoading ? "Adding..." : "Add"}
            </button>
            <Link
              to="/admin/books"
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

export default AddBook;
