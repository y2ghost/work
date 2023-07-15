import pandas as pd
import xlwings as xw
from pathlib import Path

this_dir = Path(__file__).resolve().parent
parts = []

for path in (this_dir / "sales_data").rglob("*.xls*"):
    print(f'Reading {path.name}')
    part = pd.read_excel(path)
    parts.append(part)

df = pd.concat(parts)
pivot = pd.pivot_table(df, index="transaction_date", columns="store",
    values="amount", aggfunc="sum")

summary = pivot.resample("M").sum()
summary.index.name = "Month"
summary = summary.loc[:, summary.sum().sort_values().index]
summary.loc[:, "Total"] = summary.sum(axis=1)
summary.sum(axis=0).rename("Total")

template = xw.Book(this_dir / "xl" / "sales_report_template.xlsx")
sheet = template.sheets["Sheet1"]
sheet["B3"].value = summary
sheet["B3"].expand().columns.autofit()
sheet.charts["Chart 1"].set_source_data(sheet["B3"].expand()[:-1, :-1])
template.save(this_dir / "sales_report_xlwings.xlsx")
