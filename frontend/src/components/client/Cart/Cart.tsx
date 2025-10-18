import Loading from "../../Loading";
import CartItem from "./CartItem";
import ProductSlider from "../Product/ProductSlider";

function Cart() {
  const isLoading = false;
  return (
    <>
      {isLoading ? (
        <Loading height={70} size={50} color="black" thickness={2} />
      ) : (
        <>
          <CartItem cart={cart!} />

          <ProductSlider products={[]} title={"Best seller"} />
        </>
      )}
    </>
  );
}

export default Cart;
