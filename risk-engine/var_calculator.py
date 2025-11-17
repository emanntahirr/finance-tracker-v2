import numpy as np
from data_loader import daily_returns

def historical_var(ticker: str, confidence: float = 0.95, years: int = 3) -> float:
    #get array of returns r
    r = daily_returns(ticker, years)
    q = np.quantile(r, 1 - confidence)#finds the value below which (1-confidence) of the data falls
    return float(-q)

def volatility_annualized(ticker: str, years: int = 3) -> float:
    r = daily_returns(ticker, years)
    vol = r.std() * np.sqrt(252)
    return float(vol)