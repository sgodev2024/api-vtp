param([string]$OutputDirectory = "backups/business-modules")
$ErrorActionPreference = "Stop"
$repo = Split-Path $PSScriptRoot -Parent
$destination = [IO.Path]::GetFullPath((Join-Path $repo $OutputDirectory))
New-Item -ItemType Directory -Force $destination | Out-Null
node (Join-Path $PSScriptRoot "module-manager.mjs") pack --out (Join-Path $destination "aicrm-suite.module.json")
if($LASTEXITCODE -ne 0){throw "Module packaging failed"}
