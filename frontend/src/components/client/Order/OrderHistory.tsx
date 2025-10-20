import { Link, useNavigate, useSearchParams } from "react-router-dom";
import Image from "../../Image";
import Loading from "../../Loading";
import { CiCalendar } from "react-icons/ci";
import type { OrderFull } from "../../../types/type";

function OrderHistory() {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const isLoading = false;
  const array = [
    { status: "", name: "All" },
    { status: 0, name: "Pending Confirmation" },
    { status: 1, name: "Confirmed" },
    { status: 2, name: "Delivering" },
    { status: 3, name: "Delivered Successfully" },
    { status: 4, name: "Cancelled" },
  ];

  const handleStatusChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    const status = e.target.value;
    const params = new URLSearchParams(searchParams.toString());

    if (!isNaN(Number(status))) {
      params.set("status", status);
    } else {
      params.delete("status");
    }

    params.set("page", "1");
    navigate(`?${params.toString()}`);
  };
  const orders: OrderFull[] = [
    {
      id: "1",
      orderCode: "ORD12345",
      user: "user1",
      fullname: "John Doe",
      phone: "0987654321",
      speaddress: "123 Green Street",
      city: "Ho Chi Minh City",
      ward: "District 1",
      paymethod: "Credit Card",
      productsBuy: [
        {
          _id: "i1",
          name: "book 1",
          images: ["/assets/products/book1.jpg"],
          quantity: 2,
          price: 150000,
          discount: 20000,
        },
        {
          _id: "i2",
          name: "book 2",
          images: ["/assets/products/book2.jpg"],
          quantity: 1,
          price: 250000,
          discount: 0,
        },
      ],
      status: 2,
      total: 530000,
      createdAt: "2025-10-10T09:00:00Z",
    },
  ];
  return (
    <div className="w-full max-w-full flex-1 sm:px-[15px] px-[10px]">
      <div className="flex justify-between items-center mb-[20px]">
        <h2>Order history</h2>

        <select
          onChange={handleStatusChange}
          value={searchParams.get("status") ?? ""}
          className="bg-gray-50 border border-gray-300 text-gray-900 text-[0.9rem] rounded-sm block p-2 outline-0"
        >
          {array.map((item) => (
            <option value={item.status} key={item.status}>
              {item.name}
            </option>
          ))}
        </select>
      </div>

      <div className="flex gap-5 flex-col">
        {isLoading ? (
          <Loading height={70} size={50} color="black" thickness={3} />
        ) : orders.length > 0 ? (
          orders.map((order) => (
            <div className="border border-gray-300 px-[15px]" key={order._id}>
              <div className="space-y-[10px] py-[15px] border-b border-gray-300">
                <div className="flex justify-between flex-wrap gap-[10px]">
                  <h5 className="font-semibold">Order {order.orderCode}</h5>

                  <p
                    className={`font-medium ${
                      order.status === 0
                        ? "text-gray-500"
                        : order.status === 1
                        ? "text-gray-500"
                        : order.status === 2
                        ? "text-gray-500"
                        : order.status === 3
                        ? "text-green-600"
                        : order.status === 4
                        ? "text-red-500"
                        : "text-gray-500"
                    }`}
                  >
                    {order.status === 0
                      ? "Pending Confirmation"
                      : order.status === 1
                      ? "Confirmed"
                      : order.status === 2
                      ? "Delivering"
                      : order.status === 3
                      ? "Delivered Successfully"
                      : order.status === 4
                      ? "Cancelled"
                      : ""}
                  </p>
                </div>

                <div className="text-gray-500 font-medium flex items-center gap-1">
                  <CiCalendar size={18} />{" "}
                  <span>
                    {new Date(order.createdAt).toLocaleString("vi-VN", {
                      year: "numeric",
                      month: "2-digit",
                      day: "2-digit",
                    })}
                  </span>
                </div>
              </div>

              {order.productsBuy.map((item, index) => (
                <div
                  key={index}
                  className="relative py-[15px] border-b border-gray-300 w-full"
                >
                  <Link to={`/order/${order.orderCode}`}>
                    <div className="flex items-center gap-[10px] w-full">
                      <div className="w-full max-w-[120px]">
                        <Image
                          source={item.images[0]}
                          alt={item.name}
                          className={"w-full object-cover"}
                          loading="lazy"
                        />
                      </div>

                      <div className="space-y-[15px]">
                        <div className="flex gap-[10px] flex-wrap">
                          <h5 className="font-medium">{item.name}</h5>
                          <span>x{item.quantity}</span>
                        </div>

                        <div className="flex gap-[10px] flex-wrap font-medium">
                          {item.discount > 0 ? (
                            <>
                              <del>{item.price.toLocaleString("vi-VN")}₫</del>
                              <span className="font-medium text-[#C62028]">
                                {(item.price - item.discount).toLocaleString(
                                  "vi-VN"
                                )}
                                ₫
                              </span>
                            </>
                          ) : (
                            <span className="font-medium text-[#C62028]">
                              {item.price.toLocaleString("vi-VN")} ₫
                            </span>
                          )}
                        </div>
                      </div>
                    </div>
                  </Link>
                </div>
              ))}

              <div className="py-[15px]">
                <div className="flex justify-between items-center flex-wrap gap-[10px]">
                  <h5 className="font-medium">
                    Total: {order.total.toLocaleString("vi-VN")}₫
                  </h5>

                  <Link
                    to={`/order/${order.orderCode}`}
                    className="text-white text-[0.9rem] font-medium px-[10px] py-[6px] bg-[#ee4d2d] hover:text-white"
                  >
                    View Details
                  </Link>
                </div>
              </div>
            </div>
          ))
        ) : (
          <div className="flex justify-center items-center h-[70vh]">
            <div className="flex flex-col justify-center items-center gap-[15px]">
              <Image
                source={"/assets/empty-order.png"}
                alt={""}
                className={"w-[120px]"}
                loading="eager"
              />

              <h4 className="text-gray-600">No orders found</h4>
            </div>
          </div>
        )}

        {/*
    <Pagination
          totalPages={totalPages}
          currentPage={currentPage}
          totalItems={totalItems}
        />
    */}
      </div>
    </div>
  );
}

export default OrderHistory;
