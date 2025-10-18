import { memo, useMemo } from "react";
import type { Address, Province } from "../../../types/type";

type Props = {
  data: {
    fullname: string;
    phone: string;
    speaddress: string;
  };
  addresses: Address[];
  provinces: Province[];
  provinceName: string;
  ward: string;
  handleChange: (
    e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>
  ) => void;
  handleGetAddress: (address: Address | null) => void;
  setProvinceName: (value: string) => void;
  setWard: (value: string) => void;
};

function ShippingInfoForm({
  data,
  addresses,
  provinces,
  provinceName,
  ward,
  handleChange,
  handleGetAddress,
  setProvinceName,
  setWard,
}: Props) {
  const selectedProvince = useMemo(() => {
    return provinces?.find((province) => province.province === provinceName);
  }, [provinces, provinceName]);

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
              const selected = addresses.find((addr) => addr._id === value);
              if (selected) handleGetAddress(selected);
            }
          }}
          className="w-full rounded-md text-[0.9rem] border border-gray-200 px-2.5 py-2 outline-none focus:z-10 focus:border-blue-500 focus:ring-blue-500"
        >
          <option value="">Select saved address</option>
          {addresses.map((address, index) => (
            <option value={address._id} key={index}>
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
          className="w-full rounded-md border border-gray-200 px-2.5 py-2 text-[0.9rem] outline-none focus:z-10 focus:border-blue-500 focus:ring-blue-500"
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
          className="w-full rounded-md border border-gray-200 px-2.5 py-2 text-[0.9rem] outline-none focus:z-10 focus:border-blue-500 focus:ring-blue-500"
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
          className="w-full rounded-md border border-gray-200 px-2.5 py-2 text-[0.9rem] outline-none focus:z-10 focus:border-blue-500 focus:ring-blue-500"
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
            value={provinceName}
            onChange={(e) => {
              setProvinceName(e.target.value);
              setWard("");
            }}
            className="w-full rounded-md text-[0.9rem] border border-gray-200 px-2.5 py-2 outline-none focus:z-10 focus:border-blue-500 focus:ring-blue-500"
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
            value={ward}
            onChange={(e) => setWard(e.target.value)}
            className="w-full rounded-md text-[0.9rem] border border-gray-200 px-2.5 py-2 text-sm outline-none focus:z-10 focus:border-blue-500 focus:ring-blue-500"
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
