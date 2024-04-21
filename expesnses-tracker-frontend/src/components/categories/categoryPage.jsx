import React from "react";
import { useState, useEffect } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useParams } from "react-router-dom";
import config from './../config';
import api from './../api';
import Loading from './../basics/Loading/loading';
import SubCategoryList from './../subCategories/SubCategoryList';


function CategoryPage() {
  const { refNo } = useParams();
  const url_api =
    config.api +
    "/categories/refNo/" +
    refNo;

  const [loading, setLoading] = useState(true);
  const [category, setCategory] = useState(null);

  const navigate = useNavigate();

  const getCategory = (url_api) => {
    api
      .get(url_api)
      .then((res) => {
        console.log(res);
        if (res.status === 200) {
          setCategory(res.data.data);
          setLoading(false);
        }
      })
      .catch((err) => {
        console.error(err);
        navigate("/not-found");
      });
  };

  useEffect(() => {
    if (category) {
      window.location.reload();
    } else {
    getCategory(url_api);
    }
  }, [url_api]);


  if (loading) {
    return <Loading />;
  } else {
    return (
      <div>
        <div className="SubHeading course-title course-description">
          <br />
            <Link to="/categories" className="redirect">Categories</Link> / {category.name}
        </div>
        <div className="row">
          <SubCategoryList filter={category} />
        </div>
      </div>
    );
  }
}

export default CategoryPage;
