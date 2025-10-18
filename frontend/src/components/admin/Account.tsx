function Account() {
  const user = 0;
  return (
    <div className="py-[30px] sm:px-[25px] px-[15px] bg-[#F1F4F9] h-full">
      <form className="flex flex-col gap-7 w-full">
        <h2 className="text-[#74767d]">My account</h2>

        <div className="gap-[25px] w-full flex flex-wrap lg:flex-nowrap">
          <div className="sm:p-[25px] p-[15px] bg-white rounded-md flex flex-col gap-[20px] w-full">
            <h5 className="font-bold text-[#74767d]">Personal information</h5>

            <div className="flex flex-col gap-1 w-full ">
              <label htmlFor="" className="text-[0.9rem]  font-medium">
                Fullname
              </label>
              <input
                type="text"
                name="fullname"
                value={"Quang lam"}
                readOnly
                className="border border-gray-300 p-[6px_10px] text-[0.9rem] w-full outline-none focus:border-gray-400  "
              />
            </div>

            <div className="flex flex-col gap-1 w-full ">
              <label htmlFor="" className="text-[0.9rem]  font-medium">
                Email
              </label>
              <input
                type="text"
                name="email"
                value={"quang@gmail.com"}
                readOnly
                className="border border-gray-300 p-[6px_10px] text-[0.9rem] w-full outline-none focus:border-gray-400  "
              />
            </div>

            <div className="flex flex-wrap md:flex-nowrap gap-[15px]">
              <div className="flex flex-col gap-1 w-full">
                <label htmlFor="" className="text-[0.9rem]  font-medium">
                  Phone
                </label>
                <input
                  type="text"
                  name="phone"
                  inputMode="numeric"
                  value={"057975757575"}
                  readOnly
                  className="border border-gray-300 p-[6px_10px] text-[0.9rem] w-full outline-none focus:border-gray-400  "
                />
              </div>

              <div className="flex flex-col gap-1 w-full">
                <label htmlFor="" className="text-[0.9rem]  font-medium">
                  Birthday
                </label>
                <input
                  type="date"
                  name="birthday"
                  value={"02-05-2004"}
                  readOnly
                  className="border border-gray-300 p-[6px_10px] text-[0.9rem] w-full outline-none focus:border-gray-400  "
                />
              </div>
            </div>

            <div className="flex flex-col gap-1 w-full ">
              <label htmlFor="" className="text-[0.9rem]  font-medium">
                Role
              </label>
              <input
                type="text"
                name="role"
                value={user === 0 ? "System admin" : ""}
                readOnly
                className="border border-gray-300 p-[6px_10px] text-[0.9rem] w-full outline-none focus:border-gray-400  "
              />
            </div>
          </div>
        </div>
      </form>
    </div>
  );
}

export default Account;
