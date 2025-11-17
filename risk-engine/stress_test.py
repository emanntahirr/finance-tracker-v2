from data_loader import load_prices

def run_stress_test(ticker: str, shock: float = -0.2, position: float = 1.0):
    prices = load_prices(ticker, years=3)
    last_price = float(prices.iloc[-1])
    shocked_price = last_price * (1 + shock)
    pnl = (shocked_price - last_price) * position
    loss_pct = shock
    return {
        "last_price": last_price,
        "shocked_price": shocked_price,
        "position": position,
        "pnl": pnl,
        "loss_pct": loss_pct
    }