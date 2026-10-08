param([string]$OutputDirectory = "backups/business-modules")
$ErrorActionPreference = "Stop"
$repo = Split-Path $PSScriptRoot -Parent
$destination = [IO.Path]::GetFullPath((Join-Path $repo $OutputDirectory))
New-Item -ItemType Directory -Force $destination | Out-Null
$packages = @(
 @{Key="aicrm-shared";RuntimeKey="aicrm-retail";Java="shared";Frontend="";Requires=@("java-core >=1.1.0 <2.0.0")},
 @{Key="aicrm-stock";Java="inventory";Frontend="inventory";Requires=@("aicrm-retail")},
 @{Key="aicrm-sales";Java="sales";Frontend="sales";Requires=@("aicrm-retail","aicrm-stock")},
 @{Key="aicrm-purchase";Java="purchase";Frontend="purchase";Requires=@("aicrm-retail","aicrm-stock")}
)
foreach($package in $packages){
 $staging = Join-Path $destination ($package.Key + "-" + [guid]::NewGuid().ToString("N"))
 New-Item -ItemType Directory -Force $staging | Out-Null
 $jar = Join-Path $repo ("backend/target/core-platform-runtime-1.0.0-SNAPSHOT-"+$package.Key+".jar")
 if(!(Test-Path -LiteralPath $jar)){throw "Build backend bằng mvn package trước: thiếu $($package.Key)"}
 Copy-Item -LiteralPath $jar -Destination $staging
 Copy-Item -LiteralPath (Join-Path $repo ("backend/src/main/java/vn/aicrm/"+$package.Java)) -Destination (Join-Path $staging "java") -Recurse
 $frontend = Join-Path $staging "frontend"
 New-Item -ItemType Directory -Force $frontend | Out-Null
 if($package.Frontend){Copy-Item -LiteralPath (Join-Path $repo ("frontend/app/retail/"+$package.Frontend)) -Destination $frontend -Recurse}
 else{
  Get-ChildItem -LiteralPath (Join-Path $repo "frontend/app/retail") -File | Copy-Item -Destination $frontend
  New-Item -ItemType Directory -Force (Join-Path $staging "core-patch") | Out-Null
  foreach($coreFile in @("kernel/ModuleRuntime.java","kernel/ResourceRegistry.java","controlplane/ControlPlaneController.java","CorePlatformApplication.java")){Copy-Item -LiteralPath (Join-Path $repo ("backend/src/main/java/vn/coreplatform/"+$coreFile)) -Destination (Join-Path $staging "core-patch")}
  Copy-Item -LiteralPath (Join-Path $repo "backend/src/main/resources/db/migration") -Destination (Join-Path $staging "migrations") -Recurse
 }
 $manifest = @{key=$(if($package.RuntimeKey){$package.RuntimeKey}else{$package.Key});artifact=$package.Key;formatVersion=1;version=$(if($package.RuntimeKey -eq "aicrm-retail"){"1.1.0"}else{"1.0.0"});requires=$package.Requires;jarSha256=(Get-FileHash -LiteralPath $jar -Algorithm SHA256).Hash;runtimeScope="deployment";installMode="build-time"}
 $manifest | ConvertTo-Json -Depth 5 | Set-Content -Encoding utf8 (Join-Path $staging "module.json")
 Copy-Item -LiteralPath (Join-Path $repo "docs/BUSINESS_MODULES_PHASE2.md") -Destination (Join-Path $staging "README.md")
 Compress-Archive -Path (Join-Path $staging "*") -DestinationPath (Join-Path $destination ($package.Key+".zip")) -Force
}
Write-Output "Đã đóng gói 4 archive tại $destination"
