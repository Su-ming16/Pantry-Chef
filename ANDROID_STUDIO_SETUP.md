# Android Studio 运行配置指南

本指南介绍如何在 Android Studio 中配置和运行 PantryChef 项目。

## 第一步：打开项目

1. **启动 Android Studio**
2. **打开项目**：
   - 点击 "Open" 或 "File → Open"
   - 选择 `/Users/suming/Desktop/PantryChef` 文件夹
   - 点击 "OK"

3. **等待 Gradle 同步**：
   - Android Studio 会自动检测项目并开始同步
   - 等待底部状态栏显示 "Gradle sync finished"

## 第二步：配置运行配置（Run Configuration）

### 方法一：自动配置（推荐）

1. **打开运行配置**：
   - 点击顶部工具栏的 "Run" 下拉菜单
   - 或使用快捷键：`Ctrl+Alt+R` (Windows/Linux) 或 `Cmd+Option+R` (Mac)

2. **如果已有配置**：
   - 选择 "app" 配置即可

3. **如果没有配置**：
   - 点击 "Edit Configurations..."
   - 点击左上角的 "+" 号
   - 选择 "Android App"

### 方法二：手动创建配置

1. **打开配置对话框**：
   - 点击顶部工具栏的 "Run" → "Edit Configurations..."
   - 或：`Run → Edit Configurations...`

2. **添加新配置**：
   - 点击左上角的 "+" 号
   - 选择 "Android App"

3. **配置参数**：
   ```
   Name: app
   Module: PantryChef.app
   Launch: Default Activity
   Target: USB Device (或 Emulator)
   ```

4. **应用并保存**：
   - 点击 "Apply"
   - 点击 "OK"

## 第三步：选择运行设备

### 选项 A：使用 Android 模拟器

1. **创建模拟器**（如果还没有）：
   - `Tools → Device Manager`
   - 点击 "Create Device"
   - 选择设备型号（如 Pixel 5）
   - 选择系统镜像（推荐 API 33 或更高）
   - 完成创建

2. **启动模拟器**：
   - 在 Device Manager 中点击设备旁边的播放按钮
   - 等待模拟器启动完成

### 选项 B：使用物理设备

1. **启用 USB 调试**：
   - 在手机上：`设置 → 关于手机 → 连续点击"版本号"7次`
   - 然后：`设置 → 系统 → 开发者选项 → USB 调试`

2. **连接设备**：
   - 用 USB 线连接手机和电脑
   - 手机上允许 USB 调试

3. **验证连接**：
   - 在 Android Studio 底部会显示设备名称
   - 或在终端运行：`adb devices`

## 第四步：运行应用

### 方法一：使用工具栏按钮

1. **选择设备**：
   - 在顶部工具栏的设备选择器中选择设备

2. **运行应用**：
   - 点击绿色的 "Run" 按钮（▶️）
   - 或使用快捷键：`Shift+F10` (Windows/Linux) 或 `Ctrl+R` (Mac)

### 方法二：使用菜单

1. `Run → Run 'app'`
2. 或使用快捷键：`Shift+F10`

## 配置检查清单

运行前确保：

- ✅ 项目已成功同步（底部状态栏显示 "Gradle sync finished"）
- ✅ 已选择设备（模拟器或物理设备）
- ✅ MainActivity 已正确配置在 AndroidManifest.xml 中
- ✅ 没有编译错误（检查 "Build" 窗口）

## 常见问题

### Q1: 找不到 "Android App" 选项

**解决方案**：
1. 确保项目已正确识别为 Android 项目
2. 检查 `app/build.gradle.kts` 文件是否存在
3. 尝试：`File → Sync Project with Gradle Files`

### Q2: 设备列表为空

**解决方案**：
1. 检查设备是否已连接（物理设备）
2. 检查模拟器是否已启动
3. 运行：`adb devices` 查看设备列表
4. 如果使用物理设备，确保 USB 调试已启用

### Q3: "Gradle sync failed"

**解决方案**：
1. 检查网络连接
2. 检查 `gradle.properties` 中的代理设置（如果有）
3. 尝试：`File → Invalidate Caches / Restart`
4. 检查 `gradle/wrapper/gradle-wrapper.properties` 中的 Gradle 版本

### Q4: 应用安装失败

**解决方案**：
1. 检查设备存储空间
2. 卸载旧版本：`adb uninstall com.example.pantrychef`
3. 检查设备是否允许安装未知来源应用

### Q5: 找不到 MainActivity

**解决方案**：
1. 检查 `app/src/main/java/com/example/pantrychef/MainActivity.kt` 是否存在
2. 检查 `AndroidManifest.xml` 中的配置
3. 运行：`./check_mainactivity.sh` 进行诊断
4. `File → Invalidate Caches / Restart`

## 快速运行命令

如果配置正确，你也可以在终端运行：

```bash
# 构建并安装
./gradlew installDebug

# 启动应用（需要先配置 adb）
adb shell am start -n com.example.pantrychef/.MainActivity
```

## 调试配置

如果需要调试：

1. **设置断点**：
   - 在代码行号左侧点击设置断点

2. **以调试模式运行**：
   - 点击绿色的 "Debug" 按钮（🐛）
   - 或：`Run → Debug 'app'`
   - 或快捷键：`Shift+F9`

3. **查看日志**：
   - 底部 "Logcat" 窗口查看应用日志
   - 过滤：输入 "PantryChef" 或 "MainActivity"

## 配置截图说明

### 运行配置界面
```
Run/Debug Configurations
├── + (添加)
│   └── Android App
│       ├── Name: app
│       ├── Module: PantryChef.app
│       ├── Launch: Default Activity
│       └── Target: USB Device / Emulator
```

### 设备选择器
```
[设备选择下拉菜单]
├── Pixel 5 API 33 (模拟器)
├── SM-G991B (物理设备)
└── No devices
```

---

**提示**：首次运行可能需要较长时间（下载依赖、构建等），请耐心等待。

