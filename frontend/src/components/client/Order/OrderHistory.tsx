import { Link, useNavigate, useSearchParams } from "react-router-dom";
import Image from "../../Image";
import Loading from "../../Loading";
import { CiCalendar } from "react-icons/ci";
import useGetOrders from "../../../hooks/client/useGetOrders";
import useGetCurrentUser from "../../../hooks/useGetCurrentUser";
import Pagination from "../Pagination";

function OrderHistory() {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const { user } = useGetCurrentUser("client");
  const { orders, isLoading, totalItems, totalPages, currentPage } =
    useGetOrders(user?.id || "");
  const array = [
    { status: "", name: "Tất cả" },
    { status: 0, name: "Chờ xác nhận" },
    { status: 1, name: "Xác nhận" },
    { status: 2, name: "Đang giao" },
    { status: 3, name: "Giao thành công" },
    { status: 4, name: "Đã hủy" },
    { status: 5, name: "Trả hàng" },
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

  return (
    <div className="w-full max-w-full flex-1 sm:px-[15px] px-[10px]">
      <div className="flex justify-between items-center mb-[20px]">
        <h2>Đơn hàng</h2>

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
          <Loading height={60} size={50} color="black" thickness={3} />
        ) : orders.length > 0 ? (
          orders.map((order) => (
            <div className="border border-gray-300 px-[15px]" key={order.id}>
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
                        : order.status === 5
                        ? "text-red-500"
                        : "text-gray-500"
                    }`}
                  >
                    {order.status === 0
                      ? "Chờ xác nhận"
                      : order.status === 1
                      ? "Xác nhận"
                      : order.status === 2
                      ? "Đang giao"
                      : order.status === 3
                      ? "Giao thành công"
                      : order.status === 4
                      ? "Đã hủy"
                      : order.status === 5
                      ? "Trả hàng"
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

              {order.items.map((item, index) => (
                <div
                  key={index}
                  className="relative py-[15px] border-b border-gray-300 w-full"
                >
                  <Link to={`/order/${order.orderCode}`}>
                    <div className="flex items-center gap-[10px] w-full">
                      <div className="w-[120px] h-[120px] overflow-hidden">
                        <Image
                          source={`${import.meta.env.VITE_BACKEND_URL}${
                            item.images[0]
                          }`}
                          alt={item.title}
                          className={"w-full h-full object-contain"}
                          loading="lazy"
                        />
                      </div>

                      <div className="space-y-[15px]">
                        <div className="flex gap-[10px] flex-wrap">
                          <h5 className="font-medium">{item.title}</h5>
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
                    Tổng tiền: {order.total.toLocaleString("vi-VN")}₫
                  </h5>

                  <Link
                    to={`/order/${order.orderCode}`}
                    className="text-white text-[0.9rem] font-medium px-[10px] py-[6px] bg-[#C62028]"
                  >
                    Xem chi tiết
                  </Link>
                </div>
              </div>
            </div>
          ))
        ) : (
          <div className="flex justify-center items-center h-[60vh]">
            <div className="flex flex-col justify-center items-center gap-[15px]">
              <Image
                source={"/assets/empty-order.png"}
                alt={""}
                className={"w-[120px]"}
                loading="eager"
              />

              <h4>Không có đơn hàng nào</h4>
            </div>
          </div>
        )}

        <Pagination
          totalPages={totalPages}
          currentPage={currentPage}
          totalItems={totalItems}
        />
      </div>
    </div>
  );
}

export default OrderHistory;
