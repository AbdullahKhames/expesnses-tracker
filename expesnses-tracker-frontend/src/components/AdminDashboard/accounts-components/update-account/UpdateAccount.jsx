import { useFormik } from 'formik';
import React, { useEffect, useState } from 'react'
import { useLocation, useParams } from 'react-router-dom'
import * as Yup from 'yup';
import api from '../../../api';
import config from '../../../config';
import toast, { Toaster } from 'react-hot-toast';
import './updateAccount.css'
import Loading from '../../../basics/Loading/loading';

export default function UpdateAccount() {
  const { refNo } = useParams(); 
  const location = useLocation()
  const [loading, setLoading] = useState(true)
  const [isLoading, setisLoading] = useState(false)
  const { accountData } = location.state;
  const [account, setaccount] = useState(accountData);
  const [addedBudgets, setaddedBudgets] = useState([]);
  const [removedBudgets, setremovedBudgets] = useState([]);
  const [accountsBudgets, setaccountBudgets] = useState([])
  const [noaccountsBudgets, setnoaccountBudgets] = useState([])

  const fetchaccountsData = async (refNo) => {
    // Fetch accounts data using refNo
    try {
      const response = await api.get(`${config.api}/accounts/refNo/${refNo}`);
      setaccount(response.data.data.data); // Set accounts data obtained from API
    } catch (error) {
      console.error("Error fetching accounts data:", error);
    }
  };
  useEffect(() => {
    if (accountData === null) {
      if (refNo) {
        fetchaccountsData(refNo);
      }
    } else {
      setaccount(accountData);
    }
      setLoading(false);
  }, [accountData, refNo]);
  
  function handleaccountsSubmit(values){
    console.log(values);
    setisLoading(true)
    api.put(`${config.api}/accounts/${values.refNo}`, values)
    .then((resp) => {
      toast.success("updated Successfully");
      setaccount(resp.data.data)
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

  function getaccountsBudgets(accounts_id, page, per_page) {
    api.get(`${config.api}/accounts/${accounts_id}/budgets?page=${page}&per_page=${per_page}`)
    .then((resp) => {
      console.log(resp);
      toast.success("accounts Budgets retrived");
      setaccountBudgets(resp.data.data);
    }).catch((err)=> {
      console.error(err);
      toast.error("something went wrong");
    })
  }
  function getnoaccountsBudgets(page, per_page) {
    api.get(`${config.api}/budgets/noAccount?page=${page}&per_page=${per_page}`)
    .then((resp) => {
      console.log(resp);
      toast.success("all Budgets retrived");
      setnoaccountBudgets(resp.data.data.content);
    }).catch((err)=> {
      console.error(err);
      toast.error("something went wrong");
    })
  }
  useEffect(() => {
    if (accountData !== null) {
      setLoading(false);
      getnoaccountsBudgets(1, 100);
      getaccountsBudgets(account.refNo, 1, 100);
    }
  }, [])
  const formik = useFormik({
    initialValues: {
      refNo: account.refNo,
      name: account.name,
      details: account.details,
    },
    validationSchema: validScheme,
    onSubmit: handleaccountsSubmit
  })


  // Function to handle selecting/deselecting a subaccounts
  const handleAddBudgetselection = (subaccountsId) => {

    setaddedBudgets((prevSelectedBudgets) => {
      if (prevSelectedBudgets.includes(subaccountsId)) {
        return prevSelectedBudgets.filter((id) => id !== subaccountsId);
      } else {
        return [...prevSelectedBudgets, subaccountsId];
      }
    });
  };

  const handleRemoveBudgetselection = (subaccountsId) => {
    console.log(subaccountsId);
    setremovedBudgets((prevSelectedBudgets) => {
      console.log(prevSelectedBudgets);
      if (prevSelectedBudgets.includes(subaccountsId)) {
        return prevSelectedBudgets.filter((id) => id !== subaccountsId);
      } else {
        return [...prevSelectedBudgets, subaccountsId];
      }
    });
  };

  const handleAddToaccounts = async () => {
    setisLoading(true);
  
    const data = {
      'associationRefNos': addedBudgets
    };
  
    try {
      await api.put(`${config.api}/association/accounts/${account.refNo}/add-budgets`, data);
      toast.success("Budgets added to accounts successfully");
      
      // Fetch and update the accounts and uncategorized Budgets after adding
      getaccountsBudgets(account.refNo, 1, 100);
      getnoaccountsBudgets(1, 100);
    } catch (error) {
      console.error(error);
      toast.error("Error occurred while adding Budgets to accounts");
    }
  
    setisLoading(false);
    setaddedBudgets([]);
  };
  
  const handleRemoveToaccounts = async () => {
    const data = {
      'associationRefNos': removedBudgets
    };
  
    setisLoading(true);
  
    try {
      await api.put(`${config.api}/association/accounts/${account.refNo}/remove-budgets`, data);
      toast.success("Budgets removed from accounts successfully");
      
      // Fetch and update the accounts and uncategorized Budgets after removing
      getaccountsBudgets(account.refNo, 1, 100);
      getnoaccountsBudgets(1, 100);
    } catch (error) {
      console.error(error);
      toast.error("Error occurred while removing Budgets from accounts");
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
        placeholder='accounts refNo'
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
        placeholder='accounts name'
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
        placeholder='accounts details'
         />
         
        {formik.errors.details && formik.touched.details ? 
        <div className="alert alert-danger">{formik.errors.details}</div>:
        null}
        
        {isLoading ?  
        <button type='button' className='register-button'><i className='fas fa-spinner fa-spin'></i></button> :
        <button  className='btn btn-primary btn-lg btn-block' disabled={!formik.dirty && formik.isValid} type="submit" onSubmit={handleaccountsSubmit} >update accounts</button>}
        <Toaster/>
      </form>
      <br/>

      <div>
      <div className="accounts-box">
        <h2>Add accounts To Budgets</h2>
        <ul>
          {noaccountsBudgets.map((subaccounts, index) => (
              <li key={subaccounts.refNo}>
              <label htmlFor={`subaccounts${index}`}>{subaccounts.name}</label>
                <input
                  name={`subaccounts${index}`}
                  type="checkbox"
                  checked={addedBudgets.includes(subaccounts.refNo)}
                  onChange={() => handleAddBudgetselection(subaccounts.refNo)}
                />
              </li>
            ))}
        </ul>
        <button className='btn btn-secondary' disabled={addedBudgets.length === 0} onClick={handleAddToaccounts}>Add to accounts</button>
      </div>

      <br/>

      <div className="accounts-box">
        <h2>Remove Budgets from accounts</h2>
        <ul>
          {accountsBudgets.map((subaccounts, index) => (
              <li key={subaccounts.refNo}>
                <label htmlFor={`subaccountsToRemove${index}`}>
                {subaccounts.name}
                </label>
                  <input
                    name={`subaccountsToRemove${index}`}
                    type="checkbox"
                    checked={removedBudgets.includes(subaccounts.refNo)}
                    onChange={() => handleRemoveBudgetselection(subaccounts.refNo)}
                  />

              </li>
            ))}
        </ul>
        <button className='btn btn-danger' disabled={removedBudgets.length === 0} onClick={handleRemoveToaccounts}>Remove from accounts</button>
      </div>
    </div>


    </>
  }
}
