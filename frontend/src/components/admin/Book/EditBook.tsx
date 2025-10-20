import { useCallback, useEffect, useState } from "react";
import { Swiper, SwiperSlide } from "swiper/react";
import "swiper/css";
import { FreeMode } from "swiper/modules";
import InputImage from "../InputImage";
import Image from "../../Image";
import ImageViewer from "../../ImageViewer";
import { HiMiniXMark } from "react-icons/hi2";
import toast from "react-hot-toast";
import TextBoxEditor from "../TextBoxEditor/TextBoxEditor";
import { Link, useNavigate, useParams } from "react-router-dom";
import { VscTrash } from "react-icons/vsc";
import InputImage1 from "../InputImage1";
import useGetCategories from "../../../hooks/admin/useGetCategories";
import useGetAuthors from "../../../hooks/admin/useGetAuthors";
import useGetPublishers from "../../../hooks/admin/useGetPublishers";
import useGetBook from "../../../hooks/admin/useGetBook";
import { useInputImage } from "../../../hooks/admin/useInputImage";
import { useInputImage1 } from "../../../hooks/admin/useInputImage1";
import useUpdateBook from "../../../hooks/admin/useUpdateBook";
import useDeleteImageBook from "../../../hooks/admin/useDeleteImageBook";
import useUpdateImageBook from "../../../hooks/admin/useUpdateImagesBook";

function EditBook() {
  const navigate = useNavigate();
  const { id } = useParams();

  const { categories } = useGetCategories();
  const { authors } = useGetAuthors();
  const { publishers } = useGetPublishers();
  const { book, isLoading, mutate } = useGetBook(id as string);
  const { updateBook, isLoading: isLoadingUpdate } = useUpdateBook(
    id as string
  );
  const { deleteImageBook } = useDeleteImageBook();
  const { updateImagesBook } = useUpdateImageBook();

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
  const [openViewer, setOpenViewer] = useState<boolean>(false);
  const [viewerImage, setViewerImage] = useState<string>("");

  const max = 20;

  const {
    previewImages,
    setPreviewImages,
    selectedFiles,
    setSelectedFiles,
    handlePreviewImage,
    handleRemovePreviewImage,
  } = useInputImage(max);

  const {
    previewImages1,
    selectedFiles1,
    setPreviewImages1,
    setSelectedFiles1,
    onFileSelect,
    handleClear,
  } = useInputImage1();

  const handleOpenViewer = (image: string) => {
    setViewerImage(image);
    setOpenViewer(true);
  };

  useEffect(() => {
    if (isLoading) return;

    if (!book) {
      toast.error("Book not found");
      navigate("/admin/books");
      return;
    }

    setData({
      title: book.title || "",
      price: book.price || 1,
      discount: book.discount || 0,
      description: book.description || "",
      publicationDate: book.publicationDate?.slice(0, 10) || "",
      numberOfPages: book.numberOfPages || 1,
      weight: book.weight || 1,
      width: book.width || 1,
      length: book.length || 1,
      thickness: book.thickness || 1,
      stock: book.stock || 1,
      author: book.author?.id || "",
      publisher: book.publisher?.id || "",
      category: book.category?.id || "",
      status: book.status?.toString() || "",
    });
  }, [isLoading, book, navigate]);

  const handleChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>
  ) => {
    const { name, value } = e.target;
    setData((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleDescriptionChange = useCallback((val: string) => {
    setData((prev) => ({ ...prev, description: val }));
  }, []);

  const handleDeleteImage = async (imageId: string) => {
    if (book?.images.length === 1) {
      toast.error(
        "You cannot delete this image because the book must have at least one image"
      );
      return;
    }

    try {
      await deleteImageBook(imageId);
      mutate();
    } catch (err: any) {
      toast.error(err?.response?.data?.msg);
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (data.price <= 0) {
      toast.error("Price must be greater than 0");
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

      await updateBook(formData);

      if (selectedFiles1.length > 0) {
        const newFiles = selectedFiles1.filter((f) => f !== null);
        if (newFiles.length > 0) {
          const oldImageIds = book?.images
            .map((img, index) => (selectedFiles1[index] ? img.id : null))
            .filter((id) => id !== null);

          const formData = new FormData();
          selectedFiles1.forEach((file) => formData.append("files", file));
          oldImageIds?.forEach((id) => formData.append("oldImageIds", id));

          await updateImagesBook(formData);
        }
      }

      mutate();
      setPreviewImages([]);
      setSelectedFiles([]);
      setPreviewImages1([]);
      setSelectedFiles1([]);
    } catch (err: any) {
      toast.error(err?.response?.data?.message);
      mutate();
    }
  };

  return (
    <>
      <div className="py-[30px] sm:px-[25px] px-[15px] bg-[#F1F4F9] h-auto">
        <form className="flex flex-col gap-7 w-full" onSubmit={handleSubmit}>
          <h2 className="text-[#74767d]">Edit book</h2>

          <div className="flex gap-[25px] w-full flex-col">
            <div className="md:p-[25px] p-[15px] bg-white rounded-md w-full space-y-[20px]">
              <InputImage
                InputId="images-Book"
                previewImages={previewImages}
                onPreviewImage={handlePreviewImage}
                onRemovePreviewImage={handleRemovePreviewImage}
                blockIndex={0}
              />

              <div className="flex justify-center items-center">
                <Swiper
                  spaceBetween={15}
                  slidesPerView={"auto"}
                  modules={[FreeMode]}
                  grabCursor={true}
                >
                  {book?.images.map((image, index) => (
                    <SwiperSlide key={index} className="!w-auto relative">
                      <div
                        className="w-[150px] border border-gray-300 cursor-pointer"
                        onClick={() => {
                          if (image)
                            handleOpenViewer(
                              `${import.meta.env.VITE_BACKEND_URL}${
                                image.image
                              }`
                            );
                        }}
                      >
                        <Image
                          source={
                            previewImages1?.[index]
                              ? previewImages1[index]
                              : `${import.meta.env.VITE_BACKEND_URL}${
                                  image.image
                                }`
                          }
                          alt={book.title}
                          className="w-full h-full"
                          loading="lazy"
                        />
                      </div>

                      <div className="absolute top-[6px] right-[6px]">
                        <div className="flex items-center flex-col gap-2">
                          {!previewImages1?.[index] ? (
                            <>
                              <button
                                className="bg-white rounded-full p-1 border border-gray-300"
                                onClick={() => handleDeleteImage(image.id)}
                                type="button"
                              >
                                <VscTrash
                                  size={22}
                                  className="text-[#d9534f]"
                                />
                              </button>
                              <InputImage1
                                InputId={`img-${index}`}
                                sizeIcon={22}
                                imageIndex={index}
                                onFileSelect={onFileSelect}
                              />
                            </>
                          ) : (
                            <button
                              type="button"
                              className="p-2"
                              onClick={() => handleClear(index)}
                            >
                              <HiMiniXMark size={26} />
                            </button>
                          )}
                        </div>
                      </div>
                    </SwiperSlide>
                  ))}
                </Swiper>
              </div>
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
              disabled={isLoadingUpdate}
              type="submit"
              className="p-[6px_10px] bg-teal-500 text-white text-[0.9rem] font-medium text-center hover:bg-teal-600 rounded-sm"
            >
              {isLoadingUpdate ? "Updating..." : "Update"}
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

      {openViewer && (
        <ImageViewer
          image={viewerImage}
          open={openViewer}
          onClose={() => setOpenViewer(false)}
        />
      )}
    </>
  );
}

export default EditBook;
