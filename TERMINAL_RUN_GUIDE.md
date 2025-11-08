# 终端运行指南

本指南介绍如何在终端（Terminal）中构建和运行 PantryChef Android 应用。

## 前置要求

1. **Java Development Kit (JDK)**
   - 需要 JDK 11 或更高版本
   - 检查安装：`java -version`

2. **Android SDK**
   - 需要安装 Android SDK（通常通过 Android Studio 安装）
   - 设置环境变量（可选但推荐）

3. **连接设备或模拟器**
   - 连接 Android 设备（启用 USB 调试）
   - 或启动 Android 模拟器

## 环境变量设置（可选）

如果 Android SDK 不在默认路径，需要设置环境变量：

```bash
# 添加到 ~/.zshrc 或 ~/.bash_profile
export ANDROID_HOME=$HOME/Library/Android/sdk
export PATH=$PATH:$ANDROID_HOME/platform-tools
export PATH=$PATH:$ANDROID_HOME/tools
```

然后重新加载：
```bash
source ~/.zshrc  # 或 source ~/.bash_profile
```

## 常用命令

### 1. 检查设备连接

```bash
# 查看连接的设备
adb devices
```

输出示例：
```
List of devices attached
emulator-5554    device
```

### 2. 清理构建

```bash
# 清理之前的构建文件
./gradlew clean
```

### 3. 构建 APK

```bash
# 构建 Debug APK
./gradlew assembleDebug

# 构建 Release APK（需要签名配置）
./gradlew assembleRelease
```

构建完成后，APK 文件位于：
```
app/build/outputs/apk/debug/app-debug.apk
```

### 4. 安装到设备

```bash
# 构建并安装 Debug 版本
./gradlew installDebug

# 构建并安装 Release 版本
./gradlew installRelease
```

### 5. 运行应用（推荐）

```bash
# 构建、安装并启动应用
./gradlew installDebug && adb shell am start -n com.example.pantrychef/.MainActivity
```

或者使用更简单的方式：
```bash
# 构建并安装
./gradlew installDebug

# 然后手动启动应用（在设备上点击图标）
# 或使用 adb 启动
adb shell am start -n com.example.pantrychef/.MainActivity
```

### 6. 卸载应用

```bash
adb uninstall com.example.pantrychef
```

### 7. 查看日志

```bash
# 实时查看日志
adb logcat

# 过滤特定标签
adb logcat -s PantryChef

# 查看崩溃日志
adb logcat | grep -i "fatal\|exception\|error"
```

### 8. 其他有用命令

```bash
# 检查构建配置
./gradlew tasks

# 查看所有可用任务
./gradlew tasks --all

# 运行测试
./gradlew test

# 运行 Android 测试
./gradlew connectedAndroidTest

# 查看依赖树
./gradlew app:dependencies
```

## 完整运行流程

### 方法一：使用快速脚本（推荐）

```bash
# 进入项目目录
cd /Users/suming/Desktop/PantryChef

# 运行脚本（会自动检查设备、构建、安装并启动）
./run.sh
```

### 方法二：手动运行

#### 第一次运行

```bash
# 1. 进入项目目录
cd /Users/suming/Desktop/PantryChef

# 2. 确保设备已连接
adb devices

# 3. 清理并构建
./gradlew clean assembleDebug

# 4. 安装到设备
./gradlew installDebug

# 5. 启动应用
adb shell am start -n com.example.pantrychef/.MainActivity
```

#### 后续运行（快速）

```bash
# 直接安装并运行
./gradlew installDebug && adb shell am start -n com.example.pantrychef/.MainActivity
```

## 常见问题

### Q1: 提示 "command not found: gradlew"

**解决方案**：
```bash
# 确保在项目根目录
cd /Users/suming/Desktop/PantryChef

# 给 gradlew 添加执行权限（如果需要）
chmod +x gradlew
```

### Q2: 提示 "SDK location not found"

**解决方案**：
1. 检查 `local.properties` 文件是否存在
2. 如果不存在，创建它：
```bash
echo "sdk.dir=$HOME/Library/Android/sdk" > local.properties
```

### Q3: 构建失败，提示内存不足

**解决方案**：
编辑 `gradle.properties`，增加内存：
```properties
org.gradle.jvmargs=-Xmx4096m -Dfile.encoding=UTF-8
```

### Q4: 找不到设备

**解决方案**：
```bash
# 检查设备连接
adb devices

# 如果设备未显示，尝试：
adb kill-server
adb start-server
adb devices

# 检查 USB 调试是否启用（在设备上）
```

### Q5: 构建很慢

**解决方案**：
1. 启用 Gradle 守护进程（默认已启用）
2. 启用并行构建（在 `gradle.properties` 中）：
```properties
org.gradle.parallel=true
```

## 快速脚本

创建一个便捷脚本 `run.sh`：

```bash
#!/bin/bash
cd /Users/suming/Desktop/PantryChef
./gradlew installDebug && adb shell am start -n com.example.pantrychef/.MainActivity
```

使用方法：
```bash
chmod +x run.sh
./run.sh
```

## 在 macOS 上的特殊说明

1. **权限问题**：如果遇到权限问题，使用 `chmod +x gradlew`
2. **路径问题**：macOS 上 Android SDK 通常在 `~/Library/Android/sdk`
3. **Shell**：默认使用 zsh，配置文件是 `~/.zshrc`

## 验证安装

运行后，检查应用是否正常：

```bash
# 查看应用是否在运行
adb shell "ps | grep pantrychef"

# 查看应用信息
adb shell pm list packages | grep pantrychef
```

---

**提示**：首次构建可能需要较长时间（下载依赖），请耐心等待。

