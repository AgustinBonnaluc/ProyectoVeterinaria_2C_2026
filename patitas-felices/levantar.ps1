param(
    [switch]$SinCompilar,
    [switch]$SinFront
)

$ErrorActionPreference = "Stop"
$raiz = $PSScriptRoot
$logs = Join-Path $raiz "logs"
New-Item -ItemType Directory -Force $logs | Out-Null

$infra = @(
    @{ nombre = "eureka-server"; puerto = 8761 },
    @{ nombre = "config-server"; puerto = 8888 }
)
$servicios = @(
    @{ nombre = "ms-auth"; puerto = 8085 },
    @{ nombre = "ms-dueno"; puerto = 8081 },
    @{ nombre = "ms-mascota"; puerto = 8082 },
    @{ nombre = "ms-veterinario"; puerto = 8083 },
    @{ nombre = "ms-turno"; puerto = 8084 },
    @{ nombre = "ms-medicamento"; puerto = 8086 }
)
$gateway = @{ nombre = "api-gateway"; puerto = 8080 }

function Paso($texto) { Write-Host ""; Write-Host "==> $texto" -ForegroundColor Cyan }
function Ok($texto) { Write-Host "    [OK] $texto" -ForegroundColor Green }
function Aviso($texto) { Write-Host "    [!] $texto" -ForegroundColor Yellow }
function Falla($texto) { Write-Host "    [X] $texto" -ForegroundColor Red }

function ProcesoEnPuerto($puerto) {
    $con = Get-NetTCPConnection -State Listen -LocalPort $puerto -ErrorAction SilentlyContinue | Select-Object -First 1
    if ($con) { return Get-Process -Id $con.OwningProcess -ErrorAction SilentlyContinue }
    return $null
}

function Responde($puerto) {
    try {
        Invoke-RestMethod "http://localhost:$puerto/actuator/health" -TimeoutSec 3 | Out-Null
        return $true
    } catch {
        return [bool]$_.Exception.Response
    }
}

function Iniciar($svc) {
    $proc = ProcesoEnPuerto $svc.puerto
    if ($proc) {
        if ($proc.ProcessName -like "java*") { Aviso "$($svc.nombre) ya estaba corriendo en $($svc.puerto)" }
        else { Falla "El puerto $($svc.puerto) lo ocupa '$($proc.ProcessName)'. $($svc.nombre) no se va a poder levantar." }
        return
    }
    $jar = Join-Path $raiz "$($svc.nombre)\target\$($svc.nombre)-1.0.0.jar"
    if (-not (Test-Path $jar)) { Falla "No existe $jar. Corre el script sin -SinCompilar."; exit 1 }
    Start-Process java -ArgumentList "-jar", "`"$jar`"" -WindowStyle Hidden `
        -RedirectStandardOutput (Join-Path $logs "$($svc.nombre).log") `
        -RedirectStandardError (Join-Path $logs "$($svc.nombre).err.log") | Out-Null
    Write-Host "    iniciando $($svc.nombre) en el puerto $($svc.puerto)..."
}

function Esperar($lista, $segundos = 120) {
    $limite = (Get-Date).AddSeconds($segundos)
    $pendientes = [System.Collections.ArrayList]@($lista)
    while ($pendientes.Count -gt 0 -and (Get-Date) -lt $limite) {
        foreach ($svc in @($pendientes)) {
            if (Responde $svc.puerto) { Ok "$($svc.nombre) arriba (http://localhost:$($svc.puerto))"; $pendientes.Remove($svc) }
        }
        if ($pendientes.Count -gt 0) { Start-Sleep -Seconds 2 }
    }
    foreach ($svc in $pendientes) { Falla "$($svc.nombre) no respondio. Mira logs\$($svc.nombre).log" }
    return $pendientes.Count -eq 0
}

Paso "Chequeando MySQL y Redis"
if (ProcesoEnPuerto 3306) { Ok "MySQL escuchando en 3306" } else { Falla "MySQL no esta corriendo en 3306. Inicialo y volve a ejecutar."; exit 1 }

docker info *> $null
if ($LASTEXITCODE -ne 0) {
    $dockerExe = "C:\Program Files\Docker\Docker\Docker Desktop.exe"
    if (Test-Path $dockerExe) {
        Write-Host "    abriendo Docker Desktop..."
        Start-Process $dockerExe
        $limite = (Get-Date).AddSeconds(90)
        do { Start-Sleep -Seconds 3; docker info *> $null } while ($LASTEXITCODE -ne 0 -and (Get-Date) -lt $limite)
    }
}
if ($LASTEXITCODE -eq 0) {
    docker start patitas-redis *> $null
    if ($LASTEXITCODE -ne 0) { docker run -d --name patitas-redis -p 6379:6379 redis:7 *> $null }
    if ($LASTEXITCODE -eq 0) { Ok "Redis (patitas-redis) en 6379" } else { Aviso "No pude levantar Redis: el rate limiter va a dejar pasar todo" }
} else {
    Aviso "Docker no responde: sin Redis el rate limiter deja pasar todo"
}

$p8080 = ProcesoEnPuerto 8080
if ($p8080 -and $p8080.ProcessName -notlike "java*") {
    Aviso "El puerto 8080 lo ocupa '$($p8080.ProcessName)'. Si es Oracle (TNSLSNR) frenalo con: lsnrctl stop"
}

if (-not $SinCompilar) {
    Paso "Compilando todos los modulos (mvnw package)"
    Push-Location $raiz
    & .\mvnw.cmd -q -DskipTests package
    $codigo = $LASTEXITCODE
    Pop-Location
    if ($codigo -ne 0) { Falla "La compilacion fallo"; exit 1 }
    Ok "Compilacion terminada"
}

Paso "1/3 Infraestructura: Eureka y despues Config Server"
Iniciar $infra[0]
if (-not (Esperar @($infra[0]))) { exit 1 }
Iniciar $infra[1]
if (-not (Esperar @($infra[1]))) { exit 1 }

Paso "2/3 Microservicios de negocio (en paralelo)"
foreach ($svc in $servicios) { Iniciar $svc }
Esperar $servicios 180 | Out-Null

Paso "3/3 API Gateway"
Iniciar $gateway
Esperar @($gateway) | Out-Null

Paso "Servicios registrados en Eureka"
try {
    $apps = Invoke-RestMethod "http://localhost:8761/eureka/apps" -Headers @{ Accept = "application/json" }
    $apps.applications.application | ForEach-Object { Ok "$($_.name) ($(@($_.instance).Count) instancia)" }
} catch { Aviso "No pude consultar Eureka" }

Write-Host ""
Write-Host "Listo. Eureka: http://localhost:8761  Gateway: http://localhost:8080  Logs: $logs" -ForegroundColor Green
Write-Host "Para apagar todo: .\detener.ps1" -ForegroundColor Green

if (-not $SinFront) { Start-Process (Join-Path $raiz "frontend\index.html") }
