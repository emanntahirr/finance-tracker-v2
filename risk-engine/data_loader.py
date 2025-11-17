import pandas as pd # data table
import yfinance as yf
from functools import lru_cache
from datetime import datetime, timedelta

@lru_cache (maxsize = 128)
def load_prices(ticker: str, years: int =3) -> pd.Series: #stock ticker and years of data to load
    end = datetime.utcnow() #get current UTC time
    start = end - timedelta(days=365*years)# subtract N years to get start date
    data = yf.download(ticker, start = start.strftime("%Y-%m-%d"), end=end.strftime("%Y-%m-%d"), progress=False)
    #download price data from yahoo
    if "Adj Close" in data.columns: #checks to see if theres an adjusted close column
        return data["Adj Close"].dropna()
    return data["Close"].dropna() # fall back to close

def daily_returns(ticker: str, years: int = 3) -> pd.Series:
    prices = load_prices(ticker, years)# fetches the series of prices
    returns = prices.pct_change().dropna()# calculates the percentage change between each day and previous day
    return returns