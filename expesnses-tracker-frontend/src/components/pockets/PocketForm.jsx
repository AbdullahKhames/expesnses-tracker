import React, { useContext, useEffect, useState } from "react";
import { Formik, Form, Field, ErrorMessage, useFormik } from "formik";
import * as Yup from "yup";
import toast, { Toaster } from "react-hot-toast";
import { useNavigate } from "react-router-dom";
import config from "../config";
import api from "../api";
import { UserDataContext } from "../basics/UserContextProvider/UserContextProvider";

export default function PocketForm( { type, accountRefNo, pockets, setPockets } ) {
  const userContext = useContext(UserDataContext);
  const userData = userContext.userData;
  const [isLoading, setIsLoading] = useState(false);
  const [errorMessage, seterrorMessage] = useState("");
  const nav = useNavigate();
  useEffect(() => {
    if (!userData) {
      nav("/login");
    }
  }, [userData]);
  function handlePocketSubmit(values) {
    setIsLoading(true);

    api
      .post(`${config.api}/pockets`, values)
      .then((response) => {
        if (response.status === 200) {
          toast.success("Pocket added successfully");
          setPockets([...pockets, response.data.data]);
        }
        setIsLoading(false);
      })
      .catch((err) => {
        if (err.response.status === 401) {
          localStorage.removeItem("access_token");
          localStorage.removeItem("refresh_token");
          localStorage.removeItem("deviceId");
          nav("/login");
          window.location.reload();
        }
        setIsLoading(false);
        toast.error("error saving Pocket");
        console.error(err);
        seterrorMessage(`${JSON.stringify(err.response.data)}`);
      });
  }
  const validScheme = Yup.object({
    name: Yup.string().required("Required"),
    amount: Yup.number().required("Required"),
    pocketType: Yup.string().required("Required"),
  });
  const formik = useFormik({
    initialValues: {
      name: "",
      details: "",
      amount: "",
      pocketType: type,
      customerId: userData.customerId, 
      accountRefNo: accountRefNo,
    },
    validationSchema: validScheme,
    onSubmit: handlePocketSubmit,
  });
  return (
    <>
      <div>
        <h1>Add a New {type} Pocket</h1>
        {errorMessage.length > 0 ? (
          <div className="alert alert-danger">{errorMessage}</div>
        ) : null}

        <form onSubmit={formik.handleSubmit}>
          <label htmlFor="name">Pocket Name:</label>
          <input
            type="text"
            name="name"
            id="name"
            placeholder="Pocket name"
            value={formik.values.name}
            onChange={formik.handleChange}
            onBlur={formik.handleBlur}
            required
          />
          {formik.errors.name && formik.touched.name ? (
            <div className="alert alert-danger">{formik.errors.name}</div>
          ) : null}

          <label htmlFor="details">Pocket Details:</label>
          <input
            type="text"
            name="details"
            id="details"
            placeholder="Pocket details"
            value={formik.values.details}
            onChange={formik.handleChange}
            onBlur={formik.handleBlur}
          />
          {formik.errors.details && formik.touched.details ? (
            <div className="alert alert-danger">{formik.errors.details}</div>
          ) : null}

          <label htmlFor="amount">Amount:</label>
          <input
            type="number"
            name="amount"
            id="amount"
            placeholder="Pocket amount"
            value={formik.values.amount}
            onChange={formik.handleChange}
            onBlur={formik.handleBlur}
            required
          />
          {formik.errors.amount && formik.touched.amount ? (
            <div className="alert alert-danger">{formik.errors.amount}</div>
          ) : null}

          <label htmlFor="pocketType">Pocket Type:</label>
          <input
            type="text"
            name="pocketType"
            id="pocketType"
            placeholder="Pocket type"
            value={formik.values.pocketType}
            onChange={formik.handleChange}
            onBlur={formik.handleBlur}
            readOnly // Make the field read-only
            // You can set the value dynamically based on your application's logic
            // For example: value="SERVICE"
            // Or if you have the pocketType value in your formik values, you can use that directly
            // value={formik.values.pocketType}
          />
          {formik.errors.pocketType && formik.touched.pocketType ? (
            <div className="alert alert-danger">{formik.errors.pocketType}</div>
          ) : null}

          <label htmlFor="customerId">Customer ID:</label>
          <input
            type="text"
            name="customerId"
            id="customerId"
            placeholder="Customer ID"
            value={formik.values.customerId}
            onChange={formik.handleChange}
            onBlur={formik.handleBlur}
            readOnly // Make the field read-only
            // You can set the value dynamically based on your application's logic
            // For example: value={customerId}
            // Or if you have the customerId value in your formik values, you can use that directly
            // value={formik.values.customerId}
          />
          {formik.errors.customerId && formik.touched.customerId ? (
            <div className="alert alert-danger">{formik.errors.customerId}</div>
          ) : null}

          <label htmlFor="accountRefNo">Account Reference Number:</label>
          <input
            type="text"
            name="accountRefNo"
            id="accountRefNo"
            placeholder="Account Reference Number"
            value={formik.values.accountRefNo}
            onChange={formik.handleChange}
            onBlur={formik.handleBlur}
            readOnly // Make the field read-only
            // You can set the value dynamically based on your application's logic
            // For example: value={accountRefNo}
            // Or if you have the accountRefNo value in your formik values, you can use that directly
            // value={formik.values.accountRefNo}
          />
          {formik.errors.accountRefNo && formik.touched.accountRefNo ? (
            <div className="alert alert-danger">
              {formik.errors.accountRefNo}
            </div>
          ) : null}

          {isLoading ? (
            <button type="button" className="register-button">
              <i className="fas fa-spinner fa-spin"></i>
            </button>
          ) : (
            <button
              className="btn btn-primary btn-lg btn-block"
              disabled={!formik.dirty && formik.isValid}
              type="submit"
              onSubmit={handlePocketSubmit}
            >
              Create Pocket
            </button>
          )}
          <Toaster />
        </form>
      </div>
    </>
  );
};
