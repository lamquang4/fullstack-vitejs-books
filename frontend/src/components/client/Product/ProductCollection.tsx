import { useParams } from "react-router-dom";
import Pagination from "../Pagination";
import ProductList from "./ProductList";

function ProductCollection() {
  const { slug } = useParams();
  return (
    <section className="my-[40px]  px-[15px]">
      <div className="mx-auto max-w-[1230px] w-full">
        <ProductList
          products={products}
          category={category}
          isLoading={isLoadingCategory || isLoadingProductsSlug}
          total={totalItems}
        />

        {/*
   <Pagination
          totalPages={totalPages}
          currentPage={currentPage}
          totalItems={totalItems}
        />
    */}
      </div>
    </section>
  );
}

export default ProductCollection;
