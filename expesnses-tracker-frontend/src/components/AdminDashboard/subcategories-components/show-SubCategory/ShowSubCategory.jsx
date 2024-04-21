import React, { useContext, useEffect, useState } from "react";
import { UserDataContext } from "../../../basics/UserContextProvider/UserContextProvider";
import { useNavigate } from "react-router-dom";
import api from "../../../api";
import config from "../../../config";
import Loading from "../../../basics/Loading/loading";
import toast, { Toaster } from "react-hot-toast";

export default function ShowSubCategory() {
  const [loading, setLoading] = useState(true);
  const userContext = useContext(UserDataContext);
  const userData = userContext.userData;
  const [SubCategory, setSubCategory] = useState([]);
  const navigate = useNavigate();
  useEffect(() => {
    if (userData === null) {
      navigate("/login");
    }
    api
      .get(`${config.api}/sub-categories?page=1&per_page=100`)
      .then((response) => {
        if (response.status === 200) {
          setSubCategory(response.data.data.content);
          setLoading(false);
          toast.success("all SubCategory retrived");
        }
      })
      .catch((err) => {
        console.error(err);
        toast.error("error loading SubCategory");
      });
  }, [navigate, userData]);
  function handleUpdate(subCategory) {
    navigate(`/admin/updateSubCategory/${subCategory.refNo}`, {
      state: { SubCategoryData: subCategory },
    });
  }

  function handleDelete(refNo) {
    api
      .delete(`${config.api}/sub-categories/${refNo}`)
      .then((resp) => {
        toast.success("deleted successfully");
        setSubCategory((prevSubCategory) =>
          prevSubCategory.filter((c) => c.refNo !== refNo)
        );
      })
      .catch((err) => {
        console.error(err);
        toast.error("cant delete this subCategory");
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
            {SubCategory.map((subCategory, index) => (
              <tr key={subCategory.refNo || index}>
                <th scope="row">{subCategory.refNo}</th>
                <td>{subCategory.name}</td>
                <td>
                  <button
                    className="btn btn-secondary"
                    onClick={() => handleUpdate(subCategory)}
                  >
                    Update
                  </button>
                  <Toaster />
                </td>
                <td>
                  <button
                    className="btn btn-danger"
                    onClick={() => handleDelete(subCategory.refNo)}
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
