Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Moonlight SDK 测试脚本" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# 检查必要文件
Write-Host "1. 检查项目文件..." -ForegroundColor Yellow
if (-not (Test-Path "app\build.gradle")) {
    Write-Host "❌ 错误: 找不到 app/build.gradle" -ForegroundColor Red
    exit 1
}
if (-not (Test-Path "test-app\build.gradle")) {
    Write-Host "❌ 错误: 找不到 test-app/build.gradle" -ForegroundColor Red
    exit 1
}
Write-Host "✅ 项目文件检查通过" -ForegroundColor Green
Write-Host ""

# 检查源代码
Write-Host "2. 检查源代码..." -ForegroundColor Yellow
if (-not (Test-Path "app\src\main\java\com\limelight")) {
    Write-Host "⚠️ 警告: SDK 源代码目录不存在" -ForegroundColor Yellow
} else {
    Write-Host "✅ SDK 源代码目录存在" -ForegroundColor Green
}

if (-not (Test-Path "test-app\src\main\java\com\moonlight\test")) {
    Write-Host "⚠️ 警告: 测试应用源代码目录不存在" -ForegroundColor Yellow
} else {
    Write-Host "✅ 测试应用源代码目录存在" -ForegroundColor Green
}
Write-Host ""

# 检查配置文件
Write-Host "3. 检查配置文件..." -ForegroundColor Yellow
if (-not (Test-Path "gradle\wrapper\gradle-wrapper.properties")) {
    Write-Host "❌ 错误: 找不到 Gradle 包装器配置" -ForegroundColor Red
    exit 1
}
Write-Host "✅ 配置文件检查通过" -ForegroundColor Green
Write-Host ""

# 显示配置信息
Write-Host "4. 当前配置信息:" -ForegroundColor Yellow
Get-Content "gradle\wrapper\gradle-wrapper.properties" | Select-String "distributionUrl"
Write-Host ""

# 显示建议的构建命令
Write-Host "5. 建议的构建命令:" -ForegroundColor Yellow
Write-Host ""
Write-Host "方案 A - 使用 Gradle 包装器:" -ForegroundColor Green
Write-Host "    .\gradlew :app:assembleDebug"
Write-Host "    .\gradlew :test-app:assembleDebug"
Write-Host ""
Write-Host "方案 B - 使用系统 Gradle:" -ForegroundColor Green
Write-Host "    gradle :app:assembleDebug"
Write-Host "    gradle :test-app:assembleDebug"
Write-Host ""
Write-Host "方案 C - 在 Android Studio 中:" -ForegroundColor Green
Write-Host "    1. 打开项目"
Write-Host "    2. 等待 Gradle 同步"
Write-Host "    3. 运行 test-app 模块"
Write-Host ""

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "测试脚本完成" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "请根据您的环境选择合适的构建方案。"
Write-Host "如果遇到网络问题，请参考 MANUAL_TEST_GUIDE.md"
Write-Host ""

# 等待用户输入
Read-Host "按 Enter 键继续..."