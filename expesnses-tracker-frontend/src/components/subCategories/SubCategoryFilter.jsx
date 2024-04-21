import React, { useEffect, useState } from "react";
import SubCategoryList from "./SubCategoryList";
import { useLocation } from "react-router-dom";
import Loading from './../basics/Loading/loading';
import Search from './../basics/search/search';


function SubCategoryFilter() {
  const [loading, setLoading] = useState(true);
  const [query, setQuery] = useState(null);

  const location = useLocation();

  useEffect(() => {
    if (location.state) {
      setQuery(location.state.query);
      setLoading(false);
    } else {
      setLoading(false);
    }
  }, [location.state]);

  if (loading) {
    return <Loading />;
  } else {
    return (
      <>
        <Search />
        <div className="row">
          <SubCategoryList query={query}/>
        </div>
      </>
    );
  }
}

export default SubCategoryFilter;
