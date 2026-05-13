# 创建测试用的日志文件
$Directory = "D:\Logs\"
# 创建过去多少天的日志
$days = 90

# 日志目录不存在则创建它
if(-not (Test-Path $Directory)){
    New-Item -Path $Directory -ItemType Directory
}

# 创建随机大小的日志文件
Function Set-RandomFileSize {
   param( [string]$FilePath )
    $size = Get-Random -Minimum 1 -Maximum 50
    $size = $size*1024*1024
    $file = [System.IO.File]::Open($FilePath, 4)
    $file.SetLength($Size)
    $file.Close()
    Get-Item $file.Name
}

# 循环创建日志文件
for($i = 0; $i -lt $days; $i++) {
    $Date = (Get-Date).AddDays(-$i)
    # 根据日期构建文件名
    $FileName = "u_ex$($date.ToString('yyyyMMdd')).log"
    $FilePath = Join-Path -Path $Directory -ChildPath $FileName
    $Date | Out-File $FilePath
    Set-RandomFileSize -FilePath $FilePath 

    # 更新文件的创建时间、读写时间
    Get-Item $FilePath | ForEach-Object { 
        $_.CreationTime = $date
        $_.LastWriteTime = $date
        $_.LastAccessTime = $date 
    }
}
