import { useState } from "react";
import SideBarMenu from "../SideMenuBar";
import AddressModal from "./AddressModal";
import AddressInfo from "./AddressInfo";
import BreadCrumb from "../BreadCrumb";
import useCurrentUser from "../../../hooks/useGetCurrentUser";

function Address() {
  const { user } = useCurrentUser("client");
  const [addressId, setAddressId] = useState<string>("");
  const [openAddressModal, setOpenAddressModal] = useState<boolean>(false);

  const toggleAddressModal = () => {
    setOpenAddressModal((prev) => !prev);
    setAddressId("");
  };

  const array = [
    {
      name: "Home",
      href: "/",
    },
    {
      name: "Address book",
    },
  ];

  return (
    <>
      <BreadCrumb items={array} />

      <section className="mb-[40px]">
        <div className="w-full max-w-[1350px] mx-auto">
          <div className="flex justify-center flex-wrap gap-5">
            <SideBarMenu />

            <AddressInfo
              toggleAddressModal={toggleAddressModal}
              setAddressId={setAddressId}
              userId={user?.id || ""}
            />
          </div>
        </div>

        {openAddressModal && (
          <>
            <AddressModal
              addressId={addressId}
              userId={user?.id || ""}
              toggleMenu={toggleAddressModal}
              isOpen={openAddressModal}
            />
          </>
        )}
      </section>
    </>
  );
}

export default Address;
