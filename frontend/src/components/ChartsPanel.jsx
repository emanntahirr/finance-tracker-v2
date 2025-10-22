// src/components/ChartsPanel.jsx
import React, { useState, useEffect } from 'react';
import axios from 'axios';

export default function ChartsPanel() {
    const [chartData, setChartData] = useState({ monthly: [], categories: [] });
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        fetchChartData();
    }, []);

    const fetchChartData = async () => {
        try {
            const token = localStorage.getItem('token'); // Assuming you store JWT
            const config = { headers: { Authorization: `Bearer ${token}` } };
            
            const [monthlyRes, categoriesRes] = await Promise.all([
                axios.get('http://localhost:8080/api/charts/monthly-bar', config),
                axios.get('http://localhost:8080/api/charts/category-spending', config)
            ]);

            setChartData({
                monthly: monthlyRes.data,
                categories: categoriesRes.data
            });
            setLoading(false);
        } catch (err) {
            setError('Failed to load chart data');
            setLoading(false);
        }
    };

    if (loading) return (
        <div className="nes-container is-dark with-title text-center py-8">
            <p className="title">📡 ACQUIRING DATA</p>
            <div className="text-2xl">SCANNING FINANCIAL PATTERNS...</div>
        </div>
    );

    if (error) return (
        <div className="nes-container is-dark with-title text-center py-8">
            <p className="title">⚠️ SYSTEM ERROR</p>
            <div className="text-red-400">{error}</div>
        </div>
    );

    return (
        <div className="space-y-6">
            {/* Monthly Bar Chart Display */}
            <div className="nes-container is-dark with-title">
                <p className="title text-xl text-[#FFF9DB]">📈 MONTHLY ENERGY FLOW</p>
                <div className="space-y-4">
                    {chartData.monthly.map(month => (
                        <div key={month.month} className="nes-container is-dark">
                            <div className="flex justify-between mb-2">
                                <span className="font-bold">{month.month}</span>
                                <span className={`font-bold ${month.savings >= 0 ? 'text-[#90ee90]' : 'text-[#ff6347]'}`}>
                                    Net: ${month.savings}
                                </span>
                            </div>
                            <div className="flex space-x-2">
                                <div className="flex-1 bg-red-500 rounded" style={{ height: '20px', width: `${(month.expense / (month.income + month.expense)) * 100}%` }}>
                                    <div className="text-xs text-white p-1">Exp: ${month.expense}</div>
                                </div>
                                <div className="flex-1 bg-green-500 rounded" style={{ height: '20px', width: `${(month.income / (month.income + month.expense)) * 100}%` }}>
                                    <div className="text-xs text-white p-1">Inc: ${month.income}</div>
                                </div>
                            </div>
                        </div>
                    ))}
                </div>
            </div>

            {/* Category Spending */}
            <div className="nes-container is-dark with-title">
                <p className="title text-xl text-[#FFF9DB]">🎯 EXPENSE SECTOR ANALYSIS</p>
                <div className="space-y-3">
                    {chartData.categories.map(cat => (
                        <div key={cat.category} className="flex justify-between items-center">
                            <span>{cat.category}</span>
                            <div className="flex items-center space-x-4">
                                <div className="w-32 bg-gray-700 rounded-full h-4">
                                    <div 
                                        className="bg-red-500 h-4 rounded-full" 
                                        style={{ width: `${cat.percentage}%` }}
                                    ></div>
                                </div>
                                <span className="text-sm">${cat.amount} ({cat.percentage}%)</span>
                            </div>
                        </div>
                    ))}
                </div>
            </div>
        </div>
    );
}