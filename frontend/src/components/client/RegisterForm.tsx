import { useState } from "react";
import { HiOutlineEye, HiOutlineEyeOff } from "react-icons/hi";
import { Link, useNavigate } from "react-router-dom";

function RegisterForm() {
  const navigate = useNavigate();

  const [data, setData] = useState({
    fullname: "",
    email: "",
    password: "",
  });
  const [showPassword, setShowPassword] = useState<boolean>(false);

  const toggleShowPassword = () => {
    setShowPassword((prev) => !prev);
  };

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
  };

  return (
    <>
      <section className="my-[60px] px-[15px]">
        <div className="mx-auto w-full max-w-[1350px]">
          <div className="flex items-center justify-center">
            <div className="max-w-sm w-full">
              <h2 className="uppercase mb-[20px] text-center text-[#C62028]">
                Register
              </h2>
              <form className="space-y-[15px]" onSubmit={handleSubmit}>
                <div className="space-y-[5px]">
                  <label
                    htmlFor=""
                    className="block   text-[0.9rem] font-medium"
                  >
                    Fullname
                  </label>
                  <input
                    type="text"
                    name="fullname"
                    value={data.fullname}
                    onChange={handleChange}
                    className="text-[0.9rem] block w-full px-3 py-2 border border-gray-200"
                    placeholder="Enter fullname"
                    required
                  />
                </div>

                <div className="space-y-[5px]">
                  <label
                    htmlFor=""
                    className="block   text-[0.9rem] font-medium"
                  >
                    Email
                  </label>
                  <input
                    type="email"
                    name="email"
                    value={data.email}
                    onChange={handleChange}
                    className="text-[0.9rem] block w-full px-3 py-2 border border-gray-200"
                    placeholder="Enter email"
                    required
                  />
                </div>

                <div className="space-y-[5px]">
                  <label htmlFor="" className="block text-[0.9rem] font-medium">
                    Password
                  </label>

                  <div className="relative">
                    <input
                      type={!showPassword ? "password" : "text"}
                      name="password"
                      value={data.password}
                      onChange={handleChange}
                      placeholder="Enter password"
                      className="text-[0.9rem] block w-full  px-3 pr-12 py-2 border border-gray-200"
                      required
                    />

                    <button
                      type="button"
                      className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-500"
                      onClick={toggleShowPassword}
                    >
                      {!showPassword ? (
                        <HiOutlineEye size={22} />
                      ) : (
                        <HiOutlineEyeOff size={22} />
                      )}
                    </button>
                  </div>
                </div>

                <button
                  type="submit"
                  className="w-full bg-[#C62028] text-white focus:outline-none font-semibold rounded-sm text-[0.9rem] px-5 py-2.5 text-center"
                >
                  Register
                </button>

                <p className="flex gap-1.5 justify-center font-medium">
                  Do you already have an account?
                  <Link to="/login" className="text-blue-400 font-medium">
                    Login
                  </Link>
                </p>
              </form>
            </div>
          </div>
        </div>
      </section>

      {/*
        <Overplay IndexForZ={50}>
          <Loading height={0} size={55} color="white" thickness={8} />
          <h4 className="text-white">Vui lòng chờ trong giây lát...</h4>
        </Overplay>
      */}
    </>
  );
}

export default RegisterForm;
