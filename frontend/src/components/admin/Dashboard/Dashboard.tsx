import StaticCard from "../StaticCard";
import BestsellerBook from "./BestsellerBook";
import useGetStatistic from "../../../hooks/admin/useGetStatistic";
import { FaRegMoneyBillAlt } from "react-icons/fa";
import { LuPackageCheck } from "react-icons/lu";

function Dashboard() {
  const { totalRevenue, todayRevenue, todaySoldQuantity, totalSoldQuantity } =
    useGetStatistic();
  const array1 = [
    {
      title: "Total revenue",
      number: `${totalRevenue.toLocaleString("vi-VN")}₫` || 0,
      icon1: <FaRegMoneyBillAlt size={25} />,
    },
    {
      title: "Today revenue",
      number: `${todayRevenue.toLocaleString("vi-VN")}₫` || 0,
      icon1: <FaRegMoneyBillAlt size={25} />,
    },
    {
      title: "Total sold quantity",
      number: totalSoldQuantity || 0,
      icon1: <LuPackageCheck size={25} />,
    },
    {
      title: "Tody sold quantity",
      number: todaySoldQuantity || 0,
      icon1: <LuPackageCheck size={25} />,
    },
  ];
  return (
    <>
      <div className="py-[1.3rem] px-[1.2rem] bg-[#f1f4f9] space-y-[20px]">
        <h2 className=" text-[#74767d]">Statistic</h2>

        <div>
          <StaticCard array={array1} />
        </div>
      </div>
      <BestsellerBook />
    </>
  );
}

export default Dashboard;
