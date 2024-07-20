import React from "react";
import { Link, useParams } from "react-router-dom";
import { Card } from "react-bootstrap";
import toast from "react-hot-toast";
import { useContext, useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
// import deaultimage from "../../../assets/defaultImages/sybCategory.webp";
import { UserDataContext } from "../basics/UserContextProvider/UserContextProvider";
import Loading from "../basics/Loading/loading";
import config from "../config";
import api from "../api";

function BudgetsCard({ budgetData, admin, budgets, setBudgets, budgetTypes }) {
  const { refNo } = useParams();
  const userContext = useContext(UserDataContext);
  const userData = userContext.userData;
  const navigate = useNavigate();
  const [budget, setBudget] = useState(null);
  const [loading, setLoading] = useState(true);
  const url_api = config.api + "/budgets/refNo/" + refNo;
  const [registered, setRegistered] = useState(
    budgetData.currentCustomerRegistered
  );

  function handleAddbudget(value) {
    console.log(value);
    const data = {
      associationRefNos: [value],
    };
    api
      .put(`${config.api}/customers/add-budgets`, data)
      .then((response) => {
        console.log(response);
        toast.success(response.data.message);
        setRegistered(true);
      })
      .catch((err) => {
        toast.error(err);
      });
  }
  function handleRemovebudget(value) {
    console.log(value);
    const data = {
      associationRefNos: [value],
    };
    api
      .put(`${config.api}/customers/remove-budgets`, data)
      .then((response) => {
        console.log(response);
        toast.success(response.data.message);
        setRegistered(false);
        setBudgets(budgets.filter((budget) => budget.refNo !== value));
      })
      .catch((err) => {
        toast.error(err);
      });
  }
  const getbudget = (url_api) => {
    if (budgetData === null || budgetData === undefined) {
      api
        .get(url_api)
        .then((res) => {
          console.log(res);
          if (res.status === 200) {
            setBudget(res.data.data);
            setLoading(false);
          }
        })
        .catch((err) => {
          console.error(err);
          navigate("/not-found");
        });
    } else {
      setBudget(budgetData);
      setLoading(false);
    }
  };

  useEffect(() => {
    if (budgetData) {
      setBudget(budgetData);
      setLoading(false);
    } else {
      getbudget(url_api);
    }
  }, [url_api]);

  function handlebudgetDataPage(budgetData) {
    navigate(`/budgets/${budgetData.refNo}`, {
      state: { budgetData: budget, budgetTypes:budgetTypes },
    });
  }

  function handleCreateATransaction(value) {}
  function handleMoneyTransfer(value) {}
  if (loading) {
    return <Loading />;
  } else {
    return (
      <Card className="my-3 p-3 rounded">
        <Card.Body>
          <Card.Title as="div" className="card-head">
            <strong style={{ fontSize: "30px" }}>{budget.name}</strong>
          </Card.Title>
          {/* <Card.Text as="div" className="d-flex justify-content-between"> */}
          <h6>budget details: {budget.details}</h6>
          <h6>budget customer name: {budget.customerName}</h6>
          <h6>budget type: {budget.budgetType}</h6>
          <h6>Ref No: {budget.refNo}</h6>
          <p style={{ color: "#0C356A", paddingTop: "8px" }}>
            Current balance: {budget.amount}
          </p>
          <h6>account name: {budget.accountName}</h6>
          <h6>account Ref No: {budget.accountRefNo}</h6>

          <div>
            <button
              className="btn btn-outline-success mb basic"
              type="submit"
              onClick={() => handlebudgetDataPage(budget)}
            >
              More Details
            </button>
          </div>
          <div>
            <button
              className="btn btn-primary"
              value={budget.refNo}
              onClick={(e) => handleCreateATransaction(e.target.value)}
            >
              create a transaction
            </button>
          </div>
          <div>
            <button
              className="btn btn-primary"
              value={budget.refNo}
              onClick={(e) => handleMoneyTransfer(e.target.value)}
            >
              make a transfer
            </button>
          </div>
          {!registered ? (
            <>
              <button
                className="btn btn-primary"
                value={budget.refNo}
                onClick={(e) => handleAddbudget(e.target.value)}
              >
                Register To This budget
              </button>
            </>
          ) : (
            <button
              className="btn btn-danger"
              value={budget.refNo}
              onClick={(e) => handleRemovebudget(e.target.value)}
            >
              Delete This budget
            </button>
          )}
          {/* </Card.Text> */}
        </Card.Body>
      </Card>
    );
  }
}

export default BudgetsCard;
