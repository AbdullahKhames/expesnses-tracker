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
    expenses: false,
    transactions: false,
    transfers: false,
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
                {/* expenses section ! */}
                <div className="dash-section" id="expenses">
                  <div
                    className="dash-section-title"
                    onClick={() => toggleOptions("expenses")}
                  >
                    expenses
                  </div>
                  {optionsVisibility.expenses && (
                    <div className="options">
                      <>
                        <Link className="nav-link" to="addExpense">
                          <div className="option">
                            <span className="icon">+</span> Add
                          </div>
                        </Link>

                        <Link className="nav-link" to="showExpenses">
                          <div className="option">
                            <span className="icon"></span> Show
                          </div>
                        </Link>
                        <Link className="nav-link" to="SearchExpense">
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
                <div className="dash-section" id="transactions">
                  <div
                    className="dash-section-title"
                    onClick={() => toggleOptions("transactions")}
                  >
                    transactions
                  </div>
                  {optionsVisibility.transactions && (
                    <div className="options">
                      <>
                        <Link className="nav-link" to="addTransaction">
                          <div className="option">
                            <span className="icon">+</span> Add
                          </div>
                        </Link>

                        <Link className="nav-link" to="showTransactions">
                          <div className="option">
                            <span className="icon"></span> Show
                          </div>
                        </Link>
                        <Link className="nav-link" to="SearchTransactions">
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

                {/* transfers section */}
                <>
                  <div className="dash-section" id="transfers">
                    <div
                      className="dash-section-title"
                      onClick={() => toggleOptions("transfers")}
                    >
                      transfers
                    </div>
                    {optionsVisibility.transfers && (
                      <div className="options">
                        <Link className="nav-link" to="addTransfer">
                          <div className="option">
                            <span className="icon">+</span> Add
                          </div>
                        </Link>
                        <Link className="nav-link" to="showTransfers">
                          <div className="option">
                            <span className="icon"></span> show transfers
                          </div>
                        </Link>
                        <Link className="nav-link" to="SearchTransfers">
                          <div className="option">
                            <span className="icon">+</span> Search
                          </div>
                        </Link>

                        {/* <Link className="nav-link" to='updatetransfers'>
                  <div className="option">
                    <span className="icon">✎</span> Update
                  </div>
                  </Link>


                  <Link className="nav-link" to='deletetransfers'>
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
