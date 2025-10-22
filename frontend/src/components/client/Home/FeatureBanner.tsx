import { Swiper, SwiperSlide } from "swiper/react";
import "swiper/css";

import Image from "../../Image";

function FeatureBanner() {
  const banners = [
    "/assets/banner/feature-banner1.png",
    "/assets/banner/feature-banner2.png",
    "/assets/banner/feature-banner3.png",
  ];
  return (
    <section className="mb-[40px] px-[15px]">
      <div className="mx-auto max-w-[1200px] w-full">
        <Swiper
          spaceBetween={15}
          slidesPerView={3}
          freeMode={true}
          grabCursor={true}
          breakpoints={{
            0: { slidesPerView: 1.2 },
            480: { slidesPerView: 2 },
            768: { slidesPerView: 3 },
          }}
        >
          {banners.map((src, index) => (
            <SwiperSlide key={index}>
              <Image
                source={src}
                alt={`Banner ${index + 1}`}
                className="w-full"
                loading="lazy"
              />
            </SwiperSlide>
          ))}
        </Swiper>
      </div>
    </section>
  );
}

export default FeatureBanner;
