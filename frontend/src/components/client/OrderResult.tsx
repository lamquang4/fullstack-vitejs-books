import { useLocation, useNavigate } from "react-router-dom";
import Image from "../Image";
import { useEffect } from "react";
function OrderResult() {
  const location = useLocation();
  const navigate = useNavigate();
  const searchParams = new URLSearchParams(location.search);

  const result = searchParams.get("result");

  useEffect(() => {
    if (!result) {
      navigate("/", { replace: true });
    }
  }, [result, navigate]);

  if (!result) return null;
  return (
    <section className="my-[40px] px-[15px]">
      <div className="mx-auto max-w-[1200px] w-full">
        <div className="flex justify-center items-center h-[40vh]">
          {result === "successfully" ? (
            <div className="flex flex-col justify-center items-center gap-[15px]">
              <Image
                source={"/assets/packing.png"}
                alt={""}
                className={"w-[100px]"}
                loading="eager"
              />
              <h4 className="uppercase text-center">
                Order Placed Successfully
              </h4>
              <p className="font-medium">Thank you for shopping with us.</p>
              <p className="font-medium">
                We have received your order details and will contact you as soon
                as possible.
              </p>
            </div>
          ) : result === "fail" ? (
            <div className="flex flex-col justify-center items-center gap-[15px]">
              <Image
                source={"/assets/order-fail.png"}
                alt={""}
                className={"w-[120px]"}
                loading="eager"
              />
              <h4 className="uppercase text-center">Order Failed</h4>
              <p className="font-medium">
                Unfortunately, we could not complete your order.
              </p>
              <p className="font-medium">
                ne or more items in your order are out of stock. Your payment
                has been refunded.
              </p>
            </div>
          ) : (
            ""
          )}
        </div>
      </div>
    </section>
  );
}

export default OrderResult;
