"use client";
import Link from "next/link";
import { useCallback, useEffect, useMemo, useState } from "react";
import Image from "../../Image";
import Loading from "../../Loading";
import Overplay from "./../Overplay";
import ProductBuyList from "./ProductBuyList";
import ShippingInfoForm from "./ShippingInfoForm";
import PaymentMethod from "./PaymentMethod";

function CheckoutForm() {
  const [data, setData] = useState({
    fullname: "",
    phone: "",
    speaddress: "",
  });
  const [provinceName, setProvinceName] = useState<string>("");
  const [ward, setWard] = useState<string>("");
  const [menuOpen, setMenuOpen] = useState<boolean>(false);
  const [couponCode, setCouponCode] = useState<string>("");
  const [paymethod, setPaymethod] = useState<string>("");
  const [isOrdering, setIsOrdering] = useState<boolean>(false);

  const totalPrice = useMemo(() => {
    return (
      cart?.productsInCart.reduce((sum, item) => {
        const finalPrice =
          item.discount > 0 ? item.price - item.discount : item.price;

        return sum + finalPrice * item.variant.quantity;
      }, 0) || 0
    );
  }, [cart?.productsInCart]);

  const handleChange = useCallback(
    (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
      const { name, value } = e.target;
      setData((prev) => ({ ...prev, [name]: value }));
    },
    []
  );

  const isLoading = false;

  return (
    <section className="my-[40px] px-[15px]">
      <div className="mx-auto max-w-[1230px] w-full">
        <Link href={"/"}>
          <Image
            Src={"/assets/other/logo.png"}
            Alt={"logo"}
            ClassName={"w-[80px]"}
            loadingType="eager"
          />
        </Link>

        <hr className="border-gray-300 my-[15px]" />

        <form onSubmit={handleSubmit}>
          <div className="grid lg:grid-cols-2 gap-[40px]">
            <div className="order-last lg:order-first space-y-[15px]">
              <div className="space-y-[30px]">
                <ShippingInfoForm
                  data={data}
                  provinceName={provinceName}
                  setProvinceName={setProvinceName}
                  ward={ward}
                  setWard={setWard}
                  handleGetAddress={handleGetAddress}
                  handleChange={handleChange}
                  addresses={addresses ?? []}
                  provinces={provinces ?? []}
                />

                <PaymentMethod
                  paymethod={paymethod}
                  setPaymethod={setPaymethod}
                />

                <div className="flex justify-between items-center">
                  <button
                    disabled={isLoadingAddOrder}
                    className="text-[0.9rem] rounded-md bg-[#197FB6] px-4 py-2 font-medium text-white"
                  >
                    Đặt hàng
                  </button>

                  <Link
                    href={"/cart"}
                    className="text-[0.95rem] rounded-md bg-transparent py-2 font-medium text-[#338dbc] hover:text-blue-400"
                  >
                    Giỏ hàng
                  </Link>
                </div>
              </div>
            </div>

            <div className="order-first lg:order-last space-y-[15px] lg:sticky lg:top-0 lg:self-start">
              <ProductBuyList productsInCart={cart?.productsInCart ?? []} />

              <hr className="border-gray-300" />

              <CouponApply
                toggleOpen={toggleOpen}
                setCouponCode={setCouponCode}
                handleApplyCoupon={handleApplyCoupon}
                coupon={coupon!}
                isLoadingCoupon={isLoadingCoupon}
              />

              <hr className="border-gray-300" />

              <div className="flex items-center justify-between  ">
                <h4>Total</h4>
                <h4>{totalPrice.toLocaleString("vi-VN")}₫</h4>
              </div>
            </div>
          </div>
        </form>
      </div>

      {isLoading && (
        <Overplay IndexForZ={50}>
          <Loading height={0} size={55} color="white" thickness={8} />
          <h4 className="text-white">Vui lòng chờ trong giây lát...</h4>
        </Overplay>
      )}
    </section>
  );
}

export default CheckoutForm;
