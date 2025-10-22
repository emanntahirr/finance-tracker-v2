import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useInvestmentAPI } from '../hooks/useInvestmentAPI';
import { useFinanceAPI } from '../hooks/useFinanceAPI';

export default function InvestmentsPage() {
    const navigate = useNavigate();
    const { investments, loading, error, addInvestment } = useInvestmentAPI();
    const { transactions } = useFinanceAPI();

    const availableBalance = transactions.reduce((acc, item) => (acc += item.amount), 0).toFixed(2);

    const [newInvestment, setNewInvestment] = useState({
        symbol: '',
        shares: '',
        type: 'Stock',
    });

    const formatCurrency = (amount) => {
        if (amount === null || amount === undefined) return '£0.00';
        return `£${Math.abs(amount).toFixed(2)}`;
    };

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setNewInvestment({ ...newInvestment, [name]: value });
    };

    const handleFormSubmit = async (e) => {
        e.preventDefault();

        if (!newInvestment.symbol || !newInvestment.shares) {
            alert("Please enter a stock symbol and number of shares.");
            return;
        }

        const shares = parseFloat(newInvestment.shares);

        // 🧠 Backend now handles fetching the live market price
        const payload = {
            symbol: newInvestment.symbol,
            shares: shares,
            type: newInvestment.type
        };

        console.log("Sending payload:", payload);
        const success = await addInvestment(payload);

        if (success) {
            setNewInvestment({ symbol: '', shares: '', type: 'Stock' });
            alert("Investment added successfully at live market price!");
        } else {
            alert("Failed to add investment. Check console for errors.");
        }
    };

    if (loading) {
        return (
            <div className="min-h-screen bg-[#0a0f2c] text-white flex items-center justify-center text-4xl font-silk">
                📈 FETCHING PORTFOLIO DATA...
            </div>
        );
    }

    if (error) {
        return (
            <div className="min-h-screen bg-[#0a0f2c] text-[#ff6347] flex items-center justify-center text-3xl font-bold p-10 text-center nes-container is-dark">
                ⚠️ {error}
            </div>
        );
    }

    return (
        <div className="min-h-screen bg-[#0a0f2c] text-white p-8 flex flex-col items-center">
            <div className="w-full max-w-4xl flex justify-start mb-6">
                <button
                    className="nes-btn is-dark"
                    onClick={() => navigate("/dashboard/finances")}
                >
                    &lt; Back to Finances
                </button>
            </div>

            <h1 className="text-4xl font-silk text-[#FFF9DB] tracking-wider mb-8 text-center">
                Investment Portfolio Manager
            </h1>

            <div className="max-w-4xl w-full nes-container is-dark with-title mb-8 text-center">
                <p className="title text-xl text-[#FFF9DB]">Total Available Funds</p>
                <p className={`text-4xl font-bold ${availableBalance >= 0 ? 'text-[#90ee90]' : 'text-[#ff6347]'}`}>
                    {formatCurrency(availableBalance)}
                </p>
            </div>

            {/* A: Add New Investment Form */}
            <div className="max-w-4xl w-full nes-container is-dark with-title mb-10">
                <p className="title text-xl text-[#FFF9DB]">Initiate New Investment</p>
                <form onSubmit={handleFormSubmit}>
                    <div className="nes-field mb-4">
                        <label htmlFor="symbol_field">Stock Symbol</label>
                        <input
                            type="text"
                            id="symbol_field"
                            name="symbol"
                            className="nes-input is-dark text-white"
                            value={newInvestment.symbol}
                            onChange={handleInputChange}
                            placeholder="e.g. TSLA, AAPL"
                            required
                        />
                    </div>

                    <div className="nes-field mb-4">
                        <label htmlFor="shares_field">Number of Shares</label>
                        <input
                            type="number"
                            id="shares_field"
                            name="shares"
                            className="nes-input is-dark text-white"
                            value={newInvestment.shares}
                            onChange={handleInputChange}
                            placeholder="10.0"
                            step="0.01"
                            min="0.01"
                            required
                        />
                    </div>

                    <div className="nes-field mb-6">
                        <label htmlFor="type_select">Type</label>
                        <div className="nes-select">
                            <select
                                id="type_select"
                                name="type"
                                className="w-full bg-[#0a0f2c] text-white border-2 border-white cursor-pointer"
                                value={newInvestment.type}
                                onChange={handleInputChange}
                            >
                                <option value="Stock">Stock</option>
                                <option value="Crypto">Crypto</option>
                                <option value="Mutual Fund">Mutual Fund</option>
                                <option value="Real Estate">Real Estate</option>
                                <option value="Other">Other</option>
                            </select>
                        </div>
                    </div>

                    <button type="submit" className="nes-btn is-success w-full text-xl">
                        LOG INVESTMENT
                    </button>
                </form>
            </div>

            {/* B: Investment Portfolio List */}
            <div className="max-w-4xl w-full nes-container is-dark with-title mb-10">
                <p className="title text-xl text-[#FFF9DB]">Investment Portfolio</p>

                {investments.length === 0 ? (
                    <div className="text-center py-8">
                        <p className="text-gray-400 text-lg mb-4">No investments yet</p>
                        <p className="text-gray-500 text-sm">Add your first investment using the form above</p>
                    </div>
                ) : (
                    <ul className="h-64 overflow-y-auto pr-4">
                        {investments.map(inv => (
                            <li key={inv.id} className="flex justify-between items-center py-3 border-b border-gray-700 last:border-b-0">
                                <div className="flex flex-col">
                                    <span className="text-lg font-bold">{inv.symbol}</span>
                                    <span className="text-sm text-gray-400">{inv.type} • {inv.shares} shares</span>
                                    <span className={`text-sm font-bold ${(inv.profitLoss || 0) >= 0 ? 'text-[#90ee90]' : 'text-[#ff6347]'}`}>
                                        P/L: {(inv.profitLoss || 0) >= 0 ? '+' : ''}{formatCurrency(inv.profitLoss || 0)}
                                    </span>
                                </div>
                                <div className="text-right">
                                    <span className="text-xl font-bold">
                                        {formatCurrency(inv.currentValue || inv.totalCostBasis)}
                                    </span>
                                    <span className="block text-xs text-gray-500">
                                        Cost: {formatCurrency(inv.totalCostBasis)}
                                    </span>
                                    <span className="block text-xs text-gray-500">
                                        Current: £{inv.currentPricePerShare?.toFixed(2) || inv.purchasePricePerShare?.toFixed(2)}
                                    </span>
                                </div>
                            </li>
                        ))}
                    </ul>
                )}
            </div>
        </div>
    );
}
