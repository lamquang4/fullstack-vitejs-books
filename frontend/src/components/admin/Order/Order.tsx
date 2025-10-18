import { LiaExternalLinkAltSolid } from "react-icons/lia";
import Image from "../../Image";
import Pagination from "../Pagination";
import FilterDropDownMenu from "../FilterDropDownMenu";
import InputSearch from "../InputSearch";
import Loading from "../../Loading";
import {
  Link,
  useLocation,
  useNavigate,
  useSearchParams,
} from "react-router-dom";

function Order() {
  const navigate = useNavigate();
  const location = useLocation();
  const [searchParams, setSearchParams] = useSearchParams();
  const array = [
    { name: "All", value: null },
    { name: "Pending Payment", value: -1 },
    { name: "Waiting for Confirmation", value: 0 },
    { name: "Confirmed", value: 1 },
    { name: "Shipping", value: 2 },
    { name: "Delivered Successfully", value: 3 },
    { name: "Cancelled", value: 4 },
  ];

  const handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();

    const form = e.currentTarget;
    const formData = new FormData(form);

    const start = formData.get("start") as string;
    const end = formData.get("end") as string;

    const params = new URLSearchParams(searchParams.toString());

    if (start) params.set("start", start);
    else params.delete("start");

    if (end) params.set("end", end);
    else params.delete("end");

    navigate(`${location.pathname}?${params.toString()}`);
  };
  return (
    <>
      <div className="py-[1.3rem] px-[1.2rem] bg-[#f1f4f9] space-y-[20px]">
        <h2 className=" text-[#74767d]">Đơn hàng</h2>

        <div>
          <form onSubmit={handleSubmit}>
            <div className="flex gap-[15px] flex-wrap">
              <div className="relative flex gap-1.5 items-center">
                <label htmlFor="" className="text-[0.9rem]   font-medium">
                  From:
                </label>
                <input
                  name="start"
                  type="date"
                  className="bg-gray-50 border border-gray-300 text-[0.9rem] p-[6px_10px] outline-none focus:border-gray-400  "
                />
              </div>

              <div className="relative flex gap-1.5 items-center">
                <label htmlFor="" className="text-[0.9rem]   font-medium">
                  To:
                </label>
                <input
                  name="end"
                  type="date"
                  className="bg-gray-50 border border-gray-300 text-[0.9rem] p-[6px_10px] outline-none focus:border-gray-400  "
                />
              </div>

              <button className="p-[6px_10px] text-[0.9rem] bg-[#22BAA0] text-white">
                Search
              </button>
            </div>
          </form>
        </div>
      </div>

      <div className=" bg-white  w-full overflow-auto">
        <div className="p-[1.2rem]">
          <InputSearch />
        </div>

        <table className="w-[350%] border-collapse sm:w-[220%] xl:w-full text-[0.9rem]">
          <thead>
            <tr className="bg-[#E9EDF2] text-left">
              <th className="p-[1rem]  ">Order Code</th>

              <th className="p-[1rem]  ">Order recipient</th>
              <th className="p-[1rem]  ">Account</th>
              <th className="p-[1rem]  ">Payment method</th>

              <th className="p-[1rem]  ">Total</th>
              <th className="p-[1rem]  ">Created date</th>
              <th className="p-[1rem]   relative">
                <FilterDropDownMenu
                  title="Status"
                  array={array}
                  paramName="status"
                />
              </th>
              <th className="p-[1rem]  ">Action</th>
            </tr>
          </thead>
          <tbody>
            {isLoading ? (
              <tr>
                <td colSpan={8} className="w-full">
                  <Loading height={60} size={50} color="black" thickness={2} />
                </td>
              </tr>
            ) : orders.length > 0 ? (
              orders.map((order) => (
                <tr key={order._id} className="hover:bg-[#f2f3f8]">
                  <td className="p-[1rem] text-[#22BAA0] font-semibold text-[0.9rem]">
                    {order.orderCode}
                  </td>
                  <td className="p-[1rem]  ">{order.fullname}</td>
                  <th className="p-[1rem]  ">Quanglam</th>
                  <td className="p-[1rem]  ">{order.paymethod}</td>
                  <td className="p-[1rem]  ">
                    {order.total!.toLocaleString("vi-VN")}₫
                  </td>
                  <td className="p-[1rem]">
                    {new Date(order.createdAt).toLocaleString("vi-VN", {
                      year: "numeric",
                      month: "2-digit",
                      day: "2-digit",
                      hour: "2-digit",
                      minute: "2-digit",
                    })}
                  </td>

                  <td className="p-[1rem]">
                    <select
                      name="status"
                      value={order.status}
                      className="border border-gray-300 p-[6px_10px] text-[0.9rem] outline-none focus:border-gray-400  "
                    >
                      {order.status === -1 && (
                        <option value="-1">Pending Payment</option>
                      )}
                      {order.status === 0 && (
                        <>
                          <option value="0">Waiting for Confirmation</option>
                          <option value="1">Confirmed</option>
                          <option value="4">Cancelled</option>
                        </>
                      )}
                      {order.status === 1 && (
                        <>
                          <option value="1">Confirmed</option>
                          <option value="2">Shipping</option>
                          <option value="4">Cancelled</option>
                        </>
                      )}
                      {order.status === 2 && (
                        <>
                          <option value="2">Shipping</option>
                          <option value="3">Delivered Successfully</option>
                          <option value="4">Cancelled</option>
                        </>
                      )}
                      {order.status === 3 && (
                        <option value="3">Delivered Successfully</option>
                      )}
                      {order.status === 4 && (
                        <option value="4">Cancelled</option>
                      )}
                    </select>
                  </td>
                  <td className="p-[1rem]  ">
                    <div className="flex items-center gap-[15px]">
                      <Link to={`/admin/order/${order._id}`}>
                        <LiaExternalLinkAltSolid
                          size={23}
                          className="text-[#076ffe]"
                        />
                      </Link>
                    </div>
                  </td>
                </tr>
              ))
            ) : (
              <tr>
                <td colSpan={8} className="w-full h-[70vh]">
                  <div className="flex justify-center items-center">
                    <Image
                      source={"/assets/notfound1.png"}
                      alt={""}
                      className={"w-[135px]"}
                      loading="lazy"
                    />
                  </div>
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>

      <Pagination
        totalPages={totalPages}
        currentPage={currentPage}
        limit={limit}
        totalItems={totalItems}
      />
    </>
  );
}

export default Order;
