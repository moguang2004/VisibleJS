# VisibleJS

**让 KubeJS 配方编写变得简单可视化！**

VisibleJS 是一个 Minecraft Forge 模组，提供了一个交互式的配方创建工具，让你可以通过直观的图形界面快速生成 KubeJS 配方脚本，无需手动编写代码。

## 功能特性

- **多种配方类型支持**：原版工作台/熔炉系配方 + Create 常用配方类型
- **JEI 风格顶部分类栏**：支持左右翻页切换不同配方类型，悬浮显示提示
- **一键生成脚本**：填入物品后点击按钮，自动生成标准 KubeJS 配方代码
- **自动文件管理**：生成的脚本自动保存到 `kubejs/server_scripts/` 下对应的分类文件中
- **实时配方预览**：游戏内即时显示生成的脚本内容

## 使用方法

1. 在创造模式物品栏获取 **recipe_creator** 工具
2. 手持工具 **右键** 打开配方编辑器
3. 使用顶部的 **< >** 按钮切换配方类型
4. 按照原版习惯放入输入材料和输出物品
5. 点击 **"生成脚本"** 按钮
6. 脚本会自动保存到对应文件并显示在聊天栏

## 支持的配方类型与文件

| 配方类型 | 保存位置 | 说明 |
|---------|---------|------|
| 有序合成 | `crafting.js` | 3x3 工作台有序配方 |
| 无序合成 | `crafting.js` | 任意材料组合配方 |
| 熔炉冶炼 | `smelting.js` | 标准熔炉配方 |
| 高炉冶炼 | `smelting.js` | 高炉快速冶炼 |
| 烟熏炉 | `smelting.js` | 烟熏炉烹饪 |
| 营火烹饪 | `smelting.js` | 营火慢速烹饪 |
| 锻造台 | `smithing.js` | 1.20+ 锻造配方 |
| 切石机 | `stonecutting.js` | 切石机加工配方 |
| Create 粉碎 / 研磨 / 压制 / 切割 / 砂纸抛光 / 闹鬼处理 / 飞溅 / 倒空 / 清洗 | `create.js` | 安装 Create 后自动显示 |
| Create 混合 / 压缩 | `create.js` | 3x3 输入网格 |
| Create 部署 | `create.js` | 双输入配方 |

## 环境要求

- Minecraft **1.20.1**
- Forge **47.3.10+**
- KubeJS **2001.6.5+**（可选，但推荐使用以测试生成的脚本）

## 生成示例

### 工作台有序合成
```javascript
ServerEvents.recipes(event => {
  event.shaped('minecraft:diamond_pickaxe', [
    'AAA',
    ' B ',
    ' B '
  ], {
    A: 'minecraft:diamond',
    B: 'minecraft:stick'
  })
})
```

### 熔炉冶炼
```javascript
ServerEvents.recipes(event => {
  event.smelting('minecraft:iron_ingot', 'minecraft:raw_iron')
})
```

## 安装

将模组文件放入游戏的 `mods` 文件夹即可。

## 作者

**mo_guang**

---

*让配方创作回归直觉，把繁琐的代码交给 VisibleJS！*
