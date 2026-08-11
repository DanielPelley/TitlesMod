# Builds TitlesMod-Design.xlsx from the pipe-delimited CSVs in .\data
# Edit the CSVs, re-run this script, and the workbook is regenerated.
# Requires Excel to be installed.

$ErrorActionPreference = 'Stop'
$here = Split-Path -Parent $MyInvocation.MyCommand.Path
$dataDir = Join-Path $here 'data'
$outFile = Join-Path $here 'TitlesMod-Design.xlsx'

$rarityColors = @{
    'Common'    = 0xAAAAAA
    'Uncommon'  = 0x55FF55
    'Rare'      = 0xFFFF55
    'Epic'      = 0xFF55FF
    'Legendary' = 0x00AAFF
    'Mythic'    = 0x5555FF
    'Secret'    = 0x808080
}

$excel = New-Object -ComObject Excel.Application
$excel.Visible = $false
$excel.DisplayAlerts = $false

try {
    $wb = $excel.Workbooks.Add()
    while ($wb.Worksheets.Count -gt 1) { $wb.Worksheets.Item($wb.Worksheets.Count).Delete() }

    $files = Get-ChildItem -Path $dataDir -Filter '*.csv' | Sort-Object Name
    $index = 0

    foreach ($file in $files) {
        $index++
        $sheetName = ($file.BaseName -replace '^\d+-', '') -replace '-', ' '
        if ($sheetName.Length -gt 31) { $sheetName = $sheetName.Substring(0, 31) }

        if ($index -eq 1) {
            $ws = $wb.Worksheets.Item(1)
        } else {
            $ws = $wb.Worksheets.Add([System.Reflection.Missing]::Value, $wb.Worksheets.Item($wb.Worksheets.Count))
        }
        $ws.Name = $sheetName

        $lines = @(Get-Content -Path $file.FullName -Encoding UTF8 | Where-Object { $_.Trim().Length -gt 0 })
        $rows = $lines.Count
        $cols = 0
        $parsed = New-Object 'System.Collections.Generic.List[string[]]'
        foreach ($line in $lines) {
            $cells = $line -split '\|'
            if ($cells.Count -gt $cols) { $cols = $cells.Count }
            $parsed.Add($cells)
        }

        $arr = New-Object 'object[,]' $rows, $cols
        for ($r = 0; $r -lt $rows; $r++) {
            $cells = $parsed[$r]
            for ($c = 0; $c -lt $cols; $c++) {
                $arr[$r, $c] = if ($c -lt $cells.Count) { $cells[$c] } else { '' }
            }
        }

        $target = $ws.Range($ws.Cells.Item(1, 1), $ws.Cells.Item($rows, $cols))
        $target.NumberFormat = '@'          # text, so "+15%" and "-25%" are never parsed as formulas
        $target.Value2 = $arr

        # header row
        $header = $ws.Range($ws.Cells.Item(1, 1), $ws.Cells.Item(1, $cols))
        $header.Font.Bold = $true
        $header.Font.Color = 0xFFFFFF
        $header.Interior.Color = 0x6B3410   # BGR -> a deep blue
        $header.HorizontalAlignment = -4131
        $ws.Rows.Item(1).RowHeight = 22

        $target.VerticalAlignment = -4160
        $target.WrapText = $true
        $target.Columns.AutoFit() | Out-Null
        for ($c = 1; $c -le $cols; $c++) {
            if ($ws.Columns.Item($c).ColumnWidth -gt 52) { $ws.Columns.Item($c).ColumnWidth = 52 }
            if ($ws.Columns.Item($c).ColumnWidth -lt 10) { $ws.Columns.Item($c).ColumnWidth = 10 }
        }
        $target.Rows.AutoFit() | Out-Null

        $ws.Activate()
        $excel.ActiveWindow.FreezePanes = $false
        $ws.Range('A2').Select() | Out-Null
        $excel.ActiveWindow.FreezePanes = $true
        if ($rows -gt 1) { $header.AutoFilter() | Out-Null }

        # colour the Rarity column by tier wherever a sheet has one
        $rarityCol = 0
        for ($c = 1; $c -le $cols; $c++) {
            if ($ws.Cells.Item(1, $c).Text -eq 'Rarity') { $rarityCol = $c; break }
        }
        if ($rarityCol -gt 0) {
            for ($r = 2; $r -le $rows; $r++) {
                $v = $ws.Cells.Item($r, $rarityCol).Text
                if ($rarityColors.ContainsKey($v)) {
                    $cell = $ws.Cells.Item($r, $rarityCol)
                    $cell.Font.Color = $rarityColors[$v]
                    $cell.Font.Bold = $true
                }
            }
        }

        Write-Host ("  sheet: {0}  ({1} rows x {2} cols)" -f $sheetName, $rows, $cols)
    }

    $wb.Worksheets.Item(1).Activate()

    # If the workbook is open in Excel we cannot overwrite it. Fall back to a
    # side-by-side file rather than killing the user's Excel session.
    $locked = $false
    if (Test-Path $outFile) {
        try { Remove-Item $outFile -Force -ErrorAction Stop } catch { $locked = $true }
    }
    $savePath = $outFile
    if ($locked) {
        $savePath = Join-Path $here ('TitlesMod-Design-{0}.xlsx' -f (Get-Date -Format 'MMdd-HHmm'))
    }

    $wb.SaveAs($savePath, 51)   # 51 = xlOpenXMLWorkbook (.xlsx)
    $wb.Close($false)
    Write-Host ""
    if ($locked) {
        Write-Warning "TitlesMod-Design.xlsx is open in Excel and could not be replaced."
        Write-Host "Wrote $savePath instead. Close the open workbook and re-run to write the canonical file."
    } else {
        Write-Host "Wrote $savePath"
    }
}
finally {
    $excel.Quit()
    [void][System.Runtime.InteropServices.Marshal]::ReleaseComObject($excel)
    [GC]::Collect()
}
