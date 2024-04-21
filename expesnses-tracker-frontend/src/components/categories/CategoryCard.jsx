// CategoryCard.js

import React, { useEffect } from "react";
import { toast } from "react-hot-toast";
import { Toaster } from "react-hot-toast";
import { useState } from "react";
import { Link } from "react-router-dom";
import SubCategoryList from "./../subCategories/SubCategoryList";
import api from "./../api";
import config from "../config";

const CategoryCard = ({ category, index }) => {
  const [registered, setRegistered] = useState(
    category.currentCustomerRegistered
  );
  useEffect(() => {
    console.log(category);
    setRegistered(category.currentCustomerRegistered);
  }, [category]);

  function handleAddCategory(value) {
    console.log(value);
    const data = {
      associationRefNos: [value],
    };
    api
      .put(`${config.api}/customers/add-categories`, data)
      .then((response) => {
        console.log(response);
        toast.success(response.data.message);
        setRegistered(true);
      })
      .catch((err) => {
        toast.error(err);
      });
  }
  function handleRemoveCategory(value) {
    console.log(value);
    const data = {
      associationRefNos: [value],
    };
    api
      .put(`${config.api}/customers/remove-categories`, data)
      .then((response) => {
        console.log(response);
        toast.success(response.data.message);
        setRegistered(false);
      })
      .catch((err) => {
        toast.error(err);
      });
  }
  return (
    // <div className="card Category-card">
    //   <div className="card-body">
    <>
      {category.subCategories.length > 0 && (
        <>
          <Link
            key={index}
            to={"/categories/" + category.refNo}
            className="head-link"
          >
            <div className="SubHeading box">
              <h2>{category.name}</h2>
              <h4>total spent: {category.totalSpent}</h4>
            </div>
          </Link>
          <div className="container">
            {!registered ? (
              <>
                <button
                  className="btn btn-primary"
                  value={category.refNo}
                  onClick={(e) => handleAddCategory(e.target.value)}
                >
                  Register To This Category
                </button>
              </>
            ) : (
              <button
                className="btn btn-danger"
                value={category.refNo}
                onClick={(e) => handleRemoveCategory(e.target.value)}
              >
                un Register from This Category
              </button>
            )}
          </div>
          <SubCategoryList key={category.refNo} filter={category} />
        </>
      )}
      <Toaster />
    </>
    //   </div>
    // </div>
  );
};

export default CategoryCard;
