import "./App.css";
import { RouterProvider, createBrowserRouter } from "react-router-dom";

import Layout from './components/basics/layout/Layout';
import Home from './components/basics/home/Home';
import About from './components/basics/about/About';
import ContactUs from './components/basics/contact-us/ContactUs';
import NotFound from './components/basics/not-found/NotFound';
import Forbidden from './components/basics/forbidden/Forbidden';
import ProtectedRoute from './components/basics/ProtectedRoute/ProtectedRoute';
import UserContextProvider from "./components/basics/UserContextProvider/UserContextProvider";

import Login from './components/auth/login/Login';
import Activation from './components/auth/activation/activation';
import Register from './components/auth/register/Register';
import ForgotPassword from './components/auth/Password-components/ForgotPassword/ForgotPassword';
import ResetPassword from './components/auth/Password-components/ResetPassword/ResetPassword';
import Logout from './components/auth/logout/Logout';
import NotApproved from './components/basics/not_approved/not_approved';


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
