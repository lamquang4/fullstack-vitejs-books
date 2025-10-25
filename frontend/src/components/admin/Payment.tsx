import Image from "../Image";
import Loading from "../Loading";
import InputSearch from "./InputSearch";
import useGetPayments from "../../hooks/admin/useGetPayments";
import Pagination from "./Pagination";
import FilterDropDownMenu from "./FilterDropDownMenu";

function Payment() {
  const { payments, isLoading, totalItems, totalPages, currentPage, limit } =
    useGetPayments();

  const array = [
    { name: "All", value: null },
    { name: "Successfully", value: 1 },
    { name: "Refund", value: 0 },
  ];

  console.log(payments);

  return (
    <>
      <div className="py-[1.3rem] px-[1.2rem] bg-[#f1f4f9]">
        <div className="flex justify-between items-center">
          <h2 className=" text-[#74767d]">Payments ({totalItems})</h2>
        </div>
      </div>

      <div className=" bg-white w-full overflow-auto">
        <div className="p-[1.2rem]">
          <InputSearch />
        </div>

        <table className="w-[350%] border-collapse sm:w-[220%] xl:w-full text-[0.9rem]">
          <thead>
            <tr className="bg-[#E9EDF2] text-left">
              <th className="p-[1rem]   ">Order code</th>

              <th className="p-[1rem]  ">Payment method</th>
              <th className="p-[1rem]  ">Amount</th>
              <th className="p-[1rem]  ">Transaction ID</th>
              <th className="p-[1rem]   relative">
                <FilterDropDownMenu
                  title="Status"
                  array={array}
                  paramName="status"
                />
              </th>
              <th className="p-[1rem]  ">Created at</th>
            </tr>
          </thead>
          <tbody>
            {isLoading ? (
              <tr>
                <td colSpan={8} className="w-full">
                  <Loading height={60} size={50} color="black" thickness={2} />
                </td>
              </tr>
            ) : payments.length > 0 ? (
              payments.map((payment) => (
                <tr key={payment.id} className="hover:bg-[#f2f3f8]">
                  <td className="p-[1rem] font-semibold">
                    {payment.orderCode}
                  </td>

                  <td className="p-[1rem] uppercase">{payment.paymethod}</td>

                  <td className="p-[1rem]">{payment.amount}</td>
                  <td className="p-[1rem]">{payment.transactionId}</td>
                  <td className="p-[1rem]">
                    {payment.status === 1
                      ? "Successfully"
                      : payment.status === 0
                      ? "Refund"
                      : ""}
                  </td>
                  <td className="p-[1rem]">
                    {new Date(payment.createdAt).toLocaleString("vi-VN", {
                      year: "numeric",
                      month: "2-digit",
                      day: "2-digit",
                      hour: "2-digit",
                      minute: "2-digit",
                    })}
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

export default Payment;
