# PantryChef

Pantry Chef 是一款帮助用户根据现有食材推荐菜谱的Android应用。核心痛点是：用户打开冰箱看到一堆食材，却不知道能做什么菜。这个App让用户输入手头的食材，自动推荐最合适的菜谱，减少食材浪费，提升烹饪效率。

## 项目状态

**当前版本**: Alpha (Week 9)

## 核心功能模块

1. **我的食材（My Pantry）**
   - 用户可以添加/删除自己冰箱里的食材
   - 食材数据存储在本地数据库（Room）
   - 支持自动补全输入，避免拼写错误和大小写问题

2. **发现食谱（Discover Recipes）**
   - 点击"立即烹饪"按钮，基于已添加的食材推荐菜谱
   - 显示每个菜谱的食材覆盖率（Beta版本）
   - 按覆盖率从高到低排序，优先推荐最容易做的菜
   - 点击菜谱卡片进入详情页

3. **我的收藏（My Favorites）**
   - 用户可以收藏喜欢的菜谱到本地
   - 收藏数据存储在本地数据库，无需联网即可查看
   - 支持取消收藏

## 技术架构

### MVVM架构
- **Model**: 数据模型（Ingredient, Recipe, RecipeDetail, FavoriteRecipe）
- **View**: Fragment + ViewBinding
- **ViewModel**: 业务逻辑和状态管理

### 技术栈
- **语言**: Kotlin
- **UI**: Material Design 3, ViewBinding
- **架构组件**: ViewModel, LiveData, Navigation Component
- **数据库**: Room
- **网络**: Retrofit（预留，Alpha版本使用假数据）
- **图片加载**: Glide
- **异步**: Kotlin Coroutines + Flow

## 项目结构

```
app/src/main/java/com/example/pantrychef/
├── data/
│   ├── model/              # 数据模型
│   │   ├── Ingredient.kt
│   │   ├── Recipe.kt
│   │   └── FavoriteRecipe.kt
│   ├── database/           # Room数据库
│   │   ├── PantryChefDatabase.kt
│   │   ├── IngredientDao.kt
│   │   └── FavoriteRecipeDao.kt
│   └── repository/         # 数据仓库
│       ├── IngredientRepository.kt
│       ├── RecipeRepository.kt
│       ├── FakeIngredientRepository.kt
│       └── FakeRecipeRepository.kt
└── ui/
    ├── common/             # 通用组件
    │   └── StateView.kt
    ├── pantry/             # 我的食材页
    │   ├── MyPantryFragment.kt
    │   ├── MyPantryViewModel.kt
    │   └── IngredientAdapter.kt
    ├── discover/           # 发现食谱页
    │   ├── DiscoverRecipesFragment.kt
    │   ├── DiscoverRecipesViewModel.kt
    │   └── RecipeAdapter.kt
    ├── detail/             # 菜谱详情页
    │   ├── RecipeDetailFragment.kt
    │   ├── RecipeDetailViewModel.kt
    │   └── IngredientDetailAdapter.kt
    ├── favorites/          # 我的收藏页
    │   ├── MyFavoritesFragment.kt
    │   └── MyFavoritesViewModel.kt
    └── ViewModelFactory.kt
```

## 运行方式

### 前置要求
- Android Studio Hedgehog 或更高版本
- JDK 11 或更高版本
- Android SDK 24 或更高版本

### 构建步骤

1. 克隆项目
```bash
git clone <repository-url>
cd PantryChef
```

2. 打开项目
   - 使用Android Studio打开项目
   - 等待Gradle同步完成

3. 运行应用
   - 连接Android设备或启动模拟器
   - 点击"Run"按钮或使用快捷键运行

### 依赖说明

项目使用Version Catalog管理依赖，所有依赖定义在 `gradle/libs.versions.toml` 中。

主要依赖：
- AndroidX Core KTX
- Material Design Components
- Navigation Component
- Room Database
- Retrofit（预留）
- Glide
- Kotlin Coroutines

## Alpha版本限制

- ✅ 只实现单个食材的API调用，不涉及多食材逻辑
- ✅ UI设计简洁实用即可，不需要过度美化
- ✅ 使用假数据实现，便于UI开发和演示
- ✅ MVVM架构基础框架搭建完成

## 后续版本计划

### Beta版本
- 接入TheMealDB真实API
- 实现多食材搜索和匹配度计算
- 优化UI和用户体验
- 添加更多功能

## 契约文档

详细的数据层契约文档请参考 [CONTRACT.md](./CONTRACT.md)

## 许可证

[待定]

## 贡献者

[待定]
