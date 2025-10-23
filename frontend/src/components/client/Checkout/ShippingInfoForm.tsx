import { memo, useMemo } from "react";
import type { Address, Province } from "../../../types/type";

type Props = {
  data: {
    fullname: string;
    phone: string;
    speaddress: string;
    city: string;
    ward: string;
  };
  setData: React.Dispatch<
    React.SetStateAction<{
      fullname: string;
      phone: string;
      speaddress: string;
      city: string;
      ward: string;
    }>
  >;
  addresses: Address[];
  handleChange: (
    e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>
  ) => void;
  handleGetAddress: (address: Address | null) => void;
  provinces: Province[];
};

function ShippingInfoForm({
  data,
  setData,
  addresses,
  handleChange,
  handleGetAddress,
  provinces,
}: Props) {
  const selectedProvince = useMemo(
    () => provinces?.find((p) => p.province === data.city),
    [provinces, data.city]
  );

  return (
    <div className="space-y-[15px]">
      <h4>Shipping information</h4>

      <div className="space-y-[5px]">
        <label htmlFor="" className="block text-[0.9rem] font-medium">
          Saved addresses
        </label>
        <select
          onChange={(e) => {
            const value = e.target.value;
            if (value === "") {
              handleGetAddress(null);
            } else {
              const selected = addresses.find((addr) => addr.id === value);
              if (selected) handleGetAddress(selected);
            }
          }}
          className="w-full rounded-md text-[0.9rem] border border-gray-200 px-2.5 py-2 outline-none focus:z-10 focus:border-[#197FB6] focus:ring-[#197FB6]"
        >
          <option value="">Select saved address</option>
          {addresses.map((address, index) => (
            <option value={address.id} key={index}>
              {address.speaddress}, {address.city}, {address.ward}
            </option>
          ))}
        </select>
      </div>

      <div className="space-y-[5px]">
        <label htmlFor="" className="block text-[0.9rem] font-medium">
          Fullname
        </label>
        <input
          type="text"
          name="fullname"
          value={data.fullname}
          onChange={handleChange}
          required
          className="w-full rounded-md border border-gray-200 px-2.5 py-2 text-[0.9rem] outline-none focus:z-10 focus:border-[#197FB6] focus:ring-[#197FB6]"
          placeholder="Họ và tên"
        />
      </div>

      <div className="space-y-[5px]">
        <label htmlFor="" className="block text-[0.9rem] font-medium">
          Phone
        </label>
        <input
          type="number"
          inputMode="numeric"
          name="phone"
          value={data.phone}
          onChange={handleChange}
          required
          className="w-full rounded-md border border-gray-200 px-2.5 py-2 text-[0.9rem] outline-none focus:z-10 focus:border-[#197FB6] focus:ring-[#197FB6]"
          placeholder="Số điện thoại"
        />
      </div>

      <div className="space-y-[5px]">
        <label htmlFor="" className="block text-[0.9rem] font-medium">
          Specific address
        </label>
        <input
          type="text"
          name="speaddress"
          value={data.speaddress}
          onChange={handleChange}
          required
          className="w-full rounded-md border border-gray-200 px-2.5 py-2 text-[0.9rem] outline-none focus:z-10 focus:border-[#197FB6] focus:ring-[#197FB6]"
          placeholder="Địa chỉ cụ thể"
        />
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-[14px]">
        <div className="space-y-[5px]">
          <label htmlFor="" className="block text-[0.9rem] font-medium">
            Province/city
          </label>
          <select
            name="city"
            required
            value={data.city}
            onChange={(e) =>
              setData((prev) => ({
                ...prev,
                city: e.target.value,
                ward: "",
              }))
            }
            className="w-full rounded-md text-[0.9rem] border border-gray-200 px-2.5 py-2 outline-none focus:z-10 focus:border-[#197FB6] focus:ring-[#197FB6]"
          >
            <option value="">Select province/city</option>
            {provinces?.map((province) => (
              <option key={province.id} value={province.province}>
                {province.province}
              </option>
            ))}
          </select>
        </div>

        <div className="space-y-[5px]">
          <label htmlFor="" className="block text-[0.9rem] font-medium">
            Ward/commune
          </label>
          <select
            name="ward"
            required
            disabled={!selectedProvince}
            value={data.ward}
            onChange={handleChange}
            className="w-full rounded-md text-[0.9rem] border border-gray-200 px-2.5 py-2 text-sm outline-none focus:z-10 focus:border-[#197FB6] focus:ring-[#197FB6]"
          >
            <option value="">Select ward/Commune</option>
            {selectedProvince?.wards.map((ward, idx) => (
              <option key={idx} value={ward.name}>
                {ward.name}
              </option>
            ))}
          </select>
        </div>
      </div>
    </div>
  );
}

export default memo(ShippingInfoForm);
