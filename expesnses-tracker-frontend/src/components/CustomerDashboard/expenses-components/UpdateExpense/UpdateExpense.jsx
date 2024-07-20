import { useFormik } from "formik";
import React, { useEffect, useState } from "react";
import { useLocation, useParams } from "react-router-dom";
import * as Yup from "yup";
import api from "../../../api";
import config from "../../../config";
import toast, { Toaster } from "react-hot-toast";
import Loading from "../../../basics/Loading/loading";
import { UserDataContext } from "./../../../basics/UserContextProvider/UserContextProvider";

export default function UpdateExpense() {
  const { refNo } = useParams();
  const location = useLocation();
  const [loading, setLoading] = useState(true);
  const [isLoading, setisLoading] = useState(false);
  const { ExpenseData } = location.state;
  const [Expense, setExpense] = useState(ExpenseData);
  const [addedExpenses, setaddedExpenses] = useState([]);
  const [removedExpenses, setremovedExpenses] = useState([]);
  const [subCategoryExpenses, setsubCategoryExpenses] = useState([]);
  const [nosubCategoryExpenses, setnosubCategoryExpenses] = useState([]);
  const userContext = React.useContext(UserDataContext);
  const userData = userContext.userData;

  const fetchExpenseData = async (refNo) => {
    // Fetch Expense data using refNo
    try {
      const response = await api.get(`${config.api}/expenses/refNo/${refNo}`);
      setExpense(response.data.data.data); // Set Expense data obtained from API
    } catch (error) {
      console.error("Error fetching Expense data:", error);
    }
  };
  useEffect(() => {
    if (ExpenseData === null) {
      if (refNo) {
        fetchExpenseData(refNo);
      }
    } else {
      setExpense(ExpenseData);
    }
    setLoading(false);
  }, [ExpenseData, refNo]);

  function handleExpenseSubmit(values) {
    console.log(values);
    setisLoading(true);
    api
      .put(`${config.api}/expenses/${values.refNo}`, values)
      .then((resp) => {
        console.log(resp);
        toast.success("updated Successfully");
        setExpense(resp.data.data);
        setisLoading(false);
      })
      .catch((err) => {
        console.error(err);
        toast.error("error can't update");
        setisLoading(false);
      });
  }

  function getsubCategoryExpenses(subCategoryRef, page, per_page) {
    api
      .get(
        `${config.api}/sub-categories/${subCategoryRef}/expenses?page=${page}&per_page=${per_page}`
      )
      .then((resp) => {
        toast.success("Expense Expenses retrived");
        setsubCategoryExpenses(resp.data.data);
      })
      .catch((err) => {
        console.error(err);
        setsubCategoryExpenses([]);
        toast.error("something went wrong");
      });
  }
  function getnosubCategoryExpenses(page, per_page) {
    api
      .get(
        `${config.api}/expenses/noSubCategory?page=${page}&per_page=${per_page}`
      )
      .then((resp) => {
        toast.success("all Expenses retrived");
        setnosubCategoryExpenses(resp.data.data.content);
      })
      .catch((err) => {
        console.error(err);
        setnosubCategoryExpenses([]);
        toast.error("something went wrong");
      });
  }
  useEffect(() => {
    if (ExpenseData !== null) {
      setLoading(false);
      getnosubCategoryExpenses(1, 100);
      getsubCategoryExpenses(Expense.subCategoryRefNo, 1, 100);
    }
  }, []);

  const validScheme = Yup.object().shape({
    name: Yup.string().required().min(3),
    amount: Yup.number().required().min(1),
    customerId: Yup.number().required(),
    subCategoryRefNo: Yup.string().required(),
  });
  const formik = useFormik({
    initialValues: {
      refNo: Expense.refNo,
      name: Expense.name,
      details: Expense.details,
      amount: Expense.amount,
      customerId: userData.customerId,
      subCategoryRefNo: Expense.subCategoryRefNo,
    },
    validationSchema: validScheme,
    onSubmit: handleExpenseSubmit,
  });

  // Function to handle selecting/deselecting a subCategory
  const handleAddExpenseselection = (subCategoryId) => {
    console.log(subCategoryId);
    setaddedExpenses((prevSelectedExpenses) => {
      if (prevSelectedExpenses.includes(subCategoryId)) {
        return prevSelectedExpenses.filter((id) => id !== subCategoryId);
      } else {
        return [...prevSelectedExpenses, subCategoryId];
      }
    });
    console.log(addedExpenses);
  };

  const handleRemoveExpenseselection = (subCategoryId) => {
    console.log(subCategoryId);
    setremovedExpenses((prevSelectedExpenses) => {
      console.log(prevSelectedExpenses);
      if (prevSelectedExpenses.includes(subCategoryId)) {
        return prevSelectedExpenses.filter((id) => id !== subCategoryId);
      } else {
        return [...prevSelectedExpenses, subCategoryId];
      }
    });
  };

  const handleAddToExpense = async () => {
    setisLoading(true);

    const data = {
      associationRefNos: addedExpenses,
    };

    try {
      await api.put(
        `${config.api}/association/sub-categories/${Expense.subCategoryRefNo}/add-expenses`,
        data
      );
      toast.success("Expenses added to SubCategory successfully");

      // Fetch and update the Expense and uncategorized Expenses after adding
      getsubCategoryExpenses(Expense.subCategoryRefNo, 1, 100);
      getnosubCategoryExpenses(1, 100);
    } catch (error) {
      console.error(error);
      toast.error("Error occurred while adding Expenses to Expense");
    }

    setisLoading(false);
    setaddedExpenses([]);
  };

  const handleRemoveToExpense = async () => {
    const data = {
      associationRefNos: removedExpenses,
    };

    setisLoading(true);

    try {
      await api.put(
        `${config.api}/association/sub-categories/${Expense.subCategoryRefNo}/remove-expenses`,
        data
      );
      toast.success("Expenses removed from SubCategory successfully");

      // Fetch and update the Expense and uncategorized Expenses after removing
      getsubCategoryExpenses(Expense.subCategoryRefNo, 1, 100);
      getnosubCategoryExpenses(1, 100);
    } catch (error) {
      console.error(error);
      toast.error("Error occurred while removing Expenses from Expense");
    }

    setisLoading(false);
    setremovedExpenses([]);
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
            placeholder="Expense refNo"
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
            placeholder="Expense name"
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
            placeholder="Expense details"
            required
          />

          {formik.errors.details && formik.touched.details ? (
            <div className="alert alert-danger">{formik.errors.details}</div>
          ) : null}

          <input
            type="text"
            name="amount"
            id="amount"
            value={formik.values.amount}
            onChange={formik.handleChange}
            onBlur={formik.handleBlur}
            placeholder="Expense amount"
            required
          />

          {formik.errors.amount && formik.touched.amount ? (
            <div className="alert alert-danger">{formik.errors.amount}</div>
          ) : null}
          <input
            type="text"
            name="customerId"
            id="customerId"
            value={formik.values.customerId}
            onChange={formik.handleChange}
            onBlur={formik.handleBlur}
            placeholder="Expense customerId"
            disabled
          />

          {formik.errors.customerId && formik.touched.customerId ? (
            <div className="alert alert-danger">{formik.errors.customerId}</div>
          ) : null}
          <input
            type="text"
            name="subCategoryRefNo"
            id="subCategoryRefNo"
            value={formik.values.subCategoryRefNo}
            onChange={formik.handleChange}
            onBlur={formik.handleBlur}
            placeholder="Expense subCategoryRefNo"
            disabled
          />

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
              update Expense
            </button>
          )}
          <Toaster />
        </form>
        <br />

        <div>
          <div className="Expense-box">
            <h2>Add Expense To SubCategory</h2>
            <ul>
              {nosubCategoryExpenses?.map((subCategory, index) => (
                <li key={subCategory.refNo}>
                  <label htmlFor={`subCategory${index}`}>
                    {subCategory.name}
                  </label>
                  <input
                    name={`subCategory${index}`}
                    type="checkbox"
                    checked={addedExpenses.includes(subCategory.refNo)}
                    onChange={() =>
                      handleAddExpenseselection(subCategory.refNo)
                    }
                  />
                </li>
              ))}
            </ul>
            <button
              className="btn btn-secondary"
              disabled={addedExpenses.length === 0}
              onClick={handleAddToExpense}
            >
              Add to SubCategory
            </button>
          </div>

          <br />

          <div className="Expense-box">
            <h2>Remove Expenses from SubCategory</h2>
            <ul>
              {Array.isArray(subCategoryExpenses) &&
                subCategoryExpenses.length > 0 &&
                subCategoryExpenses.map((subCategory, index) => (
                  <li key={subCategory.refNo}>
                    <label htmlFor={`subCategoryToRemove${index}`}>
                      {subCategory.name}
                    </label>
                    <input
                      name={`subCategoryToRemove${index}`}
                      type="checkbox"
                      checked={removedExpenses.includes(subCategory.refNo)}
                      onChange={() =>
                        handleRemoveExpenseselection(subCategory.refNo)
                      }
                    />
                  </li>
                ))}
            </ul>
            <button
              className="btn btn-danger"
              disabled={removedExpenses.length === 0}
              onClick={handleRemoveToExpense}
            >
              Remove from SubCategory
            </button>
          </div>
        </div>
      </>
    );
  }
}
