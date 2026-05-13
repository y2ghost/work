from openpyxl import Workbook


def create_workbook(path):
    workbook = Workbook()
    sheet = workbook.active
    sheet['A1'] = '你好'
    sheet['A2'] = '来自'
    sheet['A3'] = 'YY'
    workbook.save(path)


if __name__ == '__main__':
    create_workbook('hello.xlsx')
