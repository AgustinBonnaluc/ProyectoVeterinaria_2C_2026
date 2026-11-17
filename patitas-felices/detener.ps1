$puertos = 8080, 8081, 8082, 8083, 8084, 8085, 8086, 8888, 8761

foreach ($puerto in $puertos) {
    $con = Get-NetTCPConnection -State Listen -LocalPort $puerto -ErrorAction SilentlyContinue | Select-Object -First 1
    if (-not $con) { continue }
    $proc = Get-Process -Id $con.OwningProcess -ErrorAction SilentlyContinue
    if ($proc -and $proc.ProcessName -like "java*") {
        Stop-Process -Id $proc.Id -Force
        Write-Host "[OK] detenido el servicio del puerto $puerto" -ForegroundColor Green
    } elseif ($proc) {
        Write-Host "[!] el puerto $puerto es de '$($proc.ProcessName)', no lo toco" -ForegroundColor Yellow
    }
}
