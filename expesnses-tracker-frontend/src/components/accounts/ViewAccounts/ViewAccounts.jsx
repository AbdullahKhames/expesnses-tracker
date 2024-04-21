import React, { useState, useEffect } from 'react';
import api from './../../api';
import config from './../../config';
import Loading from './../../basics/Loading/loading';
import './pagination.css';
import AccountCard from './AccountCard';

export default function ViewAccounts() {
    const [accounts, setAccounts] = useState([]);
    const [loading, setLoading] = useState(true);
    const [currentPage, setCurrentPage] = useState(1);
    const [totalPages, setTotalPages] = useState(1);
    const [perPage, setPerPage] = useState(5);

    useEffect(() => {
        const url = `${config.api}/accounts?page=${currentPage}&per_page=${perPage}`;

        api.get(url)
            .then((response) => {
                console.log(response);
                if (response.status === 200) {
                    setAccounts(response.data.data.content);
                    setTotalPages(response.data.data.totalPages);
                    setLoading(false);
                }
            })
            .catch((error) => {
                console.error(error);
            });
    }, [currentPage, perPage]);

    const handleNextPage = () => {
        setCurrentPage((prevPage) => prevPage + 1);
    };

    const handlePrevPage = () => {
        setCurrentPage((prevPage) => prevPage - 1);
    };

    if (loading) {
        return <Loading />;
    } else {
        return (
            <div className="container">
                <div className="row">
                    {accounts.map((account) => (
                        <div key={account.refNo} className="col-sm">
                        <AccountCard account={account} />
                    </div>
                    ))}
                </div>
                <div className="pagination-container">
                    <button
                        className="pagination-button"
                        onClick={handlePrevPage}
                        disabled={currentPage === 1}
                    >
                        Prev
                    </button>
                    <span>{currentPage}</span>
                    <button
                        className="pagination-button"
                        onClick={handleNextPage}
                        disabled={currentPage === totalPages}
                    >
                        Next
                    </button>
                </div>
                <div className='pagination-select'>
                    <label htmlFor="perPage">Page Size:</label>
                    <select
                        
                        id="perPage"
                        value={perPage}
                        onChange={(e) => setPerPage(parseInt(e.target.value))}
                    >
                        <option value="1">1</option>
                        <option value="3">3</option>
                        <option value="5">5</option>
                        <option value="10">10</option>
                        <option value="20">20</option>
                        <option value="30">30</option>
                    </select>
                </div>
            </div>
        );
    }
}
