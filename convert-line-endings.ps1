param (
    [string]$fileName
)

# Check if the file name is provided
if (-not $fileName) {
    Write-Host "Please provide a file name."
    exit
}

# Check if the file exists
if (-not (Test-Path -Path $fileName -PathType Leaf)) {
    Write-Host "File '$fileName' does not exist."
    exit
}

# Read the content of the file
$content = Get-Content -Path $fileName -Raw

# Convert line endings to Unix-style
$content = $content -replace "`r`n", "`n"

# Write the updated content back to the file
$content | Set-Content -Path $fileName -NoNewline
