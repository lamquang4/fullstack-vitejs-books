import { useCallback, useEffect, useMemo, useState } from "react";
import Image from "../../Image";
import Loading from "../../Loading";
import Overplay from "./../Overplay";
import ProductBuyList from "./ProductBuyList";
import ShippingInfoForm from "./ShippingInfoForm";
import PaymentMethod from "./PaymentMethod";
import useCurrentUser from "../../../hooks/useGetCurrentUser";
import useGetCart from "../../../hooks/client/useGetCart";
import { Link, useNavigate } from "react-router-dom";
import useGetAddresses from "../../../hooks/client/useGetAddresses";
import type { Address } from "../../../types/type";
import useGetProvinces from "../../../hooks/useGetProvincesVN";
import { MdOutlineKeyboardBackspace } from "react-icons/md";
import toast from "react-hot-toast";
import useAddOrder from "../../../hooks/client/useAddOrder";
import usePaymentMomo from "../../../hooks/client/usePaymentMomo";

function CheckoutForm() {
  const navigate = useNavigate();
  const { provinces } = useGetProvinces();
  const { user } = useCurrentUser("client");
  const {
    cart,
    isLoading: isLoadingCart,
    mutate: mutateCart,
  } = useGetCart(user?.id || "");
  const { addresses } = useGetAddresses(user?.id || "");
  const { addOrder, isLoading: isLoadingAdd } = useAddOrder(user?.id || "");
  const { createPaymentMomo, isLoading: isLoadingPaymentMomo } =
    usePaymentMomo();

  const [data, setData] = useState({
    fullname: "",
    phone: "",
    speaddress: "",
    city: "",
    ward: "",
  });
  const [paymethod, setPaymethod] = useState<string>("");

  // lấy những sản phẩm không đủ số lượng mua (số lượng mua > số lượng tồn kho)
  const outOfStockItems = useMemo(() => {
    if (!cart?.items) return [];
    return cart.items.filter((item) => item.quantity > item.stock);
  }, [cart?.items]);

  useEffect(() => {
    if (isLoadingCart) return;

    if (!cart || !cart.items?.length) {
      toast.error("There’s nothing in your cart");
      navigate("/cart");
      return;
    }

    if (outOfStockItems.length > 0) {
      toast.error(
        "Some products do not have enough stock for the quantity you want to purchase"
      );
      navigate("/cart");
    }
  }, [cart, outOfStockItems, navigate, isLoadingCart]);

  const totalPrice = useMemo(() => {
    return (
      cart?.items.reduce((sum, item) => {
        const finalPrice =
          item.discount > 0 ? item.price - item.discount : item.price;

        return sum + finalPrice * item.quantity;
      }, 0) || 0
    );
  }, [cart?.items]);

  const handleChange = useCallback(
    (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
      const { name, value } = e.target;
      setData((prev) => ({ ...prev, [name]: value }));
    },
    []
  );

  const handleGetAddress = useCallback((address: Address | null) => {
    if (address) {
      setData({
        fullname: address.fullname,
        phone: address.phone,
        speaddress: address.speaddress,
        city: address.city,
        ward: address.ward,
      });
    } else {
      setData({ fullname: "", phone: "", speaddress: "", city: "", ward: "" });
    }
  }, []);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!paymethod) {
      toast.error("Please select a payment method");
      return;
    }

    if (totalPrice < 10000 && paymethod === "momo") {
      toast.error("MOMO is only available for orders of 10,000₫ or more");
      setPaymethod("cod");
      return;
    }

    if (cart?.items.length === 0) {
      toast.error("There’s nothing in your cart");
      navigate("/cart");
      return;
    }

    const items = cart?.items.map((item) => {
      return {
        bookId: item.bookId,
        quantity: item.quantity,
        price: item.price,
        discount: item.discount,
      };
    });

    if (paymethod === "cod") {
      try {
        await addOrder({
          fullname: data.fullname,
          phone: data.phone,
          speaddress: data.speaddress,
          city: data.city,
          ward: data.ward,
          paymethod: paymethod,
          items: items!,
        });

        navigate("/order-success");

        mutateCart({ items: [] }, false);
      } catch (err: any) {
        toast.error(err?.response?.data?.msg);
      }
    } else if (paymethod === "momo") {
      try {
        const res = await addOrder({
          fullname: data.fullname,
          phone: data.phone,
          speaddress: data.speaddress,
          city: data.city,
          ward: data.ward,
          paymethod: paymethod,
          items: items!,
        });

        const momoResponse = await createPaymentMomo(res.orderCode);
        window.location.href = momoResponse.payUrl;
      } catch (err: any) {
        toast.error(err?.response?.data?.msg);
      }
    }
  };

  return (
    <section className="my-[40px] px-[15px] text-black">
      <div className="mx-auto max-w-[1200px] w-full">
        <Link to={"/"}>
          <Image
            source={"/assets/logo.png"}
            alt={"logo"}
            className={"w-[100px]"}
            loading="eager"
          />
        </Link>

        <hr className="border-gray-300 my-[15px]" />

        <form onSubmit={handleSubmit}>
          <div className="grid lg:grid-cols-2 gap-[40px]">
            <div className="order-last lg:order-first space-y-[15px]">
              <div className="space-y-[30px]">
                <ShippingInfoForm
                  data={data}
                  setData={setData}
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
                  <button className="text-[0.9rem] rounded-md bg-[#C62028] px-4 py-2 font-medium text-white">
                    Place order
                  </button>

                  <Link
                    to={"/cart"}
                    className="text-[0.95rem] rounded-md bg-transparent px-4 py-2 font-medium text-[#C62028] border border-[#C62028]"
                  >
                    <div className="flex gap-[5px] items-center">
                      <MdOutlineKeyboardBackspace size={25} /> Cart
                    </div>
                  </Link>
                </div>
              </div>
            </div>

            <div className="order-first lg:order-last space-y-[15px] lg:sticky lg:top-0 lg:self-start">
              <ProductBuyList productsInCart={cart?.items ?? []} />

              <hr className="border-gray-300" />

              <div className="flex items-center justify-between font-medium ">
                <h5>Shipping fee:</h5>
                <h5>Free ship</h5>
              </div>

              <div className="flex items-center justify-between font-semibold">
                <h5>Total price:</h5>
                <h5>{totalPrice.toLocaleString("vi-VN")}₫</h5>
              </div>
            </div>
          </div>
        </form>
      </div>

      {(isLoadingCart || isLoadingAdd || isLoadingPaymentMomo) && (
        <Overplay IndexForZ={50}>
          <Loading height={0} size={55} color="white" thickness={8} />
          <h4 className="text-white">Please wait a moment...</h4>
        </Overplay>
      )}
    </section>
  );
}

export default CheckoutForm;
