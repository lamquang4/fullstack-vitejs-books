import BreadCrumb from "../../BreadCrumb";
import OrderInfo from "./OrderInfo";
import SideBarMenu from "../../SideMenuBar";
import { useNavigate, useParams } from "react-router-dom";
import useCurrentUser from "../../../../hooks/useGetCurrentUser";
import useGetOrder from "../../../../hooks/client/useGetOrder";
import { useEffect } from "react";
import toast from "react-hot-toast";
function OrderDetail() {
  const navigate = useNavigate();
  const { code } = useParams();
  const { user } = useCurrentUser("client");
  const { order, isLoading } = useGetOrder(user?.id!, code as string);

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
      name: `Mã đơn ${code}`,
    },
  ];

  useEffect(() => {
    if (isLoading) return;

    if (!order) {
      toast.error("Đơn hàng không tìm thấy");
      navigate("/order");
    }
  }, [isLoading, order, navigate]);

  return (
    <>
      <BreadCrumb items={array} />

      <section className="mb-[40px]">
        <div className="w-full max-w-[1200px] mx-auto">
          <div className="flex justify-center flex-wrap gap-5">
            <SideBarMenu />

            <OrderInfo order={order} isLoading={isLoading} />
          </div>
        </div>
      </section>
    </>
  );
}

export default OrderDetail;
