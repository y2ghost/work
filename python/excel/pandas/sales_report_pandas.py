import pandas as pd
from pathlib import Path

this_dir = Path(__file__).resolve().parent
parts = []

for path in (this_dir / "sales_data").rglob("*.xls*"):
    print(f'Reading {path.name}')
    part = pd.read_excel(path, index_col="transaction_id")
    parts.append(part)

df = pd.concat(parts)
pivot = pd.pivot_table(df, index="transaction_date", columns="store",
    values="amount", aggfunc="sum")

# 重新采样到月底，并分配一个索引名
summary = pivot.resample("M").sum()
summary.index.name = "Month"
summary.to_excel(this_dir / "sales_report_pandas.xlsx")
