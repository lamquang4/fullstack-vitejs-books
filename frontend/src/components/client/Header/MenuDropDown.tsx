import { Link } from "react-router-dom";

type Props = {
  items: {
    name: string;
    href: string;
  }[];
};

function MenuDropDown({ items }: Props) {
  return (
    <ul className="text-[#707072] absolute font-medium top-full left-0 w-[200px] bg-white translate-y-[12px] opacity-0 invisible transition-all duration-200 z-5 group-hover:shadow-md group-hover:opacity-100 group-hover:visible group-hover:translate-y-[12px]">
      {items.map((item, index) => (
        <li key={index}>
          <Link
            to={item.href}
            className="text-[0.9rem] p-3 transition-all duration-200 hover:text-black"
          >
            {item.name}
          </Link>
        </li>
      ))}
    </ul>
  );
}

export default MenuDropDown;
