#!/bin/bash

# PantryChef 快速运行脚本
# 使用方法: ./run.sh

cd "$(dirname "$0")"

echo "🚀 开始构建并运行 PantryChef..."

# 检查 adb 是否可用
if ! command -v adb &> /dev/null; then
    # 尝试使用默认路径
    if [ -f "$HOME/Library/Android/sdk/platform-tools/adb" ]; then
        export PATH=$PATH:$HOME/Library/Android/sdk/platform-tools
    else
        echo "❌ 错误: 未找到 adb 命令"
        echo ""
        echo "请配置 Android SDK 环境变量："
        echo "  1. 查看 SETUP_ADB.md 了解详细步骤"
        echo "  2. 或使用 Android Studio 直接运行项目"
        echo "  3. 或使用 ./build.sh 仅构建 APK（不需要设备）"
        echo ""
        exit 1
    fi
fi

# 检查设备连接
echo "📱 检查设备连接..."
if ! adb devices | grep -q "device$"; then
    echo "❌ 错误: 未找到连接的设备或模拟器"
    echo ""
    echo "请确保:"
    echo "  1. 设备已通过USB连接并启用USB调试"
    echo "  2. 或已启动Android模拟器"
    echo ""
    echo "运行 'adb devices' 查看连接的设备"
    echo ""
    echo "提示:"
    echo "  - 使用 ./build.sh 可以仅构建 APK（不需要设备）"
    echo "  - 使用 Android Studio 可以直接运行项目"
    echo "  - 查看 SETUP_ADB.md 了解如何配置 adb"
    exit 1
fi

echo "✅ 设备已连接"

# 清理并构建
echo "🔨 构建应用..."
./gradlew clean assembleDebug

if [ $? -ne 0 ]; then
    echo "❌ 构建失败"
    exit 1
fi

# 安装到设备
echo "📦 安装应用到设备..."
./gradlew installDebug

if [ $? -ne 0 ]; then
    echo "❌ 安装失败"
    exit 1
fi

# 启动应用
echo "🎯 启动应用..."
adb shell am start -n com.example.pantrychef/.MainActivity

if [ $? -eq 0 ]; then
    echo "✅ 应用已启动!"
    echo ""
    echo "查看日志: adb logcat | grep PantryChef"
else
    echo "❌ 启动失败"
    exit 1
fi

