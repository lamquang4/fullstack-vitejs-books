import { Link } from "react-router-dom";
import Image from "../Image";
import { FaInstagram, FaFacebookSquare } from "react-icons/fa";
import { IoLogoYoutube } from "react-icons/io";
function Footer() {
  return (
    <footer className="bg-gray-50 border-t border-gray-200 px-[15px]">
      <div className="mx-auto w-full max-w-[1200px]">
        <div className="grid grid-cols-2 gap-8 md:grid-cols-[repeat(auto-fit,minmax(200px,1fr))] py-[60px]">
          <div>
            <Link to={"/"}>
              <Image
                source={"/assets/logo.png"}
                alt={"logo"}
                className={"w-[100px]"}
                loading="eager"
              />
            </Link>
          </div>

          <div>
            <ul>
              <li>
                <p className="relative font-bold text-black text-[0.95rem] uppercase mb-3 pb-1.5 after:content-[''] after:absolute after:bottom-0 after:left-0 after:bg-black after:w-[60px] after:h-[1.5px]">
                  Policies
                </p>
              </li>

              <li className="py-[8px]">
                <Link
                  to="/"
                  className="inline-block max-w-max text-gray-500 font-medium text-[0.9rem] hover:text-black"
                >
                  Home
                </Link>
              </li>

              <li className="py-[8px]">
                <Link
                  to="/books/all"
                  className="inline-block max-w-max text-gray-500 font-medium text-[0.9rem] hover:text-black"
                >
                  All books
                </Link>
              </li>
            </ul>
          </div>

          <div>
            <ul>
              <li>
                <p className="relative font-bold text-black text-[0.95rem] uppercase mb-3 pb-1.5 after:content-[''] after:absolute after:bottom-0 after:left-0 after:bg-black after:w-[60px] after:h-[1.5px]">
                  Follow us
                </p>
              </li>

              <li>
                <ul className="flex justify-start items-center gap-3">
                  <li className="py-[8px]">
                    <Link
                      className="inline-block max-w-max text-gray-500 font-medium text-[0.9rem]"
                      to={"/"}
                      title="Instagram"
                    >
                      <FaInstagram size={25} />
                    </Link>
                  </li>
                  <li className="py-[8px]">
                    <Link
                      className="inline-block max-w-max text-gray-500 font-medium text-[0.9rem]"
                      to={"/"}
                      title="Facebook"
                    >
                      <FaFacebookSquare size={25} />
                    </Link>
                  </li>
                  <li className="py-[8px]">
                    <Link
                      className="inline-block max-w-max text-gray-500 font-medium text-[0.9rem]"
                      to={"/"}
                      title="Youtube"
                    >
                      <IoLogoYoutube size={25} />
                    </Link>
                  </li>
                </ul>
              </li>
            </ul>
          </div>
        </div>

        <div className="text-center py-[15px]">
          <p className="font-medium text-gray-500">© Aura Vietnam 2025</p>
        </div>
      </div>
    </footer>
  );
}

export default Footer;
