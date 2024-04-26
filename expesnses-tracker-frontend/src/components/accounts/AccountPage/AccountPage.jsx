import React, { useEffect, useState } from 'react';
import { useNavigate, useParams, useLocation } from 'react-router-dom';
import api from './../../api';
import config from './../../config';
import Loading from './../../basics/Loading/loading';

export default function AccountPage() {
  const nav = useNavigate();
  const { refNo } = useParams();
  const location = useLocation();
  const accountData = location.state ? location.state.accountData : null;
  const [account, setAccount] = useState(null);
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    console.log(location.state);
    console.log(accountData);
    if (!accountData) {
      getAccount();
    } else {
      setAccount(accountData);
      setLoading(false);
    }
  }, [accountData]);

  async function getAccount() {
    try {
      const response = await api.get(config.api + "/accounts/refNo/" + refNo);
      console.log(response);
      if (response.status === 200 && response.data.code === 800) {
        setAccount(response.data.data);
        setLoading(false);
      } else {
        nav('/not-found');
      }
    } catch (error) {
      console.error("Error fetching account:", error);
      nav('/not-found');
    }
  }
  if (loading) {
    return <Loading/>;
  }else{
    return (
      <div>Account Name : {account.name}</div>
    );
  }


}
