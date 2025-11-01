import useGetActiveBooks from "../../../hooks/client/useGetActiveBooks";
import useGetBestsellerBooks from "../../../hooks/client/useGetBestsellerBooks";
import BookSlider from "../Book/BookSlider";
import FeatureBanner from "./FeatureBanner";
import MainBanner from "./MainBanner";
import PromotionBanner from "./PromotionBanner";
import ServiceFeature from "./ServiceFeature";

function Home() {
  const { books } = useGetActiveBooks();
  const { books: bestsellerBooks } = useGetBestsellerBooks();

  return (
    <>
      <MainBanner />
      <FeatureBanner />
      {books && books.length > 0 && (
        <>
          <BookSlider books={books} title="Mới nhất" />
          <PromotionBanner
            banner={{
              desktop: "/assets/banner/banner1.png",
              mobile: "/assets/banner/banner1.png",
            }}
          />
        </>
      )}

      {bestsellerBooks && bestsellerBooks.length > 0 && (
        <>
          <BookSlider books={bestsellerBooks} title="Bán chạy nhất" />
          <PromotionBanner
            banner={{
              desktop: "/assets/banner/banner2.png",
              mobile: "/assets/banner/banner2.png",
            }}
          />
        </>
      )}

      <ServiceFeature />
    </>
  );
}

export default Home;
