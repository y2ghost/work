# 自定义用户路径变量
$yyPath = 'D:\progs\Bin'
$yyPath += ';D:\progs\gradle\bin'
$yyPath += ';D:\progs\Java\17\bin'
$yyPath += ';D:\progs\Vim\vim91'
$yyPath += ';D:\progs\maven\bin'
$yyPath += ';D:\progs\liquibase'

# 系统路径变量优先使用自定义用户路径变量
$Env:Path = $yyPath + ';' + $Env:Path

# 命令行UTF-8编码
$OutputEncoding = [console]::InputEncoding = [console]::OutputEncoding = New-Object System.Text.UTF8Encoding

