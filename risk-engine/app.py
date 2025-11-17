from fastapi import FastAPI, Query
from fastapi.middleware.cors import CORSMiddleware
from var_calculator import historical_var, volatility_annualized
from stress_test import run_stress_test

app = FastAPI(title="Risk Engine")

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_methods=["*"],
    allow_headers=["*"],
)

@app.get("/risk/var")
def get_var(ticker: str, confidence: float = 0.95, years: int = 3):
    var_value = historical_var(ticker, confidence, years)
    return {"ticker": ticker, "confidence": confidence, "years": years, "var": var_value}

@app.get("/risk/volatility")
def get_vol(ticker: str, years: int = 3):
    vol = volatility_annualized(ticker, years)
    return {"ticker": ticker, "years": years, "vol_annualized": vol}

@app.get("/risk/stress")
def get_stress(ticker: str, shock: float = -0.2, position: float = 1.0):
    result = run_stress_test(ticker, shock, position)
    result["ticker"] = ticker
    result["shock"] = shock
    return result
