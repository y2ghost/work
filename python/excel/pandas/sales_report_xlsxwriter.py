import pandas as pd
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

# 写入数据到excel文件
startrow, startcol = 2, 1
nrows, ncols = summary.shape

with pd.ExcelWriter(this_dir / "sales_report_xlsxwriter.xlsx",
    engine="xlsxwriter", datetime_format="mmm yy") as writer:
    summary.to_excel(writer, sheet_name="Sheet1", startrow=startrow, startcol=startcol)
    book = writer.book
    sheet = writer.sheets["Sheet1"]
    title_format = book.add_format({"bold": True, "size": 24})
    sheet.write(0, startcol, "Sales Report", title_format)
    sheet.hide_gridlines(2)

    number_format = book.add_format({"num_format": "#,##0", "align": "center"})
    below_target_format = book.add_format({"font_color": "#E93423"})
    sheet.set_column(first_col=startcol, last_col=startcol + ncols,
        width=14, cell_format=number_format)
    sheet.conditional_format(first_row=startrow + 1,
        first_col=startcol + 1,
        last_row=startrow + nrows,
        last_col=startcol + ncols,
        options={"type": "cell", "criteria": "<=",
            "value": 20000,
            "format": below_target_format})

    chart = book.add_chart({"type": "column"})
    chart.set_title({"name": "Sales per Month and Store"})
    chart.set_size({"width": 830, "height": 450})

    for col in range(1, ncols):
        chart.add_series({
            "name": ["Sheet1", startrow, startcol + col],
            "categories": ["Sheet1", startrow + 1, startcol,
                startrow + nrows - 1, startcol],
            "values": ["Sheet1", startrow + 1, startcol + col,
                startrow + nrows - 1, startcol + col],
        })

    chart.set_x_axis({"name": summary.index.name, "major_tick_mark": "none"})
    chart.set_y_axis({"name": "Sales",
        "line": {"none": True},
        "major_gridlines": {"visible": True},
        "major_tick_mark": "none"})
    sheet.insert_chart(startrow + nrows + 2, startcol, chart)
