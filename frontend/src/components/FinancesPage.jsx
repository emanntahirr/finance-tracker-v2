// src/components/FinancesPage.jsx
import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useFinanceAPI } from "../hooks/useFinanceAPI";
import InvestmentsPage from "./InvestmentsPage";
import ChartsPanel from "./ChartsPanel";

export default function FinancesPage() {
    const navigate = useNavigate();
    const { transactions, loading, error, addTransaction } = useFinanceAPI();

    // State to manage which panel is active
    const [activePanel, setActivePanel] = useState('transactions'); // 'transactions', 'investments', 'charts'

    const [newTransaction, setNewTransaction] = useState({
        text: '',
        amount: '',
        type: 'expense',
        category: categories[0]
    });

    // Financial calculations
    const balance = transactions.reduce((acc, item) => (acc += item.amount), 0).toFixed(2);
    const income = transactions
        .filter(item => item.amount > 0)
        .reduce((acc, item) => (acc += item.amount), 0)
        .toFixed(2);
    const expense = (
        transactions
            .filter(item => item.amount < 0)
            .reduce((acc, item) => (acc += item.amount), 0) * -1
    ).toFixed(2);

    const formatCurrency = (amount) => `$${Math.abs(amount).toFixed(2)}`;

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setNewTransaction({ ...newTransaction, [name]: value });
    };

    const handleFormSubmit = async (e) => {
        e.preventDefault();
        if (!newTransaction.text || !newTransaction.amount) {
            alert("Please enter both text and a positive amount.");
            return;
        }
        const success = await addTransaction(newTransaction);
        if (success) {
            setNewTransaction({
                text: '', amount: '', type: 'expense', category: categories[0]
            });
        }
    };

    if (loading) {
        return <div className="min-h-screen bg-[#0a0f2c] text-white flex items-center justify-center text-4xl font-silk">
            🛰️ DATA STREAM INITIATED...
        </div>;
    }

    if (error) {
        return <div className="min-h-screen bg-[#0a0f2c] text-[#ff6347] flex items-center justify-center text-3xl font-bold p-10 text-center nes-container is-dark">
            🔴 ERROR: {error}
        </div>;
    }

    return (
        <div className="min-h-screen bg-[#0a0f2c] text-white p-8 flex flex-col items-center">
            
            {/* Back Button */}
            <div className="w-full max-w-6xl flex justify-start mb-6">
                <button
                    className="nes-btn is-dark"
                    onClick={() => navigate("/dashboard")}
                >
                    &lt; Back to Mission Control
                </button>
            </div>

            <h1 className="text-4xl font-silk text-[#FFF9DB] tracking-wider mb-8 text-center">
                FINANCE COMMAND CENTER
            </h1>

            {/* Navigation Tabs - Retro Style */}
            <div className="max-w-6xl w-full mb-8">
                <div className="flex justify-center space-x-4">
                    <button
                        className={`nes-btn ${activePanel === 'transactions' ? 'is-primary' : 'is-dark'}`}
                        onClick={() => setActivePanel('transactions')}
                    >
                        💳 TRANSACTIONS
                    </button>
                    <button
                        className={`nes-btn ${activePanel === 'investments' ? 'is-warning' : 'is-dark'}`}
                        onClick={() => setActivePanel('investments')}
                    >
                        📈 INVESTMENTS
                    </button>
                    <button
                        className={`nes-btn ${activePanel === 'charts' ? 'is-success' : 'is-dark'}`}
                        onClick={() => setActivePanel('charts')}
                    >
                        📊 CHARTS
                    </button>
                </div>
            </div>

            {/* Financial Overview - Always Visible */}
            <div className="max-w-6xl w-full nes-container is-dark with-title mb-8 text-center">
                <p className="title text-xl text-[#FFF9DB]">MISSION STATUS</p>
                <div className="grid grid-cols-3 gap-6 text-center">
                    <div>
                        <h4 className="text-sm text-gray-400">TOTAL BALANCE</h4>
                        <p className={`text-4xl font-bold ${balance >= 0 ? 'text-[#90ee90]' : 'text-[#ff6347]'}`}>
                            {formatCurrency(balance)}
                        </p>
                    </div>
                    <div>
                        <h4 className="text-sm text-green-400">INCOME STREAM</h4>
                        <p className="text-4xl font-bold text-[#90ee90]">
                            {formatCurrency(income)}
                        </p>
                    </div>
                    <div>
                        <h4 className="text-sm text-red-400">EXPENSE DRAIN</h4>
                        <p className="text-4xl font-bold text-[#ff6347]">
                            {formatCurrency(expense)}
                        </p>
                    </div>
                </div>
            </div>

            {/* Dynamic Panel Content */}
            <div className="max-w-6xl w-full">
                {activePanel === 'transactions' && (
                    <div className="space-y-6">
                        {/* Add Transaction Form */}
                        <div className="nes-container is-dark with-title">
                            <p className="title text-xl text-[#FFF9DB]">LOG NEW TRANSMISSION</p>
                            <form onSubmit={handleFormSubmit}>
                                <div className="flex justify-around mb-4">
                                    <label>
                                        <input type="radio" className="nes-radio" name="type" value="expense"
                                            checked={newTransaction.type === 'expense'} onChange={handleInputChange} />
                                        <span>🔴 EXPENSE</span>
                                    </label>
                                    <label>
                                        <input type="radio" className="nes-radio" name="type" value="income"
                                            checked={newTransaction.type === 'income'} onChange={handleInputChange} />
                                        <span>🟢 INCOME</span>
                                    </label>
                                </div>

                                <div className="nes-field mb-4">
                                    <label htmlFor="text_field">Transaction Description</label>
                                    <input type="text" id="text_field" name="text" className="nes-input is-dark text-white"
                                        value={newTransaction.text} onChange={handleInputChange} placeholder="e.g. Starship Maintenance" />
                                </div>

                                <div className="nes-field mb-4">
                                    <label htmlFor="amount_field">Amount (GBP)</label>
                                    <input type="number" id="amount_field" name="amount" className="nes-input is-dark text-white"
                                        value={newTransaction.amount} onChange={handleInputChange} placeholder="100.00" />
                                </div>

                                <div className="nes-field mb-6">
                                    <label htmlFor="category_select">Sector</label>
                                    <div className="nes-select">
                                        <select id="category_select" name="category" className="w-full bg-[#0a0f2c] text-white border-2 border-white cursor-pointer"
                                            value={newTransaction.category} onChange={handleInputChange}>
                                            {categories.map(cat => (
                                                <option key={cat} value={cat} className="bg-[#0a0f2c] text-white">{cat}</option>
                                            ))}
                                        </select>
                                    </div>
                                </div>

                                <button type="submit" className="nes-btn is-primary w-full text-xl">
                                    ⚡ LOG TRANSMISSION
                                </button>
                            </form>
                        </div>

                        {/* Transaction History */}
                        <div className="nes-container is-dark with-title">
                            <p className="title text-xl text-[#FFF9DB]">TRANSACTION LOG</p>
                            <ul className="h-64 overflow-y-auto pr-4">
                                {transactions.map(transaction => (
                                    <li key={transaction.id} className="flex justify-between items-center py-2 border-b border-gray-700 last:border-b-0">
                                        <div className="flex flex-col">
                                            <span className="text-lg">{transaction.text}</span>
                                            <span className="text-sm text-gray-500">{transaction.category}</span>
                                        </div>
                                        <span className={`text-xl font-bold ${transaction.amount > 0 ? 'text-[#90ee90]' : 'text-[#ff6347]'}`}>
                                            {transaction.amount > 0 ? '+' : '-'}{formatCurrency(transaction.amount)}
                                        </span>
                                    </li>
                                ))}
                            </ul>
                        </div>
                    </div>
                )}

                {activePanel === 'investments' && (
                    <InvestmentsPage />
                )}

                {activePanel === 'charts' && (
                    <ChartsPanel />
                )}
            </div>
        </div>
    );
}

const categories = ["Food", "Transportation", "Salary", "Bills", "Healthcare", "Other"];