import ProductSlider from "../Product/ProductSlider";
import MainBanner from "./MainBanner";
import PromotionBanner from "./PromotionBanner";
import PublisherList from "./PublisherList";

function Home() {
  return (
    <>
      <MainBanner />
      <ProductSlider products={[]} title="" />
      <PromotionBanner banner={"/assets/banner/banner1.png"} />
      <ProductSlider products={[]} title="" />
      <PromotionBanner banner={"/assets/banner/banner2.png"} />
      <PublisherList />
    </>
  );
}

export default Home;
