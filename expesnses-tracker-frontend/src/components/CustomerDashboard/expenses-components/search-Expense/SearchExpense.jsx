import React, {useState, useEffect, useContext} from 'react'
import toast, { Toaster } from 'react-hot-toast';
import api from '../../../api';
import config from '../../../config';
import Loading from '../../../basics/Loading/loading';
import { useNavigate } from 'react-router-dom';
import { UserDataContext } from '../../../basics/UserContextProvider/UserContextProvider';


export default function SearchExpense() {
  const [searchTerm, setSearchTerm] = useState('');
  const [filterType, setFilterType] = useState('refNo');
  const [expenses, setexpenses] = useState([]);
  const [loading, setLoading] = useState(true);
  const [isLoading, setisLoading] = useState(false);

  const userContext = useContext(UserDataContext);
  const userData = userContext.userData;

  const navigate = useNavigate();
  useEffect(() => {
    if (userData) {
      setLoading(false);
    }
  }, [userData]);

  const handleSearch = () => {
    setisLoading(true);
    if (searchTerm !== '') {
      api.get(`${config.api}/expenses/${filterType}/${searchTerm}`)
        .then(response => {
          toast.success(response.data.message)
          if(response.data.code === 800){
            if (Array.isArray(response.data.data) || Array.isArray(response.data.data.content)) {
              setexpenses(response.data.data.content);
            } else {
              setexpenses([response.data.data]);
            }
          }
          setisLoading(false)
        })
        .catch(error => {
          console.error('Error fetching data: ', error);
          toast.error(error.errorMessage);
          setisLoading(false)
        });
    }
    setisLoading(false)
  };


  function handleUpdate(expense) {
    navigate(`/admin/updateexpense/${expense.refNo}`, { state: { expenseData: expense } });  
    }
    function handleDelete(refNo) {
      api.delete(`${config.api}/expenses/${refNo}`)
      .then((resp) => {
          toast.success("deleted successfully");
          setexpenses(expenses.filter((expense) => {
            return expense.refNo !== refNo;
          }))
      })
      .catch((err) => {
          console.error(err);
          toast.error("cant delete this expense")
      })
    }

  
    if (loading){
      return <Loading/>
    } else {
      return <>
       <div>
            <input
              type="text"
              placeholder="Search..."
              value={searchTerm}
              onChange={e => setSearchTerm(e.target.value)}
            />
            <select value={filterType} onChange={e => setFilterType(e.target.value)}>
              <option value="name">Name</option>
              <option value="refNo">Reference Number</option>
            </select>
            {isLoading? <button type='button' className='fas fa-spinner fa-spin'></button>: 
            <button className="btn btn-primary btn-block" onClick={handleSearch}>Search</button>}
            <div className="row">
                {expenses.map((expense, index) => (
                  <div key={expense.refNo || index} className="col-md-10 mb-4">
                    <div className="card">
                      <div className="card-body">
                        <h5 className="card-title">{expense.name}</h5>


                        <div className="d-flex justify-content-between">
                          <button
                            className="btn btn-primary"
                            onClick={(e) => {
                              e.stopPropagation();
                              handleUpdate(expense);
                            }}
                          >
                            Update
                          </button>
                          <Toaster/>
                          <button
                            className="btn btn-danger"
                            onClick={(e) => {
                              e.stopPropagation();
                              handleDelete(expense.refNo);
                            }}
                          >
                            Delete
                          </button>
                        </div>
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          </>
        }
}
