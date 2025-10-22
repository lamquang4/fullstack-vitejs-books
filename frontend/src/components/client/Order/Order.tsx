import SideBarMenu from "../SideMenuBar";
import OrderHistory from "./OrderHistory";
import BreadCrumb from "../BreadCrumb";
function Order() {
  const array = [
    {
      name: "Home",
      href: "/",
    },
    {
      name: "Order history",
    },
  ];
  return (
    <>
      <BreadCrumb items={array} />

      <section className="mb-[40px]">
        <div className="w-full max-w-[1200px] mx-auto relative">
          <div className="flex justify-center flex-wrap gap-5">
            <SideBarMenu />

            <OrderHistory />
          </div>
        </div>
      </section>
    </>
  );
}

export default Order;
