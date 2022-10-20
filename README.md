# JSON-Paintings-Mod
A minecraft mod that allows custom paintings to be easily added through a json file!

-Doesn't add any new painting entities or painting type handlers, but rather expands on vanilla's system, which means that this mod will work nicely with all other mods that don't override how paintings work!

-Paintings added through this mod use their own textures instead of being stuck to using vanilla's painting texture atlas!

-Allows paintings added through this mod to have custom back and side textures!

-Can override paintings! This could be used for example to give certain vanilla paintings custom back or side textures that match the front.

---

### Info for Players and Modpack Developers
To get started with adding your own custom paintings, create a new folder in your `.minecraft` (same folder where your `mods` folder is), and name it `paintings`.
Inside your newly created paintings folder make a new file and call it `paintings.json` (file structure should be `.minecraft/paintings/paintings.json`). This file will be in charge of actually adding all the custom paintings to your game! More info about how to use this file can be found under [Getting Started with paintings.json](https://github.com/jbredwards/JSON-Paintings-Mod#getting-started-with-paintingsjson) below.

---

### Info for Mod Developers
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
The following is a summery which goes over the possible values and customizations you can give your paintings:
```
[
    {
        (required) "motive": The title of the painting, this is what gets saved in the painting entity nbt.
        (optional) "textures": {
            (optional) "front": This value defaults to the motive name in lowercase.
            (optional) "back":  This value defaults to vanilla's back painting texture.
            (optional) "side":  This value defaults to whatever the back texture is.
        }
        (optional) "width":  The amount of blocks along the x-axis this occupies, defaults to 1.
        (optional) "height": The amount of blocks along the y-axis this occupies, defaults to 1.
    }
]
```
