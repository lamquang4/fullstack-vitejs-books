import Loading from "../Loading";
import Image from "../Image";
import { memo, useEffect } from "react";
import { Link } from "react-router-dom";
type Props = {
  search: string;
};
function SuggestionProduct({ search }: Props) {
  const products = [];
  useEffect(() => {
    if (search) {
      setKeyword(search.trim());
    }
  }, [search, setKeyword]);
  return (
    <>
      <div className="p-2.5">
        <p className="font-medium text-balance">
          Kết quả tìm kiếm cho <span className="text-[#C62028]">{search}</span>
        </p>
      </div>

      <div className="overflow-y-auto max-h-96 flex flex-col">
        {isLoading ? (
          <Loading height={25} size={35} color={"#C62028"} thickness={3} />
        ) : products.length > 0 ? (
          products.map((product) => (
            <div className="flex w-full" key={product._id}>
              <Link to={`/product/${product.slug}`} className="w-full">
                <div className="hover:bg-[#F7F7F7] p-2.5 w-full flex gap-3.5 border-t border-gray-200">
                  <div>
                    <Image
                      source={product.variants[0].images[0]}
                      alt=""
                      className="w-[80px]"
                      loading="eager"
                    />
                  </div>

                  <div className="flex flex-col gap-1.5">
                    <p className="font-medium">{product.name}</p>
                    {product.discount > 0 ? (
                      <div className="flex items-center gap-[12px]">
                        <del className="text-[#707072] text-[0.9rem]">
                          {product.price.toLocaleString("vi-VN")}₫
                        </del>

                        <p className="font-medium text-[#C62028]">
                          {(product.price - product.discount).toLocaleString(
                            "vi-VN"
                          )}
                          ₫
                        </p>
                      </div>
                    ) : (
                      <p className="font-medium">
                        {product.price.toLocaleString("vi-VN")}₫
                      </p>
                    )}
                  </div>
                </div>
              </Link>
            </div>
          ))
        ) : (
          <p className="p-4 text-center text-[0.9rem] text-gray-500">
            Không tìm thấy kết quả
          </p>
        )}
      </div>
    </>
  );
}

export default memo(SuggestionProduct);
