import useGetCurrentUser from "../../../hooks/useGetCurrentUser";

function AccountInfo() {
  const { user } = useGetCurrentUser("client");

  return (
    <div className="w-full flex-1 px-[15px] bg-white">
      <div className="space-y-[20px]">
        <h2>Thông tin tài khoản</h2>

        <div className="flex flex-col gap-[15px]">
          <div className="space-y-[5px]">
            <label
              htmlFor=""
              className="block text-left text-[0.9rem] font-medium"
            >
              Họ tên:
            </label>
            <input
              type="text"
              name="fullname"
              className="w-full  rounded-sm p-[6px_10px] text-[0.9rem] border border-gray-300  focus:outline-0"
              value={user?.fullname || ""}
              readOnly
            />
          </div>

          <div className="space-y-[5px]">
            <label
              htmlFor=""
              className="block text-left text-[0.9rem] font-medium"
            >
              Email:
            </label>
            <input
              type="text"
              name="email"
              className="w-full  rounded-sm p-[6px_10px] text-[0.9rem] border border-gray-300  focus:outline-0"
              value={user?.email || ""}
              readOnly
            />
          </div>
        </div>
      </div>
    </div>
  );
}

export default AccountInfo;
