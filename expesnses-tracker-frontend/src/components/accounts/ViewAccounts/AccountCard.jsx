// AccountCard.js

import React, { useEffect } from "react";
import "./AccountCard.css"; // Import the CSS file for styling
import api from "./../../api";
import config from "./../../config";
import { toast } from "react-hot-toast";
import { Toaster } from "react-hot-toast";
import { useState } from 'react';

const AccountCard = ({ account }) => {
    
    const [registered, setRegistered] = useState(account.currentCustomerRegistered)
    useEffect(() => {
      console.log(account);
      setRegistered(account.currentCustomerRegistered)
    }
  , [account])

  const totalBalance = account.pockets.reduce(
    (total, pocket) => total + pocket.amount,
    0
  );
  function handleAddAccount(value) {
    console.log(value);
    const data = {
      associationRefNos: [value],
    };
    api
      .put(`${config.api}/customers/add-accounts`, data)
      .then((response) => {
        console.log(response);
        toast.success(response.data.message);
        setRegistered(true);
      })
      .catch((err) => {
        toast.error(err);
      });
  }
  function handleRemoveAccount(value) {
    console.log(value);
    const data = {
      associationRefNos: [value],
    };
    api
      .put(`${config.api}/customers/remove-accounts`, data)
      .then((response) => {
        console.log(response);
        toast.success(response.data.message);
        setRegistered(false);

      })
      .catch((err) => {
        toast.error(err);
      });
  }
  return (
    <div className="card account-card">
      <div className="card-body">
        <h5 className="card-title">{account.name}</h5>
        <p className="card-text">Details: {account.details}</p>
        <p className="card-text">Created At: {account.createdAt}</p>
        <p className="card-text">Updated At: {account.updatedAt}</p>
        <p className="card-text total-balance">Total Balance: {totalBalance}</p>
        {!registered ? (
          <>
            <button
              className="btn btn-primary"
              value={account.refNo}
              onClick={(e) => handleAddAccount(e.target.value)}
            >
              
              Register To This Account
            </button>
          </>
        ) : <button
        className="btn btn-danger"
        value={account.refNo}
        onClick={(e) => handleRemoveAccount(e.target.value)}
      >
        
        un Register from This Account
      </button>}
      </div>
      <Toaster />
    </div>
  );
};

export default AccountCard;
