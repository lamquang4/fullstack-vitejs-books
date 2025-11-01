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
import useGetBook from "../../../hooks/admin/useGetBook";
import { useInputImage } from "../../../hooks/admin/useInputImage";
import { useInputImage1 } from "../../../hooks/admin/useInputImage1";
import useUpdateBook from "../../../hooks/admin/useUpdateBook";
import useDeleteImageBook from "../../../hooks/admin/useDeleteImageBook";
import useUpdateImageBook from "../../../hooks/admin/useUpdateImagesBook";
import useGetCategories1 from "../../../hooks/admin/useGetCategories1";
import useGetAuthors1 from "../../../hooks/admin/useGetAuthors1";
import useGetPublishers1 from "../../../hooks/admin/useGetPublishers1";

function EditBook() {
  const navigate = useNavigate();
  const { id } = useParams();

  const { categories } = useGetCategories1();
  const { authors } = useGetAuthors1();
  const { publishers } = useGetPublishers1();
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

  const max = 10;

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
      toast.error("Sách không tìm thấy");
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
        "Bạn không thể xóa hình này vì mỗi sách phải có ít nhất một hình ảnh"
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

    if (Number(data.price) < Number(data.discount)) {
      toast.error("Số tiền giảm không được lớn hơn giá gốc");
      return;
    }

    if (Number(data.discount) < 0) {
      toast.error("Số tiền giảm phải lớn hơn hoặc bằng 0");
      return;
    }

    if (Number(data.price) <= 0) {
      toast.error("Giá gốc phải lớn hơn 0");
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
          <h2 className="text-[#74767d]">Chỉnh sửa sách</h2>

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
                    Giá gốc
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
              disabled={isLoadingUpdate}
              type="submit"
              className="p-[6px_10px] bg-teal-500 text-white text-[0.9rem] font-medium text-center hover:bg-teal-600 rounded-sm"
            >
              {isLoadingUpdate ? "Đang cập nhật..." : "Cập nhật"}
            </button>
            <Link
              to="/admin/books"
              className="p-[6px_10px] bg-red-500 text-white text-[0.9rem] text-center hover:bg-red-600 rounded-sm"
            >
              Trở về
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
