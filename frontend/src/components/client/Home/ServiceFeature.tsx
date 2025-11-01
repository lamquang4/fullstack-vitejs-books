import Image from "../../Image";
function ServiceFeature() {
  const services = [
    {
      img: "/assets/service1.png",
      title: "Miễn phí đổi trả nhanh chóng",
    },
    {
      img: "/assets/service2.png",
      title: "100% sách có bản quyền",
    },
    {
      img: "/assets/service3.png",
      title: "Giao hàng toàn quốc",
    },
    {
      img: "/assets/service4.png",
      title: "Quà tặng cho đơn hàng",
    },
  ];
  return (
    <section className="px-[15px] bg-[#C62028]">
      <div className="mx-auto max-w-[1200px] w-full text-white py-4">
        <div className="gap-y-[25px] grid grid-cols-2 lg:grid-cols-4 lg:gap-[15px]">
          {services.map((service, index) => (
            <div
              key={index}
              className="flex flex-col items-center text-center xl:flex-row xl:text-start gap-2.5"
            >
              <Image
                source={service.img}
                alt={""}
                className={"w-[40px]"}
                loading="eager"
              />
              <div>
                <p className="font-medium">{service.title}</p>
              </div>
            </div>
          ))}
        </div>
      </div>
    </section>
  );
}

export default ServiceFeature;
