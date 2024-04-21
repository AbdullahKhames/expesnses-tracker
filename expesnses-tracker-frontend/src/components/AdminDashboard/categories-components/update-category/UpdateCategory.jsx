import { useFormik } from "formik";
import React, { useEffect, useState } from "react";
import { useLocation, useParams } from "react-router-dom";
import * as Yup from "yup";
import api from "./../../../api";
import config from "./../../../config";
import toast, { Toaster } from "react-hot-toast";
import "./updateCategory.css";
import Loading from "./../../../basics/Loading/loading";

export default function UpdateCategory() {
  const { refNo } = useParams();
  const location = useLocation();
  const [loading, setLoading] = useState(true);
  const [isLoading, setisLoading] = useState(false);
  const { categoryData } = location.state;
  const [category, setCategory] = useState(categoryData);
  const [addedSubCategories, setaddedSubCategories] = useState([]);
  const [removedSubCategories, setremovedSubCategories] = useState([]);
  const [categorySubCategories, setcategorySubCategories] = useState([]);
  const [noCategorySubCategories, setnoCategorySubCategories] = useState([]);

  const fetchCategoryData = async (refNo) => {
    // Fetch category data using refNo
    try {
      const response = await api.get(`${config.api}/categories/refNo/${refNo}`);
      setCategory(response.data.data.data); // Set category data obtained from API
    } catch (error) {
      console.error("Error fetching category data:", error);
    }
  };
  useEffect(() => {
    if (categoryData === null) {
      if (refNo) {
        fetchCategoryData(refNo);
      }
    } else {
      setCategory(categoryData);
    }
    setLoading(false);
  }, [categoryData, refNo]);

  function handleCategorySubmit(values) {
    console.log(values);
    setisLoading(true);
    api
      .put(`${config.api}/categories/${values.refNo}`, values)
      .then((resp) => {
        toast.success("updated Successfully");
        setCategory(resp.data.data.data);
        setisLoading(false);
      })
      .catch((err) => {
        console.error(err);
        toast.error("error can't update");
        setisLoading(false);
      });
  }
  const validScheme = Yup.object().shape({
    name: Yup.string().required().min(3),
  });

  function getCategorySubCategories(category_id, page, per_page) {
    api
      .get(
        `${config.api}/categories/${category_id}/subCategories?page=${page}&per_page=${per_page}`
      )
      .then((resp) => {
        toast.success("category SubCategories retrived");
        setcategorySubCategories(resp.data.data.content);
      })
      .catch((err) => {
        console.error(err);
        toast.error("something went wrong");
      });
  }
  function getnoCategorySubCategories(page, per_page) {
    api
      .get(
        `${config.api}/sub-categories/noCategory?page=${page}&per_page=${per_page}`
      )
      .then((resp) => {
        toast.success("all SubCategories retrived");
        setnoCategorySubCategories(resp.data.data.content);
      })
      .catch((err) => {
        console.error(err);
        toast.error("something went wrong");
      });
  }
  useEffect(() => {
    if (categoryData !== null) {
      setLoading(false);
      getnoCategorySubCategories(1, 100);
      getCategorySubCategories(category.refNo, 1, 100);
    }
  }, []);
  const formik = useFormik({
    initialValues: {
      refNo: category.refNo,
      name: category.name,
      details: category.details,
    },
    validationSchema: validScheme,
    onSubmit: handleCategorySubmit,
  });

  // Function to handle selecting/deselecting a subCategory
  const handleAddSubCategorieselection = (subCategoryId) => {
    console.log(subCategoryId);
    setaddedSubCategories((prevSelectedSubCategories) => {
      if (prevSelectedSubCategories.includes(subCategoryId)) {
        return prevSelectedSubCategories.filter((id) => id !== subCategoryId);
      } else {
        return [...prevSelectedSubCategories, subCategoryId];
      }
    });
    console.log(addedSubCategories);
  };

  const handleRemoveSubCategorieselection = (subCategoryId) => {
    console.log(subCategoryId);
    setremovedSubCategories((prevSelectedSubCategories) => {
      console.log(prevSelectedSubCategories);
      if (prevSelectedSubCategories.includes(subCategoryId)) {
        return prevSelectedSubCategories.filter((id) => id !== subCategoryId);
      } else {
        return [...prevSelectedSubCategories, subCategoryId];
      }
    });
  };

  const handleAddToCategory = async () => {
    setisLoading(true);

    const data = {
      associationRefNos: addedSubCategories,
    };

    try {
      await api.put(
        `${config.api}/association/categories/${category.refNo}/add-sub-categories`,
        data
      );
      toast.success("SubCategories added to category successfully");

      // Fetch and update the category and uncategorized SubCategories after adding
      getCategorySubCategories(category.refNo, 1, 100);
      getnoCategorySubCategories(1, 100);
    } catch (error) {
      console.error(error);
      toast.error("Error occurred while adding SubCategories to category");
    }

    setisLoading(false);
    setaddedSubCategories([]);
  };

  const handleRemoveToCategory = async () => {
    const data = {
      associationRefNos: removedSubCategories,
    };

    setisLoading(true);

    try {
      await api.put(
        `${config.api}/association/categories/${category.refNo}/remove-sub-categories`,
        data
      );
      toast.success("SubCategories removed from category successfully");

      // Fetch and update the category and uncategorized SubCategories after removing
      getCategorySubCategories(category.refNo, 1, 100);
      getnoCategorySubCategories(1, 100);
    } catch (error) {
      console.error(error);
      toast.error("Error occurred while removing SubCategories from category");
    }

    setisLoading(false);
    setremovedSubCategories([]);
  };
  if (loading) {
    return <Loading />;
  } else {
    return (
      <>
        <form onSubmit={formik.handleSubmit}>
          <input
            type="text"
            name="refNo"
            id="refNo"
            value={formik.values.refNo}
            placeholder="category refNo"
            disabled
            required
          />
          <input
            type="text"
            name="name"
            id="name"
            value={formik.values.name}
            onChange={formik.handleChange}
            onBlur={formik.handleBlur}
            placeholder="category name"
            required
          />

          {formik.errors.name && formik.touched.name ? (
            <div className="alert alert-danger">{formik.errors.name}</div>
          ) : null}
          <input
            type="text"
            name="details"
            id="details"
            value={formik.values.details}
            onChange={formik.handleChange}
            onBlur={formik.handleBlur}
            placeholder="category details"
            required
          />

          {formik.errors.details && formik.touched.details ? (
            <div className="alert alert-danger">{formik.errors.details}</div>
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
              onSubmit={handleCategorySubmit}
            >
              update category
            </button>
          )}
          <Toaster />
        </form>
        <br />

        <div>
          <div className="category-box">
            <h2>Add Category To SubCategories</h2>
            <ul>
              {noCategorySubCategories.map((subCategory, index) => (
                <li key={subCategory.refNo}>
                  <label htmlFor={`subCategory${index}`}>
                    {subCategory.name}
                  </label>
                  <input
                    name={`subCategory${index}`}
                    type="checkbox"
                    checked={addedSubCategories.includes(subCategory.refNo)}
                    onChange={() =>
                      handleAddSubCategorieselection(subCategory.refNo)
                    }
                  />
                </li>
              ))}
            </ul>
            <button
              className="btn btn-secondary"
              disabled={addedSubCategories.length === 0}
              onClick={handleAddToCategory}
            >
              Add to Category
            </button>
          </div>

          <br />

          <div className="category-box">
            <h2>Remove SubCategories from Category</h2>
            <ul>
              {categorySubCategories.map((subCategory, index) => (
                <li key={subCategory.refNo}>
                  <label htmlFor={`subCategoryToRemove${index}`}>
                    {subCategory.name}
                  </label>
                  <input
                    name={`subCategoryToRemove${index}`}
                    type="checkbox"
                    checked={removedSubCategories.includes(subCategory.refNo)}
                    onChange={() =>
                      handleRemoveSubCategorieselection(subCategory.refNo)
                    }
                  />
                </li>
              ))}
            </ul>
            <button
              className="btn btn-danger"
              disabled={removedSubCategories.length === 0}
              onClick={handleRemoveToCategory}
            >
              Remove from Category
            </button>
          </div>
        </div>
      </>
    );
  }
}
