import { useNavigate, useParams } from "react-router-dom";
import useGetBookDetail from "../../../../hooks/client/useGetBookDetail";
import BookDetail from "./BookDetail";
import BreadCrumb from "../../BreadCrumb";
import toast from "react-hot-toast";
import { useEffect } from "react";
import Loading from "../../../Loading";

function BookDetailSlug() {
  const { slug } = useParams();
  const navigate = useNavigate();
  const { book, isLoading } = useGetBookDetail(slug as string);

  useEffect(() => {
    if (isLoading) return;

    if (!book) {
      toast.error("Book not found");
      navigate("/", { replace: true });
      return;
    }
  }, [book, isLoading, navigate]);

  const array = [
    {
      name: "Home",
      href: "/",
    },
    {
      name: book?.category?.name ?? "",
      href: `/books/${book?.category?.slug ?? ""}`,
    },
    {
      name: book?.title ?? "",
    },
  ];

  return (
    <>
      {isLoading ? (
        <Loading height={70} size={50} color="black" thickness={2} />
      ) : (
        <>
          <BreadCrumb items={array} />

          {book && <BookDetail book={book} />}
        </>
      )}
    </>
  );
}

export default BookDetailSlug;
