import { Link } from "react-router-dom";
import Image from "../../Image";

type Props = {
  banner: {
    mobile: string;
    desktop: string;
  };
};

function PromotionBanner({ banner }: Props) {
  if (!banner || !banner.desktop) return null;

  return (
    <section className="mb-[40px]">
      <div className="mx-auto max-w-[1200px] w-full">
        <div className="flex flex-col gap-[20px] sm:gap-[30px]">
          <Link to="/books/all">
            <div className="relative">
              <picture>
                <source srcSet={banner.mobile} media="(max-width: 768px)" />
                <Image
                  source={banner.desktop}
                  alt="banner"
                  className="w-full object-cover"
                  loading="lazy"
                />
              </picture>
            </div>
          </Link>
        </div>
      </div>
    </section>
  );
}

export default PromotionBanner;
