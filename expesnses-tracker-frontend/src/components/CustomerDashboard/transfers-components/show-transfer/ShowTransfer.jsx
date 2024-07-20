import React, { useContext, useEffect, useState } from "react";
import { UserDataContext } from "../../../basics/UserContextProvider/UserContextProvider";
import { useNavigate } from "react-router-dom";
import api from "../../../api";
import config from "../../../config";
import Loading from "../../../basics/Loading/loading";
import toast, { Toaster } from "react-hot-toast";

export default function ShowTransfer() {
  const [loading, setLoading] = useState(true);
  const userContext = useContext(UserDataContext);
  const userData = userContext.userData;
  const [Transfer, setTransfer] = useState([]);
  const navigate = useNavigate();
  useEffect(() => {
    if (userData === null) {
      navigate("/login");
    }
    api
      .get(`${config.api}/sub-categories?page=1&per_page=100`)
      .then((response) => {
        if (response.status === 200) {
          setTransfer(response.data.data.content);
          setLoading(false);
          toast.success("all Transfer retrived");
        }
      })
      .catch((err) => {
        console.error(err);
        toast.error("error loading Transfer");
      });
  }, [navigate, userData]);
  function handleUpdate(Transfer) {
    navigate(`/admin/updateTransfer/${Transfer.refNo}`, {
      state: { TransferData: Transfer },
    });
  }

  function handleDelete(refNo) {
    api
      .delete(`${config.api}/sub-categories/${refNo}`)
      .then((resp) => {
        toast.success("deleted successfully");
        setTransfer((prevTransfer) =>
          prevTransfer.filter((c) => c.refNo !== refNo)
        );
      })
      .catch((err) => {
        console.error(err);
        toast.error("cant delete this Transfer");
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
              <th scope="col">delete</th>
            </tr>
          </thead>
          <tbody>
            {Transfer.map((Transfer, index) => (
              <tr key={Transfer.refNo || index}>
                <th scope="row">{Transfer.refNo}</th>
                <td>{Transfer.name}</td>
                <td>
                  <button
                    className="btn btn-secondary"
                    onClick={() => handleUpdate(Transfer)}
                  >
                    Update
                  </button>
                  <Toaster />
                </td>
                <td>
                  <button
                    className="btn btn-danger"
                    onClick={() => handleDelete(Transfer.refNo)}
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
