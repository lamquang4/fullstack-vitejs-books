import { useParams } from "react-router-dom";
import Pagination from "../Pagination";
import BookList from "./BookList";
import useGetActiveDiscountBooks from "../../../hooks/client/useGetActiveDiscountBooks";

function BookSale() {
  const { slug } = useParams();
  const { books, isLoading, totalItems, totalPages, currentPage } =
    useGetActiveDiscountBooks();

  return (
    <section className="my-[40px] px-[15px]">
      <div className="mx-auto max-w-[1350px] w-full">
        <BookList
          books={books}
          category={slug}
          isLoading={isLoading}
          total={totalItems}
        />

        <Pagination
          totalPages={totalPages}
          currentPage={currentPage}
          totalItems={totalItems}
        />
      </div>
    </section>
  );
}

export default BookSale;
