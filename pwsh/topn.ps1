# CPU最高的前N个进程
Function Get-Top {
    param(
        [Parameter(Mandatory = $true)]
        [int]$top
    )

    Get-Process | Sort-Object CPU -Descending |
        Select-Object -First $top -Property ID,
        ProcessName, @{l='CPU';e={'{0:N}' -f $_.CPU}}
}
