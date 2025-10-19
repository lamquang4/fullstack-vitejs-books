import { useEffect, useState } from "react";
import type { Book } from "../../../../types/type";
import ImageViewer from "../../../ImageViewer";
import Image from "../../../Image";
import toast from "react-hot-toast";
import { GrNext, GrPrevious } from "react-icons/gr";
import { HiOutlineMinusSmall } from "react-icons/hi2";
import { HiOutlinePlusSmall } from "react-icons/hi2";

type Props = {
  book: Book;
};

function BookDetail({ book }: Props) {
  const [quantity, setQuantity] = useState<number>(1);
  const [mainImage, setMainImage] = useState<string>("");
  const [openViewer, setOpenViewer] = useState<boolean>(false);
  const [viewerImage, setViewerImage] = useState<string>("");
  const [currentImageIndex, setCurrentImageIndex] = useState<number>(0);

  useEffect(() => {
    if (book?.images?.length > 0) {
      setMainImage(book.images[0].image);
      setCurrentImageIndex(0);
    }
  }, [book]);

  const handleNextImage = (e: React.MouseEvent<HTMLButtonElement>) => {
    e.stopPropagation();
    setCurrentImageIndex((prev) => {
      const newIndex = (prev + 1) % book.images.length;
      setMainImage(book.images[newIndex].image);
      return newIndex;
    });
  };

  const handlePrevImage = (e: React.MouseEvent<HTMLButtonElement>) => {
    e.stopPropagation();
    setCurrentImageIndex((prev) => {
      const newIndex = (prev - 1 + book.images.length) % book.images.length;
      setMainImage(book.images[newIndex].image);
      return newIndex;
    });
  };

  const handleOpenViewer = (image: string) => {
    setViewerImage(image);
    setOpenViewer(true);
  };

  const HandleIncrement = () => {
    const maxQuantity = book?.stock! > 15 ? 15 : book?.stock!;
    setQuantity((prev) => (prev < maxQuantity ? prev + 1 : prev));
  };

  const HandleDecrement = () => {
    setQuantity((prev) => (prev > 1 ? prev - 1 : prev));
  };
  return (
    <section className="w-full mb-[40px]">
      <div className="mx-auto w-full max-w-[1350px]">
        <div className="flex flex-col lg:flex-row gap-x-[15px] gap-y-[30px] w-full">
          <div
            className="flex flex-col md:flex-col-reverse xl:flex-row flex-wrap lg:sticky lg:top-[100px]"
            id="div1"
          >
            <div className="md:order-2 relative bg-white">
              <div className="relative group flex justify-center">
                {mainImage && (
                  <div
                    className="cursor-pointer"
                    onClick={(e) => {
                      e.stopPropagation();
                      e.preventDefault();
                      handleOpenViewer(
                        `${import.meta.env.VITE_BACKEND_URL}${mainImage}`
                      );
                    }}
                  >
                    <button
                      type="button"
                      onClick={handleNextImage}
                      className="absolute border border-gray-200 right-1.5 top-1/2 w-11 h-11 bg-white rounded-full flex justify-center items-center -translate-y-1/2 z-10 p-2 xl:opacity-0 xl:group-hover:opacity-100 transition duration-300 hover:bg-black hover:text-white"
                    >
                      <GrNext size={18} />
                    </button>

                    <div className="w-[600px] h-[600px] overflow-hidden">
                      <Image
                        source={`${
                          import.meta.env.VITE_BACKEND_URL
                        }${mainImage}`}
                        alt=""
                        className="w-full h-full object-contain "
                        loading="eager"
                      />
                    </div>

                    <button
                      type="button"
                      onClick={handlePrevImage}
                      className="absolute left-1.5 top-1/2 w-11 h-11 border border-gray-200 bg-white rounded-full flex justify-center items-center -translate-y-1/2 z-10 p-2 xl:opacity-0 xl:group-hover:opacity-100 transition duration-300 hover:bg-black hover:text-white"
                    >
                      <GrPrevious size={18} />
                    </button>
                  </div>
                )}
              </div>
            </div>

            <div className="md:order-1">
              <div className="max-h-[600px] w-full flex flex-row xl:flex-col gap-[15px] overflow-x-auto overflow-y-auto justify-center">
                {book?.images.map((image, index) => (
                  <div
                    key={index}
                    className={`shrink-0 border overflow-hidden cursor-pointer w-[70px] h-[90px] flex items-center justify-center ${
                      `${import.meta.env.VITE_BACKEND_URL}${mainImage}` ===
                      `${import.meta.env.VITE_BACKEND_URL}${image.image}`
                        ? "border-gray-600"
                        : "border-gray-300"
                    }`}
                    onMouseEnter={() => {
                      setMainImage(image.image);
                      setCurrentImageIndex(index);
                    }}
                  >
                    <img
                      src={`${import.meta.env.VITE_BACKEND_URL}${image.image}`}
                      alt=""
                      className="w-full h-full object-cover"
                      loading="eager"
                    />
                  </div>
                ))}
              </div>
            </div>
          </div>

          <div className="relative flex-1 min-w-0 px-[15px]" id="div2">
            <div className="space-y-[10px]">
              <h2>{book?.title}</h2>

              <div className="flex justify-between items-center gap-[15px]">
                <p className="font-medium">
                  Publisher:{" "}
                  <span className="font-semibold">{book.publisher.name}</span>
                </p>

                <p className="font-medium">
                  Author:{" "}
                  <span className="font-semibold">{book.author.fullname}</span>
                </p>
              </div>

              <div className="flex items-center gap-[15px]">
                {book && book?.discount > 0 ? (
                  <>
                    <del className="text-[#707072] font-light text-[1.4rem]">
                      {book?.price.toLocaleString("vi-VN")}₫
                    </del>

                    <h3 className="text-[#c00] font-medium">
                      {(book?.price - book?.discount).toLocaleString("vi-VN")}₫
                    </h3>

                    <p className="text-white p-1.5 bg-[#C62028] rounded-sm font-semibold">
                      -{Math.floor((book.discount / book.price) * 100)}%
                    </p>
                  </>
                ) : (
                  <h3 className="font-medium">
                    {book?.price.toLocaleString("vi-VN")}₫
                  </h3>
                )}
              </div>

              <div className="space-y-[15px]">
                <div className="w-full flex items-center gap-[15px]">
                  <h5 className="font-medium">Quantity:</h5>
                  <div className="relative flex justify-between items-center max-w-[8rem] border border-gray-300 rounded-sm">
                    <button
                      type="button"
                      onClick={HandleDecrement}
                      disabled={quantity <= 1}
                      className=" p-3 h-11 outline-none"
                    >
                      <HiOutlineMinusSmall size={22} />
                    </button>
                    <input
                      type="number"
                      name="quantity"
                      readOnly
                      className="h-11 text-center text-black w-11 outline-none placeholder:text-[1.2rem] font-semibold"
                      placeholder="1"
                      min={1}
                      max={book?.stock! > 15 ? 15 : book?.stock!}
                      value={quantity}
                    />
                    <button
                      type="button"
                      onClick={HandleIncrement}
                      disabled={
                        quantity >= (book?.stock! > 15 ? 15 : book?.stock!)
                      }
                      className=" p-3 h-11 outline-none"
                    >
                      <HiOutlinePlusSmall size={22} />
                    </button>
                  </div>
                </div>

                <button
                  type="submit"
                  className="px-[10px] py-[10px] w-full uppercase text-[0.9rem] font-medium border bg-[#C62028] text-white"
                >
                  Add to cart
                </button>

                <div>
                  <h4>Description</h4>

                  <hr className="border my-[15px]" />

                  <div
                    className="main-prose"
                    dangerouslySetInnerHTML={{
                      __html: book?.description || "",
                    }}
                  ></div>
                </div>
              </div>
            </div>
          </div>

          {openViewer && (
            <ImageViewer
              image={viewerImage}
              open={openViewer}
              onClose={() => setOpenViewer(false)}
            />
          )}
        </div>
      </div>
    </section>
  );
}

export default BookDetail;
