import React, { useState, useEffect, useContext } from "react";
import toast from "react-hot-toast";
import { MDBCol, MDBContainer, MDBRow } from "mdb-react-ui-kit";
import { UserDataContext } from "../basics/UserContextProvider/UserContextProvider";
import config from "../config";
import api from "../api";
import Loading from "../basics/Loading/loading";
import PocketsCard from "./pocketsCard";
import PocketForm from "./PocketForm";

function PocketList({ filter = null, query = null }) {
  const userContext = useContext(UserDataContext);
  const userData = userContext.userData;
  const [pockets, setPockets] = useState([]);
  const [loading, setLoading] = useState(true);
  const pocketTypes = [
    "ENTERTAINMENT",
    "SAVINGS",
    "BILLS",
    "ALLOWANCE",
    "MOM",
    "MISC",
    "DONATION",
  ];

  let url_api = config.api + "/";
  if (query) {
    url_api += "pockets/name/" + query + "?page=1&per_page=20";
  } else if (filter === null) {
    url_api += "customers/pockets?page=1&per_page=20";
  } else {
    url_api +=
      "customers/accounts/" + filter.refNo + "/pockets?page=1&per_page=20";
  }

  useEffect(() => {
    api
      .get(url_api)
      .then((response) => {
        console.log(response);
        if (response.status === 200) {
          setPockets(response.data.data.content);
          if (query) {
            if (response.data.data.length === 0) {
              toast.error("No pockets found");
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
  } else {
    return (
      <>
        <MDBContainer className="py-4">
          <MDBRow>
            {pocketTypes.map((type) => {
              const pocketOfType = pockets.find((pocket) => {
                return pocket.pocketType === type;
              });
              if (pocketOfType) {
                return (
                  <MDBCol key={pocketOfType.refNo}>
                    <PocketsCard
                      pockets={pockets}
                      setPockets={setPockets}
                      key={pocketOfType.refNo}
                      pocketData={pocketOfType}
                      admin={userData && userData.roles.includes("ROLE_ADMIN")}
                    />
                  </MDBCol>
                );
              }
              return null;
            })}
          </MDBRow>
        </MDBContainer>
        {filter && (
          <MDBContainer className="py-4">
            <MDBRow style={{ paddingLeft: "60px", paddingRight: "40px" }}>
              {pocketTypes.map((type) => {
                const pocketOfType = pockets.find((pocket) => {
                  return pocket.pocketType === type;
                });
                if (!pocketOfType) {
                  return (
                    <MDBCol col="6" sm="4" key={type}>
                      <div
                        style={{ border: "1px solid #ccc", padding: "20px" }}
                      >
                        <PocketForm
                          type={type}
                          accountRefNo={filter.refNo}
                          pockets={pockets}
                          setPockets={setPockets}
                        />
                      </div>
                    </MDBCol>
                  );
                }
                return null;
              })}
            </MDBRow>
          </MDBContainer>
        )}
      </>
    );
  }
}

export default PocketList;
