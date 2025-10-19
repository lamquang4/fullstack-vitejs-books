import useGetActiveBooks from "../../../hooks/client/useGetActiveBooks";
import BookSlider from "../Book/BookSlider";
import MainBanner from "./MainBanner";
import PromotionBanner from "./PromotionBanner";
import PublisherList from "./PublisherList";

function Home() {
  const { books } = useGetActiveBooks();
  return (
    <>
      <MainBanner />
      <BookSlider books={books} title="Book 1" />
      <PromotionBanner banner={"/assets/banner/banner1.png"} />
      <BookSlider books={books} title="Book 2" />
      <PromotionBanner banner={"/assets/banner/banner2.png"} />
      <PublisherList />
    </>
  );
}

export default Home;
