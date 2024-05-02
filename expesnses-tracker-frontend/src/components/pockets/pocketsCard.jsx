import React from "react";
import { Link, useParams } from "react-router-dom";
import { Card } from "react-bootstrap";
import toast from "react-hot-toast";
import { useContext, useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
// import deaultimage from "../../../assets/defaultImages/sybCategory.webp";
import { UserDataContext } from "./../basics/UserContextProvider/UserContextProvider";
import Loading from "./../basics/Loading/loading";
import config from "../config";
import api from "./../api";

function PocketsCard({ pocketData, admin, pockets, setPockets, pocketTypes }) {
  const { refNo } = useParams();
  const userContext = useContext(UserDataContext);
  const userData = userContext.userData;
  const navigate = useNavigate();
  const [pocket, setPocket] = useState(null);
  const [loading, setLoading] = useState(true);
  const url_api = config.api + "/pockets/refNo/" + refNo;
  const [registered, setRegistered] = useState(
    pocketData.currentCustomerRegistered
  );

  function handleAddpocket(value) {
    console.log(value);
    const data = {
      associationRefNos: [value],
    };
    api
      .put(`${config.api}/customers/add-pockets`, data)
      .then((response) => {
        console.log(response);
        toast.success(response.data.message);
        setRegistered(true);
      })
      .catch((err) => {
        toast.error(err);
      });
  }
  function handleRemovepocket(value) {
    console.log(value);
    const data = {
      associationRefNos: [value],
    };
    api
      .put(`${config.api}/customers/remove-pockets`, data)
      .then((response) => {
        console.log(response);
        toast.success(response.data.message);
        setRegistered(false);
        setPockets(pockets.filter((pocket) => pocket.refNo !== value));
      })
      .catch((err) => {
        toast.error(err);
      });
  }
  const getpocket = (url_api) => {
    if (pocketData === null || pocketData === undefined) {
      api
        .get(url_api)
        .then((res) => {
          console.log(res);
          if (res.status === 200) {
            setPocket(res.data.data);
            setLoading(false);
          }
        })
        .catch((err) => {
          console.error(err);
          navigate("/not-found");
        });
    } else {
      setPocket(pocketData);
      setLoading(false);
    }
  };

  useEffect(() => {
    if (pocketData) {
      setPocket(pocketData);
      setLoading(false);
    } else {
      getpocket(url_api);
    }
  }, [url_api]);

  function handlepocketDataPage(pocketData) {
    navigate(`/pockets/${pocketData.refNo}`, {
      state: { pocketData: pocket, pocketTypes:pocketTypes },
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
            <strong style={{ fontSize: "30px" }}>{pocket.name}</strong>
          </Card.Title>
          {/* <Card.Text as="div" className="d-flex justify-content-between"> */}
          <h6>pocket details: {pocket.details}</h6>
          <h6>pocket customer name: {pocket.customerName}</h6>
          <h6>pocket type: {pocket.pocketType}</h6>
          <h6>Ref No: {pocket.refNo}</h6>
          <p style={{ color: "#0C356A", paddingTop: "8px" }}>
            Current balance: {pocket.amount}
          </p>
          <h6>account name: {pocket.accountName}</h6>
          <h6>account Ref No: {pocket.accountRefNo}</h6>

          <div>
            <button
              className="btn btn-outline-success mb basic"
              type="submit"
              onClick={() => handlepocketDataPage(pocket)}
            >
              More Details
            </button>
          </div>
          <div>
            <button
              className="btn btn-primary"
              value={pocket.refNo}
              onClick={(e) => handleCreateATransaction(e.target.value)}
            >
              create a transaction
            </button>
          </div>
          <div>
            <button
              className="btn btn-primary"
              value={pocket.refNo}
              onClick={(e) => handleMoneyTransfer(e.target.value)}
            >
              make a transfer
            </button>
          </div>
          {!registered ? (
            <>
              <button
                className="btn btn-primary"
                value={pocket.refNo}
                onClick={(e) => handleAddpocket(e.target.value)}
              >
                Register To This pocket
              </button>
            </>
          ) : (
            <button
              className="btn btn-danger"
              value={pocket.refNo}
              onClick={(e) => handleRemovepocket(e.target.value)}
            >
              Delete This pocket
            </button>
          )}
          {/* </Card.Text> */}
        </Card.Body>
      </Card>
    );
  }
}

export default PocketsCard;
