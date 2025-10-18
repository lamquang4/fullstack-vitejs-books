import { useState } from "react";
import type { Product } from "../../../../types/type";
import ImageViewer from "../../../ImageViewer";
import Image from "../../../Image";
import toast from "react-hot-toast";
import { GrNext, GrPrevious } from "react-icons/gr";
import { HiOutlineMinusSmall } from "react-icons/hi2";
import { HiOutlinePlusSmall } from "react-icons/hi2";

type Props = {
  product: Product;
};

function ProductDetail({ product }: Props) {
  const [quantity, setQuantity] = useState<number>(1);
  const [mainImage, setMainImage] = useState<string>("");
  const [openViewer, setOpenViewer] = useState<boolean>(false);
  const [viewerImage, setViewerImage] = useState<string>("");
  const [currentImageIndex, setCurrentImageIndex] = useState<number>(0);

  const handleNextImage = () => {
    if (!product.images.length) return;
    const nextIndex = (currentImageIndex + 1) % product.images.length;
    setCurrentImageIndex(nextIndex);
    setMainImage(product.images[nextIndex]);
  };

  const handlePrevImage = () => {
    if (!product.images.length) return;
    const prevIndex =
      currentImageIndex - 1 < 0
        ? product.images.length - 1
        : currentImageIndex - 1;
    setCurrentImageIndex(prevIndex);
    setMainImage(product.images[prevIndex]);
  };

  const handleOpenViewer = (image: string) => {
    setViewerImage(image);
    setOpenViewer(true);
  };

  const HandleIncrement = () => {
    const maxQuantity = product?.stock! > 15 ? 15 : product?.stock!;
    setQuantity((prev) => (prev < maxQuantity ? prev + 1 : prev));
  };

  const HandleDecrement = () => {
    setQuantity((prev) => (prev > 1 ? prev - 1 : prev));
  };
  return (
    <section className="w-full mb-[40px]">
      <div className="mx-auto w-full max-w-[1350px]">
        <div className="flex flex-wrap gap-x-[15px] gap-y-[30px]">
          <div className="flex justify-center">
            <div className="flex flex-col md:flex-col-reverse xl:flex-row flex-wrap gap-[15px] lg:sticky lg:top-[100px]">
              <div className="md:order-2 relative grow overflow-hidden bg-white">
                <div className="w-full lg:max-w-[600px] flex flex-col gap-[20px]">
                  <div className="relative group">
                    <button
                      type="button"
                      onClick={handleNextImage}
                      className="absolute border border-gray-100 right-1.5 top-1/2 w-11 h-11 bg-white rounded-full flex justify-center items-center -translate-y-1/2 z-10 p-2 xl:opacity-0 xl:group-hover:opacity-100 transition duration-300 hover:bg-black hover:text-white"
                    >
                      <GrNext size={18} />
                    </button>

                    {mainImage && (
                      <div
                        className="cursor-pointer"
                        onClick={(e) => {
                          e.stopPropagation();
                          e.preventDefault();
                          handleOpenViewer(mainImage);
                        }}
                      >
                        <Image
                          source={mainImage}
                          alt=""
                          className="w-full h-full object-cover "
                          loading="eager"
                        />
                      </div>
                    )}

                    <button
                      type="button"
                      className="absolute left-1.5 top-1/2 w-11 h-11 border border-gray-100 bg-white rounded-full flex justify-center items-center -translate-y-1/2 z-10 p-2 xl:opacity-0 xl:group-hover:opacity-100 transition duration-300 hover:bg-black hover:text-white"
                    >
                      <GrPrevious size={18} />
                    </button>
                  </div>
                </div>
              </div>

              <div className="md:order-1 flex justify-center">
                <div className="max-h-[500px] max-w-[400px] flex flex-row xl:flex-col gap-[15px] overflow-x-auto overflow-y-auto">
                  {product?.images.map((image, index) => (
                    <div
                      key={`${index}`}
                      className={`shrink-0 border  overflow-hidden cursor-pointer w-[70px] ${
                        mainImage === image
                          ? "border-gray-500"
                          : "border-gray-300"
                      }`}
                      onMouseEnter={() => {
                        setMainImage(image);
                        const indexOfImage = product.images.indexOf(image);
                        if (indexOfImage !== -1) {
                          setCurrentImageIndex(indexOfImage);
                        }
                      }}
                    >
                      <Image
                        source={image}
                        alt=""
                        className="w-full h-full object-cover"
                        loading="eager"
                      />
                    </div>
                  ))}
                </div>
              </div>
            </div>
          </div>

          <div className="relative lg:w-[500px] w-full  px-[15px]">
            <div className="space-y-[5px]">
              <h3 className="font-semibold">{product?.name}</h3>
              <div className="flex items-center gap-[15px]">
                {product && product?.discount > 0 ? (
                  <>
                    <del className="text-[#707072] font-light text-[1.4rem]">
                      {product?.price.toLocaleString("vi-VN")}₫
                    </del>

                    <h3 className="text-[#c00] font-medium">
                      {(product?.price - product?.discount).toLocaleString(
                        "vi-VN"
                      )}
                      ₫
                    </h3>
                  </>
                ) : (
                  <h3 className="font-medium">
                    {product?.price.toLocaleString("vi-VN")}₫
                  </h3>
                )}
              </div>
            </div>

            <div className="w-full flex gap-[20px] flex-wrap md:flex-nowrap items-center">
              <div className="relative flex justify-between items-center max-w-[8rem] border border-gray-300 rounded-sm">
                <button
                  type="button"
                  onClick={HandleDecrement}
                  disabled={quantity <= 1}
                  className=" p-3 h-11 outline-none"
                >
                  <HiOutlineMinusSmall size={22} />
                </button>
                <input
                  type="number"
                  name="quantity"
                  readOnly
                  className="h-11 text-center   w-11 outline-none placeholder:  text-[1rem] font-normal"
                  placeholder="1"
                  min={1}
                  max={product?.stock! > 15 ? 15 : product?.stock!}
                  value={quantity}
                />
                <button
                  type="button"
                  onClick={HandleIncrement}
                  disabled={
                    quantity >= (product?.stock! > 15 ? 15 : product?.stock!)
                  }
                  className=" p-3 h-11 outline-none"
                >
                  <HiOutlinePlusSmall size={22} />
                </button>
              </div>

              <button
                type="submit"
                className="px-[10px] py-[10px] w-full uppercase text-[0.9rem] font-medium border bg-black text-white hover:bg-[#050708]/80"
              >
                Add to cart
              </button>
            </div>

            <div>
              <h4 className="font-medium">Description</h4>

              <hr className="border my-[15px]" />

              <div
                className="main-prose"
                dangerouslySetInnerHTML={{
                  __html: product?.description || "",
                }}
              ></div>
            </div>
          </div>

          {openViewer && (
            <ImageViewer
              image={viewerImage}
              open={openViewer}
              onClose={() => setOpenViewer(false)}
            />
          )}
        </div>
      </div>
    </section>
  );
}

export default ProductDetail;
