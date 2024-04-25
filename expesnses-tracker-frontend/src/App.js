import "./App.css";
import { RouterProvider, createBrowserRouter } from "react-router-dom";

import Layout from "./components/basics/layout/Layout";
import Home from "./components/basics/home/Home";
import About from "./components/basics/about/About";
import ContactUs from "./components/basics/contact-us/ContactUs";
import NotFound from "./components/basics/not-found/NotFound";
import Forbidden from "./components/basics/forbidden/Forbidden";
import ProtectedRoute from "./components/basics/ProtectedRoute/ProtectedRoute";
import UserContextProvider from "./components/basics/UserContextProvider/UserContextProvider";

import Login from "./components/auth/login/Login";
import Activation from "./components/auth/activation/activation";
import Register from "./components/auth/register/Register";
import ForgotPassword from "./components/auth/Password-components/ForgotPassword/ForgotPassword";
import ResetPassword from "./components/auth/Password-components/ResetPassword/ResetPassword";
import Logout from "./components/auth/logout/Logout";
import NotApproved from "./components/basics/not_approved/not_approved";
import ViewAccounts from "./components/accounts/ViewAccounts/ViewAccounts";
import ShowCategories from './components/AdminDashboard/categories-components/show_categories/ShowCategories';
import SearchCategory from './components/AdminDashboard/categories-components/search-category/SearchCategory';
import AddCategory from './components/AdminDashboard/categories-components/add-category/AddCategory';
import UpdateCategory from './components/AdminDashboard/categories-components/update-category/UpdateCategory';
import AdminDashboard from './components/AdminDashboard/AdminDashboard';
import ShowAccounts from './components/AdminDashboard/accounts-components/show_account/ShowAccount';
import SearchAccount from './components/AdminDashboard/accounts-components/search-account/SearchAccount';
import AddAccount from './components/AdminDashboard/accounts-components/add-account/AddAccount';
import UpdateAccount from './components/AdminDashboard/accounts-components/update-account/UpdateAccount';
import ShowSubCategory from './components/AdminDashboard/subcategories-components/show-SubCategory/ShowSubCategory';
import SearchSubCategory from './components/AdminDashboard/subcategories-components/search-SubCategory/SearchSubCategory';
import AddSubCategory from './components/AdminDashboard/subcategories-components/add-SubCategory/AddSubCategory';
import UpdateSubCategory from "./components/AdminDashboard/subcategories-components/update-SubCategory/UpdateSubCategory";
import Categories from './components/categories/Categories';
import CategoryPage from './components/categories/categoryPage';
import SubCategories from './components/subCategories/SubCategories';
import SubCategoryPage from './components/subCategories/subCategory-page/SubCategoryPage';
import SubCategoryFilter from './components/subCategories/SubCategoryFilter';
import AccountPage from './components/accounts/AccountPage/AccountPage';

function App() {
  let routers = createBrowserRouter([
    {
      path: "/",
      element: <Layout />,
      children: [
        { index: true, element: <Home /> },
        { path: "about", element: <About /> },
        { path: "contact-us", element: <ContactUs /> },
        { path: "login", element: <Login /> },
        { path: "activate", element: <Activation /> },
        { path: "register", element: <Register /> },
        { path: "forget-password", element: <ForgotPassword /> },
        { path: "reset-password", element: <ResetPassword /> },

        {
          path: "logout",
          element: (
            <ProtectedRoute>
              <Logout />
            </ProtectedRoute>
          ),
        },
        { path: "*", element: <NotFound /> },
        { path: "forbidden", element: <Forbidden /> },
        { path: "not_approved", element: <NotApproved /> },
        {
          path: "accounts",
          // element: (
          //   <ProtectedRoute>
          //     <ViewAccounts />
          //   </ProtectedRoute>
          // ),
          children: [
            {
              index: true,
              element: (
                <ProtectedRoute>
                  <ViewAccounts />
                </ProtectedRoute>
              ),
            },
            {
              path: "/accounts/:refNo",
              element:(<AccountPage />)
            }
          ]
        },
        {
          path: "categories",
          children: [
            {
              index: true,
              element: (
                <ProtectedRoute>
                  <Categories />
                </ProtectedRoute>
              ),
            },
            {
              path: ":refNo",
              element: (
                <ProtectedRoute>
                  <CategoryPage />
                </ProtectedRoute>
              ),
            },
          ],
        },
        {
          path: "subCategories",
          children: [
            {
              index: true,
              element: (
                <ProtectedRoute>
                  <SubCategories />
                </ProtectedRoute>
              ),
            },

            { path: ":refNo", element: <SubCategoryPage /> },
            { path: "filter", element: <SubCategoryFilter /> },
          ],
        },
      ],
    },
    {
      path: "/admin",
      element: (
        <ProtectedRoute roles={["ROLE_ADMIN"]}>
          <AdminDashboard />
        </ProtectedRoute>
      ),
      children: [
        // { index: true, element: <component for data visualization /> },
        { path: "showCategories", element: <ShowCategories /> },
        { path: "SearchCategory", element: <SearchCategory /> },
        { path: "addCategory", element: <AddCategory /> },
        { path: "updateCategory/:refNo", element: <UpdateCategory /> },
        { path: "showSubCategories", element: <ShowSubCategory /> },
        { path: "SearchSubCategories", element: <SearchSubCategory /> },
        { path: "addSubCategory", element: <AddSubCategory /> },
        { path: "updateSubCategory/:refNo", element: <UpdateSubCategory /> },
        { path: "showAccounts", element: <ShowAccounts /> },
        { path: "SearchAccounts", element: <SearchAccount /> },
        { path: "addAccount", element: <AddAccount /> },
        { path: "updateAccount/:refNo", element: <UpdateAccount /> },
      ],
    },

  ]);

  return (
    <UserContextProvider>
      <RouterProvider router={routers}></RouterProvider>
    </UserContextProvider>
  );
}

export default App;
