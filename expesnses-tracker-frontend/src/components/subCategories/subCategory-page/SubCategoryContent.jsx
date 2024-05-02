import React from "react";
import { Card, Alert } from "react-bootstrap";
import { useContext, useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import "./Content.css";
import { UserDataContext } from '../../basics/UserContextProvider/UserContextProvider';
import api from '../../api';
import config from '../../config';
import Loading from '../../basics/Loading/loading';

function SubCategoryContent({ user, course }) {
  const userContext = useContext(UserDataContext);
  const userData = userContext.userData;
  const [shown, setShown] = useState(false);
  const [loading, setLoading] = useState(true);
  // const [instructor, setInstructor] = useState(null);
  const [expandedSections, setExpandedSections] = useState({});
  const [expandedLessons, setExpandedLessons] = useState({});
  // const [instructorOwner, setInstructorOwner] = useState(false);
  const [alertShown, setAlertShown] = useState(true);
  const navigate = useNavigate();
  let url_api = "";

  const checkEnroll = (url_api) => {
    api
      .get(`${url_api}`)
      .then((res) => {
        if (res.status === 200) {
          if (res.data.data) {
            setShown(true);
            setLoading(false);
          } else {
            setShown(false);
            setLoading(false);
          }
        }
      })
      .catch((err) => {
        console.error(err);
        setLoading(false);
      });
  };

  const checkOwner = (url_api, course) => {
    api
      .get(`${url_api}`)
      .then((res) => {
        if (res.status === 200) {
          // setInstructor(res.data);
          setLoading(false);
        }
      })
      .catch((err) => {
        console.error(err);
        setLoading(false);
      });
    if (course.instructor_id === userData.id) {
      setShown(true);
      // setInstructorOwner(true);
    }
  };

  const toggleSection = (sectionId) => {
    setExpandedSections((prev) => ({
      ...prev,
      [sectionId]: !prev[sectionId],
    }));
  };

  const toggleLesson = (lessonId) => {
    setExpandedLessons((prev) => ({
      ...prev,
      [lessonId]: !prev[lessonId],
    }));
  };

  const handleAlertClose = () => setAlertShown(false);

  useEffect(() => {
    if (userData === null) {
      setLoading(false);
      return;
    }
    if (userData.id !== user.id) {
      navigate("/forbidden");
      return;
    }
    url_api = config.api + "/customers/sub-categories/" + userData.id + "?all=true";
    setLoading(true);
  }, [userData]);

  if (loading) {
    return <Loading />;
  } else {
    return (
      <Card className="rounded">
        <Card.Header as="div" className="course-title">
          Course Content
        </Card.Header>
        <Card.Body>
          {shown ? (
            <Card.Text className="course-content">
              {course.sections.length === 0 && (
                <Alert variant="danger">No content available</Alert>
              )}
              {course.sections.map((section, index) => (
                <>
                  <div key={section.id || index} className="list-sections">
                    <h3 className="section-head">
                      <span
                        className="toggle-icon"
                        onClick={() => toggleSection(section.id)}
                      >
                        {expandedSections[section.id] ? "▼" : "►"}
                      </span>
                      {section.name}
                    </h3>
                    {expandedSections[section.id] && (
                      <ul className="list-content">
                        {section.lessons.map((lesson, index) => (
                          <li key={lesson.id || index}>
                            <div className="lesson-item">
                              <span
                                className="toggle-icon"
                                onClick={() => toggleLesson(lesson.id)}
                              >
                                {expandedLessons[lesson.id] ? "▼ " : "► "}
                              </span>
                              {lesson.name}
                            </div>
                            {expandedLessons[lesson.id] && (
                              <div className="lesson-content">
                                {lesson.content}
                              </div>
                            )}
                          </li>
                        ))}
                      </ul>
                    )}
                  </div>
                </>
              ))}
            </Card.Text>
          ) : (
            <>
              <Alert show={alertShown} variant="warning" className="my-alert">
                You need to Enroll to access all Content
                <button
                  type="button"
                  className="close"
                  onClick={handleAlertClose}
                >
                  <span className="close-icon">&times;</span>
                </button>
              </Alert>
              <Card.Text className="course-content">
                {course.sections.length === 0 && (
                  <Alert variant="danger">No content available</Alert>
                )}
                {course.sections.map((section, index) => (
                  <div key={section.id || index} className="list-sections">
                    <h3 className="section-head">
                      <span className="point-icon">•</span>
                      {section.name}
                    </h3>
                  </div>
                ))}
              </Card.Text>
            </>
          )}
        </Card.Body>
      </Card>
    );
  }
}

export default SubCategoryContent;
