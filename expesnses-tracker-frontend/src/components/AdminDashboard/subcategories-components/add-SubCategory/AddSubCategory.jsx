import { useFormik } from "formik";
import React, { useState } from "react";
import * as Yup from "yup";
import toast, { Toaster } from "react-hot-toast";
import { useNavigate } from "react-router-dom";
import api from "../../../api";
import config from "../../../config";
import { useEffect } from "react";
export default function AddSubCategory() {
  const [isLoading, setIsLoading] = useState(false);
  const [errorMessage, seterrorMessage] = useState("");
  const [Categories, setCategories] = useState([]);
  const nav = useNavigate();
  useEffect(() => {
    api
      .get(`${config.api}/categories?page=1&per_page=100`)
      .then((resp) => {
        console.log(resp);
        setCategories(resp.data.data.content);
        toast.success(resp.data.message);
        setIsLoading(false);
      })
      .catch((err) => {
        console.log(err);
      });
  }, []);
  const handleCategoryChange = (event) => {
    const categoryId = event.target.value;
    formik.setFieldValue("categoryRefNo", categoryId);
    formik.setFieldTouched("categoryRefNo", true);
  };

  function handleSubCategorySubmit(values) {
    setIsLoading(true);

    api
      .post(`${config.api}/sub-categories`, values)
      .then((response) => {
        if (response.status === 200) {
          toast.success("SubCategory added successfully");
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
        toast.error("error saving SubCategory");
        console.error(err);
        seterrorMessage(`${JSON.stringify(err.response.data)}`);
      });
  }
  const validScheme = Yup.object().shape({
    name: Yup.string().required().min(3),
    categoryRefNo: Yup.string().required().min(3),
  });
  const formik = useFormik({
    initialValues: {
      name: "",
      details: "",
      categoryRefNo: "",
    },
    validationSchema: validScheme,
    onSubmit: handleSubCategorySubmit,
  });
  return (
    <>
      <div>
        <h1>Add a New SubCategory</h1>
        {errorMessage.length > 0 ? (
          <div className="alert alert-danger">{errorMessage}</div>
        ) : null}

        <form onSubmit={formik.handleSubmit}>
          <label htmlFor="name">SubCategory Name:</label>
          <input
            type="text"
            name="name"
            id="name"
            placeholder="SubCategory name"
            value={formik.values.name}
            onChange={formik.handleChange}
            onBlur={formik.handleBlur}
            required
          />
          {formik.errors.name && formik.touched.name ? (
            <div className="alert alert-danger">{formik.errors.name}</div>
          ) : null}
          <label htmlFor="name">SubCategory details:</label>
          <input
            type="text"
            name="details"
            id="details"
            placeholder="SubCategory details"
            value={formik.values.details}
            onChange={formik.handleChange}
            onBlur={formik.handleBlur}
          />
          {formik.errors.name && formik.touched.details ? (
            <div className="alert alert-danger">{formik.errors.details}</div>
          ) : null}
          <div>
            <label htmlFor="categoryRefNo">Category:</label>
            <select
              id="categoryRefNo"
              name="categoryRefNo"
              value={formik.values.categoryRefNo}
              onChange={handleCategoryChange}
              onBlur={formik.handleBlur}
            >
              <option value="" label="Select a category" />
              {Categories.map((category) => (
                <option key={category.refNo} value={category.refNo}>
                  {category.name}
                </option>
              ))}
            </select>
            {formik.errors.category_id && formik.touched.category_id ? (
              <div className="alert alert-danger">
                {formik.errors.category_id}
              </div>
            ) : null}
          </div>
          {isLoading ? (
            <button type="button" className="register-button">
              <i className="fas fa-spinner fa-spin"></i>
            </button>
          ) : (
            <button
              className="btn btn-primary btn-lg btn-block"
              disabled={!formik.dirty && formik.isValid}
              type="submit"
              onSubmit={handleSubCategorySubmit}
            >
              create SubCategory
            </button>
          )}
          <Toaster />
        </form>
      </div>
    </>
  );
}
