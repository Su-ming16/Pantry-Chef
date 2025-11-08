# PantryChef 数据层契约文档

本文档定义了数据层（Repository）与UI层之间的契约，确保B（数据层）和C（界面层）能够并行开发并无缝集成。

## 版本信息
- **版本**: Alpha (Week 9)
- **发布日期**: 2024
- **状态**: 基线版本，后续变更需全员确认

---

## 1. 数据模型

### 1.1 Ingredient（食材）
```kotlin
data class Ingredient(
    val id: Long,        // 唯一标识，自增
    val name: String     // 食材名称，如 "Chicken", "Potato", "Onion"
)
```

### 1.2 Recipe（菜谱列表项）
```kotlin
data class Recipe(
    val id: String,              // 菜谱标识（来自API）
    val name: String,            // 菜谱名称
    val thumbnail: String? = null,  // 缩略图URL
    val category: String? = null,   // 分类
    val area: String? = null       // 地区
)
```

### 1.3 RecipeDetail（菜谱详情）
```kotlin
data class RecipeDetail(
    val id: String,                    // 菜谱标识
    val name: String,                   // 菜谱名称
    val image: String? = null,          // 完整图片URL
    val category: String? = null,       // 分类
    val area: String? = null,           // 地区
    val instructions: String? = null,  // 做法文本
    val ingredients: List<IngredientItem> = emptyList()  // 配料-用量清单
)

data class IngredientItem(
    val name: String,      // 配料名称
    val measure: String? = null  // 用量
)
```

### 1.4 FavoriteRecipe（收藏菜谱）
```kotlin
data class FavoriteRecipe(
    val recipeId: String,      // 菜谱标识（主键）
    val name: String,          // 菜谱名称
    val thumbnail: String? = null,
    val category: String? = null,
    val area: String? = null,
    val addedAt: Long = System.currentTimeMillis()  // 收藏时间
)
```

---

## 2. Repository接口

### 2.1 IngredientRepository（食材仓库）

```kotlin
interface IngredientRepository {
    // 获取所有食材
    fun getAllIngredients(): Flow<List<Ingredient>>
    
    // 搜索食材（用于自动补全）
    fun searchIngredients(query: String): Flow<List<Ingredient>>
    
    // 添加食材
    suspend fun addIngredient(name: String)
    
    // 删除食材
    suspend fun deleteIngredient(ingredient: Ingredient)
}
```

**行为约定**:
- `getAllIngredients()`: 返回按名称排序的食材列表
- `searchIngredients()`: 返回名称包含查询关键词的食材（不区分大小写）
- `addIngredient()`: 如果食材已存在（不区分大小写），则不重复添加
- `deleteIngredient()`: 删除指定食材

### 2.2 RecipeRepository（菜谱仓库）

```kotlin
interface RecipeRepository {
    // 根据单个食材搜索菜谱（Alpha版本）
    fun searchRecipesByIngredient(ingredient: String): Flow<Result<List<Recipe>>>
    
    // 根据菜谱ID获取详情
    fun getRecipeDetail(recipeId: String): Flow<Result<RecipeDetail>>
    
    // 收藏菜谱
    suspend fun addFavorite(recipe: Recipe)
    
    // 取消收藏
    suspend fun removeFavorite(recipeId: String)
    
    // 获取所有收藏的菜谱
    fun getAllFavorites(): Flow<List<Recipe>>
    
    // 检查是否已收藏
    fun isFavorite(recipeId: String): Flow<Boolean>
}
```

**行为约定**:
- `searchRecipesByIngredient()`: 
  - 输入为空字符串时，返回空列表（Success）
  - 搜索失败时，返回 `Result.failure(Exception)`
  - 搜索成功但无结果时，返回空列表（Success）
  
- `getRecipeDetail()`:
  - 菜谱不存在时，返回 `Result.failure(Exception("Recipe not found"))`
  - 网络错误时，返回 `Result.failure(Exception)`
  
- `addFavorite()`: 如果已收藏，则更新收藏时间
- `removeFavorite()`: 如果未收藏，则静默忽略
- `getAllFavorites()`: 返回按收藏时间倒序排列的列表
- `isFavorite()`: 返回该菜谱是否已收藏

---

## 3. 错误与空数据约定

### 3.1 空数据场景
- **食材列表为空**: 返回空列表 `[]`，UI显示空状态
- **搜索结果为空**: 返回空列表 `[]`，UI显示空状态
- **收藏列表为空**: 返回空列表 `[]`，UI显示空状态

### 3.2 错误场景
- **网络错误**: 返回 `Result.failure(IOException)` 或类似异常
- **API错误**: 返回 `Result.failure(Exception(message))`
- **数据解析错误**: 返回 `Result.failure(Exception(message))`

### 3.3 重试机制
- UI层负责触发重试（通过StateView的"重试"按钮）
- 数据层负责执行重试（重新调用API或数据库查询）
- 重试时，Repository应重新执行相同的操作

---

## 4. 性能与缓存提示

### 4.1 Alpha版本
- **列表分页**: 不需要，一次性返回所有结果
- **缓存策略**: 不需要，每次请求都从API获取
- **图片加载**: 使用Glide自动缓存

### 4.2 Beta版本（预留）
- 多食材检索：返回结构包含"覆盖度/匹配度"字段
- 列表分页：可能需要（如果API支持）
- 本地缓存：考虑缓存热门菜谱

---

## 5. 导航约定

### 5.1 目的地ID
- `myPantryFragment`: 我的食材页
- `discoverRecipesFragment`: 发现食谱页
- `myFavoritesFragment`: 我的收藏页
- `recipeDetailFragment`: 菜谱详情页

### 5.2 导航参数
- **详情页入参**: `recipeId: String`（必需）
- **跳转动作名**: 
  - `action_discoverRecipesFragment_to_recipeDetailFragment`
  - `action_myFavoritesFragment_to_recipeDetailFragment`

### 5.3 返回栈行为
- 从详情页返回时，返回到来源页（发现或收藏）
- 底部导航切换时，保持各Tab的状态

---

## 6. 状态与文案规范

### 6.1 加载态
- **视觉**: 进度指示器（ProgressBar）
- **文案**: "正在加载…"
- **触发时机**: 数据请求开始时

### 6.2 空态
- **视觉**: 图标 + 标题 + 描述
- **标题**: "暂无数据"
- **描述**: 
  - 食材页: "请先添加食材"
  - 发现页: "请先添加食材，然后点击\"立即烹饪\"按钮"
  - 收藏页: "还没有收藏的菜谱"

### 6.3 错误态
- **视觉**: 错误图标 + 标题 + 描述 + 重试按钮
- **标题**: "加载失败"
- **描述**: "请检查网络后重试"
- **按钮**: "重试"（最小宽度120dp）

---

## 7. 假数据实现说明

### 7.1 当前实现
- `FakeIngredientRepository`: 内存中保存食材列表
- `FakeRecipeRepository`: 返回固定的假菜谱数据

### 7.2 切换到真实数据
1. 创建真实Repository实现（实现相同接口）
2. 在 `ViewModelFactory` 中替换绑定
3. **无需修改UI代码**

### 7.3 假数据内容
- 默认食材: Chicken, Potato, Onion
- 假菜谱: 5个固定菜谱（包含详情）
- 收藏状态: 内存中保存

---

## 8. API接口规范（Beta版本）

### 8.1 TheMealDB API
- **基础URL**: `https://www.themealdb.com/api/json/v1/1/`
- **单食材搜索**: `filter.php?i={ingredient}`
- **菜谱详情**: `lookup.php?i={recipeId}`

### 8.2 响应格式
- 搜索接口返回: `{ meals: [...] }` 或 `{ meals: null }`
- 详情接口返回: `{ meals: [{...}] }`

---

## 9. 变更管理

### 9.1 变更流程
1. 提出变更需求（在群内讨论）
2. 更新本文档
3. B/C确认变更
4. 统一实施修改

### 9.2 禁止变更项（除非全员同意）
- 数据模型的核心字段
- Repository接口签名
- 导航目的地ID和参数名
- 状态组件的视觉和文案

---

## 10. 验收标准

### 10.1 功能验收
- ✅ 用户能添加食材到"我的食材"
- ✅ 输入一个食材后能返回相关食谱列表
- ✅ 数据库能正常存储食材信息
- ✅ 能查看菜谱详情
- ✅ 能收藏/取消收藏菜谱
- ✅ 收藏页能显示收藏的菜谱

### 10.2 状态验收
- ✅ 加载/空/错误三种状态正确显示
- ✅ 重试按钮能重新加载数据
- ✅ 文案统一、视觉一致

### 10.3 导航验收
- ✅ 底部导航三页可正常切换
- ✅ 能从发现页进入详情页
- ✅ 能从收藏页进入详情页
- ✅ 详情页返回正确

---

**文档维护者**: 角色A（应用骨架与集成）
**最后更新**: Alpha版本基线

