# VisibleJS

**Make KubeJS recipe scripting simple and visual!**

VisibleJS is a Minecraft Forge mod that provides an interactive recipe creation tool, allowing you to quickly generate KubeJS recipe scripts through an intuitive GUI without manually writing code.

## Features

- **Multiple Recipe Families Supported**: Vanilla crafting/furnace recipes plus common Create recipe types
- **JEI-style Top Category Bar**: Switch recipe types with left/right paging and hover tooltips
- **One-Click Script Generation**: Fill in items and click the button to automatically generate standard KubeJS recipe code
- **Automatic File Management**: Generated scripts are automatically saved to the corresponding category files under `kubejs/server_scripts/`
- **Real-Time Recipe Preview**: View generated script content instantly in-game

## Usage

1. Obtain the **recipe_creator** tool from the Creative Mode inventory
2. Right-click with the tool to open the Recipe Editor
3. Use the **top category bar** and its **left/right page arrows** to switch recipe types
4. Place input materials and output items following vanilla conventions
5. Click the **"Generate Script"** button
6. Scripts will be automatically saved to the corresponding file and displayed in the chat

## Supported Recipe Types & Files

| Recipe Type | Save Location | Description |
|---------|---------|------|
| Shaped Crafting | `crafting.js` | 3x3 crafting table ordered recipe |
| Shapeless Crafting | `crafting.js` | Any material combination recipe |
| Smelting | `smelting.js` | Standard furnace recipe |
| Blasting | `smelting.js` | Blast furnace fast smelting |
| Smoking | `smelting.js` | Smoker cooking |
| Campfire Cooking | `smelting.js` | Campfire slow cooking |
| Smithing | `smithing.js` | 1.20+ smithing recipe |
| Stonecutting | `stonecutting.js` | Stonecutter processing recipe |
| Create Crushing / Milling / Pressing / Cutting / Sandpaper Polishing / Haunting | `create.js` | Visible when Create is installed |
| Create Mixing / Compacting | `create.js` | 3x3 input grid |
| Create Deploying | `create.js` | Dual-input recipe |

## Requirements

- Minecraft **1.20.1**
- Forge **47.3.10+**
- KubeJS **2001.6.5+** (Optional but recommended for testing generated scripts)

### Testing Create Script Generation Without Create Installed

You can still verify Create script output locally even if you do **not** install Create:

```powershell
.\gradlew.bat test
```

The project includes pure string preview tests, so no Create runtime is required to validate the generated `create.js` syntax.

## Generation Examples

### Shaped Crafting
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

### Smelting
```javascript
ServerEvents.recipes(event => {
  event.smelting('minecraft:iron_ingot', 'minecraft:raw_iron')
})
```

## Installation

Simply place the mod file into your game's `mods` folder.

## Author

**mo_guang**

---

*Let recipe creation return to intuition, leave the tedious coding to VisibleJS!*
