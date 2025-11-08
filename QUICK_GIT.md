# 快速 Git 使用指南

## 🚀 上传代码到 GitHub（最简单的方法）

### 方法一：使用脚本（推荐）

```bash
./push.sh
```

脚本会自动：
1. 添加所有更改
2. 提交更改
3. 推送到 GitHub

### 方法二：手动命令

```bash
# 1. 添加所有更改
git add .

# 2. 提交更改
git commit -m "Update: Remove comments and translate to English"

# 3. 推送到 GitHub
git push origin main
```

---

## 📥 从 GitHub 克隆到本地（在其他电脑上）

```bash
# 克隆仓库
git clone git@github.com:Su-ming16/Pantry-Chef.git

# 或使用 HTTPS（如果 SSH 未配置）
git clone https://github.com/Su-ming16/Pantry-Chef.git

# 进入项目目录
cd Pantry-Chef

# 在 Android Studio 中打开项目
```

---

## 📝 日常使用

### 每次修改代码后：

```bash
git add .
git commit -m "描述你的更改"
git push origin main
```

### 获取最新代码：

```bash
git pull origin main
```

---

## ⚠️ 常见问题

### 问题：推送时提示需要认证

**解决方案**：
1. 如果使用 SSH（当前配置）：
   - 确保已配置 SSH key
   - 查看：`cat ~/.ssh/id_rsa.pub`
   - 添加到 GitHub：Settings → SSH and GPG keys

2. 如果使用 HTTPS：
   ```bash
   # 更改远程地址
   git remote set-url origin https://github.com/Su-ming16/Pantry-Chef.git
   ```
   然后使用 Personal Access Token 作为密码

---

**详细说明请查看**: `GIT_GUIDE.md`

