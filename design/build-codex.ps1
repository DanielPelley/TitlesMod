# Builds titles-codex.html from codex-template.html plus the CSVs in .\data
# The template carries @@INJECT:<file>@@ markers; each is replaced with that CSV
# verbatim, so the page can never drift from the source of truth.
# Edit the CSVs (or the template), re-run this script. No Excel needed.

$ErrorActionPreference = 'Stop'
$here     = Split-Path -Parent $MyInvocation.MyCommand.Path
$dataDir  = Join-Path $here 'data'
$template = Join-Path $here 'codex-template.html'
$outFile  = Join-Path $here 'titles-codex.html'

if (-not (Test-Path $template)) { throw "Template not found: $template" }

$html = Get-Content -Path $template -Raw -Encoding UTF8

$markers = [regex]::Matches($html, '@@INJECT:([^@]+)@@') |
           ForEach-Object { $_.Groups[1].Value } |
           Select-Object -Unique

if (-not $markers) { throw "No @@INJECT:...@@ markers found in $template" }

foreach ($name in $markers) {
    $csv = Join-Path $dataDir $name
    if (-not (Test-Path $csv)) { throw "Marker '$name' has no matching file: $csv" }

    $body = (Get-Content -Path $csv -Raw -Encoding UTF8).TrimEnd()

    # A literal </script> in the data would close the wrapper early.
    if ($body -match '</script') { throw "$name contains '</script' and cannot be inlined safely" }

    $html = $html.Replace("@@INJECT:$name@@", $body)
    Write-Host ("  injected {0,-28} {1,5} rows" -f $name, ($body -split "`n").Count)
}

# UTF8 without BOM - a BOM ahead of the first tag trips some static hosts
[System.IO.File]::WriteAllText($outFile, $html, (New-Object System.Text.UTF8Encoding($false)))

Write-Host ""
Write-Host "Wrote $outFile ($([math]::Round((Get-Item $outFile).Length / 1KB)) KB)"
Write-Host "Open it in any browser - no server, no dependencies."
