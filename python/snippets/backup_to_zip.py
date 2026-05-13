import os
import zipfile
from pathlib import Path


def backup_to_zip(rootDir):
    folder = Path(rootDir)
    number = 1

    while True:
        zipFileName = Path(f"{folder.parts[-1]}_{number}.zip")
        if not zipFileName.exists():
            break
        number = number + 1

    print(f"creating {zipFileName} ...")
    backupZipFile = zipfile.ZipFile(zipFileName, "w")

    for currentDir, subDirs, files in os.walk(folder):
        currentFolder = Path(currentDir)
        print(f"Adding files in folder {currentFolder} ...")
        for file in files:
            print(f"Adding file {file} ...")
            backupZipFile.write(currentFolder / file)

    backupZipFile.close()
    print(f"backup to {zipFileName} Done.")


backup_to_zip(Path.home() / "tests")

