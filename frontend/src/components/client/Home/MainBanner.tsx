import { Swiper, SwiperSlide } from "swiper/react";
import { Autoplay, Pagination } from "swiper/modules";
import "swiper/css";
import "swiper/css/navigation";
import "swiper/css/pagination";
import Image from "../../Image";
function MainBanner() {
  const banners1 = [
    {
      image: "/assets/banner/mainbanner2.jpg",
    },
    {
      image: "/assets/banner/mainbanner3.png",
    },
  ];
  return (
    <>
      <section className="mb-[40px]">
        <div className="w-full max-w-[1350px] mx-auto">
          <Swiper
            modules={[Autoplay, Pagination]}
            pagination={{ clickable: true, type: "bullets" }}
            autoplay={{ delay: 5000 }}
            loop={true}
            speed={1000}
            className="w-full"
          >
            {banners1.map((banner1, index) => (
              <SwiperSlide key={index}>
                <div className="relative block w-full">
                  <div className="w-full">
                    <picture>
                      <Image
                        source={banner1.image}
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
