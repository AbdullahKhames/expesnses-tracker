import React from "react";
import { useState, useEffect } from "react";
import { useContext } from "react";
import toast from "react-hot-toast";
import { MDBCol, MDBContainer, MDBRow } from "mdb-react-ui-kit";
import { UserDataContext } from "./../basics/UserContextProvider/UserContextProvider";
import config from "./../config";
import api from "./../api";
import Loading from "./../basics/Loading/loading";
import SubCategoriesCard from "./cards/SubCategoriesCard";

function SubCategoryList({ filter = null, query = null }) {
  const userContext = useContext(UserDataContext);
  const userData = userContext.userData;
  const [subCategories, setsubCategories] = React.useState([]);
  const [loading, setLoading] = useState(true);
  let url_api = config.api + "/";
  if (query) {
    url_api += "sub-categories/name/" + query + "?page=1&per_page=20";
  } else if (filter === null) {
    url_api += "sub-categories?page=1&per_page=20";
  } else {
    // categories/<category_id>/subCategories
    url_api +=
      "categories/" + filter.refNo + "/subCategories?page=1&per_page=6";
  }
  useEffect(() => {
    api
      .get(url_api)
      .then((response) => {
        console.log(response);
        if (response.status === 200) {
          setsubCategories(response.data.data.content);
          if (query) {
            if (response.data.data.length === 0) {
              toast.error("No subCategories found");
            } else if (response.data.message) {
              toast.success(response.data.message);
            }
          }
          setLoading(false);
        }
      })
      .catch((error) => {
        console.error(error);
      });
  }, []);

  if (loading) {
    return <Loading />;
  } else if (subCategories.length === 0) {
    return <div>No subCategories found</div>;
  } else {
    return (
      <MDBContainer className="py-4">
        <MDBRow style={{ paddingLeft: "60px", paddingRight: "40px" }}>
          {subCategories.map((subCategorie, index) => (
            <MDBCol lg="4" key={index}>
              <SubCategoriesCard
                key={subCategorie.refNo}
                subCategorie={subCategorie}
                admin={
                  userData && userData.roles.includes("ROLE_ADMIN")
                    ? true
                    : false
                }
              />
            </MDBCol>
          ))}
        </MDBRow>
      </MDBContainer>
    );
  }
}

export default SubCategoryList;
