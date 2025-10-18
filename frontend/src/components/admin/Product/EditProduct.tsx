import { useCallback, useEffect, useState } from "react";
import InputImage from "../InputImage";
import Image from "../../Image";
import ImageViewer from "../../ImageViewer";
import { HiMiniXMark } from "react-icons/hi2";
import toast from "react-hot-toast";
import { GoTrash } from "react-icons/go";
import TextBoxEditor from "../TextBoxEditor/TextBoxEditor";
import { Link, useNavigate, useParams } from "react-router-dom";
import { useInputImage } from "../../../hooks/admin/useInputImage";
import { useInputImage1 } from "../../../hooks/admin/useInputImage1";
import { VscTrash } from "react-icons/vsc";
import InputImage1 from "../InputImage1";

function EditProduct() {
  const navigate = useNavigate();
  const { id } = useParams();

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

  const max = 6;

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

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
  };

  return (
    <>
      <div className="py-[30px] sm:px-[25px] px-[15px] bg-[#F1F4F9] h-auto">
        <form className="flex flex-col gap-7 w-full" onSubmit={handleSubmit}>
          <h2 className="text-[#74767d]">Edit book</h2>

          <div className="flex gap-[25px] w-full flex-col">
            <div className="md:p-[25px] p-[15px] bg-white rounded-md w-full">
              <InputImage
                InputId="images-product"
                previewImages={previewImages}
                onPreviewImage={handlePreviewImage}
                onRemovePreviewImage={handleRemovePreviewImage}
                blockIndex={0}
              />

              <div className="flex gap-3 flex-wrap justify-center">
                {product?.images?.map((img, imgIndex) => (
                  <div className="relative" key={imgIndex}>
                    <div
                      className="cursor-pointer"
                      onClick={(e) => {
                        e.stopPropagation();
                        e.preventDefault();
                        handleOpenViewer(img);
                      }}
                    >
                      <Image
                        source={previewImages1?.[imgIndex] || img}
                        alt=""
                        className="w-full max-w-[140px]"
                        loading="eager"
                      />
                    </div>

                    <div className="absolute top-[6px] right-[6px]">
                      <div className="flex items-center flex-col gap-2">
                        {!previewImages1?.[imgIndex] ? (
                          <>
                            <button
                              className="bg-white rounded-full p-1 border border-gray-300"
                              type="button"
                            >
                              <VscTrash size={22} className="text-[#d9534f]" />
                            </button>

                            <InputImage1
                              onFileSelect={(file) =>
                                onFileSelect(file, imgIndex)
                              }
                              InputId={`c-${imgIndex}`}
                              sizeIcon={22}
                            />
                          </>
                        ) : (
                          <button
                            type="button"
                            className="p-2"
                            onClick={() => handleClear(index, imgIndex)}
                          >
                            <HiMiniXMark size={26} />
                          </button>
                        )}
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            </div>

            <div className="sm:p-[25px] p-[15px] bg-white rounded-md flex flex-col gap-[20px] w-full">
              <h5 className="font-bold text-[#74767d]">General information</h5>

              <div className="flex flex-wrap md:flex-nowrap gap-[15px]">
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
                      <option value={category._id} key={category._id}>
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
                      <option value={author._id} key={author._id}>
                        {author.fullname}
                      </option>
                    ))}
                  </select>
                </div>

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
                      <option value={publisher._id} key={publisher._id}>
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

              <div className="flex flex-wrap md:flex-nowrap gap-[15px]">
                <div className="flex gap-[15px] flex-wrap">
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

                <div className="flex gap-[15px] flex-wrap">
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

                <div className="flex gap-[15px] flex-wrap">
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
                      Weight (kg)
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
          </div>

          <div className="flex justify-center gap-6">
            <button
              disabled={isLoading}
              type="submit"
              className="p-[6px_10px] bg-teal-500 text-white text-[0.9rem] font-medium text-center hover:bg-teal-600 rounded-sm"
            >
              {isLoading ? "Updating..." : "Update"}
            </button>
            <Link
              to="/admin/products"
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

export default EditProduct;
