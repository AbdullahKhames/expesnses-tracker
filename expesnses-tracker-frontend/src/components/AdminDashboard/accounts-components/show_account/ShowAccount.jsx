import React, { useContext, useEffect, useState } from "react";
import { UserDataContext } from "../../../basics/UserContextProvider/UserContextProvider";
import { useNavigate } from "react-router-dom";
import api from "../../../api";
import config from "../../../config";
import Loading from "../../../basics/Loading/loading";
import toast, { Toaster } from "react-hot-toast";

export default function ShowAccounts() {
  const [loading, setLoading] = useState(true);
  const userContext = useContext(UserDataContext);
  const userData = userContext.userData;
  const [Accounts, setAccounts] = useState([]);
  const navigate = useNavigate();
  useEffect(() => {
    if (userData === null) {
      navigate("/login");
    }
    api
      .get(`${config.api}/accounts?page=1&per_page=100`)
      .then((response) => {
        if (response.status === 200) {
          setAccounts(response.data.data.content);
          setLoading(false);
          toast.success("all Accounts retrived");
        }
      })
      .catch((err) => {
        console.error(err);
        toast.error("error loading Accounts");
      });
  }, [navigate, userData]);
  function handleUpdate(account) {
    navigate(`/admin/updateAccount/${account.refNo}`, {
      state: { accountData: account },
    });
  }

  function handleDelete(refNo) {
    api
      .delete(`${config.api}/accounts/${refNo}`)
      .then((resp) => {
        toast.success("deleted successfully");
        setAccounts((prevAccounts) =>
          prevAccounts.filter((c) => c.refNo !== refNo)
        );
      })
      .catch((err) => {
        console.error(err);
        toast.error("cant delete this account");
      });
  }

  if (loading) {
    return <Loading />;
  } else {
    return (
      <>
        <table className="table">
          <thead className="table-dark">
            <tr>
              {/* <th scope="col">id</th> */}
              <th scope="col">refNo</th>
              <th scope="col">name</th>
              <th scope="col">update</th>
              <th scope="col">deelete</th>
            </tr>
          </thead>
          <tbody>
            {Accounts.map((account, index) => (
              <tr key={account.refNo || index}>
                <th scope="row">{account.refNo}</th>
                <td>{account.name}</td>
                <td>
                  <button
                    className="btn btn-secondary"
                    onClick={() => handleUpdate(account)}
                  >
                    Update
                  </button>
                  <Toaster />
                </td>
                <td>
                  <button
                    className="btn btn-danger"
                    onClick={() => handleDelete(account.refNo)}
                  >
                    Delete
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </>
    );
  }
}
