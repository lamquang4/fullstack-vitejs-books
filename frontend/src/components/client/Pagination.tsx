import { GrFormNext, GrFormPrevious } from "react-icons/gr";
import { useNavigate, useLocation } from "react-router-dom";

interface Props {
  totalPages: number;
  currentPage: number;
  totalItems: number;
}

function Pagination({ totalPages, currentPage, totalItems }: Props) {
  const navigate = useNavigate();
  const location = useLocation();

  const goToPage = (page: number) => {
    const searchParams = new URLSearchParams(location.search);
    searchParams.set("page", page.toString());

    navigate({
      pathname: location.pathname,
      search: `?${searchParams.toString()}`,
    });
  };

  const getPageNumbers = () => {
    const pages: (number | "...")[] = [];

    if (totalPages <= 5) {
      for (let i = 1; i <= totalPages; i++) pages.push(i);
    } else {
      if (currentPage <= 3) {
        pages.push(1, 2, 3, "...", totalPages);
      } else if (currentPage >= totalPages - 2) {
        pages.push(1, "...", totalPages - 2, totalPages - 1, totalPages);
      } else {
        pages.push(
          1,
          "...",
          currentPage - 1,
          currentPage,
          currentPage + 1,
          "...",
          totalPages
        );
      }
    }

    return pages;
  };

  if (totalItems === 0) return null;

  return (
    <div className="mt-[20px] px-[15px]">
      <div className="flex items-center justify-center w-full flex-wrap gap-2.5 text-[#C62028] text-[0.9rem]">
        <button
          type="button"
          className="h-8.5 w-8.5 flex justify-center items-center border border-[#C62028]"
          disabled={currentPage <= 1}
          onClick={() => currentPage > 1 && goToPage(currentPage - 1)}
        >
          <GrFormPrevious size={20} />
        </button>

        {getPageNumbers().map((page, index) => {
          if (page === "...") {
            return (
              <button
                type="button"
                disabled
                key={`ellipsis-${index}`}
                className="group h-8.5 w-8.5 flex justify-center items-center   font-medium border border-[#C62028]"
              >
                ...
              </button>
            );
          }

          return (
            <button
              key={page}
              onClick={() => goToPage(page as number)}
              className={`h-8.5 w-8.5 border border-[#C62028] flex justify-center items-center font-medium   ${
                currentPage === page
                  ? "bg-[#C62028] text-white"
                  : " hover:bg-gray-50"
              }`}
            >
              {page}
            </button>
          );
        })}

        <button
          disabled={currentPage >= totalPages}
          onClick={() => currentPage < totalPages && goToPage(currentPage + 1)}
          className="h-8.5 w-8.5 flex justify-center items-center  border border-[#C62028]"
        >
          <GrFormNext size={20} />
        </button>
      </div>
    </div>
  );
}

export default Pagination;
