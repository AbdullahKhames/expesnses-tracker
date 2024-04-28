import React, { useContext, useEffect, useState } from "react";
import { useFormik } from "formik";
import { Link, useNavigate } from "react-router-dom";
import "./login.css";
import * as yup from "yup";
import config from "../../config";
import { UserDataContext } from "../../basics/UserContextProvider/UserContextProvider";
import api from "../../api";
import { useLocation } from "react-router-dom";
import toast, { Toaster } from "react-hot-toast";
import DeviceIdHolder from "../../basics/UserContextProvider/deviceIdHolder";

export default function Login() {
  const userContext = useContext(UserDataContext);

  const location = useLocation();
  const [isLoading, setisLoading] = useState(false);
  const [errorMessage, seterrorMessage] = useState("");
  const nav = useNavigate();

  async function handleLogin(values) {
    try {
      setisLoading(true);
      let response = await api.post(`${config.api}/users/login`, values);
      console.log(response);
      if (response.status === 200) {
        
        localStorage.setItem("access_token", response.data.data.accessToken);
        localStorage.setItem("refresh_token", response.data.data.refreshToken);
        localStorage.setItem("deviceId", DeviceIdHolder.getDeviceId());
        await handleSaveUserData(response);
        setisLoading(false);
        // if (response.data.data.role === 1) {
        //   nav("/Instructor");
        // } else if (response.data.data.role === 0) {
        //   nav("/admin");
        // } else {
        //   nav("/");
        // }
      } else {
        setisLoading(false);
      }
    } catch (error) {
      setisLoading(false);
      seterrorMessage(`${JSON.stringify(error.response.data)}`);
    }
    setisLoading(false);
  }

  const handleSaveUserData = async (response) => {
    console.log(response.data.data.data);
    await userContext.saveUserData({
      name: response.data.data.data.fullName,
      email: response.data.data.data.email,
      roles: response.data.data.data.roles,
      id: response.data.data.data.refNo,
      refNo: response.data.data.data.refNo,
      // customerId: response.data.data.data.customerId,
    });
  };
  const validShceme = yup.object({
    email: yup.string().email().required( ),
    password: yup
      .string()
      .required()
      .matches(
        "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).{8,}$",
        "Minimum eight characters, at least one uppercase letter, one lowercase letter and one number"
      ),
      token: yup.string().required(),
  });
  let formik = useFormik({
    initialValues: {
      email: "",
      password: "",
      deviceId: DeviceIdHolder.getDeviceId(),
      token: "",
    },
    validationSchema: validShceme,
    onSubmit: handleLogin,
  });

  useEffect(() => {
    if (location.state && location.state.error) {
      toast.error(location.state.error, {
        duration: 5000,
      });
    }
  }, [location.state]);

  return (
    <>
      <div className="registration-container">
        <h3>Login Now</h3>
        {errorMessage.length > 0 ? (
          <div className="alert alert-danger">{errorMessage}</div>
        ) : null}
        <form onSubmit={formik.handleSubmit}>
          <label htmlFor="email">Email :</label>
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
            <div className="alert alert-danger">{formik.errors.email}</div>
          ) : null}

          <label htmlFor="password">Password :</label>
          <input
            type="password"
            name="password"
            id="password"
            value={formik.values.password}
            onChange={formik.handleChange}
            onBlur={formik.handleBlur}
            required
          />
          {formik.errors.password && formik.touched.password ? (
            <div className="alert alert-danger">{formik.errors.password}</div>
          ) : null}
          
          <label htmlFor="token">token:</label>
          <input
            type="text"
            name="token"
            id="token"
            value={formik.values.token}
            onChange={formik.handleChange}
            onBlur={formik.handleBlur}
            required
          />
          {formik.errors.token && formik.touched.token ? (
            <div className="alert alert-danger">{formik.errors.token}</div>
          ) : null}

          <p></p>
          {isLoading ? (
            <button type="button" className="register-button">
              <i className="fas fa-spinner fa-spin"></i>
            </button>
          ) : (
            <button
              className="register-button"
              disabled={!formik.dirty && formik.isValid}
              type="submit"
              onSubmit={handleLogin}
            >
              Login
            </button>
          )}
        </form>
        <div className="container">
          dont Have An Account ?<Link to="/register"> Register Here</Link>
        </div>
        <div className="container">
          want to activate your email?<Link to="/activate"> Activate Here</Link>
        </div>
        <Toaster />
      </div>
    </>
  );
}
