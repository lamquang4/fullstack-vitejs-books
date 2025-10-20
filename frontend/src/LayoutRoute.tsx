import { Route, Routes } from "react-router-dom";
import LoginPage from "./pages/client/LoginPage";
import RegisterPage from "./pages/client/RegisterPage";
import HomePage from "./pages/client/HomePage";
import ResetPasswordPage from "./pages/client/ResetPasswordPage";
import AccountPage from "./pages/client/AccountPage";
import AddressPage from "./pages/client/AddressPage";
import OrderPage from "./pages/client/OrderPage";
import BookDetailPage from "./pages/client/BookDetailPage";
import OrderDetailPage from "./pages/client/OrderDetailPage";
import BookSalePage from "./pages/client/BookSalePage";
import LoginAdminPage from "./pages/admin/LoginAdminPage";
import AccountAdminPage from "./pages/admin/AccountAdminPage";
import AuthorPage from "./pages/admin/AuthorPage";
import AddAuthorPage from "./pages/admin/AddAuthorPage";
import EditAuthorPage from "./pages/admin/EditAuthorPage";
import PublisherPage from "./pages/admin/PublisherPage";
import AddPublisherPage from "./pages/admin/AddPublisherPage";
import EditPublisherPage from "./pages/admin/EditPublisherPage";
import CategoryPage from "./pages/admin/CategoryPage";
import AddCategoryPage from "./pages/admin/AddCategoryPage";
import EditCategoryPage from "./pages/admin/EditCategoryPage";
import AdminPage from "./pages/admin/AdminPage";
import AddAdminPage from "./pages/admin/AddAdminPage";
import EditAdminPage from "./pages/admin/EditAdminPage";
import CustomerPage from "./pages/admin/CustomerPage";
import AddCustomerPage from "./pages/admin/AddCustomerPage";
import EditCustomerPage from "./pages/admin/EditCustomerPage";
import BookPage from "./pages/admin/BookPage";
import AddBookPage from "./pages/admin/AddBookPage";
import EditBookPage from "./pages/admin/EditBookPage";
import BookCategoryPage from "./pages/client/BookCategoryPage";
function LayoutRoute() {
  return (
    <Routes>
      <Route path="/" element={<HomePage />} />
      <Route path="/login" element={<LoginPage />} />
      <Route path="/register" element={<RegisterPage />} />
      <Route path="/reset" element={<ResetPasswordPage />} />
      <Route path="/account" element={<AccountPage />} />
      <Route path="/address" element={<AddressPage />} />
      <Route path="/order" element={<OrderPage />} />
      <Route path="/order/:code" element={<OrderDetailPage />} />
      <Route path="/book/:slug" element={<BookDetailPage />} />
      <Route path="/books/:slug" element={<BookCategoryPage />} />
      <Route path="/sale" element={<BookSalePage />} />

      <Route path="/admin/login" element={<LoginAdminPage />} />
      <Route path="/admin/account" element={<AccountAdminPage />} />

      <Route path="/admin/authors" element={<AuthorPage />} />
      <Route path="/admin/add-author" element={<AddAuthorPage />} />
      <Route path="/admin/edit-author/:id" element={<EditAuthorPage />} />

      <Route path="/admin/publishers" element={<PublisherPage />} />
      <Route path="/admin/add-publisher" element={<AddPublisherPage />} />
      <Route path="/admin/edit-publisher/:id" element={<EditPublisherPage />} />

      <Route path="/admin/categories" element={<CategoryPage />} />
      <Route path="/admin/add-category" element={<AddCategoryPage />} />
      <Route path="/admin/edit-category/:id" element={<EditCategoryPage />} />

      <Route path="/admin/admins" element={<AdminPage />} />
      <Route path="/admin/add-admin" element={<AddAdminPage />} />
      <Route path="/admin/edit-admin/:id" element={<EditAdminPage />} />

      <Route path="/admin/customers" element={<CustomerPage />} />
      <Route path="/admin/add-customer" element={<AddCustomerPage />} />
      <Route path="/admin/edit-customer/:id" element={<EditCustomerPage />} />

      <Route path="/admin/books" element={<BookPage />} />
      <Route path="/admin/add-book" element={<AddBookPage />} />
      <Route path="/admin/edit-book/:id" element={<EditBookPage />} />

      <Route path="/admin/orders" element={<OrderPage />} />
      <Route path="/admin/order" element={<OrderDetailPage />} />
    </Routes>
  );
}

export default LayoutRoute;
