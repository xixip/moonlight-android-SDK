# SDK 资源完整性检查清单

## 1. 布局资源

### ✅ activity_game.xml
- 位置: `app/src/main/res/layout/activity_game.xml`
- 状态: **存在**
- 包含: StreamView (@+id/surfaceView), performanceOverlay, notificationOverlay

### ✅ 其他布局文件
- 需要检查 Game Activity 可能使用的其他布局

## 2. 自定义视图

### ✅ StreamView.java
- 位置: `app/src/main/java/com/limelight/ui/StreamView.java`
- 状态: **存在**
- 用途: 视频流显示视图

## 3. 字符串资源

### ✅ Game Activity 依赖的字符串
检查以下字符串是否存在:

1. `conn_establishing_title` - **存在**
2. `conn_establishing_msg` - **存在**
3. `conn_metered` - **存在**
4. `conn_client_latency` - **存在**
5. `conn_client_latency_hw` - **存在**
6. `conn_hardware_latency` - **存在**
7. `conn_starting` - **存在**
8. `conn_error_title` - **存在**
9. `conn_error_msg` - **存在**
10. `conn_terminated_title` - **存在**
11. `video_decoder_init_failed` - **需要检查**

## 4. 样式资源

### ✅ StreamTheme
- 位置: `app/src/main/res/values/styles.xml`
- 状态: **存在**
- 父主题: StreamBaseTheme

### ✅ StreamBaseTheme
- 需要检查是否存在

## 5. 其他资源

### ✅ 图标资源
- mipmap 图标: **存在**（多个密度）
- 注意: SDK 不应该使用应用图标，但需要保留供内部使用

### ✅ 可绘制资源
- 各种 drawable: **存在**

### ✅ XML 配置
- network_security_config.xml: **存在**
- backup_rules.xml: **存在**

## 6. 潜在问题

### ❗ 资源冲突风险
1. **应用可能定义相同名称的资源**
   - 解决方案: 使用资源前缀（如 `moonlight_`）
   - 或者: 让应用提供资源

2. **SDK 包含应用特定的资源**
   - 检查: 移除仅应用使用的资源
   - 保留: SDK 功能必需的资源

### ❗ 主题依赖
1. **StreamTheme 可能依赖其他主题**
   - 需要: StreamBaseTheme 存在
   - 需要: 所有父主题链完整

### ❗ 国际化资源
1. **多语言支持**
   - 检查: 所有 values-* 目录是否完整
   - 状态: **看起来完整**（多个语言目录）

## 7. 建议的修改

### 方案A: 重命名资源（推荐）
为所有 SDK 资源添加前缀，避免冲突:
```xml
<!-- 原: -->
<string name="conn_establishing_title">Establishing Connection</string>

<!-- 修改为: -->
<string name="moonlight_conn_establishing_title">Establishing Connection</string>
```

### 方案B: 资源隔离
创建独立的资源模块，与应用资源分离。

### 方案C: 动态资源加载
在代码中动态加载 SDK 资源，避免编译时冲突。

## 8. 立即需要检查的项目

1. **检查 `video_decoder_init_failed` 字符串是否存在**
2. **检查 `StreamBaseTheme` 样式是否存在**
3. **检查所有 values-* 目录中的关键字符串**
4. **检查是否有应用特定的资源可以移除**

## 9. 验证方法

### 编译测试
```bash
./gradlew :sdk:assembleDebug
```

### 资源冲突测试
创建测试应用，包含同名资源，检查是否冲突。

### 功能测试
使用示例应用测试 Game Activity 是否能正常启动和显示。

## 10. 结论

当前 SDK 资源基本完整，但存在资源冲突的风险。建议:

1. **短期**: 保留当前资源，但添加文档说明可能的冲突
2. **中期**: 为 SDK 资源添加前缀（如 `moonlight_`）
3. **长期**: 创建资源隔离机制

对于 MVP 版本，当前资源状态可以接受，但需要测试验证。