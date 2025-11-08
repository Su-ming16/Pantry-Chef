#!/bin/bash

# PantryChef 仅构建脚本（不需要设备）
# 使用方法: ./build.sh

cd "$(dirname "$0")"

echo "🔨 开始构建 PantryChef APK..."

# 清理并构建
echo "📦 清理之前的构建..."
./gradlew clean

echo "🔨 构建 Debug APK..."
./gradlew assembleDebug

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ 构建成功!"
    echo ""
    echo "APK 文件位置:"
    echo "  app/build/outputs/apk/debug/app-debug.apk"
    echo ""
    echo "下一步:"
    echo "  1. 在 Android Studio 中打开项目并运行"
    echo "  2. 或配置 adb 后使用 ./run.sh"
    echo "  3. 或手动安装: adb install app/build/outputs/apk/debug/app-debug.apk"
else
    echo "❌ 构建失败"
    exit 1
fi

