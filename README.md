# JSON-Paintings-Mod
A minecraft mod that allows custom paintings to be easily added through a json file!

-Doesn't add any new painting entities or painting type handlers, rather this mod uses a bytecode manuipilation framework called [ASM](https://asm.ow2.io/) 
to add new fuctions to `EnumArt`, which means that this mod will work nicely with all other mods that don't override vanilla's painting system!

-Paintings added through this mod use their own textures instead of being stuck to using vanilla's painting texture atlas!

-Allows paintings added through this mod to have custom back and side textures!

-This mod cannot be used to change existing paintings (though this may change in the future).

---

### Info for Players and Modpack Developers
To get started with adding your own custom paintings, create a new folder in your `.minecraft` (same folder where your `mods` folder is), and name it `paintings`.
Inside your newly creating paintings folder make a new file and call it `paintings.json`. This file will be in charge of actually adding any custom paintings to 
your game! More info about how to use this file can be found below.

---

### Info for Mod Developers
The problem with using forge's sytem to add custom paintings without this mod is having to edit vanilla's painting texture atlas. As you may know, only one texture can exist per location at runtime (this is how resourcepacks override textures, by simply creating duplicate textures with the same location). When multiple mods are loaded that each edit vanilla's painting texture atlas, only one will appear in game because the others will be overriden, which obviously leads to incompatabilities amongst any two mods that add their own paintings using this system.

Along with allowing players to add their own paintings, JSON Paintings gives other mod developers the ability to as well! As stated above, paintings added through this mod have custom textures, which gets around the incompatibilities that forge's system causes.

You do not need to add any dependencies to get this mod to work (no build.gradle headaches)! Instead make a folder named `paintings` in your `assts/modid` folder, and inside that create `paintings.json`. While this mod is installed, it will automatically look for that file at runtime, no extra work needed!

---

### Getting Started with paintings.json
