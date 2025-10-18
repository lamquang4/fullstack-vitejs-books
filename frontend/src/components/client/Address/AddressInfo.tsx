import toast from "react-hot-toast";
import Loading from "../../Loading";
import Image from "../../Image";

type Props = {
  toggleAddressModal: () => void;
  setAddressId: (value: string) => void;
};

function AddressInfo({ toggleAddressModal, setAddressId }: Props) {
  const addresses = [
    {
      _id: "1",
      fullname: "Nguyen Van A",
      phone: "0987654321",
      speaddress: "123 Nguyen Trai Street",
      ward: "Ward 5",
      city: "Ho Chi Minh City",
    },
    {
      _id: "2",
      fullname: "Tran Thi B",
      phone: "0912345678",
      speaddress: "45 Le Loi Street",
      ward: "Ben Thanh Ward",
      city: "Ho Chi Minh City",
    },
    {
      _id: "3",
      fullname: "Le Van C",
      phone: "0909123456",
      speaddress: "12 Nguyen Hue Boulevard",
      ward: "District 1",
      city: "Ho Chi Minh City",
    },
  ];

  const isLoading = false;
  return (
    <div className="w-full max-w-full flex-1 px-[15px]">
      <div className="space-y-[20px]">
        <h2>Address book</h2>

        <button
          onClick={toggleAddressModal}
          type="button"
          className="px-[10px] py-[6px] bg-red-600 text-white text-[0.9rem] font-medium text-center rounded-sm hover:bg-red-700"
        >
          Add address
        </button>

        <div>
          {isLoading ? (
            <Loading height={70} size={50} color="black" thickness={3} />
          ) : addresses.length > 0 ? (
            addresses.map((address) => (
              <div
                key={address._id}
                className="border-t border-gray-300 py-[20px]"
              >
                <div className="flex justify-between flex-wrap gap-y-[8px]">
                  <div className="flex flex-col gap-[8px]">
                    <p className="font-medium">
                      Fullname:{" "}
                      <span className="font-normal">{address.fullname}</span>
                    </p>

                    <p className="font-medium">
                      Phone:{" "}
                      <span className="font-normal">{address.phone}</span>
                    </p>

                    <p className="font-medium">
                      Address:{" "}
                      <span className="font-normal">
                        {address.speaddress}, {address.city}, {address.ward}
                      </span>
                    </p>
                  </div>

                  <div className="flex gap-[25px] items-center">
                    <button
                      className="border-0 p-1 outline-0 text-[0.9rem] text-blue-500 font-medium"
                      type="button"
                    >
                      Edit
                    </button>
                    <button
                      className="border-0 p-1 outline-0 text-[0.9rem] text-red-500 font-medium"
                      type="button"
                    >
                      Delete
                    </button>
                  </div>
                </div>
              </div>
            ))
          ) : (
            <div className="flex justify-center items-center h-[70vh]">
              <div className="flex flex-col justify-center items-center gap-[15px]">
                <Image
                  source={"/assets/address.png"}
                  alt={""}
                  className={"w-[80px]"}
                  loading="eager"
                />

                <h4 className="text-gray-600">No addresses found</h4>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}

export default AddressInfo;
