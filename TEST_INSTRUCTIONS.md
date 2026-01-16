# Moonlight SDK 测试程序使用说明

## 概述

这是一个完整的 Moonlight SDK 测试程序，用于验证 SDK 的所有功能。

## 项目结构

```
moonlight-android-SDK/
├── sdk/                    # SDK 库模块
├── test-app/              # 测试应用
│   ├── build.gradle      # 测试应用配置
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/moonlight/test/MainActivity.java
│       └── res/          # 资源文件
└── TEST_INSTRUCTIONS.md  # 本文件
```

## 功能测试

### 1. Root 权限检测
- 检查设备是否具有 root 权限
- 检查是否支持 evdev 输入捕获

### 2. 服务器状态检查
- 输入服务器地址和类型（Sunshine/GameStream）
- 检查服务器是否在线

### 3. 服务器配对
- 生成 PIN 码
- 提交 PIN 到 Sunshine（如果使用 Sunshine）
- 执行标准配对流程

### 4. 应用列表获取
- 获取服务器上的应用列表
- 显示应用名称和 ID

### 5. 流式传输启动
- 选择要启动的应用
- 启动游戏流式传输

## 使用步骤

### 步骤 1：构建 SDK
```bash
# 构建 SDK
./gradlew :sdk:assembleDebug

# 或者构建发布版本
./gradlew :sdk:assembleRelease
```

### 步骤 2：运行测试应用
```bash
# 安装测试应用到设备
./gradlew :test-app:installDebug

# 或者直接运行
./gradlew :test-app:assembleDebug
```

### 步骤 3：测试流程

1. **启动测试应用**
2. **检查 Root 权限**
   - 点击"检查 Root"按钮
   - 查看设备是否支持高级输入捕获

3. **检查服务器状态**
   - 点击"检查服务器"按钮
   - 输入服务器地址（如 192.168.1.100）
   - 选择服务器类型（Sunshine 或 GameStream）
   - 确认服务器是否在线

4. **配对服务器**
   - 点击"配对"按钮
   - 系统会生成 PIN 码
   - 根据服务器类型：
     - Sunshine：在 Web UI 确认 PIN
     - GameStream：在 GeForce Experience 输入 PIN
   - 等待配对结果

5. **获取应用列表**
   - 点击"获取应用"按钮
   - 查看获取到的应用列表

6. **开始流式传输**
   - 点击"开始流式传输"按钮
   - 选择要启动的应用
   - 系统会启动 Game Activity 开始流式传输

7. **清除状态**
   - 点击"清除状态"按钮重置所有状态

## 测试场景

### 场景 1：Root 设备测试
1. 在已 root 设备上运行测试
2. 验证 evdev 捕获支持
3. 测试完整的流式传输流程

### 场景 2：非 Root 设备测试
1. 在未 root 设备上运行测试
2. 验证回退到其他输入捕获方式
3. 测试完整的流式传输流程

### 场景 3：Sunshine 服务器测试
1. 使用 Sunshine 服务器地址
2. 验证 PIN 码自动提交
3. 测试配对和流式传输

### 场景 4：GameStream 服务器测试
1. 使用 NVIDIA GameStream 服务器地址
2. 验证标准配对流程
3. 测试配对和流式传输

## 日志查看

### 应用日志
- 测试应用界面显示详细的日志信息
- 包含时间戳和操作结果

### Android Logcat
```bash
# 查看 SDK 日志
adb logcat -s MoonlightTest

# 查看 SDK 详细日志
adb logcat -s MoonlightTest:I MoonlightSDK:I RootChecker:I

# 查看所有相关日志
adb logcat | grep -E "(Moonlight|Limelight|GameStream)"
```

## 故障排除

### 常见问题 1：服务器不可用
```
可能原因：
1. 服务器地址错误
2. 服务器未运行
3. 防火墙阻止连接
4. 网络不可达

解决方案：
1. 确认服务器 IP 地址正确
2. 确保 Sunshine/GameStream 正在运行
3. 检查防火墙设置
4. 测试网络连通性
```

### 常见问题 2：配对失败
```
可能原因：
1. PIN 码错误
2. 证书问题
3. 服务器配置错误

解决方案：
1. Sunshine：在 Web UI 确认 PIN 码
2. GameStream：在 GeForce Experience 输入 PIN 码
3. 重新配对获取新证书
```

### 常见问题 3：应用列表为空
```
可能原因：
1. 服务器上没有应用
2. 权限问题
3. 连接问题

解决方案：
1. 确认服务器上有游戏应用
2. 检查配对状态
3. 重新获取应用列表
```

### 常见问题 4：流式传输启动失败
```
可能原因：
1. Game Activity 资源缺失
2. 证书问题
3. 设备不支持

解决方案：
1. 确认 SDK 包含所有必要资源
2. 检查证书有效性
3. 验证设备支持 OpenGL ES 2.0+
```

## 预期结果

### 成功场景
1. ✅ Root 检测正确显示设备状态
2. ✅ 服务器状态检查返回正确结果
3. ✅ 配对成功，获取计算机信息
4. ✅ 应用列表正确显示
5. ✅ 流式传输正常启动

### 失败场景
1. ❌ 服务器不可用 - 显示错误信息
2. ❌ 配对失败 - 显示具体错误原因
3. ❌ 应用列表为空 - 显示空状态
4. ❌ 流式传输失败 - 显示异常信息

## 代码说明

### MainActivity.java
- 完整的 SDK 功能演示
- 异步操作处理
- 详细的日志记录
- 用户友好的界面

### 关键功能
1. **RootChecker 集成**：演示运行时 root 检测
2. **GameStreamSDK 使用**：展示所有 API 调用
3. **错误处理**：完善的异常处理
4. **状态管理**：清晰的流程状态

## 扩展测试

### 自动化测试
```java
// 可以扩展为自动化测试
@Test
public void testRootDetection() {
    boolean isRooted = RootChecker.isDeviceRooted();
    assertTrue(isRooted || !isRooted); // 总是通过，实际测试需要具体断言
}

@Test
public void testServerConnection() {
    GameStreamSDK sdk = new GameStreamSDK(context, "test-client");
    boolean isOnline = sdk.checkServerOnline("192.168.1.100");
    // 根据实际情况断言
}
```

### 性能测试
- 连接建立时间
- 应用列表加载时间
- 流式传输启动时间

## 注意事项

1. **网络要求**：测试需要可访问的游戏流式传输服务器
2. **权限要求**：应用需要网络和音频权限
3. **硬件要求**：需要 OpenGL ES 2.0+ 支持
4. **服务器配置**：需要正确配置的 Sunshine 或 GameStream 服务器

## 总结

这个测试程序提供了完整的 Moonlight SDK 功能验证，包括：

1. ✅ Root 权限检测和 evdev 支持检查
2. ✅ 服务器状态检查
3. ✅ 自动配对流程（支持 Sunshine 和 GameStream）
4. ✅ 应用列表获取和显示
5. ✅ 游戏流式传输启动
6. ✅ 详细的日志和状态管理

通过这个测试程序，可以全面验证 SDK 的功能和稳定性。