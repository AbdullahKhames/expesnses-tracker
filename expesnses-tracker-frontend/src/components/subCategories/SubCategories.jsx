import React from "react";
import SubCategoryList from './SubCategoryList';
import Search from "../basics/search/search";
function SubCategories() {
  return (
    <>
      <Search />

      <div className="row">
        {/* <Container> */}
        <SubCategoryList />
        {/* </Container> */}
      </div>
    </>
  );
}

export default SubCategories;
