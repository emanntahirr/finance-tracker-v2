// src/hooks/useInvestmentAPI.js
import { useState, useEffect, useCallback } from 'react';
import api from '../hooks/api';
import { useAuth } from '../auth/AuthContext';

export const useInvestmentAPI = () => {
    const [investments, setInvestments] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const { getToken } = useAuth(); // still used for login state checking

    const fetchInvestments = useCallback(async () => {
        setLoading(true);
        setError(null);

        try {
            const token = getToken();
            if (!token) {
                setError("Not authenticated. Please login first.");
                setLoading(false);
                return;
            }

            const response = await api.get('/investments'); // ✅ token auto-added
            setInvestments(response.data);
        } catch (err) {
            console.error('Error fetching investments:', err);
            if (err.response?.status === 403) {
                setError("Access denied. Please login again.");
            } else {
                setError("Failed to load investment portfolio. Check backend server and CORS.");
            }
        } finally {
            setLoading(false);
        }
    }, [getToken]);

    const addInvestment = async (investmentData) => {
        try {
            const token = getToken();
            if (!token) {
                setError("Not authenticated. Please login first.");
                return false;
            }

            const response = await api.post('/investments', investmentData); // ✅ token auto-added
            setInvestments(prev => [response.data, ...prev]);
            return true;
        } catch (err) {
            console.error('Error adding investment:', err);
            setError(`Failed to log investment: ${err.response?.data?.message || err.message}`);
            return false;
        }
    };

    useEffect(() => {
        fetchInvestments();
    }, [fetchInvestments]);
    
    return {
        investments,
        loading,
        error,
        addInvestment,
    };
};
