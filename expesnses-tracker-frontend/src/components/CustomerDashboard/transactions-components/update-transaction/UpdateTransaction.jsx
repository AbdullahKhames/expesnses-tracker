import { useFormik } from 'formik';
import React, { useEffect, useState } from 'react'
import { useLocation, useParams } from 'react-router-dom'
import * as Yup from 'yup';
import api from '../../../api';
import config from '../../../config';
import toast, { Toaster } from 'react-hot-toast';
import './updateTransaction.css'
import Loading from '../../../basics/Loading/loading';

export default function UpdateTransaction() {
  const { refNo } = useParams(); 
  const location = useLocation()
  const [loading, setLoading] = useState(true)
  const [isLoading, setisLoading] = useState(false)
  const { TransactionData } = location.state;
  const [Transaction, setTransaction] = useState(TransactionData);
  const [addedBudgets, setaddedBudgets] = useState([]);
  const [removedBudgets, setremovedBudgets] = useState([]);
  const [TransactionsBudgets, setTransactionBudgets] = useState([])
  const [noTransactionsBudgets, setnoTransactionBudgets] = useState([])

  const fetchTransactionsData = async (refNo) => {
    // Fetch Transactions data using refNo
    try {
      const response = await api.get(`${config.api}/Transactions/refNo/${refNo}`);
      setTransaction(response.data.data.data); // Set Transactions data obtained from API
    } catch (error) {
      console.error("Error fetching Transactions data:", error);
    }
  };
  useEffect(() => {
    if (TransactionData === null) {
      if (refNo) {
        fetchTransactionsData(refNo);
      }
    } else {
      setTransaction(TransactionData);
    }
      setLoading(false);
  }, [TransactionData, refNo]);
  
  function handleTransactionsSubmit(values){
    console.log(values);
    setisLoading(true)
    api.put(`${config.api}/Transactions/${values.refNo}`, values)
    .then((resp) => {
      toast.success("updated Successfully");
      setTransaction(resp.data.data)
      setisLoading(false)
    }).catch((err) => {
      console.error(err);
      toast.error("error can't update");
      setisLoading(false)
    })
  }
  const validScheme = Yup.object().shape({
    name: Yup.string().required().min(3)
  });

  function getTransactionsBudgets(Transactions_id, page, per_page) {
    api.get(`${config.api}/Transactions/${Transactions_id}/budgets?page=${page}&per_page=${per_page}`)
    .then((resp) => {
      console.log(resp);
      toast.success("Transactions Budgets retrived");
      setTransactionBudgets(resp.data.data);
    }).catch((err)=> {
      console.error(err);
      toast.error("something went wrong");
    })
  }
  function getnoTransactionsBudgets(page, per_page) {
    api.get(`${config.api}/budgets/noTransaction?page=${page}&per_page=${per_page}`)
    .then((resp) => {
      console.log(resp);
      toast.success("all Budgets retrived");
      setnoTransactionBudgets(resp.data.data.content);
    }).catch((err)=> {
      console.error(err);
      toast.error("something went wrong");
    })
  }
  useEffect(() => {
    if (TransactionData !== null) {
      setLoading(false);
      getnoTransactionsBudgets(1, 100);
      getTransactionsBudgets(Transaction.refNo, 1, 100);
    }
  }, [])
  const formik = useFormik({
    initialValues: {
      refNo: Transaction.refNo,
      name: Transaction.name,
      details: Transaction.details,
    },
    validationSchema: validScheme,
    onSubmit: handleTransactionsSubmit
  })


  // Function to handle selecting/deselecting a subTransactions
  const handleAddBudgetselection = (subTransactionsId) => {

    setaddedBudgets((prevSelectedBudgets) => {
      if (prevSelectedBudgets.includes(subTransactionsId)) {
        return prevSelectedBudgets.filter((id) => id !== subTransactionsId);
      } else {
        return [...prevSelectedBudgets, subTransactionsId];
      }
    });
  };

  const handleRemoveBudgetselection = (subTransactionsId) => {
    console.log(subTransactionsId);
    setremovedBudgets((prevSelectedBudgets) => {
      console.log(prevSelectedBudgets);
      if (prevSelectedBudgets.includes(subTransactionsId)) {
        return prevSelectedBudgets.filter((id) => id !== subTransactionsId);
      } else {
        return [...prevSelectedBudgets, subTransactionsId];
      }
    });
  };

  const handleAddToTransactions = async () => {
    setisLoading(true);
  
    const data = {
      'associationRefNos': addedBudgets
    };
  
    try {
      await api.put(`${config.api}/association/Transactions/${Transaction.refNo}/add-budgets`, data);
      toast.success("Budgets added to Transactions successfully");
      
      // Fetch and update the Transactions and uncategorized Budgets after adding
      getTransactionsBudgets(Transaction.refNo, 1, 100);
      getnoTransactionsBudgets(1, 100);
    } catch (error) {
      console.error(error);
      toast.error("Error occurred while adding Budgets to Transactions");
    }
  
    setisLoading(false);
    setaddedBudgets([]);
  };
  
  const handleRemoveToTransactions = async () => {
    const data = {
      'associationRefNos': removedBudgets
    };
  
    setisLoading(true);
  
    try {
      await api.put(`${config.api}/association/Transactions/${Transaction.refNo}/remove-budgets`, data);
      toast.success("Budgets removed from Transactions successfully");
      
      // Fetch and update the Transactions and uncategorized Budgets after removing
      getTransactionsBudgets(Transaction.refNo, 1, 100);
      getnoTransactionsBudgets(1, 100);
    } catch (error) {
      console.error(error);
      toast.error("Error occurred while removing Budgets from Transactions");
    }
  
    setisLoading(false);
    setremovedBudgets([]);
  };
  if (loading) {
    return <Loading/>
  } else {
    return <>
      <form onSubmit={formik.handleSubmit}>
        <input 
        type="text"
        name="refNo"
        id="refNo"
        value={formik.values.refNo}
        placeholder='Transactions refNo'
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
        placeholder='Transactions name'
        required
         />
         
        {formik.errors.name && formik.touched.name ? 
        <div className="alert alert-danger">{formik.errors.name}</div>:
        null}
        <input 
        type="text"
        name="details"
        id="details"
        value={formik.values.details}
        onChange={formik.handleChange}
        onBlur={formik.handleBlur}
        placeholder='Transactions details'
         />
         
        {formik.errors.details && formik.touched.details ? 
        <div className="alert alert-danger">{formik.errors.details}</div>:
        null}
        
        {isLoading ?  
        <button type='button' className='register-button'><i className='fas fa-spinner fa-spin'></i></button> :
        <button  className='btn btn-primary btn-lg btn-block' disabled={!formik.dirty && formik.isValid} type="submit" onSubmit={handleTransactionsSubmit} >update Transactions</button>}
        <Toaster/>
      </form>
      <br/>

      <div>
      <div className="Transactions-box">
        <h2>Add Transactions To Budgets</h2>
        <ul>
          {noTransactionsBudgets.map((subTransactions, index) => (
              <li key={subTransactions.refNo}>
              <label htmlFor={`subTransactions${index}`}>{subTransactions.name}</label>
                <input
                  name={`subTransactions${index}`}
                  type="checkbox"
                  checked={addedBudgets.includes(subTransactions.refNo)}
                  onChange={() => handleAddBudgetselection(subTransactions.refNo)}
                />
              </li>
            ))}
        </ul>
        <button className='btn btn-secondary' disabled={addedBudgets.length === 0} onClick={handleAddToTransactions}>Add to Transactions</button>
      </div>

      <br/>

      <div className="Transactions-box">
        <h2>Remove Budgets from Transactions</h2>
        <ul>
          {TransactionsBudgets.map((subTransactions, index) => (
              <li key={subTransactions.refNo}>
                <label htmlFor={`subTransactionsToRemove${index}`}>
                {subTransactions.name}
                </label>
                  <input
                    name={`subTransactionsToRemove${index}`}
                    type="checkbox"
                    checked={removedBudgets.includes(subTransactions.refNo)}
                    onChange={() => handleRemoveBudgetselection(subTransactions.refNo)}
                  />

              </li>
            ))}
        </ul>
        <button className='btn btn-danger' disabled={removedBudgets.length === 0} onClick={handleRemoveToTransactions}>Remove from Transactions</button>
      </div>
    </div>


    </>
  }
}
