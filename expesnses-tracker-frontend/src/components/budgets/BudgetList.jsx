import React, { useState, useEffect, useContext } from "react";
import toast from "react-hot-toast";
import { MDBCol, MDBContainer, MDBRow } from "mdb-react-ui-kit";
import { UserDataContext } from "../basics/UserContextProvider/UserContextProvider";
import config from "../config";
import api from "../api";
import Loading from "../basics/Loading/loading";
import BudgetsCard from "./BudgetsCard";
import BudgetForm from "./BudgetForm";

function BudgetList({ filter = null, query = null }) {
  const userContext = useContext(UserDataContext);
  const userData = userContext.userData;
  const [budgets, setBudgets] = useState([]);
  const [loading, setLoading] = useState(true);
  const budgetTypes = [
    "ENTERTAINMENT",
    "SAVINGS",
    "BILLS",
    "ALLOWANCE",
    "MOM",
    "MISC",
    "DONATION",
    "EXTERNAL",
  ];

  let url_api = config.api + "/";
  if (query) {
    url_api += "budgets/name/" + query + "?page=1&per_page=20";
  } else if (filter === null) {
    url_api += "customers/budgets?page=1&per_page=20";
  } else {
    url_api +=
      "customers/accounts/" + filter.refNo + "/budgets?page=1&per_page=20";
  }

  useEffect(() => {
    api
      .get(url_api)
      .then((response) => {
        console.log(response);
        if (response.status === 200) {
          setBudgets(response.data.data.content);
          if (query) {
            if (response.data.data.length === 0) {
              toast.error("No budgets found");
            } else if (response.data.message) {
              toast.success(response.data.message);
            }
          }
          setLoading(false);
        }
      })
      .catch((error) => {
        console.error(error);
      });
  }, []);

  if (loading) {
    return <Loading />;
  } else {
    return (
      <>
        {filter && (
          <div>
            customer account balance :{" "}
            {budgets.reduce((prev, curr) => {
              if (curr.amount < 0) {
                return prev;
              }
              return prev + curr.amount;
            }, 0)}
          </div>
        )}
        <MDBContainer className="py-4">
          <MDBRow>
            {budgets.map((budget, index) => {
              if (budget) {
                return (
                  <MDBCol key={index}>
                    <BudgetsCard
                      budgets={budgets}
                      setBudgets={setBudgets}
                      key={budget.refNo}
                      budgetData={budget}
                      budgetTypes={budgetTypes}
                      admin={userData && userData.roles.includes("ROLE_ADMIN")}
                    />
                  </MDBCol>
                );
              }
              return null;
            })}
          </MDBRow>
        </MDBContainer>
        {filter && (
          <MDBContainer className="py-4">
            <MDBRow style={{ paddingLeft: "60px", paddingRight: "40px" }}>
              {budgetTypes.map((type) => {
                // const budgetOfType = budgets.find((budget) => {
                //   return budget.budgetType === type;
                // });
                // if (!budgetOfType) {
                return (
                  <MDBCol col="6" sm="4" key={type}>
                    <div style={{ border: "1px solid #ccc", padding: "20px" }}>
                      <BudgetForm
                        type={type}
                        accountRefNo={filter.refNo}
                        budgets={budgets}
                        setBudgets={setBudgets}
                      />
                    </div>
                  </MDBCol>
                );
                // }
                // return null;
              })}
            </MDBRow>
          </MDBContainer>
        )}
      </>
    );
  }
}

export default BudgetList;
