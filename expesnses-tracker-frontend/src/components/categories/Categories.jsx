import React from "react";
import { MDBContainer, MDBRow } from "mdb-react-ui-kit";
import { useEffect, useState } from "react";
import "./Categories.css";
import config from "./../config";
import api from "./../api";
import Loading from "./../basics/Loading/loading";
import Search from "../basics/search/search";
import { Toaster, toast } from "react-hot-toast";
import CategoryCard from "./CategoryCard";

function Categories({ categories }) {
  const [_categories, setCategories] = useState(categories);
  const [loading, setLoading] = useState(true);
  const url_api = config.api + "/categories?page=1&per_page=50";

  useEffect(() => {
    console.log(categories);
    if (!categories) {
      api
        .get(url_api)
        .then((response) => {
          console.log(response);
          if (response.status === 200) {
            setCategories(response.data.data.content);
            setLoading(false);
          }
        })
        .catch((error) => {
          console.error(error);
        });
    } else {
      setLoading(false);
    }
  }, []);

  if (loading) {
    return <Loading />;
  } else if (_categories.length === 0) {
    return <div>No categories found</div>;
  } else {
    return (
      <>
        <Search />
        <div className="row">
          <MDBContainer className="py-4">
            <MDBRow>
              {console.log(_categories)}
              {_categories.map((category, index) => (
                <>
                  <CategoryCard key={index} category={category} index ={index}/>
                </>
              ))}
            </MDBRow>
          </MDBContainer>
          <Toaster />
        </div>
      </>
    );
  }
}

export default Categories;
