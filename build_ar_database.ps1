# AR Image Database Auto-Generator

$imagesFolder = "C:\Users\user\AndroidStudioProjects\trip_to_Hyeonchungsa\app\src\main\assets\augmented_images"
$imageListPath = Join-Path $imagesFolder "image_list.txt"
$outputDbPath = Join-Path $imagesFolder "augmented_image_database.imgdb"
$arcoreimgPath = "C:\Users\user\Downloads\arcore-android-sdk-1.51.0\tools\arcoreimg\windows\arcoreimg.exe"

# Remove existing image_list.txt
if (Test-Path $imageListPath) {
    Remove-Item $imageListPath
}

# Find all jpg, png files in augmented_images folder
$imageFiles = Get-ChildItem -Path $imagesFolder -File | Where-Object { $_.Extension -match '\.(jpg|jpeg|png)$' }

if ($imageFiles.Count -eq 0) {
    Write-Host "Error: No image files found in $imagesFolder" -ForegroundColor Red
    exit 1
}

Write-Host "Found $($imageFiles.Count) images" -ForegroundColor Green

# Create image_list.txt
foreach ($file in $imageFiles) {
    $imageName = [System.IO.Path]::GetFileNameWithoutExtension($file.Name)
    $fullPath = $file.FullName
    $pipeChar = [char]124
    $line = $imageName + $pipeChar + $fullPath
    Add-Content -Path $imageListPath -Value $line
    $fileName = $file.Name
    Write-Host "  - $imageName ($fileName)"
}

Write-Host ""
Write-Host "image_list.txt created!" -ForegroundColor Green
Write-Host "Building database..." -ForegroundColor Yellow

# Run arcoreimg to build database
& $arcoreimgPath build-db --input_image_list_path=$imageListPath --output_db_path=$outputDbPath

if ($LASTEXITCODE -eq 0) {
    Write-Host ""
    Write-Host "Success! Database created." -ForegroundColor Green
    Write-Host "Output: $outputDbPath" -ForegroundColor Cyan
} else {
    Write-Host ""
    Write-Host "Failed! Database creation failed." -ForegroundColor Red
    exit 1
}
