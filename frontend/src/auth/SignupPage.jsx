import React, { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import axios from "axios";

export default function SignupPage() {
    const navigate = useNavigate();
    const [formData, setFormData] = useState({
        username: "",
        email: "",
        password: ""
    });
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");
    const [success, setSuccess] = useState("");

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData({ ...formData, [name]: value });
    };

    const handleSignup = async (e) => {
        e.preventDefault();
        setLoading(true);
        setError("");
        setSuccess("");

        try {
            await axios.post("http://localhost:8080/auth/register", {
                username: formData.username,
                email: formData.email,
                password: formData.password
            });

            setSuccess("Account created successfully! Redirecting to login...");
            setTimeout(() => {
                navigate("/login");
            }, 2000);
        } catch (err) {
            setError(err.response?.data || "Registration failed. Try different credentials.");
        } finally {
            setLoading(false);
        }
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
                    SIGN UP
                </h1>

                <div className="nes-container is-dark with-title">
                    <p className="title text-[#FFF9DB]">NEW RECRUIT</p>
                    
                    {error && (
                        <div className="nes-container is-dark text-red-400 mb-4 text-center">
                            ⚠️ {error}
                        </div>
                    )}

                    {success && (
                        <div className="nes-container is-dark text-green-400 mb-4 text-center">
                            ✅ {success}
                        </div>
                    )}

                    <form onSubmit={handleSignup}>
                        <div className="nes-field mb-4">
                            <label htmlFor="username">USERNAME</label>
                            <input
                                type="text"
                                id="username"
                                name="username"
                                className="nes-input is-dark text-white"
                                value={formData.username}
                                onChange={handleInputChange}
                                placeholder="Choose username"
                                required
                            />
                        </div>

                        <div className="nes-field mb-4">
                            <label htmlFor="email">EMAIL</label>
                            <input
                                type="email"
                                id="email"
                                name="email"
                                className="nes-input is-dark text-white"
                                value={formData.email}
                                onChange={handleInputChange}
                                placeholder="your.email@example.com"
                                required
                            />
                        </div>

                        <div className="nes-field mb-6">
                            <label htmlFor="password">PASSWORD</label>
                            <input
                                type="password"
                                id="password"
                                name="password"
                                className="nes-input is-dark text-white"
                                value={formData.password}
                                onChange={handleInputChange}
                                placeholder="Create password"
                                required
                            />
                        </div>

                        <button 
                            type="submit" 
                            className={`nes-btn is-success w-full text-xl ${loading ? 'is-disabled' : ''}`}
                            disabled={loading}
                        >
                            {loading ? "CREATING ACCOUNT..." : "🚀 LAUNCH ACCOUNT"}
                        </button>
                    </form>

                    <div className="mt-6 text-center">
                        <p className="text-gray-400">Already have an account?</p>
                        <Link to="/login" className="nes-btn is-primary mt-2">
                            BACK TO LOGIN
                        </Link>
                    </div>
                </div>
            </div>

            <style jsx>{`
                @keyframes twinkle {
                    0%, 100% { opacity: 0.3; }
                    50% { opacity: 1; }
                }
            `}</style>
        </div>
    );
}