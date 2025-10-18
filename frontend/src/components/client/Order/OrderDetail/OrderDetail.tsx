import BreadCrumb from "../../BreadCrumb";
import OrderInfo from "./OrderInfo";
import SideBarMenu from "../../SideMenuBar";
import { useParams } from "react-router-dom";
function OrderDetail() {
  const { code } = useParams();

  const array = [
    {
      name: "Trang chủ",
      href: "/",
    },
    {
      name: "Đơn hàng",
      href: "/order",
    },
    {
      name: `Order ${code}`,
    },
  ];
  return (
    <>
      <BreadCrumb items={array} />

      <section className="mb-[40px]">
        <div className="w-full max-w-[1230px] mx-auto">
          <div className="flex justify-center flex-wrap gap-5">
            <SideBarMenu />

            <OrderInfo order={{}} isLoading={isLoading} />
          </div>
        </div>
      </section>
    </>
  );
}

export default OrderDetail;
