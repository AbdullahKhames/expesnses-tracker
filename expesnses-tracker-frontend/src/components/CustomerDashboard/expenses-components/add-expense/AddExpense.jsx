import { useFormik } from "formik";
import React, { useState, useEffect } from "react";
import * as Yup from "yup";
import toast, { Toaster } from "react-hot-toast";
import { useNavigate } from "react-router-dom";
import api from "../../../api";
import config from "../../../config";
import DateTimePicker from "react-datetime-picker";
import { UserDataContext } from "./../../../basics/UserContextProvider/UserContextProvider";
import {parseDateTimeStr} from "./../../../Utils";

export default function AddExpense() {
  const [isLoading, setIsLoading] = useState(false);
  const [errorMessage, seterrorMessage] = useState("");
  const userContext = React.useContext(UserDataContext);
  const userData = userContext.userData;
  const [subCategories, setSubCategories] = useState([]);
  const nav = useNavigate();

  const [date, setDate] = useState(new Date());
  const handleDateChange = (date) => {
    formik.setFieldValue("createdAt", parseDateTimeStr(date));
    formik.handleChange("createdAt");
    setDate(date);
  };
  useEffect(() => {
    api
      .get(`${config.api}/customers/sub-categories?page=1&per_page=100`)
      .then((response) => {
        if (response.status === 200) {
          console.log(response.data.data);
          setSubCategories(response.data.data.content);
        }
        if (response.data.data.content.length === 0) {
          toast.error("No subcategories found");
          nav("subCategories");
        }
      })
      .catch((err) => {
        if (err.response.status === 401) {
          localStorage.removeItem("access_token");
          localStorage.removeItem("refresh_token");
          localStorage.removeItem("deviceId");
          window.location.reload();
        }
        console.error(err);
      });
  }, []);

  function handleExpenseSubmit(values) {
    setIsLoading(true);

    api
      .post(`${config.api}/expenses`, values)
      .then((response) => {
        if (response.status === 200) {
          toast.success("Expense added successfully");
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
        toast.error("error saving Expense");
        console.error(err);
        seterrorMessage(`${JSON.stringify(err.response.data)}`);
      });
    console.log(values);
  }
  const validScheme = Yup.object().shape({
    name: Yup.string().required().min(3),
    amount: Yup.number().required().min(1),
    customerId: Yup.number().required(),
    subCategoryRefNo: Yup.string().required(),
  });
  const formik = useFormik({
    initialValues: {
      name: "",
      details: "",
      amount: 0,
      createdAt: date,
      customerId: userData.customerId,
      subCategoryRefNo: "",
    },
    validationSchema: validScheme,
    onSubmit: handleExpenseSubmit,
  });
  return (
    <>
      <div>
        <h1>Add a New Expense</h1>
        {errorMessage.length > 0 ? (
          <div className="alert alert-danger">{errorMessage}</div>
        ) : null}

        <form onSubmit={formik.handleSubmit}>
          <label htmlFor="name">Expense Name:</label>
          <input
            type="text"
            name="name"
            id="name"
            placeholder="Expense name"
            value={formik.values.name}
            onChange={formik.handleChange}
            onBlur={formik.handleBlur}
            required
          />
          {formik.errors.name && formik.touched.name ? (
            <div className="alert alert-danger">{formik.errors.name}</div>
          ) : null}
          <label htmlFor="name">Expense details:</label>
          <input
            type="text"
            name="details"
            id="details"
            placeholder="Expense details"
            value={formik.values.details}
            onChange={formik.handleChange}
            onBlur={formik.handleBlur}
          />
          {formik.errors.name && formik.touched.details ? (
            <div className="alert alert-danger">{formik.errors.details}</div>
          ) : null}
          <label htmlFor="name">Expense amount:</label>
          <input
            type="text"
            name="amount"
            id="amount"
            placeholder="Expense amount"
            value={formik.values.amount}
            onChange={formik.handleChange}
            onBlur={formik.handleBlur}
          />
          {formik.errors.name && formik.touched.amount ? (
            <div className="alert alert-danger">{formik.errors.amount}</div>
          ) : null}
          <label htmlFor="name">Expense customerId:</label>
          <input
            type="text"
            name="customerId"
            id="customerId"
            placeholder="Expense customerId"
            value={formik.values.customerId}
            onChange={formik.handleChange}
            onBlur={formik.handleBlur}
            readOnly={true}
          />
          {formik.errors.name && formik.touched.customerId ? (
            <div className="alert alert-danger">{formik.errors.customerId}</div>
          ) : null}
          <label htmlFor="name">Expense date:</label>
          <div>
            <DateTimePicker onChange={handleDateChange} value={date} />
          </div>
          <br />
          <br />
          <br />
          <label htmlFor="subCategoryRefNo">Expense subCategoryRefNo:</label>
          <select
            name="subCategoryRefNo"
            id="subCategoryRefNo"
            value={formik.values.subCategoryRefNo}
            onChange={formik.handleChange}
            onBlur={formik.handleBlur}
          >
            <option value="">Select a subcategory</option>
            {subCategories.map((subCategory) => (
              <option key={subCategory.refNo} value={subCategory.refNo}>
                {subCategory.name}
              </option>
            ))}
          </select>
          {formik.errors.subCategoryRefNo && formik.touched.subCategoryRefNo ? (
            <div className="alert alert-danger">
              {formik.errors.subCategoryRefNo}
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
              onSubmit={handleExpenseSubmit}
            >
              create Expense
            </button>
          )}
          <Toaster />
        </form>
      </div>
    </>
  );
}
