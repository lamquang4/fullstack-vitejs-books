import { Swiper, SwiperSlide } from "swiper/react";
import { Autoplay, Pagination } from "swiper/modules";
import "swiper/css";
import "swiper/css/navigation";
import "swiper/css/pagination";
import Image from "../../Image";
function MainBanner() {
  const banners = [
    {
      mobile: "/assets/banner/mainbanner1-mobile.png",
      desktop: "/assets/banner/mainbanner1.png",
    },
    {
      mobile: "/assets/banner/mainbanner2-mobile.png",
      desktop: "/assets/banner/mainbanner2.png",
    },
  ];
  return (
    <>
      <section className="mb-[40px] text-black">
        <div className="w-full max-w-[1200px] mx-auto">
          <Swiper
            modules={[Autoplay, Pagination]}
            pagination={{ clickable: true, type: "bullets" }}
            autoplay={{ delay: 5000 }}
            loop={true}
            speed={1000}
            grabCursor={true}
            className="w-full"
          >
            {banners.map((banner, index) => (
              <SwiperSlide key={index}>
                <div className="relative block w-full">
                  <div className="w-full">
                    <picture>
                      <source
                        srcSet={banner.mobile}
                        media="(max-width: 768px)"
                      />
                      <Image
                        source={banner.desktop}
                        alt={"banner"}
                        className={"w-full object-center"}
                        loading="eager"
                      />
                    </picture>
                  </div>
                </div>
              </SwiperSlide>
            ))}
          </Swiper>
        </div>
      </section>
    </>
  );
}

export default MainBanner;
