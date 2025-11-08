#!/bin/bash

# MainActivity 诊断脚本

echo "🔍 检查 MainActivity 配置..."
echo ""

# 1. 检查文件是否存在
echo "1. 检查 MainActivity.kt 文件:"
if [ -f "app/src/main/java/com/example/pantrychef/MainActivity.kt" ]; then
    echo "   ✅ 文件存在: app/src/main/java/com/example/pantrychef/MainActivity.kt"
else
    echo "   ❌ 文件不存在!"
    exit 1
fi

echo ""

# 2. 检查包名
echo "2. 检查包名:"
PACKAGE=$(grep "^package" app/src/main/java/com/example/pantrychef/MainActivity.kt | cut -d' ' -f2)
echo "   包名: $PACKAGE"
if [ "$PACKAGE" = "com.example.pantrychef" ]; then
    echo "   ✅ 包名正确"
else
    echo "   ⚠️  包名可能不匹配"
fi

echo ""

# 3. 检查类定义
echo "3. 检查类定义:"
if grep -q "class MainActivity" app/src/main/java/com/example/pantrychef/MainActivity.kt; then
    echo "   ✅ MainActivity 类已定义"
else
    echo "   ❌ MainActivity 类未找到"
fi

echo ""

# 4. 检查 AndroidManifest.xml
echo "4. 检查 AndroidManifest.xml:"
if grep -q "android:name=\".MainActivity\"" app/src/main/AndroidManifest.xml; then
    echo "   ✅ AndroidManifest.xml 中已注册 MainActivity"
else
    echo "   ❌ AndroidManifest.xml 中未找到 MainActivity"
fi

echo ""

# 5. 检查 intent-filter
echo "5. 检查启动配置:"
if grep -q "android.intent.action.MAIN" app/src/main/AndroidManifest.xml; then
    echo "   ✅ 已配置为启动 Activity"
else
    echo "   ❌ 未配置为启动 Activity"
fi

echo ""

# 6. 检查编译后的类
echo "6. 检查编译后的类文件:"
if [ -f "app/build/intermediates/javac/debug/classes/com/example/pantrychef/MainActivity.class" ]; then
    echo "   ✅ 编译后的类文件存在"
elif [ -d "app/build/intermediates" ]; then
    echo "   ⚠️  编译后的类文件未找到（可能需要先构建）"
    echo "   运行: ./gradlew assembleDebug"
else
    echo "   ⚠️  构建目录不存在（需要先构建）"
fi

echo ""
echo "📋 总结:"
echo "   MainActivity 文件位置: app/src/main/java/com/example/pantrychef/MainActivity.kt"
echo "   完整类名: com.example.pantrychef.MainActivity"
echo "   启动命令: adb shell am start -n com.example.pantrychef/.MainActivity"
echo ""
echo "💡 如果仍然找不到，请尝试:"
echo "   1. 在 Android Studio 中: File → Invalidate Caches / Restart"
echo "   2. 重新构建: ./gradlew clean assembleDebug"
echo "   3. 检查 Android Studio 的项目结构视图"

