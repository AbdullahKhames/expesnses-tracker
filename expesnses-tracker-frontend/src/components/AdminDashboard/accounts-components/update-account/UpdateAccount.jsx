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
  const [addedPockets, setaddedPockets] = useState([]);
  const [removedPockets, setremovedPockets] = useState([]);
  const [accountsPockets, setaccountPockets] = useState([])
  const [noaccountsPockets, setnoaccountPockets] = useState([])

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

  function getaccountsPockets(accounts_id, page, per_page) {
    api.get(`${config.api}/accounts/${accounts_id}/pockets?page=${page}&per_page=${per_page}`)
    .then((resp) => {
      console.log(resp);
      toast.success("accounts Pockets retrived");
      setaccountPockets(resp.data.data);
    }).catch((err)=> {
      console.error(err);
      toast.error("something went wrong");
    })
  }
  function getnoaccountsPockets(page, per_page) {
    api.get(`${config.api}/pockets/noAccount?page=${page}&per_page=${per_page}`)
    .then((resp) => {
      console.log(resp);
      toast.success("all Pockets retrived");
      setnoaccountPockets(resp.data.data.content);
    }).catch((err)=> {
      console.error(err);
      toast.error("something went wrong");
    })
  }
  useEffect(() => {
    if (accountData !== null) {
      setLoading(false);
      getnoaccountsPockets(1, 100);
      getaccountsPockets(account.refNo, 1, 100);
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
  const handleAddPocketselection = (subaccountsId) => {

    setaddedPockets((prevSelectedPockets) => {
      if (prevSelectedPockets.includes(subaccountsId)) {
        return prevSelectedPockets.filter((id) => id !== subaccountsId);
      } else {
        return [...prevSelectedPockets, subaccountsId];
      }
    });
  };

  const handleRemovePocketselection = (subaccountsId) => {
    console.log(subaccountsId);
    setremovedPockets((prevSelectedPockets) => {
      console.log(prevSelectedPockets);
      if (prevSelectedPockets.includes(subaccountsId)) {
        return prevSelectedPockets.filter((id) => id !== subaccountsId);
      } else {
        return [...prevSelectedPockets, subaccountsId];
      }
    });
  };

  const handleAddToaccounts = async () => {
    setisLoading(true);
  
    const data = {
      'associationRefNos': addedPockets
    };
  
    try {
      await api.put(`${config.api}/association/accounts/${account.refNo}/add-pockets`, data);
      toast.success("Pockets added to accounts successfully");
      
      // Fetch and update the accounts and uncategorized Pockets after adding
      getaccountsPockets(account.refNo, 1, 100);
      getnoaccountsPockets(1, 100);
    } catch (error) {
      console.error(error);
      toast.error("Error occurred while adding Pockets to accounts");
    }
  
    setisLoading(false);
    setaddedPockets([]);
  };
  
  const handleRemoveToaccounts = async () => {
    const data = {
      'associationRefNos': removedPockets
    };
  
    setisLoading(true);
  
    try {
      await api.put(`${config.api}/association/accounts/${account.refNo}/remove-pockets`, data);
      toast.success("Pockets removed from accounts successfully");
      
      // Fetch and update the accounts and uncategorized Pockets after removing
      getaccountsPockets(account.refNo, 1, 100);
      getnoaccountsPockets(1, 100);
    } catch (error) {
      console.error(error);
      toast.error("Error occurred while removing Pockets from accounts");
    }
  
    setisLoading(false);
    setremovedPockets([]);
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
        <h2>Add accounts To Pockets</h2>
        <ul>
          {noaccountsPockets.map((subaccounts, index) => (
              <li key={subaccounts.refNo}>
              <label htmlFor={`subaccounts${index}`}>{subaccounts.name}</label>
                <input
                  name={`subaccounts${index}`}
                  type="checkbox"
                  checked={addedPockets.includes(subaccounts.refNo)}
                  onChange={() => handleAddPocketselection(subaccounts.refNo)}
                />
              </li>
            ))}
        </ul>
        <button className='btn btn-secondary' disabled={addedPockets.length === 0} onClick={handleAddToaccounts}>Add to accounts</button>
      </div>

      <br/>

      <div className="accounts-box">
        <h2>Remove Pockets from accounts</h2>
        <ul>
          {accountsPockets.map((subaccounts, index) => (
              <li key={subaccounts.refNo}>
                <label htmlFor={`subaccountsToRemove${index}`}>
                {subaccounts.name}
                </label>
                  <input
                    name={`subaccountsToRemove${index}`}
                    type="checkbox"
                    checked={removedPockets.includes(subaccounts.refNo)}
                    onChange={() => handleRemovePocketselection(subaccounts.refNo)}
                  />

              </li>
            ))}
        </ul>
        <button className='btn btn-danger' disabled={removedPockets.length === 0} onClick={handleRemoveToaccounts}>Remove from accounts</button>
      </div>
    </div>


    </>
  }
}
