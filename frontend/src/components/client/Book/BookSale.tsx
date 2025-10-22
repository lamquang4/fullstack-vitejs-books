import Pagination from "../Pagination";
import BookList from "./BookList";
import useGetActiveDiscountBooks from "../../../hooks/client/useGetActiveDiscountBooks";
import BreadCrumb from "../BreadCrumb";

function BookSale() {
  const { books, isLoading, totalItems, totalPages, currentPage } =
    useGetActiveDiscountBooks();

  const array = [
    {
      name: "Home",
      href: "/",
    },
    {
      name: "Sale",
    },
  ];
  return (
    <>
      <BreadCrumb items={array} />

      <section className="mb-[40px] px-[15px]">
        <div className="mx-auto max-w-[1200px] w-full">
          <BookList
            books={books}
            category={"Sale"}
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
    </>
  );
}

export default BookSale;
