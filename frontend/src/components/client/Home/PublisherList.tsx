import Image from "../../Image";
import { Swiper, SwiperSlide } from "swiper/react";
import "swiper/css";
import "swiper/css/free-mode";
import { FreeMode } from "swiper/modules";
import { Link } from "react-router-dom";
function PublisherList() {
  const publishers = [
    {
      name: "Dinh ti",
      slug: "dinhti",
      image: "/assets/nxb/dinhti.png",
    },
    {
      name: "Alpha",
      slug: "alpha",
      image: "/assets/nxb/alpha.png",
    },
  ];
  return (
    <section className="mb-[40px] px-[15px]">
      <div className="mx-auto max-w-[1350px] w-full">
        <div className="flex justify-center">
          <Swiper
            spaceBetween={20}
            slidesPerView={"auto"}
            modules={[FreeMode]}
            grabCursor={true}
          >
            {publishers.map((publisher, index) => (
              <SwiperSlide
                key={index}
                className="!flex !flex-col !items-center !w-auto group"
              >
                <Link
                  to={`/collection/${publisher.slug}`}
                  className="space-y-[8px]"
                >
                  <div className="w-[80px] h-[80px] border-gray-300">
                    <Image
                      source={publisher.image}
                      alt={publisher.name}
                      className="w-full rounded-full border-0 border-gray-400 group-hover:border-1"
                      loading="lazy"
                    />
                  </div>
                  <div className="text-center">
                    <h5 className="font-medium">{publisher.name} </h5>
                  </div>
                </Link>
              </SwiperSlide>
            ))}
          </Swiper>
        </div>
      </div>
    </section>
  );
}

export default PublisherList;
