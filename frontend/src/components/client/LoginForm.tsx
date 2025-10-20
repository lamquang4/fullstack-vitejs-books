import React, { useState } from "react";
import Overplay from "./Overplay";
import Loading from "../Loading";
import { Link } from "react-router-dom";
import { HiOutlineEyeOff, HiOutlineEye } from "react-icons/hi";
import useLogin from "../../hooks/useLogin";
import toast from "react-hot-toast";
function LoginForm() {
  const { handleLogin, isLoading } = useLogin();

  const [data, setData] = useState({ email: "", password: "" });
  const [showPassword, setShowPassword] = useState<boolean>(false);

  const toggleShowPassword = () => {
    setShowPassword((prev) => !prev);
  };

  const handleChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>
  ) => {
    const { name, value } = e.target;
    setData((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    try {
      await handleLogin(data.email, data.password);

      setData({
        email: "",
        password: "",
      });
    } catch (err: any) {
      toast.error(err?.response?.data?.message);
    }
  };
  return (
    <>
      <section className="my-[60px] px-[15px]">
        <div className="mx-auto w-full max-w-[1350px]">
          <div className="flex items-center justify-center">
            <div className="max-w-sm w-full">
              <h2 className="uppercase mb-[20px] text-center text-[#C62028]">
                Login
              </h2>
              <form className="space-y-[15px]" onSubmit={handleSubmit}>
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
                  Login
                </button>

                <p className="flex text-black gap-1.5 justify-center font-medium">
                  Don't have an account yet?
                  <Link to="/register" className="text-blue-400 font-medium">
                    Register
                  </Link>
                </p>
              </form>
            </div>
          </div>
        </div>
      </section>

      {isLoading && (
        <Overplay IndexForZ={50}>
          <Loading height={0} size={55} color="white" thickness={8} />
          <h4 className="text-white">Please wait a moment...</h4>
        </Overplay>
      )}
    </>
  );
}

export default LoginForm;
