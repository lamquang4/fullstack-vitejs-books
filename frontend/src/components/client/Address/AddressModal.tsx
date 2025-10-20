"use client";
import { HiMiniXMark } from "react-icons/hi2";
import Overplay from "../Overplay";
import { memo, useEffect, useMemo, useState } from "react";
import toast from "react-hot-toast";
import type { Province } from "../../../types/type";

type Props = {
  isOpen: boolean;
  toggleMenu: () => void;
  addressId: string;
  provinces?: Province[];
};
function AddressModal({ isOpen, toggleMenu, addressId, provinces }: Props) {
  const [data, setData] = useState({
    fullname: "",
    phone: "",
    speaddress: "",
    city: "",
    ward: "",
  });

  const selectedProvince = useMemo(
    () => provinces?.find((p) => p.province === data.city),
    [provinces, data.city]
  );

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

  const handleChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>
  ) => {
    const { name, value } = e.target;
    setData((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
  };

  return (
    <div className="fixed top-0 right-0 left-0 z-20 h-full overflow-y-auto overflow-x-hidden">
      <div className="py-[40px] px-[10px] sm:px-[15px] flex justify-center items-center">
        <div className="relative w-full max-w-lg max-h-full">
          <div className="relative sm:p-[25px_20px] p-[25px_15px] bg-white z-20 space-y-[15px] rounded-lg">
            <div className="flex items-center justify-between">
              <h4 className="uppercase">Your address</h4>

              <button
                type="button"
                className="bg-transparent ms-auto"
                onClick={toggleMenu}
              >
                <HiMiniXMark size={30} />
              </button>
            </div>

            <hr className=" border-gray-300" />

            <form onSubmit={handleSubmit} className="space-y-[30px]">
              <div className="grid gap-[15px] grid-cols-2">
                <div className="col-span-2 w-full space-y-[5px]">
                  <label
                    htmlFor="fullname"
                    className="block text-[0.9rem] font-medium"
                  >
                    Fullname
                  </label>
                  <input
                    type="text"
                    name="fullname"
                    required
                    onChange={handleChange}
                    value={data.fullname}
                    className="bg-gray-50 border border-gray-300   text-[0.9rem] rounded-sm block w-full p-2 outline-0"
                    placeholder="Fullname"
                  />
                </div>

                <div className="col-span-2 w-full space-y-[5px]">
                  <label
                    htmlFor="phone"
                    className="block text-[0.9rem] font-medium"
                  >
                    Phone
                  </label>
                  <input
                    type="number"
                    inputMode="numeric"
                    required
                    name="phone"
                    onChange={handleChange}
                    value={data.phone}
                    className="bg-gray-50 border border-gray-300   text-[0.9rem] rounded-sm block w-full p-2 outline-0"
                    placeholder="Phone"
                  />
                </div>

                <div className="col-span-2 w-full space-y-[5px]">
                  <label
                    htmlFor="speaddress"
                    className="block text-[0.9rem] font-medium"
                  >
                    Specific address
                  </label>
                  <input
                    type="text"
                    name="speaddress"
                    required
                    onChange={handleChange}
                    value={data.speaddress}
                    className="bg-gray-50 border border-gray-300 text-[0.9rem] rounded-sm block w-full p-2 outline-0"
                    placeholder="Specific address"
                  />
                </div>

                <div className="col-span-2 w-full space-y-[5px]">
                  <label
                    htmlFor="city"
                    className="block text-[0.9rem] font-medium"
                  >
                    Province/city
                  </label>
                  <select
                    name="cỉty"
                    required
                    value={data.city}
                    onChange={(e) =>
                      setData((prev) => ({
                        ...prev,
                        city: e.target.value,
                        ward: "",
                      }))
                    }
                    className="bg-gray-50 border border-gray-300   text-[0.9rem] rounded-sm block w-full p-2 outline-0"
                  >
                    <option value="">Select province/city</option>
                    {provinces?.map((province) => (
                      <option key={province.id} value={province.province}>
                        {province.province}
                      </option>
                    ))}
                  </select>
                </div>

                <div className="col-span-2 w-full space-y-[5px]">
                  <label
                    htmlFor="ward"
                    className="block text-[0.9rem] font-medium"
                  >
                    Ward/Commune
                  </label>
                  <select
                    name="ward"
                    required
                    value={data.ward}
                    onChange={handleChange}
                    className="bg-gray-50 border border-gray-300   text-[0.9rem] rounded-sm block w-full p-2 outline-0"
                  >
                    <option value="">Select ward/commune</option>
                    {selectedProvince?.wards.map((ward, idx) => (
                      <option key={idx} value={ward.name}>
                        {ward.name}
                      </option>
                    ))}
                  </select>
                </div>
              </div>

              <div className="flex justify-center items-center">
                <button
                  type="submit"
                  className="px-[14px] py-[6px] bg-red-600 text-white text-[0.9rem] font-medium text-center rounded-sm hover:bg-red-700"
                >
                  Save
                </button>
              </div>
            </form>
          </div>
        </div>

        {isOpen && <Overplay closeMenu={toggleMenu} IndexForZ={15} />}
      </div>
    </div>
  );
}

export default memo(AddressModal);
