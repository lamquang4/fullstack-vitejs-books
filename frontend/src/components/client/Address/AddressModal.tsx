import { HiMiniXMark } from "react-icons/hi2";
import Overplay from "../Overplay";
import { memo, useEffect, useMemo, useState } from "react";
import toast from "react-hot-toast";
import useGetAddress from "../../../hooks/client/useGetAddress";
import useGetProvinces from "../../../hooks/useGetProvincesVN";
import useAddAddress from "../../../hooks/client/useAddAddress";
import useUpdateAddress from "../../../hooks/client/useUpdateAddress";
import { validatePhone } from "../../../utils/validatePhone";

type Props = {
  isOpen: boolean;
  toggleMenu: () => void;
  mutateAddresses: () => void;
  addressId: string;
  addressesLength: number;
  userId: string;
};
function AddressModal({
  isOpen,
  toggleMenu,
  mutateAddresses,
  addressesLength,
  addressId,
  userId,
}: Props) {
  const { provinces } = useGetProvinces();
  const { address, mutate, isLoading } = useGetAddress(addressId, userId);
  const { addAddress, isLoading: isLoadingAddAddress } = useAddAddress();
  const { updateAddress, isLoading: isLoadingUpdateAddress } =
    useUpdateAddress(addressId);

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
    if (address && !isLoading) {
      setData({
        fullname: address?.fullname || "",
        phone: address?.phone || "",
        speaddress: address?.speaddress || "",
        city: address?.city || "",
        ward: address?.ward || "",
      });
    }
  }, [address, isLoading]);

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

    if (!validatePhone(data.phone)) {
      toast.error("Số điện thoại không hợp lệ");
      mutate();
      return;
    }

    if (addressesLength === 5 && !addressId) {
      toast.error("Bạn chỉ có thể lưu tối đa 5 địa chỉ cho tài khoản");
      mutate();
      return;
    }

    try {
      const payload = {
        fullname: data.fullname.trim(),
        phone: data.phone.trim(),
        speaddress: data.speaddress.trim(),
        city: data.city,
        ward: data.ward,
        userId: userId,
      };

      if (userId) {
        if (addressId) {
          await updateAddress(payload);
        } else {
          await addAddress(payload);

          setData({
            fullname: "",
            phone: "",
            speaddress: "",
            city: "",
            ward: "",
          });
        }
      }

      mutateAddresses();
      mutate();
    } catch (err: any) {
      toast.error(err?.response?.data?.msg);
      mutate();
    }
  };

  return (
    <div className="fixed top-0 right-0 left-0 z-20 h-full overflow-y-auto overflow-x-hidden">
      <div className="py-[40px] px-[10px] sm:px-[15px] flex justify-center items-center">
        <div className="relative w-full max-w-lg max-h-full">
          <div className="relative sm:p-[25px_20px] p-[25px_15px] bg-white z-20 space-y-[15px] rounded-lg">
            <div className="flex items-center justify-between">
              <h4 className="uppercase">Địa chỉ của bạn</h4>

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
                    Họ tên
                  </label>
                  <input
                    type="text"
                    name="fullname"
                    required
                    onChange={handleChange}
                    value={data.fullname}
                    className="bg-gray-50 border border-gray-300   text-[0.9rem] rounded-sm block w-full p-2 outline-0"
                    placeholder="Họ tên"
                  />
                </div>

                <div className="col-span-2 w-full space-y-[5px]">
                  <label
                    htmlFor="phone"
                    className="block text-[0.9rem] font-medium"
                  >
                    Số điện thoại
                  </label>
                  <input
                    type="number"
                    inputMode="numeric"
                    required
                    name="phone"
                    onChange={handleChange}
                    value={data.phone}
                    className="bg-gray-50 border border-gray-300   text-[0.9rem] rounded-sm block w-full p-2 outline-0"
                    placeholder="Số điện thoại"
                  />
                </div>

                <div className="col-span-2 w-full space-y-[5px]">
                  <label
                    htmlFor="speaddress"
                    className="block text-[0.9rem] font-medium"
                  >
                    Địa chỉ cụ thể
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
                    Tỉnh/thành phố
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
                    <option value="">Chọn Tỉnh/thành phố</option>
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
                    Phường/xã
                  </label>
                  <select
                    name="ward"
                    required
                    value={data.ward}
                    onChange={handleChange}
                    className="bg-gray-50 border border-gray-300   text-[0.9rem] rounded-sm block w-full p-2 outline-0"
                  >
                    <option value="">Chọn Phường/xã</option>
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
                  {isLoadingAddAddress || isLoadingUpdateAddress
                    ? "Đang lưu..."
                    : "Lưu"}
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
