import React, { useContext, useEffect, useState } from 'react'
import { UserDataContext } from '../../../basics/UserContextProvider/UserContextProvider';
import { useNavigate } from 'react-router-dom';
import api from './../../../api';
import config from './../../../config';
import Loading from './../../../basics/Loading/loading';
import toast, { Toaster } from 'react-hot-toast';


export default function ShowCategories() {
  const [loading, setLoading] = useState(true)
  const userContext = useContext(UserDataContext);
  const userData = userContext.userData;
  const [categories, setcategories] = useState([]);
  const navigate = useNavigate();
  useEffect(() =>{
      if (userData === null) {
          navigate('/login')
      }
      api.get(`${config.api}/categories?page=1&per_page=100`)
      .then((response) => {
          if (response.status === 200) {
              setcategories(response.data.data.content);
              setLoading(false);
              toast.success("all categories retrived");
          }
      })
      .catch((err) => { 
          console.error(err);
          toast.error("error loading categories");
      })
  }, [navigate, userData])
  function handleUpdate(category) {
    navigate(`/admin/updateCategory/${category.refNo}`, { state: { categoryData: category } });  
    }

    function handleDelete(refNo) {
      api.delete(`${config.api}/categories/${refNo}`)
      .then((resp) => {
          toast.success("deleted successfully");
          setcategories(prevCategories => prevCategories.filter(c => c.refNo !== refNo));
      })
      .catch((err) => {
          console.error(err);
          toast.error("cant delete this category")
      })
    }

    if (loading) {
      return <Loading/>
    }
    else {
      return <>
      <table className="table">
      <thead className='table-dark'>
          <tr>
          {/* <th scope="col">id</th> */}
          <th scope="col">refNo</th>
          <th scope="col">name</th>
          <th scope="col">update</th>
          <th scope="col">deelete</th>
          </tr>
      </thead>
      <tbody>
      {categories.map((category, index) => (
          <tr key={category.refNo || index}>
              <th scope="row">{category.refNo}</th>
              <td>{category.name}</td>
              <td>
                  <button className='btn btn-secondary' onClick={() => handleUpdate(category)}>Update</button>
                  <Toaster/>
              </td>
              <td>
                  <button className='btn btn-danger' onClick={() => handleDelete(category.refNo)}>Delete</button>
              </td>
          </tr>
  
      ))}
      </tbody>
      </table>
    </>
    }
}
