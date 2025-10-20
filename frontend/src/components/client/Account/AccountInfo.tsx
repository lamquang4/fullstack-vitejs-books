function AccountInfo() {
  const user = {
    fullname: "Nguyen Van A",
    email: "nguyenvana@example.com",
    phone: "0987654321",
    birthday: "1998-05-20",
  };
  return (
    <div className="w-full flex-1 px-[15px] bg-white">
      <div className="space-y-[20px]">
        <h2>My account</h2>

        <div className="flex flex-col gap-[15px]">
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
              value={user.email || ""}
              readOnly
            />
          </div>
        </div>
      </div>
    </div>
  );
}

export default AccountInfo;
