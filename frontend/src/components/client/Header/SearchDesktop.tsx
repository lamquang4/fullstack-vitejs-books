import { memo, useState } from "react";
import { CiSearch } from "react-icons/ci";
import { useNavigate, useLocation, matchPath } from "react-router-dom";
import Suggestion from "../Suggestion";

function SearchDesktop() {
  const navigate = useNavigate();
  const location = useLocation();
  const [search, setSearch] = useState<string>("");
  const [focused, setFocused] = useState<boolean>(false);
  const handleSearch = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    const query = search.trim();
    if (!query) return;

    const isBooksPage =
      location.pathname === "/books/all" ||
      location.pathname === "/sale" ||
      matchPath("/books/:slug", location.pathname) !== null;

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
  };

  return (
    <div className="relative">
      <form onSubmit={handleSearch}>
        <input
          type="text"
          className="px-3 py-1.5 pr-7 w-[145px] border border-gray-300 text-[0.8rem] placeholder:text-[0.8rem] bg-transparent outline-none"
          required
          maxLength={50}
          placeholder="Search..."
          autoComplete="off"
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          onFocus={() => setFocused(true)}
          onBlur={() => {
            setTimeout(() => {
              setFocused(false);
            }, 200);
          }}
        />
        <button
          className="absolute top-1/2 right-[7px] transform -translate-y-1/2 flex items-center"
          type="submit"
        >
          <CiSearch size={20} />
        </button>
      </form>

      {focused && search && (
        <div className="fixed right-12 z-12 w-96 max-w-[calc(100%-30px)] bg-white shadow-lg border-gray-300 border">
          <Suggestion search={search} />
        </div>
      )}
    </div>
  );
}

export default memo(SearchDesktop);
