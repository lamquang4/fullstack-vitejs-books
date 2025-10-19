import { Link, useLocation } from "react-router-dom";

function SideMenuBar() {
  const location = useLocation();
  const pathname = location.pathname;
  return (
    <div className="w-full max-w-full lg:max-w-[300px] self-start lg:sticky lg:top-[5rem] bg-white ">
      <div className="text-[0.9rem] font-medium">
        <div className="px-3.5 py-3 flex gap-5 items-center">
          <svg
            xmlns="http://www.w3.org/2000/svg"
            fill="none"
            stroke="currentColor"
            strokeWidth="2"
            strokeLinecap="round"
            strokeLinejoin="round"
            className="lucide lucide-user-star-icon lucide-user-star w-4 h-4"
            viewBox="2 2 21.06 20.67"
          >
            <path d="M16.051 12.616a1 1 0 0 1 1.909.024l.737 1.452a1 1 0 0 0 .737.535l1.634.256a1 1 0 0 1 .588 1.806l-1.172 1.168a1 1 0 0 0-.282.866l.259 1.613a1 1 0 0 1-1.541 1.134l-1.465-.75a1 1 0 0 0-.912 0l-1.465.75a1 1 0 0 1-1.539-1.133l.258-1.613a1 1 0 0 0-.282-.866l-1.156-1.153a1 1 0 0 1 .572-1.822l1.633-.256a1 1 0 0 0 .737-.535z"></path>
            <path d="M8 15H7a4 4 0 0 0-4 4v2"></path>
            <circle cx="10" cy="7" r="4"></circle>
          </svg>

          <div>
            <h5 className="font-medium">Your account</h5>
            <p className="font-normal">Quang lam</p>
          </div>
        </div>

        <Link
          to="/account"
          className={` py-3 px-3.5 ${
            pathname === "/account" ? "bg-gray-100" : "hover:bg-gray-100"
          }`}
        >
          <div className="flex items-center gap-5">
            <svg
              xmlns="http://www.w3.org/2000/svg"
              fill="none"
              stroke="currentColor"
              strokeWidth="2"
              strokeLinecap="round"
              strokeLinejoin="round"
              className="lucide lucide-user-round-icon lucide-user-round w-4 h-4"
              viewBox="3 2 18 20"
            >
              <circle cx="12" cy="8" r="5"></circle>
              <path d="M20 21a8 8 0 0 0-16 0"></path>
            </svg>
            <span>My account</span>
          </div>
        </Link>

        <Link
          to="/order"
          className={` py-3 px-3.5  ${
            pathname === "/order" ? "bg-gray-100" : "hover:bg-gray-100"
          }`}
        >
          <div className="flex items-center gap-5">
            <svg
              xmlns="http://www.w3.org/2000/svg"
              fill="none"
              stroke="currentColor"
              strokeWidth="2"
              strokeLinecap="round"
              strokeLinejoin="round"
              className="lucide lucide-shopping-bag-icon lucide-shopping-bag w-4 h-4"
              viewBox="2 1 20 22"
            >
              <path d="M16 10a4 4 0 0 1-8 0"></path>
              <path d="M3.103 6.034h17.794"></path>
              <path d="M3.4 5.467a2 2 0 0 0-.4 1.2V20a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2V6.667a2 2 0 0 0-.4-1.2l-2-2.667A2 2 0 0 0 17 2H7a2 2 0 0 0-1.6.8z"></path>
            </svg>
            <span>Order history</span>
          </div>
        </Link>

        <Link
          to="/address"
          className={`py-3 px-3.5  ${
            pathname === "/address" ? "bg-gray-100" : "hover:bg-gray-100"
          }`}
        >
          <div className="flex items-center gap-5">
            <svg
              xmlns="http://www.w3.org/2000/svg"
              fill="none"
              stroke="currentColor"
              strokeWidth="2"
              strokeLinecap="round"
              strokeLinejoin="round"
              className="lucide lucide-map-pin-house-icon lucide-map-pin-house w-4 h-4 "
              viewBox="1 1 22 22"
            >
              <path d="M15 22a1 1 0 0 1-1-1v-4a1 1 0 0 1 .445-.832l3-2a1 1 0 0 1 1.11 0l3 2A1 1 0 0 1 22 17v4a1 1 0 0 1-1 1z"></path>
              <path d="M18 10a8 8 0 0 0-16 0c0 4.993 5.539 10.193 7.399 11.799a1 1 0 0 0 .601.2"></path>
              <path d="M18 22v-3"></path>
              <circle cx="10" cy="10" r="3"></circle>
            </svg>
            <span>Address book</span>
          </div>
        </Link>

        <button
          type="button"
          className=" py-3 px-3.5 text-left text-[#C62028] font-medium hover:bg-gray-100 w-full"
        >
          <div className="flex items-center gap-5">
            <svg
              xmlns="http://www.w3.org/2000/svg"
              fill="none"
              stroke="currentColor"
              strokeWidth="2"
              strokeLinecap="round"
              strokeLinejoin="round"
              className="lucide lucide-door-open-icon lucide-door-open w-4 h-4"
              viewBox="1 1.56 22 21.16"
            >
              <path d="M11 20H2"></path>
              <path d="M11 4.562v16.157a1 1 0 0 0 1.242.97L19 20V5.562a2 2 0 0 0-1.515-1.94l-4-1A2 2 0 0 0 11 4.561z"></path>
              <path d="M11 4H8a2 2 0 0 0-2 2v14"></path>
              <path d="M14 12h.01"></path>
              <path d="M22 20h-3"></path>
            </svg>
            <span>Logout</span>
          </div>
        </button>
      </div>
    </div>
  );
}

export default SideMenuBar;
