// src/components/Dashboard.jsx
import React, { useEffect, useRef, useState } from "react";
import { useNavigate } from "react-router-dom";
import * as d3 from "d3";
import { useAuth } from '../auth/AuthContext';

// Import images now that they are in the src folder
import ProfileImage from "../assets/Profile.png";
import FinancesImage from "../assets/Finances.png";

export default function Dashboard() {
  const { logout, user } = useAuth();
  const navigate = useNavigate();
  const svgRef = useRef(null);
  const pathRefs = {
    arc1: useRef(null),
    arc2: useRef(null),
    arc3: useRef(null),
    arc4: useRef(null),
  };

  const [pos, setPos] = useState({
    profile: null,
    finances: null,
  });

  const fractions = {
    profile: 0.4,
    finances: 0.75,
  };

  useEffect(() => {
    function computePositions() {
      if (!pathRefs.arc2.current || !pathRefs.arc3.current) return;
      const newPos = {};

      const getPoint = (ref, fraction) => {
        if (!ref.current) return null;
        const L = ref.current.getTotalLength();
        return ref.current.getPointAtLength(L * fraction);
      };

      newPos.profile = getPoint(pathRefs.arc2, fractions.profile);
      newPos.finances = getPoint(pathRefs.arc3, fractions.finances);

      setPos(newPos);
    }

    computePositions();
    window.addEventListener("resize", computePositions);
    return () => window.removeEventListener("resize", computePositions);
  }, []);

  const go = (path) => {
    if (path === "profile") navigate("/profile");
    if (path === "finances") navigate("/dashboard/finances");
  };

  return (
    <div className="w-screen h-screen bg-[#0a0f2c] text-white relative overflow-hidden">
      {/* Add logout button in top corner */}
      <div className="absolute top-4 right-4 z-50">
        <div className="nes-container is-dark flex items-center space-x-4">
          <span>Welcome, {user?.username}</span>
          <button
            onClick={logout}
            className="nes-btn is-error"
          >
            🚪 Logout
          </button>
        </div>
      </div>
      
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
      
      <style jsx>{`
        @keyframes twinkle {
          0%, 100% { opacity: 0.3; }
          50% { opacity: 1; }
        }
      `}</style>

      <svg
        ref={svgRef}
        viewBox="0 0 1920 1080"
        className="absolute inset-0 w-full h-full pointer-events-none z-10"
        xmlns="http://www.w3.org/2000/svg"
      >
        <>
          <path
            ref={pathRefs.arc1}
            d="M 0 400 Q 400 250, 600 0"
            stroke="#FFF9DB"
            strokeWidth="2"
            fill="none"
          />
          <path
            ref={pathRefs.arc2}
            d="M 0 1000 Q 800 800, 1200 0"
            stroke="#FFF9DB"
            strokeWidth="2"
            fill="none"
          />
          <path
            ref={pathRefs.arc3}
            d="M 0 1600 Q 1200 1350, 1700 0"
            stroke="#FFF9DB"
            strokeWidth="2"
            fill="none"
          />
          <path
            ref={pathRefs.arc4}
            d="M 0 2200 Q 1600 1900, 2200 0"
            stroke="#FFF9DB"
            strokeWidth="2"
            fill="none"
          />

          {pos.profile && (
            <g
              className="cursor-pointer pointer-events-auto"
              onClick={() => go("profile")}
            >
              <image
                href={ProfileImage}
                x={pos.profile.x - 175}
                y={pos.profile.y - 175}
                width="350"
                height="350"
              />

              <text
                x={pos.profile.x}
                y={pos.profile.y + 195} 
                fontSize="24"
                fill="#FFF9DB"
                textAnchor="middle"
              >
                PROFILE
              </text>
            </g>
          )}

          {pos.finances && (
            <g
              className="cursor-pointer pointer-events-auto"
              onClick={() => go("finances")}
            >
              <image
                href={FinancesImage}
                x={pos.finances.x - 180}
                y={pos.finances.y - 180}
                width="360"
                height="360"
              />
              <text
                x={pos.finances.x}
                y={pos.finances.y + 200}
                fontSize="24"
                fill="#FFF9DB"
                textAnchor="middle"
              >
                FINANCES
              </text>
            </g>
          )}
        </>
      </svg>
    </div>
  );
}