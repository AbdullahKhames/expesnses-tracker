import React, { useEffect, useState, useCallback } from 'react';
import { Navigate } from 'react-router-dom';
import { jwtDecode } from 'jwt-decode';

export default function ProtectedRoute(props) {
  const [userData, setUserData] = useState(null);
  const tokenToUserData = (decodedToken) => {
    return {
      'name': decodedToken.name,
      'email': decodedToken.email,
      'roles': decodedToken.roles,
      'id': decodedToken.refNo,
      'refNo': decodedToken.refNo,
      'customerId': decodedToken.customerId,
    };
  };

  const saveUserData = useCallback(() => {
    let encodedToken = localStorage.getItem('access_token');
    if (encodedToken !== null) {
      let decodedToken = jwtDecode(encodedToken);
      setUserData(tokenToUserData(decodedToken));
    } else {
      setUserData(null);
    }
  }, []);
  if (userData === null && localStorage.getItem('access_token')) {
   saveUserData();
  }

  useEffect(() => {
    saveUserData();
  }, [localStorage.getItem('access_token')]);


function rolesAnyMatch(propsRoles, userRoles){
  return propsRoles.some((role) => userRoles.includes(role));
}
  function getRoleFromUserData() {
   if (userData === null && localStorage.getItem('access_token')) {
      saveUserData();
     }
   if (userData && userData.roles !== null){
      return userData.roles;
   }
   else{
      return null;
   }
  
 }
  
  if (userData === null) {
    return <Navigate to={'/login'} />;
  } else {
    const userRoles = getRoleFromUserData();
    if (!props.roles || rolesAnyMatch(props.roles, userRoles)) {
      return props.children;
    }

    return <Navigate to={'/forbidden'} />;
  }
}
