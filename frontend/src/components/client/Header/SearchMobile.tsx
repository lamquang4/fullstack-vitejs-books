import { memo, useEffect, useState } from "react";
import { matchPath, useLocation, useNavigate } from "react-router-dom";

type Props = {
  toggleSearch: () => void;
  openSearch: boolean;
};

function SearchMobile({ toggleSearch, openSearch }: Props) {
  const navigate = useNavigate();
  const location = useLocation();
  const [search, setSearch] = useState<string>("");

  const handleSearch = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    const query = search.trim();
    if (!query) return;

    const isBooksPage =
      location.pathname === "/books/all" ||
      "/sale" ||
      matchPath("/books/:slug", location.pathname);

    let target = "";
    
    if (location.pathname === "/sale") {
      target = `/sale?q=${encodeURIComponent(query)}`;
    } else if (isBooksPage) {
      target = `${location.pathname}?q=${encodeURIComponent(query)}`;
    } else {
      target = `/books/all?q=${encodeURIComponent(query)}`;
    }

    navigate(target);
    setSearch("");
    toggleSearch();
  };

  useEffect(() => {
    if (openSearch) {
      document.body.style.overflow = "hidden";
    } else {
      document.body.style.overflow = "";
    }

    return () => {
      document.body.style.overflow = "";
    };
  }, [openSearch]);

  return (
    <div
      className={`absolute left-0 w-full bg-white border-t border-gray-300 transition-all duration-300 overflow-hidden ${
        openSearch
          ? "opacity-100 visible top-[68px]"
          : "opacity-0 invisible top-[90px]"
      }`}
    >
      <div className="relative">
        <div className="flex items-center px-[15px] py-4">
          <form onSubmit={handleSearch} className="w-full">
            <input
              type="text"
              required
              placeholder="Search..."
              maxLength={50}
              autoComplete="off"
              className="w-full rounded outline-none text-[0.9rem]"
              value={search}
              onChange={(e) => setSearch(e.target.value)}
            />
          </form>

          <button onClick={toggleSearch}>
            <svg
              xmlns="http://www.w3.org/2000/svg"
              fill="none"
              stroke="currentColor"
              strokeWidth="2"
              strokeLinecap="round"
              strokeLinejoin="round"
              className="lucide lucide-x-icon lucide-x w-3"
              viewBox="5 5 14 14"
            >
              <path d="M18 6 6 18"></path>
              <path d="m6 6 12 12"></path>
            </svg>
          </button>
        </div>
      </div>
    </div>
  );
}

export default memo(SearchMobile);
