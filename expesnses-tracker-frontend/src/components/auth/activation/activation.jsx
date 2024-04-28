import React, { useEffect, useState } from "react";
import { useFormik } from "formik";
import { Link, useNavigate } from "react-router-dom";
import "./activation.css";
import * as yup from "yup";
import config from "../../config";
import api from "../../api";
import { useLocation } from "react-router-dom";
import toast, { Toaster } from "react-hot-toast";
import DeviceIdHolder from "../../basics/UserContextProvider/deviceIdHolder";

export default function Activation() {
  const location = useLocation();
  const [isLoading, setisLoading] = useState(false);
  const [errorMessage, seterrorMessage] = useState("");
  const [popupData, setPopupData] = useState(null);
  const nav = useNavigate();

  const validScheme = yup.object({
    email: yup.string().email().required(),
    otp: yup.string().required(),
    // refNo: yup.string().required(),
  });

  const activationFormik = useFormik({
    initialValues: {
      email: "",
      otp: "",
      // refNo: "",
      deviceId: DeviceIdHolder.getDeviceId(),
    },
    validationSchema: validScheme,
    onSubmit: handleLogin,
  });

  const requestActivationFormik = useFormik({
    initialValues: {
      email: "",
      type: "",
      deviceId: DeviceIdHolder.getDeviceId(),
    },
    validationSchema: yup.object({
      email: yup.string().email().required(),
      type: yup
        .string()
        .oneOf(["LOGIN", "REGISTER", "RESET_ACCOUNT", "CHANGE_EMAIL"])
        .required(),
    }),
    onSubmit: (values) => handleRequestActivation(values),
  });

  useEffect(() => {
    if (location.state && location.state.error) {
      toast.error(location.state.error, {
        duration: 5000,
      });
    }
  }, []);

  async function handleLogin(values) {
    try {
      setisLoading(true);
      console.log(values);
      let response = await api.post(`${config.api}/otp/Verification`, values);
      if (response.status === 200) {
        setisLoading(false);
        toast.success(response.data.message);
        setTimeout(() => {
          setPopupData(response.data.data.token);
        }, 2000);
      } else {
        setisLoading(false);
      }
    } catch (error) {
      setisLoading(false);
      seterrorMessage(`${JSON.stringify(error.response.data)}`);
    }
  }

  async function handleRequestActivation(values) {
    try {
      console.log(values);
      setisLoading(true);
      let response = await api.post(`${config.api}/otp/sendOtp`, values);
      if (response.status === 200) {
        setisLoading(false);
        toast.success(response.data.message);
        if (response.data?.data) {
          setPopupData(response.data.data);
          setActivationFormik(response.data.data);
        }
      } else {
        setisLoading(false);
      }
    } catch (error) {
      setisLoading(false);
      seterrorMessage(`${JSON.stringify(error.response.data.error)}`);
    }
  }

  function setActivationFormik(activationData) {
    activationFormik.values.email = activationData.email;
    activationFormik.values.otp = activationData.otp;
    activationFormik.values.refNo = activationData.refNo;
  }

  return (
    <>
      <div className="registration-container">
        <h3>Activate Now</h3>
        {errorMessage.length > 0 ? (
          <div className="alert alert-danger">{errorMessage}</div>
        ) : null}
        {popupData && (
          <div className="popup">
            <div className="popup-inner">
              <h2>Data Received</h2>
              <p>{JSON.stringify(popupData)}</p>
              <button
                onClick={() => {
                  navigator.clipboard
                    .writeText(popupData)
                    .then(() => {
                      console.log("Content copied to clipboard");
                    })
                    .catch((err) => {
                      console.error(
                        "Unable to copy content to clipboard: ",
                        err
                      );
                    });
                }}
              >
                Copy to Clipboard
              </button>
              <button
                onClick={() => {
                  setPopupData(null);
                }}
              >
                Close
              </button>
            </div>
          </div>
        )}
        <form onSubmit={activationFormik.handleSubmit}>
          <label htmlFor="email">Email :</label>
          <input
            type="email"
            name="email"
            id="email"
            value={activationFormik.values.email}
            onChange={activationFormik.handleChange}
            onBlur={activationFormik.handleBlur}
            required
          />
          {activationFormik.errors.email && activationFormik.touched.email ? (
            <div className="alert alert-danger">
              {activationFormik.errors.email}
            </div>
          ) : null}

          <label htmlFor="otp">OTP :</label>
          <input
            type="text"
            name="otp"
            id="otp"
            value={activationFormik.values.otp}
            onChange={activationFormik.handleChange}
            onBlur={activationFormik.handleBlur}
            required
          />
          {activationFormik.errors.otp && activationFormik.touched.otp ? (
            <div className="alert alert-danger">
              {activationFormik.errors.otp}
            </div>
          ) : null}
          {/* <label htmlFor="refNo">refNo :</label>
          <input
            type="text"
            name="refNo"
            id="refNo"
            value={activationFormik.values.refNo}
            onChange={activationFormik.handleChange}
            onBlur={activationFormik.handleBlur}
            required
          />
          {activationFormik.errors.refNo && activationFormik.touched.refNo ? (
            <div className="alert alert-danger">
              {activationFormik.errors.refNo}
            </div>
          ) : null} */}

          <p></p>
          {isLoading ? (
            <button type="button" className="register-button">
              <i className="fas fa-spinner fa-spin"></i>
            </button>
          ) : (
            <>
            <button
              className="register-button"
              // disabled={!activationFormik.dirty && activationFormik.isValid}
              type="submit"
            >
              Activate Now
            </button>
            <button
              className="btn btn-danger"
              type="button"
              onClick={() => {
                console.log(activationFormik.values);
                console.log(activationFormik.errors);
                console.log("dirty ? " + activationFormik.dirty);
                console.log("isValid ? " + activationFormik.isValid);
              }}
            >
              log values
            </button>

            </>
          )}
        </form>

        <form onSubmit={requestActivationFormik.handleSubmit}>
          <p>Didn't recive activation token ?</p>
          <label htmlFor="email">Email :</label>
          <input
            type="email"
            name="email"
            id="email"
            value={requestActivationFormik.values.email}
            onChange={requestActivationFormik.handleChange}
            onBlur={requestActivationFormik.handleBlur}
            required
          />
          {requestActivationFormik.errors.email &&
          requestActivationFormik.touched.email ? (
            <div className="alert alert-danger">
              {requestActivationFormik.errors.email}
            </div>
          ) : null}
          <label htmlFor="type">Token Type:</label>
          <select
            name="type"
            id="type"
            value={requestActivationFormik.values.type}
            onChange={requestActivationFormik.handleChange}
            onBlur={requestActivationFormik.handleBlur}
            required
          >
            <option value="">Select Token Type</option>
            <option value="LOGIN">Login</option>
            <option value="REGISTER">Register</option>
            <option value="RESET_ACCOUNT">Reset Account</option>
            <option value="CHANGE_EMAIL">Change Email</option>
          </select>
          {requestActivationFormik.errors.type &&
          requestActivationFormik.touched.type ? (
            <div className="alert alert-danger">
              {requestActivationFormik.errors.type}
            </div>
          ) : null}

          <p></p>
          {isLoading ? (
            <button type="button" className="register-button">
              <i className="fas fa-spinner fa-spin"></i>
            </button>
          ) : (
            <button
              type="button"
              className="register-button"
              onClick={requestActivationFormik.handleSubmit}
              disabled={isLoading}
            >
              Request activation token now
            </button>
          )}
        </form>

        <Toaster />
        <div className="container">
          Don't Have An Account? <Link to="/register">Register Here</Link>
        </div>
        <div className="container">
          Already Have An Account? <Link to="/login">Login Here</Link>
        </div>
      </div>
    </>
  );
}
