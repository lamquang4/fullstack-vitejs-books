import { memo, useState } from "react";
import { CiSearch } from "react-icons/ci";
import { useNavigate } from "react-router-dom";

function SearchDesktop() {
  const navigate = useNavigate();
  const [search, setSearch] = useState<string>("");
  const handleSearch = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    if (!search.trim()) {
      return;
    }

    navigate(`/search?q=${encodeURIComponent(search.trim())}`);
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
          className="absolute top-1/2 right-[7px] transform -translate-y-1/2   flex items-center"
          type="submit"
        >
          <CiSearch size={20} />
        </button>
      </form>
    </div>
  );
}

export default memo(SearchDesktop);
