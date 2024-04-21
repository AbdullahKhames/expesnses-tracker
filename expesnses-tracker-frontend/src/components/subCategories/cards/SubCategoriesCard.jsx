import React from "react";
import { Link, useParams } from "react-router-dom";
import { Card } from "react-bootstrap";
import toast from "react-hot-toast";
import { useContext, useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import "./SubCategoriesCard.css";
import deaultimage from "../../../assets/defaultImages/sybCategory.webp";
import { UserDataContext } from "./../../basics/UserContextProvider/UserContextProvider";
import Loading from "./../../basics/Loading/loading";
import config from "../../config";
import api from "./../../api";

function SubCategoriesCard({ subCategorie, admin }) {
  const { refNo } = useParams();
  const userContext = useContext(UserDataContext);
  const userData = userContext.userData;
  const navigate = useNavigate();
  const [subCategory, setSubCategory] = useState(null);
  const [loading, setLoading] = useState(true);
  const url_api = config.api + "/sub-categories/refNo/" + refNo;
  const [registered, setRegistered] = useState(
    subCategorie.currentCustomerRegistered
  );
  useEffect(() => {
    console.log(subCategorie);
    setRegistered(subCategorie.currentCustomerRegistered);
  }, [subCategorie]);

  function handleAddSubCategory(value) {
    console.log(value);
    const data = {
      associationRefNos: [value],
    };
    api
      .put(`${config.api}/customers/add-sub-categories`, data)
      .then((response) => {
        console.log(response);
        toast.success(response.data.message);
        setRegistered(true);
      })
      .catch((err) => {
        toast.error(err);
      });
  }
  function handleRemoveSubCategory(value) {
    console.log(value);
    const data = {
      associationRefNos: [value],
    };
    api
      .put(`${config.api}/customers/remove-sub-categories`, data)
      .then((response) => {
        console.log(response);
        toast.success(response.data.message);
        setRegistered(false);
      })
      .catch((err) => {
        toast.error(err);
      });
  }
  const getSubCategory = (url_api) => {
    if (subCategorie === null || subCategorie === undefined) {
      api
        .get(url_api)
        .then((res) => {
          console.log(res);
          if (res.status === 200) {
            setSubCategory(res.data.data);
            setLoading(false);
          }
        })
        .catch((err) => {
          console.error(err);
          navigate("/not-found");
        });
    } else {
      setSubCategory(subCategorie);
      setLoading(false);
    }
  };

  useEffect(() => {
    if (subCategory) {
      window.location.reload();
    } else {
      getSubCategory(url_api);
    }
  }, [url_api]);

  function handlesubCategoriePage(subCategorie) {
    navigate(`/subCategories/${subCategorie.refNo}`, {
      state: { subCategorieData: subCategorie },
    });
  }

  if (loading) {
    return <Loading />;
  } else {
    return (
      <Card className="my-3 p-3 rounded">
        <Link to={`/subCategories/${subCategorie.refNo}`}>
          <Card.Img
            src={
              subCategorie.image
                ? `${config.baseURL}/images/${subCategorie.image}`
                : deaultimage
            }
            alt="subCategorie"
            variant="top"
            className="card-img"
          />
        </Link>
        <Card.Body>
          <Card.Title as="div" className="card-head">
            <Link
              to={`/subCategories/${subCategorie.refNo}`}
              className="link-style"
            >
              <strong style={{ fontSize: "30px" }}>{subCategorie.name}</strong>
            </Link>
          </Card.Title>
          <Card.Text as="div" className="d-flex justify-content-between">
            <p style={{ color: "#0C356A", paddingTop: "8px" }}>
              Total spent: {subCategorie.totalSpent}
            </p>
            <div>
              <button
                className="btn btn-outline-success mb basic"
                type="submit"
                onClick={() => handlesubCategoriePage(subCategorie)}
              >
                More Details
              </button>
            </div>
            {!registered ? (
              <>
                <button
                  className="btn btn-primary"
                  value={subCategorie.refNo}
                  onClick={(e) => handleAddSubCategory(e.target.value)}
                >
                  Register To This SubCategory
                </button>
              </>
            ) : (
              <button
                className="btn btn-danger"
                value={subCategorie.refNo}
                onClick={(e) => handleRemoveSubCategory(e.target.value)}
              >
                un Register from This SubCategory
              </button>
            )}
          </Card.Text>
        </Card.Body>
      </Card>
    );
  }
}

export default SubCategoriesCard;
