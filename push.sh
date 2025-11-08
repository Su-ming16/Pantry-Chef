#!/bin/bash

# Git 推送脚本
# 使用方法: ./push.sh

cd "$(dirname "$0")"

echo "📦 准备上传代码到 GitHub..."
echo ""

# 检查是否在 git 仓库中
if ! git rev-parse --git-dir > /dev/null 2>&1; then
    echo "❌ 错误: 当前目录不是 Git 仓库"
    echo "请先运行: git init"
    exit 1
fi

# 检查是否有更改
if [ -z "$(git status --porcelain)" ]; then
    echo "✅ 没有需要提交的更改"
    exit 0
fi

# 显示更改状态
echo "📋 当前更改:"
git status --short
echo ""

# 添加所有更改
echo "➕ 添加所有更改..."
git add .

# 提交
echo "💾 提交更改..."
read -p "请输入提交信息 (或按回车使用默认): " commit_msg
if [ -z "$commit_msg" ]; then
    commit_msg="Update: Remove comments and translate to English"
fi

git commit -m "$commit_msg"

# 推送到 GitHub
echo "🚀 推送到 GitHub..."
git push origin main

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ 成功上传到 GitHub!"
    echo ""
    echo "查看仓库: https://github.com/Su-ming16/Pantry-Chef"
else
    echo ""
    echo "❌ 推送失败"
    echo "请检查:"
    echo "  1. 网络连接"
    echo "  2. GitHub 认证"
    echo "  3. 远程仓库地址是否正确"
    exit 1
fi

