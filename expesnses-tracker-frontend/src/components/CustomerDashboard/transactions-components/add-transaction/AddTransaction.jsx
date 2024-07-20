import { useFormik } from "formik";
import React, { useContext, useState, useEffect } from "react";
import * as Yup from "yup";
import toast, { Toaster } from "react-hot-toast";
import { useNavigate } from "react-router-dom";
import api from "../../../api";
import config from "../../../config";
import { UserDataContext } from "../../../basics/UserContextProvider/UserContextProvider";
import "./AddTransactionForm.css"; // Import the CSS file
import { parseDateTimeStr } from "./../../../Utils";
import DateTimePicker from "react-datetime-picker";

export default function AddTransaction() {
  const userContext = useContext(UserDataContext);
  const userData = userContext.userData;
  const [isLoading, setIsLoading] = useState(false);
  const [errorMessage, seterrorMessage] = useState("");
  const [customerBudgets, setCustomerBudgets] = useState([]);
  const [usedBudgetRefNumbers, setUsedBudgetRefNumbers] = useState([]);

  const [customerSubCategories, setCustomerSubCategories] = useState([]);
  const [date, setDate] = useState(new Date());
  const handleDateChange = (date) => {
    formik.setFieldValue("expense.createdAt", parseDateTimeStr(date));
    formik.handleChange("expense.createdAt");
    setDate(date);
  };

  async function fetchCustomerBudgets() {
    try {
      const response = await api.get(`${config.api}/customers/budgets?page=1&per_page=100`);
      if (response.status === 200) {
        setCustomerBudgets(response.data.data.content);
        toast.success(response.data.message);
      }
    } catch (error) {
      console.error(error);
    }
  }
  async function fetchCustomerSubCategories() {
    try {
      const response = await api.get(`${config.api}/customers/sub-categories?page=1&per_page=100`);
      if (response.status === 200) {
        setCustomerSubCategories(response.data.data.content);
        toast.success(response.data.message);
      }
    } catch (error) {
      console.error(error);
    }
  }

  useEffect(() => {
    fetchCustomerBudgets();
    fetchCustomerSubCategories();
  }, []);
  const nav = useNavigate();
  function handleTransactionSubmit(values) {
    setIsLoading(true);

    api
      .post(`${config.api}/transactions`, values)
      .then((response) => {
        if (response.status === 200 && response.data.code === 801) {
          toast.success(response.data.message);
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
        toast.error("error saving Transaction");
        console.error(err);
        seterrorMessage(`${JSON.stringify(err.response.data)}`);
      });
  }
  // Yup validation schema for BudgetAmountReqDto
  const budgetAmountReqDtoSchema = Yup.object().shape({
    budgetRefNo: Yup.string().required().trim(),
    amount: Yup.number().required().min(0),
  });

  // Yup validation schema for ExpenseReqDto
  const expenseReqDtoSchema = Yup.object().shape({
    name: Yup.string().required().trim(),
    details: Yup.string().trim(),
    amount: Yup.number().required().min(1),
    createdAt: Yup.string().required().min(1),
    customerId: Yup.number().required(),
    subCategoryRefNo: Yup.string().min(1).required().trim(),
  });

  // Yup validation schema for TransactionReqDto
  const transactionReqDtoSchema = Yup.object().shape({
    name: Yup.string().required().min(3).trim(),
    details: Yup.string().trim(),
    budgetAmountReqDtos: Yup.array()
      .of(budgetAmountReqDtoSchema)
      .min(1, "At least one budget amount is required"),
    expense: expenseReqDtoSchema,
  });

  // Formik object
  const formik = useFormik({
    initialValues: {
      name: "",
      details: "",
      budgetAmountReqDtos: [{ budgetRefNo: "", amount: 0 }],
      expense: {
        name: "",
        details: "",
        amount: 0,
        createdAt: date,
        customerId: userData.customerId,
        subCategoryRefNo: "",
      },
    },
    validationSchema: transactionReqDtoSchema,
    onSubmit: handleTransactionSubmit, // Assuming handleTransactionSubmit is defined elsewhere
  });
  // Function to add a new budget amount input field
  const addBudgetAmount = () => {
    formik.setValues({
      ...formik.values,
      budgetAmountReqDtos: [
        ...formik.values.budgetAmountReqDtos,
        { budgetRefNo: "", amount: 0 },
      ],
    });
  };

  const handleBudgetRefNoChange = (index, value) => {
    // Remove the previously selected budget reference number from used list
    const prevRefNo = formik.values.budgetAmountReqDtos[index].budgetRefNo;
    setUsedBudgetRefNumbers((prevNumbers) =>
      prevNumbers.filter((number) => number !== prevRefNo)
    );

    // Update formik values
    formik.setFieldValue(`budgetAmountReqDtos[${index}].budgetRefNo`, value);

    // Add the selected budget reference number to the used list
    setUsedBudgetRefNumbers((prevNumbers) => [...prevNumbers, value]);
  };
  // Function to remove a budget amount
  const removeBudgetAmount = (index) => {
    // Get the removed budget reference number
    const removedRefNo = formik.values.budgetAmountReqDtos[index].budgetRefNo;

    // Update formik values and remove the budget amount reqDto
    const updatedBudgetAmountReqDtos = formik.values.budgetAmountReqDtos.filter(
      (_, i) => i !== index
    );
    formik.setFieldValue("budgetAmountReqDtos", updatedBudgetAmountReqDtos);

    // Allow the removed budget reference number to be used again
    setUsedBudgetRefNumbers((prevNumbers) =>
      prevNumbers.filter((number) => number !== removedRefNo)
    );
  };
  return (
    <>
      <div className="form-container">
        <h1>Add a New Transaction</h1>
        {errorMessage.length > 0 ? (
          <div className="error-message">{errorMessage}</div>
        ) : null}

        <form onSubmit={formik.handleSubmit}>
          <label htmlFor="name">Transaction Name:</label>
          <input
            type="text"
            name="name"
            id="name"
            placeholder="Transaction name"
            value={formik.values.name}
            onChange={formik.handleChange}
            onBlur={formik.handleBlur}
            required
          />
          {formik.errors.name && formik.touched.name ? (
            <div className="alert alert-danger">{formik.errors.name}</div>
          ) : null}
          <label htmlFor="name">Transaction details:</label>
          <input
            type="text"
            name="details"
            id="details"
            placeholder="Transaction details"
            value={formik.values.details}
            onChange={formik.handleChange}
            onBlur={formik.handleBlur}
          />
          {formik.errors.name && formik.touched.details ? (
            <div className="alert alert-danger">{formik.errors.details}</div>
          ) : null}
          <div className="box">
            <h2>Budget Amounts</h2>
            <label>Budget Amounts:</label>
            {formik.values.budgetAmountReqDtos.map((budget, index) => (
              <div key={index}>
                <label htmlFor={`budgetRefNo_${index}`}>Budget Ref No:</label>
                <select
                  name={`budgetAmountReqDtos[${index}].budgetRefNo`}
                  id={`budgetRefNo_${index}`}
                  value={formik.values.budgetAmountReqDtos[index].budgetRefNo}
                  onChange={(e) =>
                    handleBudgetRefNoChange(index, e.target.value)
                  }
                  onBlur={formik.handleBlur}
                  required
                >
                  <option value="">Select Budget Ref No</option>
                  {customerBudgets.map(
                    (customerBudget) =>
                      // Check if the budget reference number is not already used
                      (!usedBudgetRefNumbers.includes(customerBudget.refNo) ||
                        formik.values.budgetAmountReqDtos[index].budgetRefNo ===
                          customerBudget.refNo) && (
                        <option
                          key={customerBudget.refNo}
                          value={customerBudget.refNo}
                        >
                          {customerBudget.name +
                            " {" +
                            customerBudget.details +
                            "}"}
                        </option>
                      )
                  )}
                </select>
                {/* Error message for budgetRefNo */}
                {formik.errors.budgetAmountReqDtos &&
                formik.errors.budgetAmountReqDtos[index] &&
                formik.touched.budgetAmountReqDtos &&
                formik.touched.budgetAmountReqDtos[index] ? (
                  <div className="alert alert-danger">
                    {formik.errors.budgetAmountReqDtos[index].budgetRefNo}
                  </div>
                ) : null}
                <label htmlFor={`amount_${index}`}>Amount:</label>
                <input
                  type="number"
                  name={`budgetAmountReqDtos[${index}].amount`}
                  id={`amount_${index}`}
                  placeholder="Amount"
                  value={formik.values.budgetAmountReqDtos[index].amount}
                  onChange={formik.handleChange}
                  onBlur={formik.handleBlur}
                  required
                />
                {/* Error message for amount */}
                {formik.errors.budgetAmountReqDtos &&
                formik.errors.budgetAmountReqDtos[index] &&
                formik.touched.budgetAmountReqDtos &&
                formik.touched.budgetAmountReqDtos[index] ? (
                  <div className="alert alert-danger">
                    {formik.errors.budgetAmountReqDtos[index].amount}
                  </div>
                ) : null}
                {/* Button to remove budget amount */}
                {index >= 0 && (
                  <button
                    type="button"
                    className="btn btn-danger"
                    onClick={() => removeBudgetAmount(index)}
                  >
                    Remove Budget Amount
                  </button>
                )}
              </div>
            ))}

            {/* Button to add budget amount */}
            <br />
            <button
              type="button"
              className="button add-button"
              onClick={addBudgetAmount}
            >
              Add Budget Amount
            </button>
          </div>
          <br />
          <div className="box">
            <label htmlFor="expense.name">Expense Name:</label>
            <input
              type="text"
              name="expense.name"
              id="expense.name"
              placeholder="Expense name"
              value={formik.values.expense.name}
              onChange={formik.handleChange}
              onBlur={formik.handleBlur}
              required
            />
            {formik.errors.expense &&
            formik.errors.expense.name &&
            formik.touched.expense &&
            formik.touched.expense.name ? (
              <div className="alert alert-danger">
                {formik.errors.expense.name}
              </div>
            ) : null}
            <label htmlFor="expense.details">Expense Details:</label>
            <input
              type="text"
              name="expense.details"
              id="expense.details"
              placeholder="Expense details"
              value={formik.values.expense.details}
              onChange={formik.handleChange}
              onBlur={formik.handleBlur}
            />
            {formik.errors.expense &&
            formik.errors.expense.details &&
            formik.touched.expense &&
            formik.touched.expense.details ? (
              <div className="alert alert-danger">
                {formik.errors.expense.details}
              </div>
            ) : null}
            <label htmlFor="expense.amount">Expense Amount:</label>
            <input
              type="number"
              name="expense.amount"
              id="expense.amount"
              placeholder="Expense amount"
              value={formik.values.expense.amount}
              onChange={formik.handleChange}
              onBlur={formik.handleBlur}
              required
            />
            {formik.errors.expense &&
            formik.errors.expense.amount &&
            formik.touched.expense &&
            formik.touched.expense.amount ? (
              <div className="alert alert-danger">
                {formik.errors.expense.amount}
              </div>
            ) : null}
            <label htmlFor="name">Expense date:</label>
            <div>
              <DateTimePicker onChange={handleDateChange} value={date} />
            </div>
            <br />
            <br />
            <br />
            <select
              type="text"
              name="expense.subCategoryRefNo"
              id="expense.subCategoryRefNo"
              placeholder="subCategoryRefNo"
              value={formik.values.expense.subCategoryRefNo}
              onChange={formik.handleChange}
              onBlur={formik.handleBlur}
              required
            >
              <option value="">Select Expense Ref No</option>
              {customerSubCategories.map((customerSubCategory) => (
                <option
                  key={customerSubCategory.refNo}
                  value={customerSubCategory.refNo}
                >
                  {customerSubCategory.name}
                </option>
              ))}
            </select>

            {formik.errors.expense &&
            formik.errors.expense.amount &&
            formik.touched.expense &&
            formik.touched.expense.amount ? (
              <div className="alert alert-danger">
                {formik.errors.expense.amount}
              </div>
            ) : null}
          </div>
          <button
            type="button"
            className="btn btn-primary btn-lg btn-block"
            onClick={() => {
              console.log(formik.values);
              console.log(formik.errors);
            }}
          >
            log stuff
          </button>
          {isLoading ? (
            <button type="button" className="register-button">
              <i className="fas fa-spinner fa-spin"></i>
            </button>
          ) : (
            <button
              className="btn btn-primary btn-lg btn-block"
              disabled={!formik.dirty && formik.isValid}
              type="submit"
              onSubmit={handleTransactionSubmit}
            >
              create Transaction
            </button>
          )}
          <Toaster />
        </form>
      </div>
    </>
  );
}
