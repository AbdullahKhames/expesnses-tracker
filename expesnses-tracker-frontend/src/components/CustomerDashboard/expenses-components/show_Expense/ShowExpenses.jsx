import React, { useContext, useEffect, useState } from 'react'
import { UserDataContext } from '../../../basics/UserContextProvider/UserContextProvider';
import { useNavigate } from 'react-router-dom';
import api from '../../../api';
import config from '../../../config';
import Loading from '../../../basics/Loading/loading';
import toast, { Toaster } from 'react-hot-toast';


export default function ShowExpenses() {
  const [loading, setLoading] = useState(true)
  const userContext = useContext(UserDataContext);
  const userData = userContext.userData;
  const [expenses, setexpenses] = useState([]);
  const navigate = useNavigate();
  useEffect(() =>{
      if (userData === null) {
          navigate('/login')
      }
      api.get(`${config.api}/customers/expenses?page=1&per_page=100&sortBy=createdAt&sortDirection=DESC`)
      .then((response) => {
          if (response.status === 200) {
              setexpenses(response.data.data.content);
              setLoading(false);
              toast.success("all expenses retrived");
          }
      })
      .catch((err) => { 
          console.error(err);
          toast.error("error loading expenses");
      })
  }, [navigate, userData])
  function handleUpdate(Expense) {
    navigate(`/customers/updateExpense/${Expense.refNo}`, { state: { ExpenseData: Expense } });  
    }

    function handleDelete(refNo) {
      api.delete(`${config.api}/expenses/${refNo}`)
      .then((resp) => {
          toast.success("deleted successfully");
          setexpenses(prevexpenses => prevexpenses.filter(c => c.refNo !== refNo));
      })
      .catch((err) => {
          console.error(err);
          toast.error("cant delete this Expense")
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
          <th scope="col">amount</th>
          <th scope="col">update</th>
          <th scope="col">delete</th>
          </tr>
      </thead>
      <tbody>
      {expenses.map((Expense, index) => (
          <tr key={Expense.refNo || index}>
              <th scope="row">{Expense.refNo}</th>
              <td>{Expense.name}</td>
              <td>{Expense.amount}</td>
              <td>
                  <button className='btn btn-secondary' onClick={() => handleUpdate(Expense)}>Update</button>
              </td>
              <td>
                  <button className='btn btn-danger' onClick={() => handleDelete(Expense.refNo)}>Delete</button>
              </td>
          </tr>
  
      ))}
      </tbody>
      <Toaster/>
      </table>
    </>
    }
}
