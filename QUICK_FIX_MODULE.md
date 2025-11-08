# 快速修复 Module 下拉菜单为空

## 最简单的解决方法（3步）

### 步骤 1：同步 Gradle
```
File → Sync Project with Gradle Files
```
等待同步完成（底部状态栏显示 "Gradle sync finished"）

### 步骤 2：重新打开配置
```
Run → Edit Configurations...
```

### 步骤 3：检查 Module
现在 Module 下拉菜单应该显示 `PantryChef.app`

---

## 如果还是不行

### 方法 A：清理并重建
```
Build → Clean Project
Build → Rebuild Project
File → Sync Project with Gradle Files
```

### 方法 B：使缓存无效
```
File → Invalidate Caches...
选择所有选项
点击 "Invalidate and Restart"
```

### 方法 C：手动输入
在 Module 字段中直接输入：`PantryChef.app`

---

## 验证项目是否正确

在终端运行：
```bash
./gradlew projects
```

应该显示：
```
Root project 'PantryChef'
\--- Project ':app'
```

如果显示这个，说明项目配置正确，只是 Android Studio 需要同步。

