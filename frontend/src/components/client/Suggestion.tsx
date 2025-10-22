import Loading from "../Loading";
import Image from "../Image";
import { memo, useEffect } from "react";
import { Link } from "react-router-dom";
import useGetSuggestion from "../../hooks/client/useGetSuggestion";
type Props = {
  search: string;
};
function Suggestion({ search }: Props) {
  const { books, isLoading, setKeyword } = useGetSuggestion();
  useEffect(() => {
    if (search) {
      setKeyword(search.trim());
    }
  }, [search, setKeyword]);
  return (
    <>
      <div className="p-2.5">
        <p className="font-medium text-balance">
          Search results for <span className="text-[#C62028]">{search}</span>
        </p>
      </div>

      <div className="overflow-y-auto max-h-96 flex flex-col">
        {isLoading ? (
          <Loading height={25} size={35} color={"#C62028"} thickness={3} />
        ) : books.length > 0 ? (
          books.map((book) => (
            <div className="flex w-full" key={book.id}>
              <Link to={`/book/${book.slug}`} className="w-full">
                <div className="hover:bg-[#F7F7F7] p-2.5 w-full flex gap-3.5 border-t border-gray-200">
                  <div className="w-[80px] h-[80px] overflow-hidden">
                    <Image
                      source={`${import.meta.env.VITE_BACKEND_URL}${
                        book.images[0].image
                      }`}
                      alt={book.title}
                      className="w-full h-full object-contain z-1 relative"
                      loading="lazy"
                    />

                    {book.images[1].image && (
                      <Image
                        source={`${import.meta.env.VITE_BACKEND_URL}${
                          book.images[1].image
                        }`}
                        alt={book.title}
                        className={
                          "w-full h-full object-contain absolute top-0 left-0 opacity-0 z-2 transition-opacity duration-300 group-hover:opacity-100"
                        }
                        loading="lazy"
                      />
                    )}
                  </div>

                  <div className="flex flex-col gap-1.5">
                    <p className="font-medium">{book.title}</p>
                    {book.discount > 0 ? (
                      <div className="flex items-center gap-[12px]">
                        <del className="text-[#707072] text-[0.9rem]">
                          {book.price.toLocaleString("vi-VN")}₫
                        </del>

                        <p className="font-medium text-[#C62028]">
                          {(book.price - book.discount).toLocaleString("vi-VN")}
                          ₫
                        </p>
                      </div>
                    ) : (
                      <p className="font-medium text-[#C62028]">
                        {book.price.toLocaleString("vi-VN")}₫
                      </p>
                    )}
                  </div>
                </div>
              </Link>
            </div>
          ))
        ) : (
          <p className="p-4 text-center text-[0.9rem] text-gray-500">
            No results found
          </p>
        )}
      </div>
    </>
  );
}

export default memo(Suggestion);
