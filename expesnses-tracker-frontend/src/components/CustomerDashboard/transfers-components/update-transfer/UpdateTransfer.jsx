import { useFormik } from 'formik';
import React, { useEffect, useState } from 'react'
import { useLocation, useParams } from 'react-router-dom'
import * as Yup from 'yup';
import api from '../../../api';
import config from '../../../config';
import toast, { Toaster } from 'react-hot-toast';
import './updateTransfer.css'
import Loading from '../../../basics/Loading/loading';

export default function UpdateTransfer() {
  const { refNo } = useParams(); 
  const location = useLocation()
  const [loading, setLoading] = useState(true)
  const [isLoading, setisLoading] = useState(false)
  const { TransferData } = location.state;
  const [Transfer, setTransfer] = useState(TransferData);
  const [addedExpenses, setaddedExpenses] = useState([]);
  const [removedExpenses, setremovedExpenses] = useState([]);
  const [TransfersExpenses, setTransferExpenses] = useState([])
  const [noTransfersExpenses, setnoTransferExpenses] = useState([])

  const fetchTransfersData = async (refNo) => {
    // Fetch Transfers data using refNo
    try {
      const response = await api.get(`${config.api}/sub-categories/refNo/${refNo}`);
      setTransfer(response.data.data.data); // Set Transfers data obtained from API
    } catch (error) {
      console.error("Error fetching Transfers data:", error);
    }
  };
  useEffect(() => {
    if (TransferData === null) {
      if (refNo) {
        fetchTransfersData(refNo);
      }
    } else {
      setTransfer(TransferData);
    }
      setLoading(false);
  }, [TransferData, refNo]);
  
  function handleTransfersSubmit(values){
    console.log(values);
    setisLoading(true)
    api.put(`${config.api}/sub-categories/${values.refNo}`, values)
    .then((resp) => {
      console.log(resp.data.data);
      toast.success(resp.data.message);
      setTransfer(resp.data.data)
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

  function getTransfersExpenses(Transfers_id, page, per_page) {
    api.get(`${config.api}/sub-categories/${Transfers_id}/expenses`)
    .then((resp) => {
      console.log(resp);
      toast.success("Transfers Expenses retrived");
      setTransferExpenses(resp.data.data);
    }).catch((err)=> {
      console.error(err);
      toast.error("something went wrong");
    })
  }
  function getnoTransfersExpenses(page, per_page) {
    api.get(`${config.api}/expenses/noTransfer?page=${page}&per_page=${per_page}`)
    .then((resp) => {
      console.log(resp);
      toast.success("all Expenses retrived");
      setnoTransferExpenses(resp.data.data.content);
    }).catch((err)=> {
      console.error(err);
      toast.error("something went wrong");
    })
  }
  useEffect(() => {
    if (TransferData !== null) {
      setLoading(false);
      getnoTransfersExpenses(1, 100);
      getTransfersExpenses(Transfer.refNo, 1, 100);
    }
  }, [])
  const formik = useFormik({
    initialValues: {
      refNo: Transfer.refNo,
      name: Transfer.name,
      details: Transfer.details,
    },
    validationSchema: validScheme,
    onSubmit: handleTransfersSubmit
  })


  // Function to handle selecting/deselecting a subTransfers
  const handleAddExpenseselection = (subTransfersId) => {

    setaddedExpenses((prevSelectedExpenses) => {
      if (prevSelectedExpenses.includes(subTransfersId)) {
        return prevSelectedExpenses.filter((id) => id !== subTransfersId);
      } else {
        return [...prevSelectedExpenses, subTransfersId];
      }
    });
  };

  const handleRemoveExpenseselection = (subTransfersId) => {
    console.log(subTransfersId);
    setremovedExpenses((prevSelectedExpenses) => {
      console.log(prevSelectedExpenses);
      if (prevSelectedExpenses.includes(subTransfersId)) {
        return prevSelectedExpenses.filter((id) => id !== subTransfersId);
      } else {
        return [...prevSelectedExpenses, subTransfersId];
      }
    });
  };

  const handleAddToTransfers = async () => {
    setisLoading(true);
  
    const data = {
      'associationRefNos': addedExpenses
    };
  
    try {
      await api.put(`${config.api}/association/sub-categories/${Transfer.refNo}/add-expenses`, data);
      toast.success("Expenses added to Transfers successfully");
      
      // Fetch and update the Transfers and uncategorized Expenses after adding
      getTransfersExpenses(Transfer.refNo, 1, 100);
      getnoTransfersExpenses(1, 100);
    } catch (error) {
      console.error(error);
      toast.error("Error occurred while adding Expenses to Transfers");
    }
  
    setisLoading(false);
    setaddedExpenses([]);
  };
  
  const handleRemoveToTransfers = async () => {
    const data = {
      'associationRefNos': removedExpenses
    };
  
    setisLoading(true);
  
    try {
      await api.put(`${config.api}/association/sub-categories/${Transfer.refNo}/remove-expenses`, data);
      toast.success("Expenses removed from Transfers successfully");
      
      // Fetch and update the Transfers and uncategorized Expenses after removing
      getTransfersExpenses(Transfer.refNo, 1, 100);
      getnoTransfersExpenses(1, 100);
    } catch (error) {
      console.error(error);
      toast.error("Error occurred while removing Expenses from Transfers");
    }
  
    setisLoading(false);
    setremovedExpenses([]);
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
        placeholder='Transfers refNo'
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
        placeholder='Transfers name'
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
        placeholder='Transfers details'
         />
         
        {formik.errors.details && formik.touched.details ? 
        <div className="alert alert-danger">{formik.errors.details}</div>:
        null}
        
        {isLoading ?  
        <button type='button' className='register-button'><i className='fas fa-spinner fa-spin'></i></button> :
        <button  className='btn btn-primary btn-lg btn-block' disabled={!formik.dirty && formik.isValid} type="submit" onSubmit={handleTransfersSubmit} >update Transfers</button>}
        <Toaster/>
      </form>
      <br/>

      <div>
      <div className="Transfers-box">
        <h2>Add Transfers To Expenses</h2>
        <ul>
          {noTransfersExpenses.map((subTransfers, index) => (
              <li key={subTransfers.refNo}>
              <label htmlFor={`subTransfers${index}`}>{subTransfers.name}</label>
                <input
                  name={`subTransfers${index}`}
                  type="checkbox"
                  checked={addedExpenses.includes(subTransfers.refNo)}
                  onChange={() => handleAddExpenseselection(subTransfers.refNo)}
                />
              </li>
            ))}
        </ul>
        <button className='btn btn-secondary' disabled={addedExpenses.length === 0} onClick={handleAddToTransfers}>Add to Transfers</button>
      </div>

      <br/>

      <div className="Transfers-box">
        <h2>Remove Expenses from Transfers</h2>
        <ul>
          {TransfersExpenses.map((subTransfers, index) => (
              <li key={subTransfers.refNo}>
                <label htmlFor={`subTransfersToRemove${index}`}>
                {subTransfers.name}
                </label>
                  <input
                    name={`subTransfersToRemove${index}`}
                    type="checkbox"
                    checked={removedExpenses.includes(subTransfers.refNo)}
                    onChange={() => handleRemoveExpenseselection(subTransfers.refNo)}
                  />

              </li>
            ))}
        </ul>
        <button className='btn btn-danger' disabled={removedExpenses.length === 0} onClick={handleRemoveToTransfers}>Remove from Transfers</button>
      </div>
    </div>


    </>
  }
}
