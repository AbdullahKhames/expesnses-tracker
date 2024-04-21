import { useFormik } from 'formik';
import React, { useEffect, useState } from 'react'
import { useLocation, useParams } from 'react-router-dom'
import * as Yup from 'yup';
import api from '../../../api';
import config from '../../../config';
import toast, { Toaster } from 'react-hot-toast';
import './updateSubCategory.css'
import Loading from '../../../basics/Loading/loading';

export default function UpdateSubCategory() {
  const { refNo } = useParams(); 
  const location = useLocation()
  const [loading, setLoading] = useState(true)
  const [isLoading, setisLoading] = useState(false)
  const { SubCategoryData } = location.state;
  const [SubCategory, setSubCategory] = useState(SubCategoryData);
  const [addedExpenses, setaddedExpenses] = useState([]);
  const [removedExpenses, setremovedExpenses] = useState([]);
  const [SubCategorysExpenses, setSubCategoryExpenses] = useState([])
  const [noSubCategorysExpenses, setnoSubCategoryExpenses] = useState([])

  const fetchSubCategorysData = async (refNo) => {
    // Fetch SubCategorys data using refNo
    try {
      const response = await api.get(`${config.api}/sub-categories/refNo/${refNo}`);
      setSubCategory(response.data.data.data); // Set SubCategorys data obtained from API
    } catch (error) {
      console.error("Error fetching SubCategorys data:", error);
    }
  };
  useEffect(() => {
    if (SubCategoryData === null) {
      if (refNo) {
        fetchSubCategorysData(refNo);
      }
    } else {
      setSubCategory(SubCategoryData);
    }
      setLoading(false);
  }, [SubCategoryData, refNo]);
  
  function handleSubCategorysSubmit(values){
    console.log(values);
    setisLoading(true)
    api.put(`${config.api}/sub-categories/${values.refNo}`, values)
    .then((resp) => {
      console.log(resp.data.data);
      toast.success(resp.data.message);
      setSubCategory(resp.data.data)
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

  function getSubCategorysExpenses(SubCategorys_id, page, per_page) {
    api.get(`${config.api}/sub-categories/${SubCategorys_id}/expenses`)
    .then((resp) => {
      console.log(resp);
      toast.success("SubCategorys Expenses retrived");
      setSubCategoryExpenses(resp.data.data);
    }).catch((err)=> {
      console.error(err);
      toast.error("something went wrong");
    })
  }
  function getnoSubCategorysExpenses(page, per_page) {
    api.get(`${config.api}/expenses/noSubCategory?page=${page}&per_page=${per_page}`)
    .then((resp) => {
      console.log(resp);
      toast.success("all Expenses retrived");
      setnoSubCategoryExpenses(resp.data.data.content);
    }).catch((err)=> {
      console.error(err);
      toast.error("something went wrong");
    })
  }
  useEffect(() => {
    if (SubCategoryData !== null) {
      setLoading(false);
      getnoSubCategorysExpenses(1, 100);
      getSubCategorysExpenses(SubCategory.refNo, 1, 100);
    }
  }, [])
  const formik = useFormik({
    initialValues: {
      refNo: SubCategory.refNo,
      name: SubCategory.name,
      details: SubCategory.details,
    },
    validationSchema: validScheme,
    onSubmit: handleSubCategorysSubmit
  })


  // Function to handle selecting/deselecting a subSubCategorys
  const handleAddExpenseselection = (subSubCategorysId) => {

    setaddedExpenses((prevSelectedExpenses) => {
      if (prevSelectedExpenses.includes(subSubCategorysId)) {
        return prevSelectedExpenses.filter((id) => id !== subSubCategorysId);
      } else {
        return [...prevSelectedExpenses, subSubCategorysId];
      }
    });
  };

  const handleRemoveExpenseselection = (subSubCategorysId) => {
    console.log(subSubCategorysId);
    setremovedExpenses((prevSelectedExpenses) => {
      console.log(prevSelectedExpenses);
      if (prevSelectedExpenses.includes(subSubCategorysId)) {
        return prevSelectedExpenses.filter((id) => id !== subSubCategorysId);
      } else {
        return [...prevSelectedExpenses, subSubCategorysId];
      }
    });
  };

  const handleAddToSubCategorys = async () => {
    setisLoading(true);
  
    const data = {
      'associationRefNos': addedExpenses
    };
  
    try {
      await api.put(`${config.api}/association/sub-categories/${SubCategory.refNo}/add-expenses`, data);
      toast.success("Expenses added to SubCategorys successfully");
      
      // Fetch and update the SubCategorys and uncategorized Expenses after adding
      getSubCategorysExpenses(SubCategory.refNo, 1, 100);
      getnoSubCategorysExpenses(1, 100);
    } catch (error) {
      console.error(error);
      toast.error("Error occurred while adding Expenses to SubCategorys");
    }
  
    setisLoading(false);
    setaddedExpenses([]);
  };
  
  const handleRemoveToSubCategorys = async () => {
    const data = {
      'associationRefNos': removedExpenses
    };
  
    setisLoading(true);
  
    try {
      await api.put(`${config.api}/association/sub-categories/${SubCategory.refNo}/remove-expenses`, data);
      toast.success("Expenses removed from SubCategorys successfully");
      
      // Fetch and update the SubCategorys and uncategorized Expenses after removing
      getSubCategorysExpenses(SubCategory.refNo, 1, 100);
      getnoSubCategorysExpenses(1, 100);
    } catch (error) {
      console.error(error);
      toast.error("Error occurred while removing Expenses from SubCategorys");
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
        placeholder='SubCategorys refNo'
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
        placeholder='SubCategorys name'
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
        placeholder='SubCategorys details'
         />
         
        {formik.errors.details && formik.touched.details ? 
        <div className="alert alert-danger">{formik.errors.details}</div>:
        null}
        
        {isLoading ?  
        <button type='button' className='register-button'><i className='fas fa-spinner fa-spin'></i></button> :
        <button  className='btn btn-primary btn-lg btn-block' disabled={!formik.dirty && formik.isValid} type="submit" onSubmit={handleSubCategorysSubmit} >update SubCategorys</button>}
        <Toaster/>
      </form>
      <br/>

      <div>
      <div className="SubCategorys-box">
        <h2>Add SubCategorys To Expenses</h2>
        <ul>
          {noSubCategorysExpenses.map((subSubCategorys, index) => (
              <li key={subSubCategorys.refNo}>
              <label htmlFor={`subSubCategorys${index}`}>{subSubCategorys.name}</label>
                <input
                  name={`subSubCategorys${index}`}
                  type="checkbox"
                  checked={addedExpenses.includes(subSubCategorys.refNo)}
                  onChange={() => handleAddExpenseselection(subSubCategorys.refNo)}
                />
              </li>
            ))}
        </ul>
        <button className='btn btn-secondary' disabled={addedExpenses.length === 0} onClick={handleAddToSubCategorys}>Add to SubCategorys</button>
      </div>

      <br/>

      <div className="SubCategorys-box">
        <h2>Remove Expenses from SubCategorys</h2>
        <ul>
          {SubCategorysExpenses.map((subSubCategorys, index) => (
              <li key={subSubCategorys.refNo}>
                <label htmlFor={`subSubCategorysToRemove${index}`}>
                {subSubCategorys.name}
                </label>
                  <input
                    name={`subSubCategorysToRemove${index}`}
                    type="checkbox"
                    checked={removedExpenses.includes(subSubCategorys.refNo)}
                    onChange={() => handleRemoveExpenseselection(subSubCategorys.refNo)}
                  />

              </li>
            ))}
        </ul>
        <button className='btn btn-danger' disabled={removedExpenses.length === 0} onClick={handleRemoveToSubCategorys}>Remove from SubCategorys</button>
      </div>
    </div>


    </>
  }
}
