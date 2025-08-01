import os
from pathlib import Path
from os.path import join, getsize


def get_file_total_size(rootDir):
    for currentDir, dirs, files in os.walk(rootDir):
        print(currentDir, "consumes", end=" ")
        print(sum(getsize(join(currentDir, name)) for name in files), end=" ")
        print("bytes in", len(files), "non-directory files")
        if '__pycache__' in dirs:
            dirs.remove('__pycache__')


# 注意比较危险，删除目录为根目录话，整个分区文件被毁删除
def clean_dir_files(rootDir):
    for currentDir, dirs, files in os.walk(rootDir, topdown=False):
        currentPath = Path(currentDir)
        for name in files:
            f  = currentPath / name
            print(f"remove file {f}")
            os.remove(f)
        for name in dirs:
            d  = currentPath / name
            print(f"remove dir {d}")
            os.rmdir(d)
    os.rmdir(rootDir)

if '__main__' == __name__:
    testDir = Path.home() / "tests"
    get_file_total_size(testDir)
    clean_dir_files(testDir)

