import React from "react";
import { useNavigate } from "react-router-dom";

// Note: No d3 or pathRefs needed here

export default function StartPage() {
  const navigate = useNavigate();

  return (
    <div className="relative min-h-screen flex flex-col items-center justify-center bg-[#0a0f2c] text-white overflow-hidden">
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

      <div className="relative z-30 text-center w-full px-4">
        <h1 className="text-8xl md:text-8xl font-silk text-[#FFF9DB] tracking-wider mb-24">
          Finance Tracker
        </h1>
        <button
          type="button"
          className="nes-btn is-primary px-16 py-6 text-3xl font-bold text-[#FFF9DB]"
          onClick={() => navigate("/login")}
        >
          START
        </button>
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