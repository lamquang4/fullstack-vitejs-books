import { useMemo, useState } from "react";
import toast from "react-hot-toast";
import { Link } from "react-router-dom";
import useAddAdmin from "../../../hooks/admin/useAddAdmin";
import useGetProvinces from "../../../hooks/useGetProvincesVN";
import { validateEmail } from "../../../utils/validateEmail";
import { validatePhone } from "../../../utils/validatePhone";

function AddAdmin() {
  const { addAdmin, isLoading } = useAddAdmin();
  const { provinces } = useGetProvinces();

  const [data, setData] = useState({
    email: "",
    password: "",
    phone: "",
    fullname: "",
    speaddress: "",
    city: "",
    ward: "",
    role: "",
  });

  const selectedProvince = useMemo(
    () => provinces?.find((p) => p.province === data.city),
    [provinces, data.city]
  );

  const handleChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>
  ) => {
    const { name, value } = e.target;
    setData((prev) => ({
      ...prev,
      [name]: name === "email" ? value.toLowerCase() : value,
    }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!validateEmail(data.email)) {
      toast.error("Invalid email");
      return;
    }
    if (!validatePhone(data.phone)) {
      toast.error("Invalid phone");
      return;
    }
    if (data.password.trim().length < 6) {
      toast.error("Password must be at least 6 characters long");
      return;
    }
    try {
      await addAdmin({
        email: data.email,
        password: data.password,
        role: parseInt(data.role),
        address: {
          fullname: data.fullname,
          phone: data.phone,
          speaddress: data.speaddress,
          ward: data.ward,
          city: data.city,
        },
      });

      setData({
        email: "",
        password: "",
        role: "0",
        fullname: "",
        phone: "",
        speaddress: "",
        city: "",
        ward: "",
      });
    } catch (err: any) {
      toast.error(err?.response?.data?.message);
    }
  };

  return (
    <div className="py-[30px] sm:px-[25px] px-[15px] bg-[#F1F4F9] h-auto">
      <form className="flex flex-col gap-7 w-full" onSubmit={handleSubmit}>
        <h2 className=" text-[#74767d]">Add administrator</h2>

        <div className="flex gap-[25px] w-full flex-col">
          <div className="md:p-[25px] p-[15px] bg-white rounded-md flex flex-col gap-[20px] w-full">
            <h5 className="font-bold text-[#74767d]">Account information</h5>

            <div className="flex flex-col gap-1">
              <label htmlFor="" className="text-[0.9rem] font-medium">
                Email
              </label>
              <input
                type="email"
                name="email"
                value={data.email}
                onChange={handleChange}
                required
                className="lowercase border border-gray-300 p-[6px_10px] text-[0.9rem] w-full outline-none focus:border-gray-400  "
              />
            </div>

            <div className="flex flex-col gap-1">
              <label htmlFor="" className="text-[0.9rem] font-medium">
                Role
              </label>
              <select
                name="role"
                value={data.role}
                onChange={handleChange}
                required
                className="border border-gray-300 p-[6px_10px] text-[0.9rem] w-full outline-none focus:border-gray-400  "
              >
                <option value="">Select role</option>
                <option value="0">System admin</option>
              </select>
            </div>

            <div className="flex flex-col gap-1">
              <label htmlFor="" className="text-[0.9rem] font-medium">
                Password
              </label>
              <input
                type="password"
                name="password"
                value={data.password}
                onChange={handleChange}
                required
                className="border border-gray-300 p-[6px_10px] text-[0.9rem] w-full outline-none focus:border-gray-400  "
              />
            </div>
          </div>

          <div className="md:p-[25px] p-[15px] bg-white rounded-md flex flex-col gap-[20px] w-full">
            <h5 className="font-bold text-[#74767d]">Contact information</h5>

            <div className="flex flex-wrap md:flex-nowrap gap-[15px]">
              <div className="flex flex-col gap-1 w-full">
                <label htmlFor="" className="text-[0.9rem] font-medium">
                  Fullname
                </label>
                <input
                  type="text"
                  name="fullname"
                  value={data.fullname}
                  onChange={handleChange}
                  required
                  className="border border-gray-300 p-[6px_10px] text-[0.9rem] w-full outline-none focus:border-gray-400  "
                />
              </div>
              <div className="flex flex-col gap-1 w-full">
                <label htmlFor="" className="text-[0.9rem] font-medium">
                  Phone
                </label>
                <input
                  type="number"
                  name="phone"
                  inputMode="numeric"
                  value={data.phone}
                  onChange={handleChange}
                  required
                  className="border border-gray-300 p-[6px_10px] text-[0.9rem] w-full outline-none focus:border-gray-400  "
                />
              </div>
            </div>

            <div className="flex flex-col gap-1">
              <label htmlFor="" className="text-[0.9rem] font-medium">
                Specific address
              </label>
              <input
                type="text"
                name="speaddress"
                value={data.speaddress}
                onChange={handleChange}
                required
                className="border border-gray-300 p-[6px_10px] text-[0.9rem] w-full outline-none focus:border-gray-400  "
              />
            </div>

            <div className="flex flex-wrap md:flex-nowrap gap-[15px]">
              <div className="flex flex-col gap-1 w-full">
                <label htmlFor="" className="text-[0.9rem] font-medium">
                  Province/city
                </label>
                <select
                  name="city"
                  value={data.city}
                  onChange={(e) =>
                    setData((prev) => ({
                      ...prev,
                      city: e.target.value,
                      ward: "",
                    }))
                  }
                  required
                  className="border border-gray-300 p-[6px_10px] text-[0.9rem] w-full outline-none focus:border-gray-400  "
                >
                  <option value="">Select province/city</option>
                  {provinces?.map((province) => (
                    <option key={province.id} value={province.province}>
                      {province.province}
                    </option>
                  ))}
                </select>
              </div>

              <div className="flex flex-col gap-1 w-full">
                <label htmlFor="" className="text-[0.9rem] font-medium">
                  Ward/Commune
                </label>
                <select
                  name="ward"
                  value={data.ward}
                  onChange={handleChange}
                  required
                  className="border border-gray-300 p-[6px_10px] text-[0.9rem] w-full outline-none focus:border-gray-400  "
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
          </div>
        </div>

        <div className="flex justify-center gap-6">
          <button
            type="submit"
            className="p-[6px_10px] bg-teal-500 text-white text-[0.9rem] font-medium text-center hover:bg-teal-600 rounded-sm"
          >
            {isLoading ? "Adding..." : "Add"}
          </button>
          <Link
            to="/admin/admins"
            className="p-[6px_10px] bg-red-500 text-white text-[0.9rem] text-center hover:bg-red-600 rounded-sm"
          >
            Back
          </Link>
        </div>
      </form>
    </div>
  );
}

export default AddAdmin;
