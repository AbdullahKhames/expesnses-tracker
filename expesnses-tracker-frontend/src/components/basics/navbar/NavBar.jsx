import React, { useContext, useEffect } from "react";
import { Link } from "react-router-dom";
import "./navbar.css";
import logo from "../../../assets/images/online-course.ico";
import api from "../../api";
import config from "../../config";
import Loading from "../Loading/loading";
import { UserDataContext } from "../UserContextProvider/UserContextProvider";
// import { jwtDecode } from 'jwt-decode';

export default function NavBar(props) {
  let userContext = useContext(UserDataContext);
  const userData = userContext.userData;

  const [categories, setCategories] = React.useState([]);
  const [subCategories, setSubCategories] = React.useState([]);
  const [loading, setLoading] = React.useState(true);
  const url_api = config.api + "/categories?page=1&per_page=50";
  const url_api_subCategories = config.api + "/sub-categories?page=1&per_page=50";

  useEffect(() => {
    console.log(userData);
    if (userData !== null && userData !== undefined) {
      api
      .get(url_api)
      .then((response) => {
        console.log(response);
        if (response.status === 200) {
          setCategories(response.data.data.content);
          setLoading(false);
        }
      })
      .catch((error) => {
        console.error(error);
      });
    }
  }, [userData, url_api]);
  useEffect(() => {
      api
      .get(url_api_subCategories)
      .then((response) => {
        console.log(response);
        if (response.status === 200) {
          setSubCategories(response.data.data.content);
          setLoading(false);
        }
      })
      .catch((error) => {
        console.error(error);
      });
    
  }, [userData, url_api_subCategories]);

  useEffect(() => {
    console.log("userData changed");
  }, [userContext.userData]);
  return (
    <>
      <nav className="navbar navbar-expand-lg bg-body-tertiary">
        <div className="container-fluid">
          <div className="logo-container">
          <Link to="/">
            <div className="logo">
              <img src={logo} alt="Logo" />
            </div>
          </Link>
          </div>
          <Link className="navbar-brand" to="/">
            Expenses Tracker
          </Link>
          <button
            className="navbar-toggler"
            type="button"
            data-bs-toggle="collapse"
            data-bs-target="#navbarSupportedContent"
            aria-controls="navbarSupportedContent"
            aria-expanded="false"
            aria-label="Toggle navigation"
          >
            <span className="navbar-toggler-icon"></span>
          </button>
          <div className="collapse navbar-collapse" id="navbarSupportedContent">
            <ul className="navbar-nav me-auto mb-2 mb-lg-0">
              {/* Left side navigation */}
              <li className="nav-item">
                <Link className="nav-link active" aria-current="page" to="/">
                  Home
                </Link>
              </li>
              <li className="nav-item">
                <Link className="nav-link" to="/about">
                  About
                </Link>
              </li>
              {/* <li className="nav-item">
                <Link className="nav-link" to="/contact-us">
                  Contact Us
                </Link>
              </li> */}
              <li className="nav-item dropdown">
                <Link
                  className="nav-link dropdown-toggle"
                  to="/categories"
                  role="button"
                  data-bs-toggle="dropdown"
                  aria-expanded="false"
                >
                  Categories
                </Link>
                <ul className="dropdown-menu">
                  {loading ? <Loading /> : categories.map((category, index) => (
                    <li key={category.refNo || index}>
                      <Link
                        className="dropdown-item"
                        to={"/categories/" + category.refNo}
                      >
                        {category.name}
                      </Link>
                    </li>
                  ))}
                  <li>
                    <hr className="dropdown-divider" />
                  </li>
                  <li>
                    <Link className="dropdown-item" to="/categories">
                      All Categories
                    </Link>
                  </li>
                </ul>
              </li>
              <li className="nav-item dropdown">
                <Link
                  className="nav-link dropdown-toggle"
                  to="/subCategories"
                  role="button"
                  data-bs-toggle="dropdown"
                  aria-expanded="false"
                >
                  Sub Categories
                </Link>
                <ul className="dropdown-menu">
                  {loading ? <Loading /> : subCategories.map((subCategory, index) => (
                    <li key={subCategory.refNo || index}>
                      <Link
                        className="dropdown-item"
                        to={"/subCategories/" + subCategory.refNo}
                      >
                        {subCategory.name}
                      </Link>
                    </li>
                  ))}
                  <li>
                    <hr className="dropdown-divider" />
                  </li>
                  <li>
                    <Link className="dropdown-item" to="/subCategories">
                      All Sub Categories
                    </Link>
                  </li>
                </ul>
              </li>
              <li className="nav-item">
                <Link className="nav-link" to="/accounts">
                  Accounts
                </Link>
              </li>
              {userData && userData.roles.includes('ROLE_CUSTOMER') ? (
                <>
                  <li className="nav-item">
                    <Link className="nav-link" to="/customers">
                      Instructor
                    </Link>
                  </li>
                </>
              ) : null}
              {userData &&userData.roles.includes('ROLE_ADMIN') ? (
                <>
                  <li className="nav-item">
                    <Link className="nav-link" to="/admin">
                      Admin
                    </Link>
                  </li>
                </>
              ) : null}
            </ul>
            {/* Right side navigation */}

            <ul className="navbar-nav">
              {userData === null ? (
                <>
                  <li className="nav-item">
                    <Link className="nav-link" to="/register">
                      Register
                    </Link>
                  </li>
                  <li className="nav-item">
                    <Link className="nav-link" to="/activate">
                      Activate
                    </Link>
                  </li>
                  <li className="nav-item">
                    <Link className="nav-link" to="/login">
                      Login
                    </Link>
                  </li>
                  <li className="nav-item">
                    <Link className="nav-link" to="/forget-password">
                      Forget Password
                    </Link>
                  </li>
                  <li className="nav-item">
                    <Link className="nav-link" to="/reset-password">
                      Reset Password
                    </Link>
                  </li>
                </>
              ) : (
                <>
                  <li className="nav-item">
                    <Link className="nav-link" to="/profile">
                      {userData.name}
                    </Link>
                  </li>
                  <li className="nav-item">
                    <Link className="nav-link" to="/logout">
                      Logout
                    </Link>
                  </li>
                </>
              )}
            </ul>
          </div>
        </div>
      </nav>
    </>
  );
}
