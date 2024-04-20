import React, { useEffect, useState } from 'react';
import { useFormik } from 'formik';
import { Link, useNavigate } from 'react-router-dom';
import './register.css';
import api from '../../api';
import config from '../../config';
import * as yup from 'yup';
import toast, { Toaster } from "react-hot-toast";
import DeviceIdHolder from '../../basics/UserContextProvider/deviceIdHolder';

function Register() {
  const [isLoading, setisLoading] = useState(false);
  const [errorMessage, seterrorMessage] = useState('');
  // const [categories, setCategories] = useState([]);
  // let url_api = config.api + "/categories?page=1&per_page=100";
  const nav = useNavigate();

  async function handleRegister(values) {
    try {
      setisLoading(true);
      let response = await api.post(`${config.api}/users/register`, values);
      console.log(response.data);
      if (response.status === 200 && response.data.status) {
        setisLoading(false);
        toast.success(response.data.message);
        // setTimeout(() => {
        //   nav('/activate');
        // }, 3000);
        setTimeout(() => {
          nav('/login');
        }, 3000);
      } else {
        setisLoading(false);
      }
    } catch (error) {
      console.error(error);
      setisLoading(false);
      seterrorMessage(`${JSON.stringify(error.response.data.message)} ${JSON.stringify(error.response.data.data)}`);
    }
    setisLoading(false);
  }

  const validShceme = yup.object({
    fullName: yup.string().required().min(3, 'fullName cannot be less than 3 characters').max(142, 'fullName cannot be more than 142 characters'),
    email: yup.string().email().required(),
    password: yup.string().required().matches("^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).{8,}$", 'Minimum eight characters, at least one uppercase letter, one lowercase letter, and one number'),
    confirmPassword: yup.string().required().matches("^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).{8,}$", 'Minimum eight characters, at least one uppercase letter, one lowercase letter, and one number').oneOf([yup.ref('password')], "Password doesn't match"),
    role: yup.string().required(),
    age: yup.number().min(16).max(60),
    deviceId: yup.string().notRequired(),
    verificationToken: yup.string().required(),
  });
  
  let formik = useFormik({
    initialValues: {
      email: '',
      fullName: '',
      password: '',
      confirmPassword: '',
      role: '',
      age: '',
      deviceId: DeviceIdHolder.getDeviceId(),
      verificationToken:'',
    },
    validationSchema: validShceme,
    onSubmit: handleRegister,
  });

  const roleOptions = [
    { fullName: 'CUSTOMER', val: 'CUSTOMER' },
  ];

  // useEffect(() => {
  //   api
  //     .get(url_api)
  //     .then((response) => {
  //       if (response.status === 200) {
  //         setCategories(response.data);
  //       }
  //     })
  //     .catch((error) => {
  //       console.error(error);
  //     });
  // }, []);

  return (
    <>
      <div classname="registration-container">
        <h3>Register Now</h3>
        {errorMessage.length > 0 ? (
          <div classname="alert alert-danger">{errorMessage}</div>
        ) : null}
        <form onSubmit={formik.handleSubmit}>
          <label htmlFor="email">Email:</label>
          <input
            type="email"
            name="email"
            id="email"
            value={formik.values.email}
            onChange={formik.handleChange}
            onBlur={formik.handleBlur}
            required
          />
          {formik.errors.email && formik.touched.email ? (
            <div classname="alert alert-danger">{formik.errors.email}</div>
          ) : null}

          <label htmlFor="fullName">fullName:</label>
          <input
            type="text"
            name="fullName"
            id="fullName"
            value={formik.values.fullName}
            onChange={formik.handleChange}
            onBlur={formik.handleBlur}
            required
          />
          {formik.errors.fullName && formik.touched.fullName ? (
            <div classname="alert alert-danger">{formik.errors.fullName}</div>
          ) : null}

          <label htmlFor="password">Password:</label>
          <input
            type="password"
            name="password"
            value={formik.values.password}
            onChange={formik.handleChange}
            onBlur={formik.handleBlur}
            required
          />
          {formik.errors.password && formik.touched.password ? (
            <div classname="alert alert-danger">{formik.errors.password}</div>
          ) : null}

          <label htmlFor="confirmPassword">Confirm Password:</label>
          <input
            type="password"
            name="confirmPassword"
            id="confirmPassword"
            value={formik.values.confirmPassword}
            onChange={formik.handleChange}
            onBlur={formik.handleBlur}
            required
          />
          {formik.errors.confirmPassword && formik.touched.confirmPassword ? (
            <div classname="alert alert-danger">{formik.errors.confirmPassword}</div>
          ) : null}
          <label htmlFor="verificationToken">verificationToken:</label>
          <input
            type="text"
            name="verificationToken"
            id="verificationToken"
            value={formik.values.verificationToken}
            onChange={formik.handleChange}
            onBlur={formik.handleBlur}
            required
          />
          {formik.errors.verificationToken && formik.touched.verificationToken ? (
            <div classname="alert alert-danger">{formik.errors.verificationToken}</div>
          ) : null}

          <label htmlFor="age">Age:</label>
          <input
            type="number"
            min="16"
            max="60"
            id="age"
            name="age"
            value={formik.values.age}
            onChange={formik.handleChange}
            onBlur={formik.handleBlur}
          />
          {formik.errors.age && formik.touched.age ? (
            <div classname="alert alert-danger">{formik.errors.age}</div>
          ) : null}

          <label htmlFor="role">Register As:</label>
          <select
            id="role"
            name="role"
            value={formik.values.role}
            onChange={(e) => {
              formik.handleChange(e);
              // formik.setFieldValue('interestedIn', '');
            }}
            onBlur={formik.handleBlur}
            required
          >
            <option value="" disabled>
              Register As:
            </option>
            {roleOptions.map((option) => (
              <option key={option.fullName} value={option.val}>
                {option.fullName}
              </option>
            ))}
          </select>
          {formik.errors.role && formik.touched.role ? (
            <div classname="alert alert-danger">{formik.errors.role}</div>
          ) : null}

          {/* {formik.values.role === '2' && (
            <div>
              <label htmlFor="interestedIn">Interested In:</label>
              <select
                id="interestedIn"
                fullName="interestedIn"
                value={formik.values.interestedIn}
                onChange={formik.handleChange}
                onBlur={formik.handleBlur}
                required={formik.values.role === '2'}
              >
                <option value="" disabled>
                  Interested In:
                </option>
                {categories.map((category, index) => (
                  <option key={category.id || index} value={category.id}>
                    {category.fullName}
                  </option>
                )
                )}
              </select>
              {formik.errors.interestedIn && formik.touched.interestedIn ? (
                <div classname="alert alert-danger">{formik.errors.interestedIn}</div>
              ) : null}
            </div>
          )} */}

          <div classname="container">
            {isLoading ? (
              <button type="button" classname="register-button">
                <i claname="fas fa-spinner fa-spin"></i>
              </button>
            ) : (
              <button
                classname="register-button"
                disabled={!formik.dirty && formik.isValid}
                type="submit"
                name={handleRegister}
              >
                Register
              </button>
            )}
          </div>
        </form>
        <div classname="container">
          Already Have An Account? <Link to="/login">Login Here</Link>
        </div>
        {/* <div classname="container">
          want to activate your email?<Link to="/activate"> Activate Here</Link>
        </div> */}
        <Toaster />
      </div>
    </>
  );
}

export default Register;
