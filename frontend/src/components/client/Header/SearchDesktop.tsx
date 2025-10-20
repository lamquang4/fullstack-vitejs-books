import { memo, useState } from "react";
import { CiSearch } from "react-icons/ci";
import { useNavigate, useLocation, matchPath } from "react-router-dom";

function SearchDesktop() {
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
        />
        <button
          className="absolute top-1/2 right-[7px] transform -translate-y-1/2 flex items-center"
          type="submit"
        >
          <CiSearch size={20} />
        </button>
      </form>
    </div>
  );
}

export default memo(SearchDesktop);
