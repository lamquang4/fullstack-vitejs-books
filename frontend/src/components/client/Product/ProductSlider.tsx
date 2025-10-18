"use client";
import { Swiper, SwiperSlide } from "swiper/react";
import "swiper/css";
import "swiper/css/free-mode";
import { FreeMode } from "swiper/modules";
import Image from "../../Image";
import { Link } from "react-router-dom";
import type { Book } from "../../../types/type";

interface Props {
  title: string;
  products: Book[];
}
function ProductSlider({ title, products }: Props) {
  return (
    <>
      {products.length > 0 && (
        <section className="mb-[40px] px-[15px]">
          <div className="mx-auto max-w-[1230px] w-full">
            <h2 className="mb-[20px]">{title}</h2>
            <Swiper
              spaceBetween={12}
              modules={[FreeMode]}
              freeMode={true}
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
              {products.map((product) => {
                return (
                  <SwiperSlide key={product._id}>
                    <div className="relative group">
                      <Link to={`/product/${product.slug}`}>
                        <Image
                          source={product.images[0]}
                          alt={product.title}
                          className={"w-full z-1 relative"}
                          loading="lazy"
                        />

                        <Image
                          source={product.images[1]}
                          alt={product.title}
                          className={
                            "w-full absolute top-0 left-0 opacity-0 z-2 transition-opacity duration-300 group-hover:opacity-100"
                          }
                          loading="lazy"
                        />
                      </Link>

                      <div className="flex gap-2 flex-col absolute top-[12px] left-[12px] z-[3] font-semibold text-center  ">
                        {product.discount > 0 && (
                          <small className="uppercase text-[0.7rem] py-1 px-1.5 bg-white">
                            Sale{" "}
                            {Math.floor(
                              (product.discount / product.price) * 100
                            )}
                            %
                          </small>
                        )}
                      </div>
                    </div>
                    <div className="py-[12px] space-y-[6px]">
                      <h5 className="  font-medium capitalize">
                        {product.name}
                      </h5>

                      <p className="font-medium text-[#444]">
                        {product.category.name} / {product.author}
                      </p>

                      {product.discount > 0 ? (
                        <div className="flex gap-[12px]  ">
                          <del className="text-[#707072] text-[1rem]">
                            {product.price.toLocaleString("vi-VN")}₫
                          </del>

                          <h5 className="font-medium text-[#c00]">
                            {(product.price - product.discount).toLocaleString(
                              "vi-VN"
                            )}
                            ₫
                          </h5>
                        </div>
                      ) : (
                        <h5 className="font-medium">
                          {product.price.toLocaleString("vi-VN")}₫
                        </h5>
                      )}
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

export default ProductSlider;
