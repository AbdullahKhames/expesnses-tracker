import { useFormik } from 'formik';
import React, { useState } from 'react'
import * as Yup from 'yup'
import toast, { Toaster } from 'react-hot-toast';
import { useNavigate } from 'react-router-dom';
import api from '../../../api';
import config from '../../../config';
export default function AddAccount() {
    const [isLoading, setIsLoading] = useState(false);
    const [errorMessage, seterrorMessage] = useState('')
    const nav = useNavigate();
    function handleAccountSubmit(values) {
        setIsLoading(true);

        api.post(`${config.api}/accounts`, values)
        .then((response) => {
            if (response.status === 200) {
                toast.success('Account added successfully');
            }
            setIsLoading(false);
        })
        .catch((err) => {
            if (err.response.status === 401){
                localStorage.removeItem('access_token');
                localStorage.removeItem('refresh_token');    
                localStorage.removeItem('deviceId');    
                nav('/login');
                window.location.reload();
              }
              setIsLoading(false);
              toast.error("error saving Account");
            console.error(err);
            seterrorMessage(`${JSON.stringify(err.response.data)}`);

        })
    }
    const validScheme = Yup.object().shape({
        name : Yup.string().required().min(3)
    })
    const formik = useFormik({
        initialValues:{
            name:'',
            details:'',
        },
        validationSchema:validScheme,
        onSubmit: handleAccountSubmit
    })
  return <>
    <div>

        
        <h1>Add a New Account</h1>
        {errorMessage.length > 0 ?   
        <div className="alert alert-danger">
            {errorMessage}
        </div> :null}

        <form onSubmit={formik.handleSubmit}>
        <label htmlFor="name">Account Name:</label>
                <input
                type="text"
                name="name"
                id = "name"
                placeholder='Account name'
                value={formik.values.name}
                onChange={formik.handleChange}
                onBlur={formik.handleBlur}
                required
                />
        {formik.errors.name && formik.touched.name ? <div className="alert alert-danger">{formik.errors.name}</div> : null}
        <label htmlFor="name">Account details:</label>
                <input
                type="text"
                name="details"
                id = "details"
                placeholder='Account details'
                value={formik.values.details}
                onChange={formik.handleChange}
                onBlur={formik.handleBlur}
                />
        {formik.errors.name && formik.touched.details ? <div className="alert alert-danger">{formik.errors.details}</div> : null}
        {isLoading ?  
        <button type='button' className='register-button'><i className='fas fa-spinner fa-spin'></i></button> :
        <button  className='btn btn-primary btn-lg btn-block' disabled={!formik.dirty && formik.isValid} type="submit" onSubmit={handleAccountSubmit} >create Account</button>}
        <Toaster/>
        </form>
    </div>
  </>
}
