import sys
import difflib


def getlines(fname):
    lines = []
    try:
        f = open(fname, "r")
        lines = f.read().splitlines()
        f.close()
    except IOError as error:
        print(f"Read file Error: {error}")

    return lines


def main():
    if 3 != len(sys.argv):
        print("Usage: {sys.argv[0]} file1 file2")
        sys.exit(1)

    file1_lines = getlines(sys.argv[1])
    file2_lines = getlines(sys.argv[2])
    d = difflib.HtmlDiff()
    print(d.make_file(file1_lines, file2_lines))


if "__main__" == __name__:
    main()
