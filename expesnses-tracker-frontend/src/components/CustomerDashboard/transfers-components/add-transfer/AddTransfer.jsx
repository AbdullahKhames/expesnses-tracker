import { useFormik } from "formik";
import React, { useContext, useState, useEffect } from "react";
import * as Yup from "yup";
import toast, { Toaster } from "react-hot-toast";
import { useNavigate } from "react-router-dom";
import api from "../../../api";
import config from "../../../config";
import { UserDataContext } from "../../../basics/UserContextProvider/UserContextProvider";
import { parseDateTimeStr } from "./../../../Utils";
import DateTimePicker from "react-datetime-picker";

export default function AddTransfer() {
  const userContext = useContext(UserDataContext);
  const userData = userContext.userData;
  const [isLoading, setIsLoading] = useState(false);
  const [errorMessage, seterrorMessage] = useState("");
  const [customerBudgets, setCustomerBudgets] = useState([]);
  const [usedBudgetRefNumbers, setUsedBudgetRefNumbers] = useState([]);

  const [date, setDate] = useState(new Date());
  const handleDateChange = (date) => {
    formik.setFieldValue("createdAt", parseDateTimeStr(date));
    formik.handleChange("createdAt");
    setDate(date);
  };

  async function fetchCustomerBudgets() {
    try {
      const response = await api.get(
        `${config.api}/customers/budgets?page=1&per_page=100`
      );
      if (response.status === 200) {
        setCustomerBudgets(response.data.data.content);
        toast.success(response.data.message);
      }
    } catch (error) {
      console.error(error);
    }
  }

  useEffect(() => {
    fetchCustomerBudgets();
  }, []);
  const nav = useNavigate();
  function handleTransactionSubmit(values) {
    setIsLoading(true);

    api
      .post(`${config.api}/budget-transfers`, values)
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

  // Yup validation schema for TransactionReqDto
  const transactionReqDtoSchema = Yup.object().shape({
    name: Yup.string().required().min(3).trim(),
    details: Yup.string().trim(),
    createdAt: Yup.string().required().trim(),
    senderBudgetAmountReqDto: budgetAmountReqDtoSchema,
    receiverBudgetAmountsReqDtos: Yup.array()
      .of(budgetAmountReqDtoSchema)
      .min(1, "At least one receiver budget amount is required"),
  });

  // Formik object
  const formik = useFormik({
    initialValues: {
      name: "",
      details: "",
      createdAt: parseDateTimeStr(date),
      senderBudgetAmountReqDto: { budgetRefNo: "", amount: 0 },
      receiverBudgetAmountsReqDtos: [{ budgetRefNo: "", amount: 0 }],
    },
    validationSchema: transactionReqDtoSchema,
    onSubmit: handleTransactionSubmit, // Assuming handleTransactionSubmit is defined elsewhere
  });
  // Function to add a new budget amount input field
  const addBudgetAmount = () => {
    formik.setValues({
      ...formik.values,
      receiverBudgetAmountsReqDtos: [
        ...formik.values.receiverBudgetAmountsReqDtos,
        { budgetRefNo: "", amount: 0 },
      ],
    });
  };

  const handleBudgetRefNoChange = (index, value) => {
    // Remove the previously selected budget reference number from used list
    const prevRefNo =
      formik.values.receiverBudgetAmountsReqDtos[index].budgetRefNo;
    setUsedBudgetRefNumbers((prevNumbers) =>
      prevNumbers.filter((number) => number !== prevRefNo)
    );

    // Update formik values
    formik.setFieldValue(
      `receiverBudgetAmountsReqDtos[${index}].budgetRefNo`,
      value
    );

    // Add the selected budget reference number to the used list
    setUsedBudgetRefNumbers((prevNumbers) => [...prevNumbers, value]);
  };
  // Function to remove a budget amount
  const removeBudgetAmount = (index) => {
    // Get the removed budget reference number
    const removedRefNo =
      formik.values.receiverBudgetAmountsReqDtos[index].budgetRefNo;

    // Update formik values and remove the budget amount reqDto
    const updatedBudgetAmountReqDtos =
      formik.values.receiverBudgetAmountsReqDtos.filter((_, i) => i !== index);
    formik.setFieldValue(
      "receiverBudgetAmountsReqDtos",
      updatedBudgetAmountReqDtos
    );

    // Allow the removed budget reference number to be used again
    setUsedBudgetRefNumbers((prevNumbers) =>
      prevNumbers.filter((number) => number !== removedRefNo)
    );
  };
  return (
    <>
      <div className="form-container">
        <h1>Add a New Transfer</h1>
        {errorMessage.length > 0 ? (
          <div className="error-message">{errorMessage}</div>
        ) : null}

        <form onSubmit={formik.handleSubmit}>
          <label htmlFor="name">Transfer Name:</label>
          <input
            type="text"
            name="name"
            id="name"
            placeholder="Transfer name"
            value={formik.values.name}
            onChange={formik.handleChange}
            onBlur={formik.handleBlur}
            required
          />
          {formik.errors.name && formik.touched.name ? (
            <div className="alert alert-danger">{formik.errors.name}</div>
          ) : null}
          <label htmlFor="name">Transfer details:</label>
          <input
            type="text"
            name="details"
            id="details"
            placeholder="Transfer details"
            value={formik.values.details}
            onChange={formik.handleChange}
            onBlur={formik.handleBlur}
          />
          {formik.errors.name && formik.touched.details ? (
            <div className="alert alert-danger">{formik.errors.details}</div>
          ) : null}
          <label htmlFor="name">Transfer date:</label>
          <div>
            <DateTimePicker onChange={handleDateChange} value={date} />
          </div>
          <br />
          <br />
          <br />

          <div className="box">
            <label htmlFor={`senderBudgetAmountReqDto`}>
              senderBudgetAmountReqDto Budget Ref No:
            </label>
            <select
              name={`senderBudgetAmountReqDto.budgetRefNo`}
              id={`senderBudgetAmountReqDto.budgetRefNo}`}
              value={formik.values.senderBudgetAmountReqDto.budgetRefNo}
              onChange={(e) => {
                const value = e.target.value;
                const prevRefNo =
                  formik.values.senderBudgetAmountReqDto.budgetRefNo;
                setUsedBudgetRefNumbers((prevNumbers) =>
                  prevNumbers.filter((number) => number !== prevRefNo)
                );

                // Update formik values
                formik.setFieldValue(
                  `senderBudgetAmountReqDto.budgetRefNo`,
                  value
                );

                // Add the selected budget reference number to the used list
                setUsedBudgetRefNumbers((prevNumbers) => [
                  ...prevNumbers,
                  value,
                ]);
                formik.handleChange("senderBudgetAmountReqDto.budgetRefNo");
              }}
              onBlur={formik.handleBlur}
              required
            >
              <option value="">Select Budget Ref No</option>
              {customerBudgets.map(
                (customerBudget) =>
                  // Check if the budget reference number is not already used
                  (!usedBudgetRefNumbers.includes(customerBudget.refNo) ||
                    formik.values.senderBudgetAmountReqDto.budgetRefNo ===
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
            {formik.errors.receiverBudgetAmountsReqDtos &&
            formik.errors.senderBudgetAmountReqDto &&
            formik.touched.receiverBudgetAmountsReqDtos &&
            formik.touched.senderBudgetAmountReqDto ? (
              <div className="alert alert-danger">
                {formik.errors.senderBudgetAmountReqDto.budgetRefNo}
              </div>
            ) : null}
            <label htmlFor={`amount`}>Amount:</label>
            <input
              type="number"
              name={`senderBudgetAmountReqDto.amount`}
              id={`senderBudgetAmountReqDto.amount`}
              placeholder="Amount"
              value={formik.values.senderBudgetAmountReqDto.amount}
              onChange={formik.handleChange}
              onBlur={formik.handleBlur}
              required
            />
            {/* Error message for amount */}
            {formik.errors.receiverBudgetAmountsReqDtos &&
            formik.errors.senderBudgetAmountReqDto &&
            formik.touched.receiverBudgetAmountsReqDtos &&
            formik.touched.senderBudgetAmountReqDto ? (
              <div className="alert alert-danger">
                {formik.errors.senderBudgetAmountReqDto.amount}
              </div>
            ) : null}
          </div>
          <div className="box">
            <h2> Receivers Budget Amounts</h2>
            <label>Receivers Budget Amounts:</label>
            {formik.values.receiverBudgetAmountsReqDtos.map((budget, index) => (
              <div key={index}>
                <label htmlFor={`budgetRefNo_${index}`}>Budget Ref No:</label>
                <select
                  name={`receiverBudgetAmountsReqDtos[${index}].budgetRefNo`}
                  id={`budgetRefNo_${index}`}
                  value={
                    formik.values.receiverBudgetAmountsReqDtos[index]
                      .budgetRefNo
                  }
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
                        formik.values.receiverBudgetAmountsReqDtos[index]
                          .budgetRefNo === customerBudget.refNo) && (
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
                {formik.errors.receiverBudgetAmountsReqDtos &&
                formik.errors.receiverBudgetAmountsReqDtos[index] &&
                formik.touched.receiverBudgetAmountsReqDtos &&
                formik.touched.receiverBudgetAmountsReqDtos[index] ? (
                  <div className="alert alert-danger">
                    {
                      formik.errors.receiverBudgetAmountsReqDtos[index]
                        .budgetRefNo
                    }
                  </div>
                ) : null}
                <label htmlFor={`amount_${index}`}>Amount:</label>
                <input
                  type="number"
                  name={`receiverBudgetAmountsReqDtos[${index}].amount`}
                  id={`amount_${index}`}
                  placeholder="Amount"
                  value={
                    formik.values.receiverBudgetAmountsReqDtos[index].amount
                  }
                  onChange={formik.handleChange}
                  onBlur={formik.handleBlur}
                  required
                />
                {/* Error message for amount */}
                {formik.errors.receiverBudgetAmountsReqDtos &&
                formik.errors.receiverBudgetAmountsReqDtos[index] &&
                formik.touched.receiverBudgetAmountsReqDtos &&
                formik.touched.receiverBudgetAmountsReqDtos[index] ? (
                  <div className="alert alert-danger">
                    {formik.errors.receiverBudgetAmountsReqDtos[index].amount}
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
