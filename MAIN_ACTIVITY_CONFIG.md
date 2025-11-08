# MainActivity 配置说明

## 配置概览

MainActivity 是应用的入口Activity，负责：
1. 初始化应用界面
2. 设置底部导航
3. 配置Navigation Component

## 关键配置点

### 1. AndroidManifest.xml 配置

```xml
<activity
    android:name=".MainActivity"
    android:exported="true"
    android:configChanges="orientation|screenSize|keyboardHidden|screenLayout">
    <intent-filter>
        <action android:name="android.intent.action.MAIN" />
        <category android:name="android.intent.category.LAUNCHER" />
    </intent-filter>
</activity>
```

**配置说明**：
- `android:name=".MainActivity"`: 指定Activity类名
- `android:exported="true"`: 允许其他应用启动此Activity（作为启动Activity必须为true）
- `android:configChanges`: 配置变更时不重建Activity，提升性能
  - `orientation`: 屏幕方向改变
  - `screenSize`: 屏幕尺寸改变
  - `keyboardHidden`: 键盘显示/隐藏
  - `screenLayout`: 屏幕布局改变

### 2. MainActivity.kt 代码结构

```kotlin
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 1. 启用Edge-to-Edge（全屏显示）
        enableEdgeToEdge()
        
        // 2. 设置布局
        setContentView(R.layout.activity_main)
        
        // 3. 设置底部导航
        setupBottomNavigation()
    }
    
    private fun setupBottomNavigation() {
        // 获取NavHostFragment
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as? NavHostFragment
            ?: return
        
        // 获取NavController
        val navController = navHostFragment.navController
        
        // 获取底部导航视图
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        
        // 将底部导航与NavController关联
        bottomNav.setupWithNavController(navController)
    }
}
```

### 3. activity_main.xml 布局结构

```xml
<androidx.constraintlayout.widget.ConstraintLayout>
    <!-- NavHostFragment: 承载Fragment的容器 -->
    <androidx.fragment.app.FragmentContainerView
        android:id="@+id/nav_host_fragment"
        android:name="androidx.navigation.fragment.NavHostFragment"
        app:defaultNavHost="true"
        app:navGraph="@navigation/nav_graph" />
    
    <!-- BottomNavigationView: 底部导航栏 -->
    <com.google.android.material.bottomnavigation.BottomNavigationView
        android:id="@+id/bottom_navigation"
        app:menu="@menu/bottom_navigation" />
</androidx.constraintlayout.widget.ConstraintLayout>
```

## 配置要点

### ✅ 已配置项

1. **Edge-to-Edge支持**
   - 使用 `enableEdgeToEdge()` 启用全屏显示
   - 适配现代Android设备的沉浸式体验

2. **Navigation Component**
   - NavHostFragment 配置在布局中
   - 使用 `app:navGraph` 指定导航图
   - `app:defaultNavHost="true"` 处理系统返回键

3. **底部导航**
   - 使用 `setupWithNavController()` 关联导航控制器
   - 自动处理Tab切换和返回栈

4. **屏幕旋转支持**
   - `configChanges` 配置允许旋转不重建Activity
   - 提升用户体验和性能

### 🔧 可选配置

如果需要限制屏幕方向（Alpha版本建议保持竖屏），可以添加：

```xml
android:screenOrientation="portrait"
```

但根据需求文档，应该允许旋转，所以当前配置是正确的。

## 常见问题

### Q1: 为什么使用 FragmentContainerView 而不是 Fragment？

**A**: FragmentContainerView 是专门为Navigation Component设计的，提供更好的性能和生命周期管理。

### Q2: setupWithNavController() 做了什么？

**A**: 这个方法会：
- 自动处理底部导航的点击事件
- 同步底部导航选中状态与当前Fragment
- 管理返回栈，确保返回键行为正确

### Q3: 如果NavHostFragment找不到怎么办？

**A**: 代码中使用了安全调用 `as? NavHostFragment ?: return`，如果找不到会静默返回，不会崩溃。

## 验证配置

运行应用后，检查以下功能：

1. ✅ 应用能正常启动
2. ✅ 底部导航三个Tab可以切换
3. ✅ 点击Tab能正确跳转到对应Fragment
4. ✅ 旋转屏幕不会崩溃
5. ✅ 从详情页返回能正确回到来源页

## 后续扩展

如果需要添加其他功能，可以在MainActivity中：

1. **添加Toolbar/ActionBar**
   ```kotlin
   setSupportActionBar(toolbar)
   ```

2. **处理深链接**
   ```kotlin
   navController.handleDeepLink(intent)
   ```

3. **添加全局导航监听**
   ```kotlin
   navController.addOnDestinationChangedListener { ... }
   ```

---

**配置完成日期**: 2024
**版本**: Alpha (Week 9)

