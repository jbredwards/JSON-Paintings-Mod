# JSON-Paintings-Mod
A minecraft mod that allows custom paintings to be easily added through a json file!

-Doesn't add any new painting entities or painting type handlers, rather this mod uses a bytecode manuipilation framework called [ASM](https://asm.ow2.io/) 
to add new fuctions to `EnumArt`, which means that this mod will work nicely with all other mods that don't override vanilla's painting system!

-Paintings added through this mod use their own textures instead of being stuck to using vanilla's painting texture atlas!

-Allows paintings added through this mod to have custom back and side textures!

---

### Info for Players and Modpack Developers
To get started with adding your own custom paintings, create a new folder in your `.minecraft` (same folder where your `mods` folder is), and name it `paintings`.
Inside your newly creating paintings folder make a new file and call it `paintings.json`. This file will be in charge of actually adding any custom paintings to 
your game! More info about how to use this file can be found below.

---

### Info for Mod Developers

---

### Getting Started with paintings.json
