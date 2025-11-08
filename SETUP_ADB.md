# 配置 ADB 环境变量指南

如果遇到 `adb: command not found` 错误，需要配置 Android SDK 的环境变量。

## 方法一：自动检测并配置（推荐）

在 macOS 上，Android SDK 通常安装在以下位置：
- `~/Library/Android/sdk` （通过 Android Studio 安装）

### 步骤

1. **检查 Android SDK 是否存在**：
```bash
ls ~/Library/Android/sdk
```

如果目录存在，继续下一步。

2. **添加到环境变量**：

编辑 `~/.zshrc` 文件（如果使用 zsh，这是 macOS 默认）：
```bash
nano ~/.zshrc
```

或者使用其他编辑器：
```bash
open -e ~/.zshrc
```

3. **添加以下内容**：
```bash
# Android SDK
export ANDROID_HOME=$HOME/Library/Android/sdk
export PATH=$PATH:$ANDROID_HOME/platform-tools
export PATH=$PATH:$ANDROID_HOME/tools
export PATH=$PATH:$ANDROID_HOME/emulator
```

4. **保存并重新加载**：
```bash
source ~/.zshrc
```

5. **验证配置**：
```bash
adb version
```

应该显示 adb 版本信息。

## 方法二：手动查找 SDK 路径

如果 SDK 不在默认位置：

1. **打开 Android Studio**
2. **Preferences → Appearance & Behavior → System Settings → Android SDK**
3. **查看 "Android SDK Location"**，这就是你的 SDK 路径
4. **使用该路径替换上面的 `$HOME/Library/Android/sdk`**

## 方法三：使用 Android Studio 运行（最简单）

如果不想配置命令行工具：

1. 打开 Android Studio
2. 打开项目：`File → Open → 选择 PantryChef 文件夹`
3. 等待 Gradle 同步完成
4. 点击工具栏的 "Run" 按钮（绿色三角形）
5. 选择设备或创建模拟器

## 启动 Android 模拟器

### 通过 Android Studio

1. 打开 Android Studio
2. `Tools → Device Manager`
3. 点击设备旁边的播放按钮启动

### 通过命令行（配置 adb 后）

```bash
# 列出可用的模拟器
emulator -list-avds

# 启动模拟器（替换 <avd_name> 为实际名称）
emulator -avd <avd_name> &

# 等待模拟器启动后，检查设备
adb devices
```

## 连接物理设备

1. **在手机上启用开发者选项**：
   - 设置 → 关于手机 → 连续点击"版本号"7次

2. **启用 USB 调试**：
   - 设置 → 系统 → 开发者选项 → USB 调试

3. **连接设备**：
   - 用 USB 线连接手机和电脑
   - 手机上允许 USB 调试

4. **验证连接**：
```bash
adb devices
```

应该显示你的设备。

## 临时使用（不配置环境变量）

如果只是临时使用，可以直接使用完整路径：

```bash
# 使用完整路径运行 adb
~/Library/Android/sdk/platform-tools/adb devices

# 或创建别名
alias adb='~/Library/Android/sdk/platform-tools/adb'
```

## 验证配置

运行以下命令验证：

```bash
# 检查 adb
adb version

# 检查设备
adb devices

# 检查模拟器
emulator -list-avds
```

如果所有命令都能正常工作，说明配置成功！

## 常见问题

### Q: 找不到 ~/.zshrc 文件

**A**: 如果文件不存在，创建它：
```bash
touch ~/.zshrc
```

### Q: 使用的是 bash 而不是 zsh

**A**: 编辑 `~/.bash_profile` 或 `~/.bashrc` 而不是 `~/.zshrc`

### Q: Android Studio 没有安装

**A**: 
1. 下载并安装 [Android Studio](https://developer.android.com/studio)
2. 安装时会自动安装 Android SDK
3. 然后按照上面的步骤配置环境变量

---

配置完成后，就可以使用 `./run.sh` 来运行应用了！

