import Loading from "../../Loading";
import CartItem from "./CartItem";
import useGetCart from "../../../hooks/client/useGetCart";
import useCurrentUser from "../../../hooks/useGetCurrentUser";

function Cart() {
  const { user } = useCurrentUser("client");
  const { cart, isLoading, mutate } = useGetCart(user?.id || "");
  return (
    <>
      {isLoading ? (
        <Loading height={60} size={50} color="black" thickness={2} />
      ) : (
        <>
          <CartItem cart={cart!} mutate={mutate} />
        </>
      )}
    </>
  );
}

export default Cart;
