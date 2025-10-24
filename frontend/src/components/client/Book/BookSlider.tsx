import { Swiper, SwiperSlide } from "swiper/react";
import "swiper/css";
import "swiper/css/free-mode";
import { FreeMode } from "swiper/modules";
import Image from "../../Image";
import { Link } from "react-router-dom";
import type { Book } from "../../../types/type";

interface Props {
  title: string;
  books: Book[];
}
function BookSlider({ title, books }: Props) {
  return (
    <>
      {books.length > 0 && (
        <section className="mb-[40px] px-[15px] text-black">
          <div className="mx-auto max-w-[1200px] w-full">
            <h2 className="mb-[20px]">{title}</h2>
            <Swiper
              spaceBetween={10}
              modules={[FreeMode]}
              freeMode={true}
              grabCursor={true}
              breakpoints={{
                0: {
                  slidesPerView: 2,
                },
                768: {
                  slidesPerView: 3,
                },
                1024: {
                  slidesPerView: 4,
                },
                1640: {
                  slidesPerView: 4,
                },
              }}
            >
              {books.map((book) => {
                return (
                  <SwiperSlide key={book.id}>
                    <div className="space-y-[15px] rounded-sm">
                      <div className="relative group">
                        <Link to={`/book/${book.slug}`}>
                          {book.images.length > 0 && (
                            <div className=" w-full overflow-hidden pt-[100%]">
                              <Image
                                source={`${import.meta.env.VITE_BACKEND_URL}${
                                  book.images[0].image
                                }`}
                                alt={book.title}
                                className="absolute inset-0 w-full h-full object-cover transition-opacity duration-300 group-hover:opacity-0"
                                loading="lazy"
                              />

                              {book.images[1]?.image && (
                                <Image
                                  source={`${import.meta.env.VITE_BACKEND_URL}${
                                    book.images[1].image
                                  }`}
                                  alt={book.title}
                                  className="absolute inset-0 w-full h-full object-cover opacity-0 transition-opacity duration-300 group-hover:opacity-100"
                                  loading="lazy"
                                />
                              )}
                            </div>
                          )}
                        </Link>

                        {book.discount > 0 && (
                          <div className="absolute top-0 right-0 z-10 font-semibold text-center">
                            <p className="text-white text-[0.8rem] px-2 py-1 bg-[#C62028] rounded-sm">
                              -{Math.floor((book.discount / book.price) * 100)}%
                            </p>
                          </div>
                        )}
                      </div>

                      <div className="space-y-[6px]">
                        <h5 className="font-medium capitalize">{book.title}</h5>

                        {book.discount > 0 ? (
                          <div className="flex gap-[12px]  ">
                            <del className="text-[#707072] text-[1rem]">
                              {book.price.toLocaleString("vi-VN")}₫
                            </del>

                            <h5 className="font-medium text-[#C62028]">
                              {(book.price - book.discount).toLocaleString(
                                "vi-VN"
                              )}
                              ₫
                            </h5>
                          </div>
                        ) : (
                          <h5 className="font-medium text-[#C62028]">
                            {book.price.toLocaleString("vi-VN")}₫
                          </h5>
                        )}

                        <p className="font-medium">{book.category.name}</p>
                      </div>
                    </div>
                  </SwiperSlide>
                );
              })}
            </Swiper>
          </div>
        </section>
      )}
    </>
  );
}

export default BookSlider;
