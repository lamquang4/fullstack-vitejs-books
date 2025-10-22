import { LiaExternalLinkAltSolid } from "react-icons/lia";
import Image from "../../Image";
import Pagination from "../Pagination";
import FilterDropDownMenu from "../FilterDropDownMenu";
import InputSearch from "../InputSearch";
import Loading from "../../Loading";
import { Link } from "react-router-dom";
import useGetOrders from "../../../hooks/admin/useGetOrders";
import useUpdateStatusOrder from "../../../hooks/admin/useUpdateStatusOrder";
import { RiTruckLine } from "react-icons/ri";
import { LuClock } from "react-icons/lu";
import { TbCancel, TbPackageImport } from "react-icons/tb";
import StaticCard from "../StaticCard";
function Order() {
  const {
    orders,
    totalByStatus,
    mutate,
    isLoading,
    totalItems,
    totalPages,
    limit,
    currentPage,
  } = useGetOrders();
  const { updateStatusOrder, isLoading: isLoadingUpdate } =
    useUpdateStatusOrder();

  const handleUpdateStatus = async (id: string, status: number) => {
    if (!id && !status) {
      return;
    }
    await updateStatusOrder(id, status);
    mutate();
  };

  const array = [
    { name: "All", value: null },
    { name: "Pending Payment", value: -1 },
    { name: "Pending Confirmation", value: 0 },
    { name: "Confirmed", value: 1 },
    { name: "Delivering", value: 2 },
    { name: "Delivered Successfully", value: 3 },
    { name: "Cancelled", value: 4 },
    { name: "Returned order", value: 5 },
  ];

  const array1 = [
    {
      title: "Pending confirmation",
      number: totalByStatus?.["0"] || 0,
      icon1: <LuClock size={25} />,
    },
    {
      title: "Delivered orders",
      number: totalByStatus?.["3"] || 0,
      icon1: <RiTruckLine size={25} />,
    },
    {
      title: "Cancelled orders",
      number: totalByStatus?.["4"] || 0,
      icon1: <TbCancel size={25} />,
    },
    {
      title: "Returned order",
      number: totalByStatus?.["5"] || 0,
      icon1: <TbPackageImport size={25} />,
    },
  ];

  return (
    <>
      <div className="py-[1.3rem] px-[1.2rem] bg-[#f1f4f9] space-y-[20px]">
        <h2 className=" text-[#74767d]">Order</h2>

        <div>
          <StaticCard array={array1} />
        </div>
      </div>

      <div className=" bg-white  w-full overflow-auto">
        <div className="p-[1.2rem]">
          <InputSearch />
        </div>

        <table className="w-[350%] border-collapse sm:w-[220%] xl:w-full text-[0.9rem]">
          <thead>
            <tr className="bg-[#E9EDF2] text-left">
              <th className="p-[1rem]  ">Order code</th>

              <th className="p-[1rem]  ">Customer</th>
              <th className="p-[1rem]  ">Account email</th>

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
                <tr key={order.id} className="hover:bg-[#f2f3f8]">
                  <td className="p-[1rem] font-semibold">{order.orderCode}</td>
                  <td className="p-[1rem]  ">{order.fullname}</td>
                  <td className="p-[1rem]  ">{order.accountEmail}</td>
                  <td className="p-[1rem] uppercase">{order.paymethod}</td>
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
                      disabled={isLoadingUpdate}
                      onChange={(e) =>
                        handleUpdateStatus(order.id, parseInt(e.target.value))
                      }
                      value={order.status}
                      className="border border-gray-300 p-[6px_10px] text-[0.9rem] outline-none focus:border-gray-400  "
                    >
                      {order.status === -1 && (
                        <option value="-1">Pending Payment</option>
                      )}
                      {order.status === 0 && (
                        <>
                          <option value="0">Pending Confirmation</option>
                          <option value="1">Confirmed</option>
                          <option value="4">Cancelled</option>
                        </>
                      )}
                      {order.status === 1 && (
                        <>
                          <option value="1">Confirmed</option>
                          <option value="2">Delivering</option>
                          <option value="4">Cancelled</option>
                        </>
                      )}
                      {order.status === 2 && (
                        <>
                          <option value="2">Delivering</option>
                          <option value="3">Delivered Successfully</option>
                          <option value="4">Cancelled</option>
                        </>
                      )}
                      {order.status === 3 && (
                        <>
                          <option value="3">Delivered Successfully</option>
                          <option value="5">Returned order</option>
                        </>
                      )}
                      {order.status === 4 && (
                        <option value="4">Cancelled</option>
                      )}
                      {order.status === 5 && (
                        <option value="5">Returned order</option>
                      )}
                    </select>
                  </td>
                  <td className="p-[1rem]  ">
                    <div className="flex items-center gap-[15px]">
                      <Link to={`/admin/order/${order.id}`}>
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
