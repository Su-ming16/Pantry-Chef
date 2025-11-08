# 修复 Module 下拉菜单为空的问题

## 问题描述
在 Android Studio 的 Run Configuration 中，Module 下拉菜单显示为空。

## 解决方案

### 方法一：重新同步 Gradle（推荐）

1. **同步项目**：
   - 点击 `File → Sync Project with Gradle Files`
   - 或点击顶部工具栏的 "Sync Project with Gradle Files" 按钮（🔄）
   - 等待同步完成（底部状态栏会显示进度）

2. **检查同步结果**：
   - 如果成功，底部状态栏会显示 "Gradle sync finished"
   - 如果失败，查看 "Build" 窗口的错误信息

3. **重新打开配置**：
   - `Run → Edit Configurations...`
   - 现在 Module 下拉菜单应该显示 `PantryChef.app`

### 方法二：清理并重新构建

1. **清理项目**：
   - `Build → Clean Project`
   - 等待清理完成

2. **重新构建**：
   - `Build → Rebuild Project`
   - 等待构建完成

3. **同步 Gradle**：
   - `File → Sync Project with Gradle Files`

### 方法三：使缓存无效并重启

1. **使缓存无效**：
   - `File → Invalidate Caches...`
   - 选择：
     - ✅ Clear file system cache and Local History
     - ✅ Clear downloaded shared indexes
   - 点击 "Invalidate and Restart"

2. **等待 Android Studio 重启**

3. **重新同步**：
   - `File → Sync Project with Gradle Files`

### 方法四：检查项目结构

1. **检查项目视图**：
   - 确保在 "Project" 视图（不是 "Android" 视图）
   - 应该能看到 `app` 文件夹

2. **检查 build.gradle.kts**：
   - 确保 `app/build.gradle.kts` 文件存在
   - 确保文件内容正确

3. **检查 settings.gradle.kts**：
   - 确保包含 `include(":app")`

### 方法五：手动指定 Module

如果下拉菜单仍然为空，可以尝试手动输入：

1. **打开配置**：
   - `Run → Edit Configurations...`

2. **手动输入**：
   - 在 Module 字段中直接输入：`PantryChef.app`
   - 或尝试：`:app`

3. **应用并保存**：
   - 点击 "Apply"
   - 点击 "OK"

## 验证步骤

同步完成后，检查：

1. **检查 Build 窗口**：
   - 应该没有错误
   - 应该显示 "BUILD SUCCESSFUL"

2. **检查项目结构**：
   - 在 Project 视图中应该能看到：
     ```
     PantryChef
     ├── app
     │   ├── build.gradle.kts
     │   └── src
     ├── build.gradle.kts
     └── settings.gradle.kts
     ```

3. **检查 Gradle 工具窗口**：
   - 打开 `View → Tool Windows → Gradle`
   - 应该能看到项目结构

## 常见错误及解决

### 错误：Gradle sync failed

**可能原因**：
- 网络问题（无法下载依赖）
- Gradle 版本不兼容
- 配置文件错误

**解决方法**：
1. 检查网络连接
2. 检查 `gradle/wrapper/gradle-wrapper.properties`
3. 查看详细错误信息（Build 窗口）

### 错误：Project structure is not recognized

**解决方法**：
1. 确保在项目根目录打开（包含 `settings.gradle.kts`）
2. 关闭项目，重新打开
3. `File → Invalidate Caches / Restart`

### 错误：Module 'app' not found

**解决方法**：
1. 检查 `settings.gradle.kts` 是否包含 `include(":app")`
2. 检查 `app/build.gradle.kts` 是否存在
3. 重新同步项目

## 快速检查清单

运行以下命令检查项目配置：

```bash
# 检查项目结构
./gradlew projects

# 检查任务
./gradlew tasks

# 清理并构建
./gradlew clean build
```

如果这些命令都能成功执行，说明项目配置是正确的。

## 如果仍然无法解决

1. **检查 Android Studio 版本**：
   - 推荐使用 Android Studio Hedgehog 或更新版本
   - `Help → About` 查看版本

2. **检查 JDK 版本**：
   - `File → Project Structure → SDK Location`
   - 确保 JDK 版本是 11 或更高

3. **重新导入项目**：
   - 关闭 Android Studio
   - 删除 `.idea` 文件夹（如果存在）
   - 重新打开项目

4. **查看日志**：
   - `Help → Show Log in Finder/Explorer`
   - 查看最新的日志文件

---

**提示**：大多数情况下，`File → Sync Project with Gradle Files` 就能解决问题。

