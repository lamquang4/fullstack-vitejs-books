import Image from "../../Image";
import { useMemo } from "react";
import { HiOutlineMinusSmall } from "react-icons/hi2";
import { HiOutlinePlusSmall } from "react-icons/hi2";
import toast from "react-hot-toast";
import type { Cart } from "../../../types/type";
import { Link } from "react-router-dom";
import { useRemoveItemCart } from "../../../hooks/client/useRemoveItemCart";
import { useChangeQuantityItemCart } from "../../../hooks/client/useChangeQuantityItemcart";

type Props = {
  cart: Cart;
  mutate: () => void;
};

function CartItem({ cart, mutate }: Props) {
  const max = 15;
  const { removeItem, isLoading: isLoadingRemove } = useRemoveItemCart();
  const { changeQuantity, isLoading: isLoadingChangeQuantity } =
    useChangeQuantityItemCart();
  const totalQuantity = useMemo(() => {
    return (
      cart?.items.reduce((sum, item) => {
        return sum + (item?.quantity || 0);
      }, 0) || 0
    );
  }, [cart?.items]);

  const totalPrice = useMemo(() => {
    return (
      cart?.items.reduce((sum, item) => {
        const finalPrice =
          item.discount > 0 ? item.price - item.discount : item.price;

        return sum + finalPrice * item.quantity;
      }, 0) || 0
    );
  }, [cart?.items]);

  // lấy những sản phẩm không đủ số lượng mua (số lượng mua > số lượng tồn kho)
  const outOfStockItems = useMemo(() => {
    if (!cart?.items) return [];
    return cart.items.filter((item) => item.quantity > item.stock);
  }, [cart?.items]);

  const handleChangeQuantity = async (id: string, quantity: number) => {
    await changeQuantity(id, quantity);
    mutate();
  };

  const handleIncrement = (
    id: string,
    currentQuantity: number,
    stock: number
  ) => {
    if (currentQuantity >= (stock > max ? max : stock)) return;
    // id ở đây là id bảng cartitem
    handleChangeQuantity(id, currentQuantity + 1);
  };

  const handleDecrement = (id: string, currentQuantity: number) => {
    if (currentQuantity <= 1) return;
    handleChangeQuantity(id, currentQuantity - 1);
  };

  const handleRemoveItem = async (id: string) => {
    await removeItem(id);
    mutate();
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
  };

  return (
    <section className="my-[40px] px-[15px] text-black">
      <div className="max-w-[1350px] mx-auto">
        <h2 className="mb-[20px]">Cart ({totalQuantity})</h2>
        {cart?.items && cart.items.length > 0 ? (
          <form onSubmit={handleSubmit}>
            <div className="flex gap-8 w-full lg:flex-row flex-col">
              <div className="flex flex-col gap-8 bg-white basis-[70%]">
                {cart?.items.map((item) => (
                  <div className="w-full relative space-y-4" key={item.id}>
                    <div className="flex gap-4 w-full sm:flex-row flex-col">
                      <Link to={`/book/${item.slug}`} className="mx-auto">
                        <div className="w-[150px] h-[150px] overflow-hidden">
                          <Image
                            source={`${import.meta.env.VITE_BACKEND_URL}${
                              item.images[0]
                            }`}
                            alt={item.name}
                            className="w-full h-full object-contain"
                            loading="eager"
                          />
                        </div>
                      </Link>

                      <div className="flex flex-col gap-4 w-full">
                        <div className="flex justify-between gap-4">
                          <div className="flex flex-col gap-2">
                            <h5 className="font-medium">{item.title}</h5>

                            {item.discount > 0 ? (
                              <div className="flex gap-[12px]">
                                <del className="text-[#707072] text-[1rem]">
                                  {item.price.toLocaleString("vi-VN")}₫
                                </del>

                                <h5 className="font-semibold text-[#C62028]">
                                  {(item.price - item.discount).toLocaleString(
                                    "vi-VN"
                                  )}
                                  ₫
                                </h5>
                              </div>
                            ) : (
                              <h5 className="font-semibold text-[#C62028]">
                                {item.price.toLocaleString("vi-VN")}₫
                              </h5>
                            )}
                          </div>

                          <button
                            type="button"
                            disabled={isLoadingRemove}
                            onClick={() => handleRemoveItem(item.id)}
                            className="mb-auto"
                          >
                            <svg
                              xmlns="http://www.w3.org/2000/svg"
                              className="w-5 h-5 cursor-pointer fill-black hover:fill-[#C62028] inline-block"
                              viewBox="0 0 24 24"
                            >
                              <path
                                d="M19 7a1 1 0 0 0-1 1v11.191A1.92 1.92 0 0 1 15.99 21H8.01A1.92 1.92 0 0 1 6 19.191V8a1 1 0 0 0-2 0v11.191A3.918 3.918 0 0 0 8.01 23h7.98A3.918 3.918 0 0 0 20 19.191V8a1 1 0 0 0-1-1Zm1-3h-4V2a1 1 0 0 0-1-1H9a1 1 0 0 0-1 1v2H4a1 1 0 0 0 0 2h16a1 1 0 0 0 0-2ZM10 4V3h4v1Z"
                                data-original="#000000"
                              ></path>
                              <path
                                d="M11 17v-7a1 1 0 0 0-2 0v7a1 1 0 0 0 2 0Zm4 0v-7a1 1 0 0 0-2 0v7a1 1 0 0 0 2 0Z"
                                data-original="#000000"
                              ></path>
                            </svg>
                          </button>
                        </div>

                        <div className="flex-wrap justify-between flex items-center gap-4 mt-auto">
                          <div className="flex items-center gap-1">
                            <button
                              type="button"
                              onClick={() =>
                                handleDecrement(item.id, item.quantity)
                              }
                              disabled={
                                item.quantity <= 1 || isLoadingChangeQuantity
                              }
                              name="button-1"
                              className="flex items-center justify-center w-7 h-7 outline-none bg-[#F7F7F7] border-gray-300 border"
                            >
                              <HiOutlineMinusSmall size={20} />
                            </button>
                            <h5 className="flex items-center justify-center w-7 h-7">
                              {item.quantity}
                            </h5>
                            <button
                              type="button"
                              name="button-1"
                              onClick={() =>
                                handleIncrement(
                                  item.id,
                                  item.quantity,
                                  item.stock
                                )
                              }
                              disabled={
                                item.quantity >=
                                  (item.stock < max ? item.stock : max) ||
                                isLoadingChangeQuantity
                              }
                              className="flex items-center justify-center w-7 h-7 outline-none bg-[#F7F7F7] border-gray-300 border"
                            >
                              <HiOutlinePlusSmall size={20} />
                            </button>
                          </div>

                          <h5 className="font-semibold text-[#C62028]">
                            {item.discount > 0
                              ? (
                                  (item.price - item.discount) *
                                  item.quantity
                                ).toLocaleString("vi-VN") + "₫"
                              : (item.price * item.quantity).toLocaleString(
                                  "vi-VN"
                                ) + "₫"}
                          </h5>
                        </div>
                      </div>
                    </div>

                    {item.stock < item.quantity && (
                      <div>
                        <p className="text-[#C62028] font-semibold">
                          The product is currently out of sufficient stock.
                          Please reduce the quantity or remove the item from
                          your cart.
                        </p>
                      </div>
                    )}
                  </div>
                ))}
              </div>

              <div className="bg-[#F7F7F7] rounded-sm px-4 py-6 h-auto basis-[30%]">
                <div className="uppercase flex justify-between items-center">
                  <h4>Total</h4>
                  <h4 className="text-[#C62028]">
                    {totalPrice.toLocaleString("vi-VN")}₫
                  </h4>
                </div>

                <hr className="border-gray-300 my-[20px]" />

                <div className="space-y-[20px]">
                  <button
                    type="submit"
                    className="text-[0.9rem] px-4 py-2.5 w-full font-semibold tracking-wide bg-[#C62028] text-white rounded-md"
                  >
                    Checkout
                  </button>

                  <Link
                    className="text-[0.9rem] px-4 py-2.5 w-full tracking-wide bg-transparent hover:bg-gray-200 text-slate-900 border border-gray-300 rounded-md text-center font-semibold"
                    to={"/books/all"}
                  >
                    Continue Shopping
                  </Link>
                </div>
              </div>
            </div>
          </form>
        ) : (
          <div className="flex justify-center items-center h-[60vh]">
            <div className="flex flex-col justify-center items-center gap-[15px]">
              <Image
                source={"/assets/empty-cart.png"}
                alt={""}
                className={"w-[150px]"}
                loading="eager"
              />

              <h4>There’s nothing in your cart</h4>

              <Link
                to={"/books/all"}
                className="text-[0.9rem] border-2 uppercase border-[#C62028] rounded-md font-semibold px-3 py-2 hover:bg-[#C62028] text-[#C62028] hover:text-white"
              >
                Shop now
              </Link>
            </div>
          </div>
        )}
      </div>
    </section>
  );
}

export default CartItem;
