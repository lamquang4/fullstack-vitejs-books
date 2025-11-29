import { useEffect, useState } from "react";
import type { Book } from "../../../../types/type";
import ImageViewer from "../../../ImageViewer";
import Image from "../../../Image";
import toast from "react-hot-toast";
import { GrNext, GrPrevious } from "react-icons/gr";
import { HiOutlineMinusSmall } from "react-icons/hi2";
import { HiOutlinePlusSmall } from "react-icons/hi2";
import { Swiper, SwiperSlide } from "swiper/react";
import "swiper/css";
import { useAddItemToCart } from "../../../../hooks/client/useAddItemToCart";
import useGetCurrentUser from "../../../../hooks/useGetCurrentUser";
import useGetCart from "../../../../hooks/client/useGetCart";
import { useNavigate } from "react-router-dom";

type Props = {
  book: Book;
};

function BookDetail({ book }: Props) {
  const navigate = useNavigate();
  const max = 15;
  const maxHeight = 200;
  const { user } = useGetCurrentUser("client");
  const { addItem, isLoading: isLoadingAddItem } = useAddItemToCart();
  const { cart, mutate } = useGetCart(user?.id || "");

  const [quantity, setQuantity] = useState<number>(1);
  const [mainImage, setMainImage] = useState<string>("");
  const [openViewer, setOpenViewer] = useState<boolean>(false);
  const [viewerImage, setViewerImage] = useState<string>("");
  const [currentImageIndex, setCurrentImageIndex] = useState<number>(0);
  const [isLargeScreen, setIsLargeScreen] = useState(window.innerWidth >= 1024);
  const [expanded, setExpanded] = useState(false);

  useEffect(() => {
    const handleResize = () => {
      setIsLargeScreen(window.innerWidth >= 1024);
    };

    window.addEventListener("resize", handleResize);

    return () => {
      window.removeEventListener("resize", handleResize);
    };
  }, []);

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
    const maxQuantity = book?.stock! > max ? max : book?.stock!;
    setQuantity((prev) => (prev < maxQuantity ? prev + 1 : prev));
  };

  const HandleDecrement = () => {
    setQuantity((prev) => (prev > 1 ? prev - 1 : prev));
  };

  const handleAddItemToCart = async () => {
    if (book.stock === 0) {
      toast.error(`Sách đã hết hàng`);
      return;
    }

    if (!user?.id) {
      toast.error("Bạn phải đăng nhập để mua sách!");
      navigate("/login");
      return;
    }

    const existingItem = cart?.items?.find(
      (item: any) => item.bookId === book.id
    );

    const currentQuantity = existingItem ? existingItem.quantity : 0;
    const newQuantity = currentQuantity + quantity;

    const maxQuantity = Math.min(book.stock, max);

    if (newQuantity > maxQuantity) {
      toast(`Cuốn sách này bạn chỉ có thể mua tối đa ${maxQuantity}`);
      return;
    }

    try {
      await addItem(user.id, book?.id, quantity);
      mutate();
    } catch (err: any) {
      toast.error(err?.response?.data?.message);
    }
  };
  return (
    <section className="w-full mb-[40px] px-[15px]">
      <div className="mx-auto w-full max-w-[1200px]">
        <div className="flex flex-col lg:flex-row gap-x-[15px] gap-y-[30px] w-full">
          <div className="flex-1/6" id="div1">
            <div className="lg:items-start items-center flex lg:flex-row flex-col-reverse gap-3 flex-1/6 lg:sticky lg:top-[100px]">
              <div className="lg:max-w-[70px] w-full">
                <Swiper
                  slidesPerView="auto"
                  spaceBetween={10}
                  className="lg:max-h-[600px] w-full flex justify-center 
                  [&&_.swiper-wrapper]:flex 
                  [&&_.swiper-wrapper]:justify-center"
                  direction={isLargeScreen ? "vertical" : "horizontal"}
                >
                  {book?.images.map((image, index) => (
                    <SwiperSlide
                      key={index}
                      className="!w-[70px] !h-[90px] cursor-pointer flex-shrink-0"
                      onMouseEnter={() => {
                        setMainImage(image.image);
                        setCurrentImageIndex(index);
                      }}
                    >
                      <div
                        className={`border flex items-center justify-center w-full h-full ${
                          currentImageIndex === index
                            ? "border-gray-600"
                            : "border-gray-300"
                        }`}
                      >
                        <Image
                          source={`${import.meta.env.VITE_BACKEND_URL}${
                            image.image
                          }`}
                          alt=""
                          className="w-full h-full object-contain"
                          loading="eager"
                        />
                      </div>
                    </SwiperSlide>
                  ))}
                </Swiper>
              </div>

              <div
                className="group flex justify-center w-full relative cursor-pointer "
                onClick={(e) => {
                  e.stopPropagation();
                  e.preventDefault();
                  handleOpenViewer(
                    `${import.meta.env.VITE_BACKEND_URL}${mainImage}`
                  );
                }}
              >
                {mainImage && (
                  <div className="group">
                    <button
                      type="button"
                      onClick={handleNextImage}
                      className="absolute border right-1.5 top-1/2 w-10 h-10 bg-white rounded-full flex justify-center items-center -translate-y-1/2 z-10 p-2 lg:opacity-0 lg:group-hover:opacity-100 transition duration-300 hover:bg-[#C62028] hover:text-white"
                    >
                      <GrNext size={20} />
                    </button>

                    <div className="w-full h-[400px] sm:h-[500px] md:h-[600px] overflow-hidden">
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
                      className="absolute left-1.5 top-1/2 w-10 h-10 border bg-white rounded-full flex justify-center items-center -translate-y-1/2 z-10 p-2 lg:opacity-0 lg:group-hover:opacity-100 transition duration-300 hover:bg-[#C62028] hover:text-white"
                    >
                      <GrPrevious size={20} />
                    </button>
                  </div>
                )}
              </div>
            </div>
          </div>

          <div className="relative flex-1" id="div2">
            <div className="space-y-[10px] ">
              <h2>{book?.title}</h2>

              <div className="flex items-center gap-[15px]">
                {book && book?.discount > 0 ? (
                  <>
                    <del className="text-[#707072] font-light text-[1.4rem]">
                      {book?.price.toLocaleString("vi-VN")}₫
                    </del>

                    <h3 className="text-[#C62028] font-medium">
                      {(book?.price - book?.discount).toLocaleString("vi-VN")}₫
                    </h3>

                    <p className="text-white p-1.5 bg-[#C62028] rounded-sm font-semibold">
                      -{Math.floor((book.discount / book.price) * 100)}%
                    </p>
                  </>
                ) : (
                  <h3 className="font-medium text-[#C62028]">
                    {book?.price.toLocaleString("vi-VN")}₫
                  </h3>
                )}
              </div>

              <div className="space-y-[15px]">
                {book && book.stock > 0 ? (
                  <>
                    <div className="w-full flex items-center gap-[15px]">
                      <h5 className="font-medium ">Số lượng:</h5>
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
                          className="h-11 text-center text-black w-11 outline-none placeholder:text-[1.2rem] font-medium"
                          placeholder="1"
                          min={1}
                          max={book?.stock! > max ? max : book?.stock!}
                          value={quantity}
                        />
                        <button
                          type="button"
                          onClick={HandleIncrement}
                          disabled={
                            quantity >=
                            (book?.stock! > max ? max : book?.stock!)
                          }
                          className=" p-3 h-11 outline-none"
                        >
                          <HiOutlinePlusSmall size={22} />
                        </button>
                      </div>
                    </div>

                    <button
                      type="button"
                      onClick={handleAddItemToCart}
                      disabled={isLoadingAddItem}
                      className="p-[10px] w-full uppercase text-[0.9rem] font-semibold border bg-[#C62028] text-white"
                    >
                      Thêm vào giỏ
                    </button>
                  </>
                ) : (
                  <button
                    type="button"
                    className="p-[10px] w-full uppercase text-[0.9rem] font-semibold border bg-transparent border-[#C62028] text-[#C62028]"
                  >
                    Hết hàng
                  </button>
                )}

                <div className="text-black space-y-[15px]">
                  <h4>Thông tin chi tiết</h4>

                  <div className="divide-y divide-gray-200 text-[0.9rem]">
                    <div className="grid grid-cols-2 gap-2 py-2">
                      <span>Danh mục</span>
                      <span className="font-medium">{book.category.name}</span>
                    </div>

                    <div className="grid grid-cols-2 gap-2 py-2">
                      <span>Ngày xuất bản</span>
                      <span className="font-medium">
                        {new Date(book.publicationDate).toLocaleDateString(
                          "vi-VN"
                        )}
                      </span>
                    </div>

                    <div className="grid grid-cols-2 gap-2 py-2">
                      <span>Tác giả</span>
                      <span className="font-medium">
                        {book.author.fullname}
                      </span>
                    </div>

                    <div className="grid grid-cols-2 gap-2 py-2">
                      <span>Nhà xuất bản</span>
                      <span className="font-medium">{book.publisher.name}</span>
                    </div>

                    <div className="grid grid-cols-2 gap-2 py-2">
                      <span>Khối lượng (gr)</span>
                      <span className="font-medium">{book.weight}</span>
                    </div>

                    <div className="grid grid-cols-2 gap-2 py-2">
                      <span>Kích thước bao bì (cm)</span>
                      <span className="font-medium">
                        {book.length} x {book.width} x {book.thickness}
                      </span>
                    </div>

                    <div className="grid grid-cols-2 gap-2 py-2">
                      <span>Số trang</span>
                      <span className="font-medium">{book.numberOfPages}</span>
                    </div>
                  </div>
                </div>

                <div className="text-black space-y-[15px]">
                  <h4>Mô tả</h4>

                  <div
                    className={`main-prose relative transition-all duration-300 ${
                      expanded ? "max-h-none" : "overflow-hidden"
                    }`}
                    style={!expanded ? { maxHeight: `${maxHeight}px` } : {}}
                  >
                    <div
                      dangerouslySetInnerHTML={{
                        __html: book.description || "",
                      }}
                    />

                    {!expanded && (
                      <div className="absolute bottom-0 left-0 w-full h-[60px] bg-gradient-to-t from-white to-transparent"></div>
                    )}
                  </div>

                  <button
                    onClick={() => setExpanded(!expanded)}
                    className="text-blue-600 text-[0.9rem] font-medium w-full"
                  >
                    {expanded ? "Thu gọn" : "Xem thêm"}
                  </button>
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
