// src/hooks/useFinanceAPI.js
import { useState, useEffect, useCallback } from 'react';
import axios from 'axios';
import { useAuth } from '../auth/AuthContext';

const API_BASE_URL = 'http://localhost:8080/api';

export const useFinanceAPI = () => {
    const [transactions, setTransactions] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const { getToken } = useAuth();

    // Create axios instance with auth header
    const authAxios = axios.create({
        baseURL: API_BASE_URL,
    });

    const fetchTransactions = useCallback(async () => {
        setLoading(true);
        setError(null);
        try {
            const token = getToken();
            if (!token) {
                setError("Not authenticated. Please login first.");
                setLoading(false);
                return;
            }

            const response = await authAxios.get('/transactions', {
                headers: { Authorization: `Bearer ${token}` }
            });
            setTransactions(response.data.data || response.data);
        } catch (err) {
            console.error('Error fetching transactions:', err);
            if (err.response?.status === 403) {
                setError("Access denied. Token may be expired.");
            } else {
                setError("Failed to connect to Finance Server. Check backend");
            }
        } finally {
            setLoading(false);
        }
    }, [getToken]);

    const addTransaction = async (transactionData) => {
        try {
            const token = getToken();
            if (!token) {
                setError("Not authenticated. Please login first.");
                return false;
            }

            const finalAmount = transactionData.type === 'expense'
                ? -Math.abs(transactionData.amount)
                : Math.abs(transactionData.amount);

            const payload = { ...transactionData, amount: finalAmount };

            const response = await authAxios.post('/transactions', payload, {
                headers: { Authorization: `Bearer ${token}` }
            });
            setTransactions(prev => [response.data.data || response.data, ...prev]);
            return true;

        } catch (err) {
            console.error('Error adding transaction:', err);
            setError("Failed to log transaction. Server denied entry");
            return false;
        }
    };

    useEffect(() => {
        fetchTransactions();
    }, [fetchTransactions]);
    
    return {
        transactions,
        loading,
        error,
        addTransaction
    };
};