import React, { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import axios from "axios";
import { useAuth } from '../auth/AuthContext';

export default function LoginPage() {
    const navigate = useNavigate();
    const { login } = useAuth();
    const [formData, setFormData] = useState({
        username: "",
        password: ""
    });
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");
    const [showPassword, setShowPassword] = useState(false);

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData({ ...formData, [name]: value });
    };

    const handleLogin = async (e) => {
        e.preventDefault();
        setLoading(true);
        setError("");

        try {
            console.log("Attempting login with:", {
                username: formData.username,
                normalized: formData.username.toLowerCase().trim()
            });
            
            const response = await axios.post("http://localhost:8080/auth/login", {
                username: formData.username.toLowerCase().trim(),
                password: formData.password
            });

            console.log("Login successful:", response.data);
            
            login(response.data.token, formData.username.toLowerCase().trim());
            navigate("/dashboard");
        } catch (err) {
            console.error("Login error:", err.response?.data);
            setError(err.response?.data?.message || "Login failed. Check credentials.");
        } finally {
            setLoading(false);
        }
    };

    const togglePasswordVisibility = () => {
        setShowPassword(!showPassword);
    };

    return (
        <div className="relative min-h-screen flex flex-col items-center justify-center bg-[#0a0f2c] text-white overflow-hidden">
            {/* Stars Background */}
            <div className="absolute inset-0">
                {[...Array(100)].map((_, i) => (
                    <div
                        key={i}
                        className="absolute bg-[#FDFD96] opacity-90"
                        style={{
                            width: "5px",
                            height: "5px",
                            top: `${Math.random() * 100}%`,
                            left: `${Math.random() * 100}%`,
                            animation: `twinkle ${2 + Math.random() * 3}s infinite`,
                        }}
                    ></div>
                ))}
            </div>

            <div className="relative z-30 w-full max-w-md px-4">
                <h1 className="text-6xl font-silk text-[#FFF9DB] tracking-wider mb-8 text-center">
                    LOGIN
                </h1>

                <div className="nes-container is-dark with-title">
                    <p className="title text-[#FFF9DB]">ACCESS TERMINAL</p>
                    
                    {error && (
                        <div className="nes-container is-dark text-red-400 mb-4 text-center">
                            ⚠️ {error}
                        </div>
                    )}

                    <form onSubmit={handleLogin}>
                        <div className="nes-field mb-4">
                            <label htmlFor="username">USERNAME</label>
                            <input
                                type="text"
                                id="username"
                                name="username"
                                className="nes-input is-dark lowercase-input"
                                value={formData.username}
                                onChange={handleInputChange}
                                placeholder="Enter username"
                                required
                            />
                        </div>

                        <div className="nes-field mb-6 relative">
                            <label htmlFor="password">PASSWORD</label>
                            <div className="relative">
                                <input
                                    type={showPassword ? "text" : "password"}
                                    id="password"
                                    name="password"
                                    className="nes-input is-dark lowercase-input pr-10"
                                    value={formData.password}
                                    onChange={handleInputChange}
                                    placeholder="Enter password"
                                    required
                                />
                                {/* Smaller, better positioned lock button */}
                                <button
                                    type="button"
                                    className="absolute right-2 top-1/2 transform -translate-y-1/2 bg-transparent border-none cursor-pointer text-sm"
                                    onClick={togglePasswordVisibility}
                                    style={{ 
                                        padding: '2px 4px',
                                        fontSize: '14px',
                                        lineHeight: '1'
                                    }}
                                >
                                    {showPassword ? "🔒" : "🔓"}
                                </button>
                            </div>
                        </div>

                        <button 
                            type="submit" 
                            className={`nes-btn is-primary w-full text-xl ${loading ? 'is-disabled' : ''}`}
                            disabled={loading}
                        >
                            {loading ? "AUTHENTICATING..." : "🚀 LOGIN"}
                        </button>
                    </form>

                    <div className="mt-6 text-center">
                        <p className="text-gray-400">New user?</p>
                        <Link to="/signup" className="nes-btn is-success mt-2">
                            CREATE ACCOUNT
                        </Link>
                    </div>
                </div>
            </div>

            <style jsx>{`
                @keyframes twinkle {
                    0%, 100% { opacity: 0.3; }
                    50% { opacity: 1; }
                }
                
                /* Force lowercase display for inputs */
                .lowercase-input {
                    text-transform: lowercase !important;
                }
                
                /* Override NES.css completely */
                .nes-input {
                    text-transform: lowercase !important;
                }
                
                /* Make sure placeholder text is also lowercase */
                .nes-input::placeholder {
                    text-transform: none !important;
                }
            `}</style>
            
            {/* Global CSS override */}
            <style jsx global>{`
                /* This will override NES.css globally for these inputs */
                input.nes-input.is-dark {
                    text-transform: lowercase !important;
                }
            `}</style>
        </div>
    );
}