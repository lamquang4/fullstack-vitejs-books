import useGetActiveBooks from "../../../hooks/client/useGetActiveBooks";
import useGetBestsellerBooks from "../../../hooks/client/useGetBestsellerBooks";
import BookSlider from "../Book/BookSlider";
import FeatureBanner from "./FeatureBanner";
import MainBanner from "./MainBanner";
import PromotionBanner from "./PromotionBanner";

function Home() {
  const { books } = useGetActiveBooks();
  const { books: bestsellerBooks } = useGetBestsellerBooks();

  return (
    <>
      <MainBanner />
      <FeatureBanner />
      {books && books.length > 0 && (
        <>
          <BookSlider books={books} title="New arrival" />
          <PromotionBanner
            banner={{
              desktop: "/assets/banner/banner1.png",
              mobile: "/assets/banner/banner1-mobile.png",
            }}
          />
        </>
      )}

      {bestsellerBooks && bestsellerBooks.length > 0 && (
        <>
          <BookSlider books={bestsellerBooks} title="Bestseller" />
          <PromotionBanner
            banner={{
              desktop: "/assets/banner/banner2.png",
              mobile: "/assets/banner/banner2-mobile.png",
            }}
          />
        </>
      )}
    </>
  );
}

export default Home;
