import { Route, Routes } from "react-router-dom";
import LoginPage from "./pages/client/LoginPage";
import RegisterPage from "./pages/client/RegisterPage";
import HomePage from "./pages/client/HomePage";
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
import PrivateRoute from "./components/PrivateRoute";
import PublicRoute from "./components/PublicRoute";
import CartPage from "./pages/client/CartPage";
import CheckoutPage from "./pages/client/CheckoutPage";
import OrderAdminPage from "./pages/admin/OrderAdminPage";
import OrderDetailAdminPage from "./pages/admin/OrderDetailAdminPage";
import DashboardPage from "./pages/admin/DashboardPage";
import OrderResultPage from "./pages/client/OrderResultPage";
import PaymentPage from "./pages/admin/PaymentPage";

function LayoutRoute() {
  return (
    <Routes>
      <Route path="/" element={<HomePage />} />
      <Route
        path="/login"
        element={
          <PublicRoute type="client" redirectPath="/">
            <LoginPage />
          </PublicRoute>
        }
      />
      <Route
        path="/register"
        element={
          <PublicRoute type="client" redirectPath="/">
            <RegisterPage />
          </PublicRoute>
        }
      />
      <Route
        path="/account"
        element={
          <PrivateRoute type="client" allowedRoles={[3]} redirectPath="/">
            <AccountPage />
          </PrivateRoute>
        }
      />
      <Route
        path="/address"
        element={
          <PrivateRoute type="client" allowedRoles={[3]} redirectPath="/">
            <AddressPage />
          </PrivateRoute>
        }
      />
      <Route
        path="/order"
        element={
          <PrivateRoute type="client" allowedRoles={[3]} redirectPath="/">
            <OrderPage />
          </PrivateRoute>
        }
      />
      <Route
        path="/order/:code"
        element={
          <PrivateRoute type="client" allowedRoles={[3]} redirectPath="/">
            <OrderDetailPage />
          </PrivateRoute>
        }
      />
      <Route path="/book/:slug" element={<BookDetailPage />} />
      <Route path="/books/:slug" element={<BookCategoryPage />} />
      <Route path="/sale" element={<BookSalePage />} />

      <Route path="/cart" element={<CartPage />} />

      <Route
        path="/checkout"
        element={
          <PrivateRoute type="client" allowedRoles={[3]} redirectPath="/">
            <CheckoutPage />
          </PrivateRoute>
        }
      />

      <Route
        path="/order-result"
        element={
          <PrivateRoute type="client" allowedRoles={[3]} redirectPath="/">
            <OrderResultPage />
          </PrivateRoute>
        }
      />

      <Route
        path="/admin/login"
        element={
          <PublicRoute type="admin" redirectPath="/admin/dashboard">
            <LoginAdminPage />
          </PublicRoute>
        }
      />

      <Route
        path="/admin/dashboard"
        element={
          <PrivateRoute
            type="admin"
            allowedRoles={[0, 1, 2]}
            redirectPath="/admin/login"
          >
            <DashboardPage />
          </PrivateRoute>
        }
      />

      <Route
        path="/admin/account"
        element={
          <PrivateRoute
            type="admin"
            allowedRoles={[0, 1, 2]}
            redirectPath="/admin/login"
          >
            <AccountAdminPage />
          </PrivateRoute>
        }
      />

      <Route
        path="/admin/authors"
        element={
          <PrivateRoute
            type="admin"
            allowedRoles={[0, 1, 2]}
            redirectPath="/admin/login"
          >
            <AuthorPage />
          </PrivateRoute>
        }
      />
      <Route
        path="/admin/add-author"
        element={
          <PrivateRoute
            type="admin"
            allowedRoles={[0, 1, 2]}
            redirectPath="/admin/login"
          >
            <AddAuthorPage />
          </PrivateRoute>
        }
      />
      <Route
        path="/admin/edit-author/:id"
        element={
          <PrivateRoute
            type="admin"
            allowedRoles={[0, 1, 2]}
            redirectPath="/admin/login"
          >
            <EditAuthorPage />
          </PrivateRoute>
        }
      />

      <Route
        path="/admin/publishers"
        element={
          <PrivateRoute
            type="admin"
            allowedRoles={[0, 1, 2]}
            redirectPath="/admin/login"
          >
            <PublisherPage />
          </PrivateRoute>
        }
      />
      <Route
        path="/admin/add-publisher"
        element={
          <PrivateRoute
            type="admin"
            allowedRoles={[0, 1, 2]}
            redirectPath="/admin/login"
          >
            <AddPublisherPage />
          </PrivateRoute>
        }
      />
      <Route
        path="/admin/edit-publisher/:id"
        element={
          <PrivateRoute
            type="admin"
            allowedRoles={[0, 1, 2]}
            redirectPath="/admin/login"
          >
            <EditPublisherPage />
          </PrivateRoute>
        }
      />

      <Route
        path="/admin/categories"
        element={
          <PrivateRoute
            type="admin"
            allowedRoles={[0, 1, 2]}
            redirectPath="/admin/login"
          >
            <CategoryPage />
          </PrivateRoute>
        }
      />
      <Route
        path="/admin/add-category"
        element={
          <PrivateRoute
            type="admin"
            allowedRoles={[0, 1, 2]}
            redirectPath="/admin/login"
          >
            <AddCategoryPage />
          </PrivateRoute>
        }
      />
      <Route
        path="/admin/edit-category/:id"
        element={
          <PrivateRoute
            type="admin"
            allowedRoles={[0, 1, 2]}
            redirectPath="/admin/login"
          >
            <EditCategoryPage />
          </PrivateRoute>
        }
      />

      <Route
        path="/admin/admins"
        element={
          <PrivateRoute
            type="admin"
            allowedRoles={[0]}
            redirectPath="/admin/login"
          >
            <AdminPage />
          </PrivateRoute>
        }
      />
      <Route
        path="/admin/add-admin"
        element={
          <PrivateRoute
            type="admin"
            allowedRoles={[0]}
            redirectPath="/admin/login"
          >
            <AddAdminPage />
          </PrivateRoute>
        }
      />
      <Route
        path="/admin/edit-admin/:id"
        element={
          <PrivateRoute
            type="admin"
            allowedRoles={[0]}
            redirectPath="/admin/login"
          >
            <EditAdminPage />
          </PrivateRoute>
        }
      />

      <Route
        path="/admin/customers"
        element={
          <PrivateRoute
            type="admin"
            allowedRoles={[0]}
            redirectPath="/admin/login"
          >
            <CustomerPage />
          </PrivateRoute>
        }
      />
      <Route
        path="/admin/add-customer"
        element={
          <PrivateRoute
            type="admin"
            allowedRoles={[0]}
            redirectPath="/admin/login"
          >
            <AddCustomerPage />
          </PrivateRoute>
        }
      />
      <Route
        path="/admin/edit-customer/:id"
        element={
          <PrivateRoute
            type="admin"
            allowedRoles={[0]}
            redirectPath="/admin/login"
          >
            <EditCustomerPage />
          </PrivateRoute>
        }
      />

      <Route
        path="/admin/books"
        element={
          <PrivateRoute
            type="admin"
            allowedRoles={[0, 1, 2]}
            redirectPath="/admin/login"
          >
            <BookPage />
          </PrivateRoute>
        }
      />
      <Route
        path="/admin/add-book"
        element={
          <PrivateRoute
            type="admin"
            allowedRoles={[0, 1, 2]}
            redirectPath="/admin/login"
          >
            <AddBookPage />
          </PrivateRoute>
        }
      />
      <Route
        path="/admin/edit-book/:id"
        element={
          <PrivateRoute
            type="admin"
            allowedRoles={[0, 1, 2]}
            redirectPath="/admin/login"
          >
            <EditBookPage />
          </PrivateRoute>
        }
      />

      <Route
        path="/admin/orders"
        element={
          <PrivateRoute
            type="admin"
            allowedRoles={[0, 1, 2]}
            redirectPath="/admin/login"
          >
            <OrderAdminPage />
          </PrivateRoute>
        }
      />
      <Route
        path="/admin/order/:id"
        element={
          <PrivateRoute
            type="admin"
            allowedRoles={[0, 1, 2]}
            redirectPath="/admin/login"
          >
            <OrderDetailAdminPage />
          </PrivateRoute>
        }
      />

      <Route
        path="/admin/payments"
        element={
          <PrivateRoute
            type="admin"
            allowedRoles={[0, 1, 2]}
            redirectPath="/admin/login"
          >
            <PaymentPage />
          </PrivateRoute>
        }
      />
    </Routes>
  );
}

export default LayoutRoute;
