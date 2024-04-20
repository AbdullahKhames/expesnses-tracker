import React, { createContext, useState, useEffect } from "react";
import { jwtDecode } from "jwt-decode";

export let UserDataContext = createContext();

export default function UserContextProvider(props) {
  const [userData, setUserData] = useState(null);

  function TokenToUserData(decodedToken) {
    if (decodedToken) {
      return {
        name: decodedToken.name,
        email: decodedToken.email,
        roles: decodedToken.roles,
        id: decodedToken.refNo,
        refNo: decodedToken.refNo,
        customerId: decodedToken.customerId,
      };
    }
    return null;
  }

  function saveUserData(userDetails) {
    if (userDetails !== null && userDetails !== undefined) {
      setUserData(userDetails);
    } else if (localStorage.getItem("access_token")) {
      let encodedToken = localStorage.getItem("access_token");
      if (encodedToken !== null && encodedToken !== undefined) {
        let decodedToken = jwtDecode(encodedToken);
        setUserData(TokenToUserData(decodedToken));
        return userData;
      } else {
        setUserData(null);
        localStorage.removeItem("access_token");
        localStorage.removeItem("refresh_token");
        localStorage.removeItem("deviceId");
        return null;
      }
    }
  }

  useEffect(() => {
    saveUserData();
  }, [localStorage.getItem("access_token")]);
  return (
    <>
      <UserDataContext.Provider
        value={{ userData, saveUserData, setUserData, TokenToUserData }}
      >
        {props.children}
      </UserDataContext.Provider>
    </>
  );
}
