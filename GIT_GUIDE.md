# Git 和 GitHub 使用指南

## 快速开始

### 1. 上传代码到 GitHub

#### 方法一：使用命令行（推荐）

```bash
# 1. 进入项目目录
cd /Users/suming/Desktop/PantryChef

# 2. 查看当前状态
git status

# 3. 添加所有更改的文件
git add .

# 4. 提交更改（添加描述信息）
git commit -m "Update: Remove all comments and translate to English"

# 5. 推送到 GitHub
git push origin main
```

#### 方法二：使用脚本（最简单）

我已经为你创建了一个脚本 `push.sh`，直接运行：

```bash
./push.sh
```

### 2. 从 GitHub 克隆到本地（在其他电脑上）

```bash
# 克隆仓库
git clone https://github.com/Su-ming16/Pantry-Chef.git

# 进入项目目录
cd Pantry-Chef

# 打开 Android Studio 或运行项目
```

---

## 详细说明

### 首次设置（如果还没有连接远程仓库）

```bash
# 1. 初始化 Git（如果还没有）
git init

# 2. 添加远程仓库
git remote add origin https://github.com/Su-ming16/Pantry-Chef.git

# 3. 添加所有文件
git add .

# 4. 提交
git commit -m "Initial commit"

# 5. 推送到 GitHub
git push -u origin main
```

### 日常使用流程

每次修改代码后：

```bash
# 1. 查看更改
git status

# 2. 添加更改的文件
git add .                    # 添加所有更改
# 或
git add <文件名>            # 添加特定文件

# 3. 提交更改
git commit -m "描述你的更改"

# 4. 推送到 GitHub
git push origin main
```

### 常用命令

```bash
# 查看状态
git status

# 查看更改内容
git diff

# 查看提交历史
git log

# 拉取最新代码（从 GitHub）
git pull origin main

# 查看远程仓库
git remote -v
```

---

## 常见问题

### Q1: 提示 "remote origin already exists"

**解决方案**：
```bash
# 查看现有远程仓库
git remote -v

# 如果需要更改，先删除再添加
git remote remove origin
git remote add origin https://github.com/Su-ming16/Pantry-Chef.git
```

### Q2: 提示 "Authentication failed"

**解决方案**：
1. 使用 Personal Access Token（推荐）
   - GitHub → Settings → Developer settings → Personal access tokens
   - 生成新 token，勾选 `repo` 权限
   - 使用 token 作为密码

2. 或使用 SSH（更安全）
   ```bash
   # 生成 SSH key
   ssh-keygen -t ed25519 -C "your_email@example.com"
   
   # 添加到 GitHub
   # Settings → SSH and GPG keys → New SSH key
   
   # 更改远程地址为 SSH
   git remote set-url origin git@github.com:Su-ming16/Pantry-Chef.git
   ```

### Q3: 提示 "Please tell me who you are"

**解决方案**：
```bash
git config --global user.name "Your Name"
git config --global user.email "your_email@example.com"
```

### Q4: 想撤销更改

```bash
# 撤销未提交的更改
git restore <文件名>

# 撤销所有未提交的更改
git restore .

# 撤销最后一次提交（保留更改）
git reset --soft HEAD~1
```

---

## 推荐工作流程

1. **开始工作前**：
   ```bash
   git pull origin main  # 拉取最新代码
   ```

2. **修改代码后**：
   ```bash
   git add .
   git commit -m "描述更改"
   git push origin main
   ```

3. **每天结束前**：
   ```bash
   git push origin main  # 确保代码已备份
   ```

---

## 注意事项

1. **不要提交敏感信息**：
   - `local.properties`（已添加到 .gitignore）
   - API keys
   - 密码

2. **提交前检查**：
   - 运行 `git status` 查看要提交的文件
   - 确保没有提交不必要的文件

3. **提交信息要清晰**：
   - 使用有意义的提交信息
   - 例如：`"Add recipe detail page"` 而不是 `"update"`

---

**提示**：如果遇到问题，可以随时查看 GitHub 网页端的帮助文档。

