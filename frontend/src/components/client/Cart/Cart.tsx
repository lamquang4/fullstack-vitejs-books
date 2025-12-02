import Loading from "../../Loading";
import CartItem from "./CartItem";
import useGetCart from "../../../hooks/client/useGetCart";
import useGetCurrentUser from "../../../hooks/useGetCurrentUser";
import useGetActiveBooks from "../../../hooks/client/useGetActiveBooks";
import BookSlider from "../Book/BookSlider";

function Cart() {
  const { user, isLoading: isLoadingCurrentUser } = useGetCurrentUser("client");
  const { cart, isLoading: isLoadingCart, mutate } = useGetCart(user?.id || "");
  const { books } = useGetActiveBooks();
  return (
    <>
      {isLoadingCart || isLoadingCurrentUser ? (
        <Loading height={60} size={50} color="black" thickness={2} />
      ) : (
        <>
          <CartItem cart={cart!} mutate={mutate} />
          <BookSlider books={books} title="Bạn có thể thích" />
        </>
      )}
    </>
  );
}

export default Cart;
