import { useState } from "react";
import SideBarMenu from "../SideMenuBar";
import AddressModal from "./AddressModal";
import AddressInfo from "./AddressInfo";
import BreadCrumb from "../BreadCrumb";
import useGetCurrentUser from "../../../hooks/useGetCurrentUser";
import useGetAddresses from "../../../hooks/client/useGetAddresses";

function Address() {
  const { user } = useGetCurrentUser("client");
  const { addresses, mutate, isLoading } = useGetAddresses(user?.id || "");
  const [addressId, setAddressId] = useState<string>("");
  const [openAddressModal, setOpenAddressModal] = useState<boolean>(false);

  const toggleAddressModal = () => {
    setOpenAddressModal((prev) => !prev);
    setAddressId("");
  };

  const array = [
    {
      name: "Trang chủ",
      href: "/",
    },
    {
      name: "Sổ địa chỉ",
    },
  ];

  return (
    <>
      <BreadCrumb items={array} />

      <section className="mb-[40px]">
        <div className="w-full max-w-[1200px] mx-auto">
          <div className="flex justify-center flex-wrap gap-5">
            <SideBarMenu />

            <AddressInfo
              onToggleModal={toggleAddressModal}
              addresses={addresses}
              mutate={mutate}
              isLoading={isLoading}
              setAddressId={setAddressId}
              userId={user?.id || ""}
            />
          </div>
        </div>

        {openAddressModal && (
          <AddressModal
            addressId={addressId}
            mutateAddresses={mutate}
            addressesLength={addresses.length}
            userId={user?.id || ""}
            onToggleModal={toggleAddressModal}
            isOpen={openAddressModal}
          />
        )}
      </section>
    </>
  );
}

export default Address;
