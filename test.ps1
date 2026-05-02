$path = Get-ChildItem "C:\Users\larss\.gradle\caches\forge_gradle\" -Recurse -Filter "forge-*.jar" | Select-Object -First 1 -ExpandProperty FullName
Write-Host "Found jar: $path"
Add-Type -AssemblyName System.IO.Compression.FileSystem
$zip = [System.IO.Compression.ZipFile]::OpenRead($path)
$entry = $zip.GetEntry("net/minecraft/server/level/ServerChunkCache.class")
if ($entry) { Write-Host "Found ServerChunkCache" } else { Write-Host "Not found" }
$zip.Dispose()
