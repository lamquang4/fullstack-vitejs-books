import toast from "react-hot-toast";
import Loading from "../../Loading";
import Image from "../../Image";
import useDeleteAddress from "../../../hooks/client/useDeleteAddress";
import type { Address } from "../../../types/type";

type Props = {
  toggleAddressModal: () => void;
  setAddressId: (value: string) => void;
  addresses: Address[];
  isLoading: boolean;
  mutate: () => void;
  userId: string;
};

function AddressInfo({
  toggleAddressModal,
  setAddressId,
  addresses,
  isLoading,
  mutate,
}: Props) {
  const { deleteAddress, isLoading: isLoadingDeleteAddress } =
    useDeleteAddress();

  const handleDelete = async (id: string) => {
    if (!id) {
      return;
    }

    if (addresses.length === 1) {
      toast.error("You need to keep at least one address for your account");
      return;
    }

    try {
      await deleteAddress(id);
      mutate();
    } catch (err: any) {
      toast.error(err?.response?.data?.message);
      mutate();
    }
  };
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
            <Loading height={60} size={50} color="black" thickness={3} />
          ) : addresses.length > 0 ? (
            addresses.map((address) => (
              <div
                key={address.id}
                className="border-t border-gray-300 py-[20px]"
              >
                <div className="flex justify-between flex-wrap gap-y-[8px]">
                  <div className="flex flex-col gap-[8px]">
                    <p>
                      Fullname:{" "}
                      <span className="font-medium">{address.fullname}</span>
                    </p>

                    <p>
                      Phone:{" "}
                      <span className="font-medium">{address.phone}</span>
                    </p>

                    <p>
                      Address:{" "}
                      <span className="font-medium">
                        {address.speaddress}, {address.city}, {address.ward}
                      </span>
                    </p>
                  </div>

                  <div className="flex gap-[25px] items-center">
                    <button
                      className="border-0 p-1 outline-0 text-[0.9rem] text-blue-500 font-medium"
                      type="button"
                      onClick={() => {
                        toggleAddressModal();
                        setAddressId(address.id || "");
                      }}
                    >
                      Edit
                    </button>
                    <button
                      className="border-0 p-1 outline-0 text-[0.9rem] text-red-500 font-medium"
                      type="button"
                      disabled={isLoadingDeleteAddress}
                      onClick={() => handleDelete(address.id || "")}
                    >
                      Delete
                    </button>
                  </div>
                </div>
              </div>
            ))
          ) : (
            <div className="flex justify-center items-center h-[60vh]">
              <div className="flex flex-col justify-center items-center gap-[15px]">
                <Image
                  source={"/assets/address.png"}
                  alt={""}
                  className={"w-[80px]"}
                  loading="eager"
                />

                <h4>No addresses found</h4>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}

export default AddressInfo;
