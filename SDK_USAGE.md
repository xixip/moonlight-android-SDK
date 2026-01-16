# WsLight Android SDK 使用指南

## 概述

WsLight Android SDK 是基于 WsLight Android 项目封装的游戏流式传输 SDK，专门支持 Sunshine 服务器。

## 功能特性

- ✅ 专门支持 Sunshine 服务器
- ✅ 自动生成和提交 PIN 码
- ✅ 完整的配对流程
- ✅ 应用列表获取
- ✅ 游戏流式传输启动
- ✅ 证书管理

## 快速开始

### 1. 添加依赖

在项目的 `build.gradle` 中添加依赖：

```gradle
dependencies {
    implementation 'com.wslight:sdk:1.0.0'
}
```

或者直接导入模块：

```gradle
implementation project(':sdk')
```

### 2. 添加权限

在 `AndroidManifest.xml` 中添加必要的权限：

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
<uses-permission android:name="android.permission.RECORD_AUDIO" />
```

### 3. 初始化 SDK

```java
public class MyActivity extends AppCompatActivity {
    private WsLightSDK sdk;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // 初始化 SDK
        sdk = new WsLightSDK(this, "your-client-id");
    }
}
```

## 使用示例

### 1. 检查服务器状态

```java
boolean isOnline = sdk.checkServerOnline("192.168.1.100");
if (isOnline) {
    // 服务器在线
}
```

### 2. 配对服务器

```java
// 配对 Sunshine 服务器
WsLightSDK.PairingResult result = sdk.pairServer("192.168.1.100", "sunshine");

if (result.success) {
    // 配对成功
    ComputerDetails computer = result.computer;
    X509Certificate serverCert = result.serverCert;
    String pin = result.pin; // 生成的 PIN 码
    
    // 保存计算机信息和证书
} else {
    // 配对失败
    String errorMessage = result.message;
}
```

### 3. 获取应用列表

```java
WsLightSDK.AppListResult appResult = sdk.getAppList(computer);

if (appResult.success) {
    List<NvApp> apps = appResult.apps;
    // 显示应用列表
}
```

### 4. 启动游戏流式传输

```java
// 启动游戏（选择第一个应用）
NvApp selectedApp = apps.get(0);
sdk.startGameStream(computer, selectedApp, false);
```

## 高级用法

### 1. 单独生成 PIN 码

```java
String pin = sdk.generatePin();
```

### 2. 仅提交 PIN 到 Sunshine

```java
boolean submitted = sdk.submitPinOnly("192.168.1.100", "1234");
```

### 3. 仅执行配对（不提交 PIN）

```java
WsLightSDK.PairingResult result = sdk.pairOnly("192.168.1.100", "1234", "gamestream");
```

## 服务器类型说明

### 1. NVIDIA GameStream (`"gamestream"`)
- 客户端生成 PIN 码
- 用户在 GeForce Experience 中输入 PIN
- 客户端执行配对

### 2. Sunshine (`"sunshine"`)
- 客户端生成 PIN 码
- 客户端提交 PIN 到 `/api/pin` 接口
- 用户在 Sunshine Web UI 确认
- 客户端执行配对

## 错误处理

### 常见错误

1. **连接失败**
   - 检查服务器地址是否正确
   - 检查防火墙设置
   - 确保服务器正在运行

2. **PIN 码错误**
   - Sunshine：确保在 Web UI 确认了 PIN 码
   - GameStream：确保在 GeForce Experience 中输入了正确的 PIN

3. **证书错误**
   - 重新配对获取新证书
   - 检查证书是否过期

## 构建和发布

### 构建 SDK

```bash
./gradlew :sdk:assembleRelease
```

### 发布到本地 Maven 仓库

```bash
./gradlew :sdk:publishToMavenLocal
```

### 生成文档

```bash
./gradlew :sdk:javadoc
```

## 示例应用

项目包含一个完整的示例应用，位于 `sample-app` 目录，展示了 SDK 的所有功能。

## 许可证

基于 Moonlight Android 项目的开源许可证。

## 支持

- 问题反馈：GitHub Issues
- 文档：查看示例应用和 API 文档
- 社区：Moonlight Discord 服务器