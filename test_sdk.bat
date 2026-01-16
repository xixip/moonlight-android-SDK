@echo off
echo ========================================
echo Moonlight SDK 测试脚本
echo ========================================
echo.

REM 检查必要文件
echo 1. 检查项目文件...
if not exist "app\build.gradle" (
    echo ❌ 错误: 找不到 app/build.gradle
    exit /b 1
)
if not exist "test-app\build.gradle" (
    echo ❌ 错误: 找不到 test-app/build.gradle
    exit /b 1
)

echo ✅ 项目文件检查通过
echo.

REM 检查源代码
echo 2. 检查源代码...
if not exist "app\src\main\java\com\limelight" (
    echo ⚠️ 警告: SDK 源代码目录不存在
) else (
    echo ✅ SDK 源代码目录存在
)

if not exist "test-app\src\main\java\com\moonlight\test" (
    echo ⚠️ 警告: 测试应用源代码目录不存在
) else (
    echo ✅ 测试应用源代码目录存在
)

echo.

REM 检查配置文件
echo 3. 检查配置文件...
if not exist "gradle\wrapper\gradle-wrapper.properties" (
    echo ❌ 错误: 找不到 Gradle 包装器配置
    exit /b 1
)

echo ✅ 配置文件检查通过
echo.

REM 显示配置信息
echo 4. 当前配置信息:
type "gradle\wrapper\gradle-wrapper.properties" | findstr distributionUrl
echo.

echo 5. 建议的构建命令:
echo.
echo 方案 A - 使用 Gradle 包装器:
echo     .\gradlew :app:assembleDebug
echo     .\gradlew :test-app:assembleDebug
echo.
echo 方案 B - 使用系统 Gradle:
echo     gradle :app:assembleDebug
echo     gradle :test-app:assembleDebug
echo.
echo 方案 C - 在 Android Studio 中:
echo     1. 打开项目
echo     2. 等待 Gradle 同步
echo     3. 运行 test-app 模块
echo.

echo ========================================
echo 测试脚本完成
echo ========================================
echo.
echo 请根据您的环境选择合适的构建方案。
echo 如果遇到网络问题，请参考 MANUAL_TEST_GUIDE.md
echo.
pause