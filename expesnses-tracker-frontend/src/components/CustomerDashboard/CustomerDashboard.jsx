import React, { useContext, useEffect, useState } from "react";
import { Outlet, Link } from "react-router-dom";
import { UserDataContext } from "../basics/UserContextProvider/UserContextProvider";
import Loading from "../basics/Loading/loading";
import Header from "../basics/header/Header";
import Footer from "../basics/footer/Footer";

export default function CustomerDashboard(props) {
  const userContext = useContext(UserDataContext);
  const userData = userContext.userData;
  const [loading, setLoading] = useState(true);
  const [optionsVisibility, setOptionsVisibility] = useState({
    categories: false,
    subCategories: false,
    accounts: false,
  });

  const toggleOptions = (sectionId) => {
    setOptionsVisibility((prevVisibility) => ({
      ...prevVisibility,
      [sectionId]: !prevVisibility[sectionId],
    }));
  };

  useEffect(() => {
    if (
      userContext.userData &&
      userContext.userData.roles !== null &&
      userContext.userData.roles !== undefined
    ) {
      setLoading(false);
    }
  }, [userContext.userData]);

  if (loading) {
    return <Loading />;
  } else {
    return (
      <>
        <div
          style={{
            display: "flex",
            flexDirection: "column",
            minHeight: "100vh",
          }}
        >
          <Header
            userData={userContext.userData}
            saveUserData={userContext.saveUserData}
          />
          <div style={{ flex: 1 }}>
            <div className="dashboard">
              <div className="sidebar">
                {/* categories section ! */}
                <div className="dash-section" id="categories">
                  <div
                    className="dash-section-title"
                    onClick={() => toggleOptions("categories")}
                  >
                    categories
                  </div>
                  {optionsVisibility.categories && (
                    <div className="options">
                      <>
                        <Link className="nav-link" to="addCategory">
                          <div className="option">
                            <span className="icon">+</span> Add
                          </div>
                        </Link>

                        <Link className="nav-link" to="showCategories">
                          <div className="option">
                            <span className="icon"></span> Show
                          </div>
                        </Link>
                        <Link className="nav-link" to="SearchCategory">
                          <div className="option">
                            <span className="icon">+</span> search
                          </div>
                        </Link>
                      </>

                      {/* <div className="option">
                    <span className="icon">✎</span> Update
                  </div>
                  <div className="option">
                    <span className="icon">✖</span> Delete
                  </div> */}
                    </div>
                  )}
                </div>

                {/* sub categories section */}
                <div className="dash-section" id="subCategories">
                  <div
                    className="dash-section-title"
                    onClick={() => toggleOptions("subCategories")}
                  >
                    subCategories
                  </div>
                  {optionsVisibility.subCategories && (
                    <div className="options">
                      <>
                        <Link className="nav-link" to="addSubCategory">
                          <div className="option">
                            <span className="icon">+</span> Add
                          </div>
                        </Link>

                        <Link className="nav-link" to="showSubCategories">
                          <div className="option">
                            <span className="icon"></span> Show
                          </div>
                        </Link>
                        <Link className="nav-link" to="SearchSubCategories">
                          <div className="option">
                            <span className="icon">+</span> search
                          </div>
                        </Link>
                      </>

                      {/* <div className="option">
                    <span className="icon">✎</span> Update
                  </div>
                  <div className="option">
                    <span className="icon">✖</span> Delete
                  </div> */}
                    </div>
                  )}
                </div>

                {/* accounts section */}
                <>
                  <div className="dash-section" id="accounts">
                    <div
                      className="dash-section-title"
                      onClick={() => toggleOptions("accounts")}
                    >
                      Accounts
                    </div>
                    {optionsVisibility.accounts && (
                      <div className="options">
                        <Link className="nav-link" to="addAccount">
                          <div className="option">
                            <span className="icon">+</span> Add
                          </div>
                        </Link>
                        <Link className="nav-link" to="showAccounts">
                          <div className="option">
                            <span className="icon"></span> show accounts
                          </div>
                        </Link>
                        <Link className="nav-link" to="SearchAccounts">
                          <div className="option">
                            <span className="icon">+</span> Search
                          </div>
                        </Link>

                        {/* <Link className="nav-link" to='updateAccounts'>
                  <div className="option">
                    <span className="icon">✎</span> Update
                  </div>
                  </Link>


                  <Link className="nav-link" to='deleteAccounts'>
                  <div className="option">
                    <span className="icon">✖</span> Delete
                  </div>
                  </Link> */}
                      </div>
                    )}
                  </div>
                </>
              </div>
              <div className="main-content">
                <div className="card">
                  <div className="card-body">
                    <h5 className="card-title">Welcome Back</h5>
                    <p className="card-text">{userContext.userData.name}</p>
                  </div>
                </div>
                <Outlet></Outlet>
              </div>
            </div>
          </div>
          <Footer />
        </div>
      </>
    );
  }
}
