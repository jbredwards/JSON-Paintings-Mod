# JSON-Paintings-Mod
A minecraft mod that allows custom paintings to be easily added through a [JSON](https://minecraft.wiki/w/JSON) file!

---

## Info for Players and Modpack Developers
To get started with adding your own custom paintings, create a new folder in your `.minecraft` (same folder where your `mods` folder is), and name it `paintings`.
Inside your newly created paintings folder make a new file and call it `paintings.json` (file structure should be `.minecraft/paintings/paintings.json`). This file will be in charge of actually adding all the custom paintings to your game! More info about how to use this file can be found under [Getting Started with paintings.json](https://github.com/jbredwards/JSON-Paintings-Mod/blob/1.12.2/README.md#getting-started-with-paintingsjson) below.

If you want to add paintings made by other players, see the [Painting Packs](https://github.com/jbredwards/JSON-Paintings-Mod/blob/1.12.2/README.md#painting-packs) section below!

---

## Info for Mod Developers
#### Why you should consider this mod for your mod's paintings:
The main problem with using forge's system to add custom paintings without this mod is having to edit vanilla's painting texture atlas and not having the option of specifying any other texture. As you may know, only one texture can exist per location at runtime (this is how resourcepacks override textures, by simply creating files with the same location). When multiple mods are loaded that each edit vanilla's painting texture atlas, only one will appear in game because the others will be overridden, which obviously leads to incompatibilities amongst any two mods that add their own paintings using forge's system.

Along with allowing players to add their own paintings, JSON Paintings gives other mod developers the ability to add them as well! As stated above, paintings added through this mod have custom textures, which allows you to easily get around the incompatibilities that the forge system causes.

You do not need to add any dependencies to add paintings through this mod's system (no build.gradle headaches)! Instead make a folder named `paintings` in your `assets/modid` folder, and inside that create `paintings.json` (file structure should be `assets/modid/paintings/paintings.json`). While this mod is installed, it will automatically look for that file at runtime, no extra work needed!

---

## Getting Started with paintings.json
The `paintings.json` file you create can be edited with any text editor. **If you're not familiar with json syntax, it's recommended to learn that before going any further. There are plenty of quick helpful sources online that should help you with this. If you've ever read minecraft nbt data before, it should all start looking very familiar to you.** Upon opening the file for the first time, it's important that you add square brackets, otherwise the file will not be read correctly! The file should look like this, remember to save any changes you make as you go:

```
[

]
```
Every custom painting needs a "motive". This value represents the painting's name, and is used for a lot of minecraft's internals, so it must be included for each custom painting!

To give your paintings textures, create a folder within `paintings` named `textures`. The texture location for the front texture for each painting will default to `paintings/textures/"#MOTIVENAME_LOWERCASE#".png` (if you're a mod developer it will default to `assets/modid/textures/paintings/"#MOTIVENAME_LOWERCASE#".png`.

This is an example of me adding two custom paintings. The square brackets are needed for the mod to be able to seperate each custom painting.
The first one does not have a specified front texture, it will default to `textures/creeper`. The second one does have a specified front texture, which is set to minecraft's apple item texture.

```js
[
    {
        "motive": "Creeper",
        "width": 2,
        "height": 2
    },
    {
        "motive": "Apple",
        "textures": {
            "front": "items/apple"
        }
    }
]
```

### JSON Options
The following is a summary which goes over the possible values and customizations you can give your paintings:

<img src="https://minecraft.wiki/images/NbtSprite_compound.png" title="JSON Object" align="absmiddle" style="width:1em; height:1em; border-radius:0;"> The root object.
> <img src="https://minecraft.wiki/images/NbtSprite_string.png" title="String" align="absmiddle" style="width:1em; height:1em; border-radius:0;"> **motive**: Required. The case-sensitive title of the painting. This is what gets saved to the painting entity nbt. To avoid conflicts with other painting motives, it's recommended to follow the [resource location](https://minecraft.wiki/w/Identifier) format. If you follow the [resource location](https://minecraft.wiki/w/Identifier) format and plan to create a [painting pack](https://github.com/jbredwards/JSON-Paintings-Mod/blob/1.12.2/README.md#painting-packs), use the pack id as the namespace.
>
> <img src="https://minecraft.wiki/images/NbtSprite_compound.png" title="JSON Object" align="absmiddle" style="width:1em; height:1em; border-radius:0;"> **textures**: Optional. Specifies the "front", "back", and "side" textures.
>
> > <img src="https://minecraft.wiki/images/NbtSprite_string.png" title="String" align="absmiddle" style="width:1em; height:1em; border-radius:0;"> **front**: Optional. [Resource location](https://minecraft.wiki/w/Identifier) of the front sprite to use. This value defaults to the motive name.
> >
> > <img src="https://minecraft.wiki/images/NbtSprite_string.png" title="String" align="absmiddle" style="width:1em; height:1em; border-radius:0;"> **back**: Optional. [Resource location](https://minecraft.wiki/w/Identifier) of the back sprite to use. This value defaults to Vanilla's back texture.
> >
> > <img src="https://minecraft.wiki/images/NbtSprite_string.png" title="String" align="absmiddle" style="width:1em; height:1em; border-radius:0;"> **side**: Optional. [Resource location](https://minecraft.wiki/w/Identifier) of the side sprite to use. This value defaults to whatever the back texture is.
>
> <img src="https://minecraft.wiki/images/NbtSprite_string.png" title="String" align="absmiddle" style="width:1em; height:1em; border-radius:0;"> **asset_id**: Optional. Alternative way to specify the front sprite to use. Must be in a `textures/painting` folder.
>
> <img src="https://minecraft.wiki/images/NbtSprite_int.png" title="Int" align="absmiddle" style="width:1em; height:1em; border-radius:0;"> **width**: Optional. The width of the painting in blocks. Defaults to 1.
>
> <img src="https://minecraft.wiki/images/NbtSprite_int.png" title="Int" align="absmiddle" style="width:1em; height:1em; border-radius:0;"> **height**: Optional. The height of the painting in blocks. Defaults to 1.
>
> <img src="https://minecraft.wiki/images/NbtSprite_string.png" title="String" align="absmiddle" style="width:1em; height:1em; border-radius:0;"><img src="https://minecraft.wiki/images/NbtSprite_list.png" title="JSON Array" align="absmiddle" style="width:1em; height:1em; border-radius:0;"><img src="https://minecraft.wiki/images/NbtSprite_compound.png" title="JSON Object" align="absmiddle" style="width:1em; height:1em; border-radius:0;"> **title**: Optional. [Text component](https://minecraft.wiki/w/Text_component_format) to use as the painting's title. Defaults to yellow text containing the motive.
>
> <img src="https://minecraft.wiki/images/NbtSprite_string.png" title="String" align="absmiddle" style="width:1em; height:1em; border-radius:0;"><img src="https://minecraft.wiki/images/NbtSprite_list.png" title="JSON Array" align="absmiddle" style="width:1em; height:1em; border-radius:0;"><img src="https://minecraft.wiki/images/NbtSprite_compound.png" title="JSON Object" align="absmiddle" style="width:1em; height:1em; border-radius:0;"> **author**: Optional. [Text component](https://minecraft.wiki/w/Text_component_format) to use as the painting's author.
>
> <img src="https://minecraft.wiki/images/NbtSprite_string.png" title="String" align="absmiddle" style="width:1em; height:1em; border-radius:0;"> **item_model**: Optional. [Resource location](https://minecraft.wiki/w/Identifier) (with a default namespace of "jsonpaintings", or "modid" if you're a mod developer) pointing to the painting's item [model](https://minecraft.wiki/w/Model). If you're using the Vanilla format, it must be in a `models` folder. For example, an "item_model" of `"item/example"` will point to a `models/item/example.json` model file. [Forge Blockstate JSON](https://docs.minecraftforge.net/en/1.12.x/models/blockstates/forgeBlockstates) is also supported if a [model resource location](https://docs.neoforged.net/docs/1.21.1/misc/resourcelocation/#modelresourcelocations) is provided. Defaults to the Vanilla painting item model.
>
> <img src="https://minecraft.wiki/images/NbtSprite_string.png" title="String" align="absmiddle" style="width:1em; height:1em; border-radius:0;"><img src="https://minecraft.wiki/images/NbtSprite_compound.png" title="JSON Object" align="absmiddle" style="width:1em; height:1em; border-radius:0;"> **rarity**: Optional. [Rarity](https://minecraft.wiki/w/Rarity) of the painting item. Defaults to "Common".
>
> > <img src="https://minecraft.wiki/images/NbtSprite_string.png" title="String" align="absmiddle" style="width:1em; height:1em; border-radius:0;"> **color**: [Color name](https://minecraft.wiki/w/Formatting_codes#Color_codes) for the rarity.
> >
> > <img src="https://minecraft.wiki/images/NbtSprite_string.png" title="String" align="absmiddle" style="width:1em; height:1em; border-radius:0;"> **name**: Name for the rarity.
>
> <img src="https://minecraft.wiki/images/NbtSprite_boolean.png" title="Boolean" align="absmiddle" style="width:1em; height:1em; border-radius:0;"> **is_treasure**: Optional. If set to true, players in survival cannot get this by cycling paintings. Defaults to false. Other valid identifiers for this field are "is_creative" and "isCreative".
>
> <img src="https://minecraft.wiki/images/NbtSprite_boolean.png" title="Boolean" align="absmiddle" style="width:1em; height:1em; border-radius:0;"> **always_capture**: Optional. If set to true, this will always be captured when broken. Defaults to the "is_treasure" value, or false if there isn't one. Another valid identifier for this field is "alwaysCapture".

---

## Painting Packs
Painting packs are new to JSON Paintings v1.5.0, and serve as a convienent way to share your paintings or use paintings made by other players! To get started, create a new folder in your `.minecraft/paintings` folder and name it `packs` (file structure should be `.minecraft/paintings/packs`). This is where you can put any painting pack files you want to add!

JSON Paintings also treats [datapacks](https://minecraft.wiki/w/Data_pack) and mods that add paintings for **1.21+** as painting packs, so those can also be added to the `.minecraft/paintings/packs` folder! Note: If a datapack has a required resourcepack, place the resourcepack in the `.minecraft/paintings/packs` folder alongside the datapack.

### Creating a Painting Pack
This is very easy, simply run the `/jsonpaintings pack <id> <name>` command in-game. This will take your `paintings.json` and turn it into a painting pack! It will not actually remove or change anything in your `.minecraft/paintings` folder. The resulting painting pack zip (with a placeholder file name) will be in a new `out` folder in `.minecraft/paintings`.

The `<id>` is the unique identifier for your painting pack. It must be lowercased, should be longer than two characters, and should be unique to avoid conflicts with other painting packs! The `<name>` is a user-friendly name for your painting pack.

Since JSON Paintings offers support for painting datapacks, consider making your paintings available as a datapack too, so players on 1.21+ may also enjoy your paintings! See the [Minecraft Wiki tutorial](https://minecraft.wiki/w/Tutorial:Adding_custom_paintings) for more information. Certain JSON Paintings features will be unavailable though, like back/side textures and item models.
