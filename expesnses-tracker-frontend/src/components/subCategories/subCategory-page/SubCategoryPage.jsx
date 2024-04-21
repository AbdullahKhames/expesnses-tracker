import React from "react";
import { Container, Row, Col, Card } from "react-bootstrap";
import { useParams, useNavigate, useLocation, Link } from "react-router-dom";
import toast from "react-hot-toast";
import { useContext, useEffect, useState } from "react";
import "./SubCategoryPage.css";
import deaultimage from "../../../assets/defaultImages/sybCategory.webp";
import { UserDataContext } from "./../../basics/UserContextProvider/UserContextProvider";
import api from "./../../api";
import config from "./../../config";
import Loading from "./../../basics/Loading/loading";
import NotFound from "./../../basics/not-found/NotFound";
// import NotApproved from "../../basics/not_approved/not_approved";
// import EnrollCard from "./EnrollCard";
// import SubCategoryContent from "./Content";

function SubCategoryPage() {
  const userContext = useContext(UserDataContext);
  const userData = userContext.userData;
  const navigate = useNavigate();
  const { refNo } = useParams();
  const url_api = config.api + "/sub-categories/refNo/" + refNo;
  const location = useLocation();
  const [loading, setLoading] = useState(true);
  const [SubCategory, setSubCategory] = useState(null);

  const getSubCategory = (url_api) => {
    api
      .get(`${url_api}`)
      .then((res) => {
        console.log(res);
        if (res.status === 200) {
          setSubCategory(res.data.data);
          toast.success(res.data.message);
          setLoading(false);
        }
      })
      .catch((err) => {
        console.error(err);
        navigate("/not-found");
      });
  };

  useEffect(() => {
    if (location.state) {
      setSubCategory(location.state.subCategorieData);
      if (location.state.subCategorieData === null) {
        setLoading(true);
        getSubCategory(url_api);
      }
      setLoading(false);
    } else {
      setLoading(true);
      getSubCategory(url_api);
    }
  }, [location.state]);

  if (loading) {
    return <Loading />;
  }
  if (SubCategory === null) {
    return <NotFound />;
    // } else if (SubCategory.approved === false && (!userData || userData.role !== 0)) {
    //   return <NotApproved />;
  } else {
    return (
      <>
        <section
          className="SubCategory-page"
          style={{ backgroundColor: "#eee" }}
        >
          <Container>
            <Row>
              <Col md={8}>
                <Card>
                  <Card.Header as="div" className="SubCategory-title">
                    {SubCategory.name}
                  </Card.Header>
                  <Card.Body>
                    <Card.Text className="SubCategory-description">
                      {SubCategory.details}
                    </Card.Text>
                    <Card.Text className="footer-text">
                      <div className="icon-block" style={{ width: "220px" }}>
                        <div className="last-updated-icon"></div>
                        <span className="icon-text">
                          Last updated{" "}
                          {SubCategory.updatedAt
                            .substring(0, 10)
                            .replace(/-/g, "/")
                            .split("/")
                            .reverse()
                            .join("/")}
                        </span>
                      </div>
                      <div className="icon-block">
                        <div className="Language-icon"></div>
                        <span className="icon-text">English</span>
                      </div>
                      <div className="icon-block" style={{ width: "160px" }}>
                        <div className="infinity-icon"></div>
                        <span className="icon-text">Full lifetime access</span>
                      </div>
                    </Card.Text>
                  </Card.Body>
                </Card>
              </Col>

              <Col md={4}>
                <Card className="SubCategory-image">
                  <Card.Body>
                    <Card.Img
                      variant="top"
                      src={deaultimage}
                      style={{ width: "100%" }}
                    />
                  </Card.Body>
                </Card>
                {/* <EnrollCard user={userData} SubCategory={SubCategory} /> */}
              </Col>
            </Row>
            <br />
            <Row>
              <Col>
                {/* <SubCategoryContent user={userData} SubCategory={SubCategory} /> */}
              </Col>
            </Row>
          </Container>
        </section>
      </>
    );
  }
}

export default SubCategoryPage;
