import { Link } from "react-router-dom";
import Image from "../../Image";
type Props = {
  banner: string;
};
function PromotionBanner({ banner }: Props) {
  return (
    <>
      {banner.length > 0 && (
        <section className="mb-[40px] px-[15px]">
          <div className="mx-auto max-w-[1350px] w-full">
            <div className="flex flex-col gap-[20px] sm:gap-[30px]">
              <Link to="/collection/nam">
                <div className="relative">
                  <Image
                    source={banner}
                    alt="banner"
                    className="w-full object-cover"
                    loading="lazy"
                  />
                </div>
              </Link>
            </div>
          </div>
        </section>
      )}
    </>
  );
}

export default PromotionBanner;
