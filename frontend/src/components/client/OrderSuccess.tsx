import Image from "../Image";
function OrderSuccess() {
  return (
    <section className="my-[40px] px-[15px]">
      <div className="mx-auto max-w-[1200px] w-full">
        <div className="flex justify-center items-center h-[40vh]">
          <div className="flex flex-col justify-center items-center gap-[15px]">
            <Image
              source={"/assets/packing.png"}
              alt={""}
              className={"w-[100px]"}
              loading="eager"
            />
            <h4 className="uppercase text-center">Order Placed Successfully</h4>
            <p className="font-medium">Thank you for shopping with us.</p>
            <p className="font-medium">
              We have received your order details and will contact you as soon
              as possible.
            </p>
          </div>
        </div>
      </div>
    </section>
  );
}

export default OrderSuccess;
