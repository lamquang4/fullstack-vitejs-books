import { HiMiniXMark } from "react-icons/hi2";
import { FaArrowRightLong } from "react-icons/fa6";
import Overplay from "./Overplay";
import { memo, useEffect } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";
type Props = {
  isOpen: boolean;
  onToggleMenu: () => void;
};
function AdvancedSearch({ isOpen, onToggleMenu }: Props) {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  useEffect(() => {
    if (isOpen) {
      document.body.style.overflow = "hidden";
    } else {
      document.body.style.overflow = "";
    }

    return () => {
      document.body.style.overflow = "";
    };
  }, [isOpen]);

  const handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();

    const form = e.currentTarget;
    const formData = new FormData(form);

    const minRaw = formData.get("min") as string;
    const maxRaw = formData.get("max") as string;

    const min = parseInt(minRaw, 10);
    const max = parseInt(maxRaw, 10);

    const params = new URLSearchParams(searchParams.toString());

    if (!isNaN(min) && !isNaN(max) && min > max) {
      params.delete("min");
      params.delete("max");
    } else {
      if (!isNaN(min)) params.set("min", min.toString());
      else params.delete("min");

      if (!isNaN(max)) params.set("max", max.toString());
      else params.delete("max");
    }

    params.set("page", "1");
    navigate(`?${params.toString()}`);
    onToggleMenu();
  };

  const handleRemovePriceFilter = () => {
    const params = new URLSearchParams(searchParams.toString());
    params.delete("min");
    params.delete("max");
    navigate(`?${params.toString()}`);
  };

  return (
    <>
      <div
        className={`fixed top-0 left-0 w-[320px] h-screen bg-white z-[25] transform transition-transform duration-300 ease-in-out ${
          isOpen ? "translate-x-[0px]" : "translate-x-[-320px]"
        }`}
      >
        <div className="sticky top-0 overflow-hidden p-[15px] bg-white z-[25] flex justify-between items-center border-b border-gray-300">
          <h4 className="uppercase">Bộ lọc</h4>

          <button onClick={onToggleMenu}>
            <HiMiniXMark size={30} color="black" />
          </button>
        </div>

        <div className="overflow-y-auto h-[calc(100vh-100px)] pb-[60px] custom-scroll">
          {(searchParams.get("min") || searchParams.get("max")) && (
            <div className="p-[15px] border-b border-gray-300 space-y-[15px]">
              <h5 className="font-semibold capitalize">Bộ lọc đã chọn</h5>
              <div className="flex items-center flex-wrap gap-3">
                {(searchParams.get("min") || searchParams.get("max")) && (
                  <div className="bg-[#f5f5f5] border border-gray-300 rounded-[4px] p-[9px_10px] flex justify-between items-center gap-1.5 cursor-pointer">
                    <button onClick={handleRemovePriceFilter}>
                      <HiMiniXMark size={20} color="black" />
                    </button>
                    <span className="text-[0.9rem]">
                      {searchParams.get("min") &&
                        Number(searchParams.get("min")).toLocaleString(
                          "vi-VN"
                        ) + "₫"}
                      {searchParams.get("min") &&
                        searchParams.get("max") &&
                        " - "}
                      {!searchParams.get("min") &&
                        searchParams.get("max") &&
                        " "}
                      {searchParams.get("max") &&
                        Number(searchParams.get("max")).toLocaleString(
                          "vi-VN"
                        ) + "₫"}
                      {searchParams.get("min") &&
                        !searchParams.get("max") &&
                        " trở lên"}
                      {!searchParams.get("min") &&
                        searchParams.get("max") &&
                        " trở xuống"}
                    </span>
                  </div>
                )}
              </div>
            </div>
          )}

          <form id="advanced-search-form" onSubmit={handleSubmit}>
            <div className="border-b border-gray-300 p-[15px] space-y-[15px]">
              <div className="flex items-center justify-between">
                <h5 className="capitalize font-semibold">Giá</h5>
              </div>

              <div className="w-full flex justify-center items-center gap-[10px]">
                <div className="border border-gray-300 p-2.5 w-full">
                  <label
                    htmlFor="min"
                    className="text-[0.9rem] text-gray-600 block mb-1"
                  >
                    Tối thiểu
                  </label>
                  <div className="flex items-center">
                    <input
                      type="number"
                      id="min"
                      className="w-full text-[0.9rem] outline-none border-none p-0"
                      name="min"
                      inputMode="numeric"
                      min={0}
                      defaultValue={searchParams.get("min") || ""}
                    />
                    <span className="text-gray-600">đ</span>
                  </div>
                </div>

                <div className="border border-gray-300 p-2.5 w-full">
                  <label
                    htmlFor="max"
                    className="text-[0.9rem] text-gray-600 block mb-1"
                  >
                    Tối đa
                  </label>
                  <div className="flex items-center">
                    <input
                      type="number"
                      id="max"
                      className="w-full text-[0.9rem] outline-none border-none p-0"
                      name="max"
                      inputMode="numeric"
                      max={1000000}
                      defaultValue={searchParams.get("max") || ""}
                    />
                    <span className=" text-gray-600">đ</span>
                  </div>
                </div>
              </div>
            </div>
          </form>
        </div>

        <div className="fixed bottom-0 left-0 w-[320px] bg-white z-[30] p-[15px] flex justify-center">
          <button
            type="submit"
            form="advanced-search-form"
            className="bg-[#C62028] text-white px-[18px] py-[10px] text-[0.9rem] flex justify-center items-center gap-2.5 font-semibold"
          >
            Áp dụng
            <FaArrowRightLong size={20} />
          </button>
        </div>
      </div>

      {isOpen && <Overplay onClose={onToggleMenu} IndexForZ={15} />}
    </>
  );
}

export default memo(AdvancedSearch);
