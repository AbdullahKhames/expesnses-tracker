import React, { useState, useEffect, useContext } from "react";
import toast, { Toaster } from "react-hot-toast";
import api from "../../../api";
import config from "../../../config";
import Loading from "../../../basics/Loading/loading";
import { useNavigate } from "react-router-dom";
import { UserDataContext } from "../../../basics/UserContextProvider/UserContextProvider";

export default function SearchSubCategory() {
  const [searchTerm, setSearchTerm] = useState("");
  const [filterType, setFilterType] = useState("refNo");
  const [SubCategorys, setSubCategorys] = useState([]);
  const [loading, setLoading] = useState(true);
  const [isLoading, setisLoading] = useState(false);

  const userContext = useContext(UserDataContext);
  let userData = userContext.userData;

  const navigate = useNavigate();
  useEffect(() => {
    if (userData) {
      setLoading(false);
    }
  }, [userData]);

  const handleSearch = () => {
    setisLoading(true);
    if (searchTerm !== "") {
      api
        .get(`${config.api}/sub-categories/${filterType}/${searchTerm}`)
        .then((response) => {
          console.log(response.data.data);
          toast.success(response.data.message);
          if (response.data.code === 800) {
            if (!Array.isArray(response.data.data)) {
              setSubCategorys([response.data.data]);
            } else {
              setSubCategorys(response.data.data);
            }
          }
          console.log(SubCategorys);
          setisLoading(false);
        })
        .catch((error) => {
          console.error("Error fetching data: ", error);
          toast.error(error.errorMessage);
          setisLoading(false);
        });
    }
    setisLoading(false);
  };

  function handleUpdate(SubCategory) {
    navigate(`/admin/updateSubCategory/${SubCategory.refNo}`, {
      state: { SubCategoryData: SubCategory },
    });
  }
  function handleDelete(refNo) {
    api
      .delete(`${config.api}/sub-categories/${refNo}`)
      .then((resp) => {
        toast.success("deleted successfully");
        setSubCategorys(
          SubCategorys.filter((SubCategory) => {
            return SubCategory.refNo !== refNo;
          })
        );
      })
      .catch((err) => {
        console.error(err);
        toast.error("cant delete this SubCategory");
      });
  }

  if (loading) {
    return <Loading />;
  } else {
    return (
      <>
        <div>
          <input
            type="text"
            placeholder="Search..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
          />
          <select
            value={filterType}
            onChange={(e) => setFilterType(e.target.value)}
          >
            <option value="name">Name</option>
            <option value="refNo">Reference Number</option>
          </select>
          {isLoading ? (
            <button type="button" className="fas fa-spinner fa-spin"></button>
          ) : (
            <button
              className="btn btn-primary btn-block"
              onClick={handleSearch}
            >
              Search
            </button>
          )}
          <div className="row">
            {SubCategorys.map((SubCategory, index) => (
              <div key={SubCategory.refNo || index} className="col-md-10 mb-4">
                <div className="card">
                  <div className="card-body">
                    <h5 className="card-title">{SubCategory.name}</h5>

                    <div className="d-flex justify-content-between">
                      <button
                        className="btn btn-primary"
                        onClick={(e) => {
                          e.stopPropagation();
                          handleUpdate(SubCategory);
                        }}
                      >
                        Update
                      </button>
                      <Toaster />
                      <button
                        className="btn btn-danger"
                        onClick={(e) => {
                          e.stopPropagation();
                          handleDelete(SubCategory.refNo);
                        }}
                      >
                        Delete
                      </button>
                    </div>
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>
      </>
    );
  }
}
