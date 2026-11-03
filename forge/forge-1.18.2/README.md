-------

## Schrottii's 1.18.2 Minecraft mod template

I use this as a template for my MC mods, it only has the basic structure to allow the thing to run at all and can be filled with all sorts of content. When I forget how to add a certain thing, let's say a crop, I can look it up in my Fisecraft mod which has a bit of everything, this thing here is only an empty mod with the minimum to produce a working .jar. 

Anyone else can use it if they like, for free, everything you need to know is listed in the steps below. Crediting me is not required but recommended. You can donate if you like but I doubt anyone would do that for a modding template hahaha: https://ko-fi.com/schrottii

-------

* **Step 1)** Download or get the repo or something

* **Step 2)** Replace all instances of 'modname' with the name of the mod
In IntelliJ, this can be easily done by selecting all files (in src) and then right clicking and selecting "Replace In Files..."

* **Step 3)** Update the build.gradle and META-INF\mods.toml files, potentially also modlogo.png

* **Step 4)** Rename com.moddername.modname, assets.modname, and data.modname

* **Step 5)** Maybe edit changelog.txt and credits.txt, and delete this file (README.md)

* **Step 6)** Add content, it already has a placeholder block (ModBlocks) and a placeholder item (ModItems),
as well as an English translation file and a recipe

After compiling/building the mod, the .jar can be found in build\libs

-------