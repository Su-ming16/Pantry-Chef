# 快速开始指南

## 在 Android Studio 中运行项目

### 最简单的步骤：

1. **打开项目**
   ```
   Android Studio → Open → 选择 PantryChef 文件夹
   ```

2. **等待同步**
   - 等待 Gradle 同步完成（底部状态栏）

3. **选择设备**
   - 点击顶部工具栏的设备选择器
   - 选择模拟器或连接的设备

4. **运行**
   - 点击绿色的 "Run" 按钮（▶️）
   - 或按 `Shift+F10` (Windows/Linux) 或 `Ctrl+R` (Mac)

## 如果没有设备

### 创建模拟器：

1. `Tools → Device Manager`
2. 点击 "Create Device"
3. 选择设备（推荐 Pixel 5）
4. 选择系统镜像（推荐 API 33）
5. 完成创建并启动

### 连接物理设备：

1. 启用 USB 调试（设置 → 开发者选项）
2. 用 USB 连接设备
3. 允许 USB 调试

## 如果遇到问题

### 找不到 MainActivity？

1. `File → Invalidate Caches / Restart`
2. `Build → Clean Project`
3. `Build → Rebuild Project`

### 找不到运行配置？

1. `Run → Edit Configurations...`
2. 点击 "+" → 选择 "Android App"
3. 配置：
   - Name: `app`
   - Module: `PantryChef.app`
   - Launch: `Default Activity`

### Gradle 同步失败？

1. 检查网络连接
2. `File → Sync Project with Gradle Files`
3. 如果还不行，查看详细错误信息

## 验证配置

运行诊断脚本：
```bash
./check_mainactivity.sh
```

## 详细文档

- `ANDROID_STUDIO_SETUP.md` - 完整的 Android Studio 配置指南
- `TERMINAL_RUN_GUIDE.md` - 终端运行指南
- `SETUP_ADB.md` - ADB 配置指南

---

**提示**：首次运行需要下载依赖，可能需要几分钟时间。

