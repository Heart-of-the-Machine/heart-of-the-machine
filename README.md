<center>
<p align="center"><img src="https://raw.githubusercontent.com/Heart-of-the-Machine/Heart-of-the-Machine.github.io/master/Plassein-Machine-Casing-tbg-C4096-256x256.png" alt="Logo"></p>
<h1 align="center">Heart of the Machine</h1>
<p align="center">
<a href="https://heart-of-the-machine.github.io/"><img src="https://raw.githubusercontent.com/Heart-of-the-Machine/Heart-of-the-Machine.github.io/master/icons/Plassein-Machine-Casing-tbg-C4096-28x28.png" alt="Website"></a>
<a href="https://discord.gg/hU4us4D"><img src="https://img.shields.io/discord/720635296131055697?logo=discord&logoColor=white&style=for-the-badge" alt="Discord"></a>
<a href="https://modrinth.com/mod/heart-of-the-machine"><img src="https://img.shields.io/modrinth/dt/7vleuAJ9?logo=modrinth&style=for-the-badge" alt="Modrinth"></a>
<a href="https://www.curseforge.com/minecraft/mc-mods/heart-of-the-machine"><img src="https://cf.way2muchnoise.eu/391897.svg?badge_style=for_the_badge" alt="CurseForge"></a>
<a href="https://github.com/Heart-of-the-Machine/heart-of-the-machine"><img src="https://raw.githubusercontent.com/Heart-of-the-Machine/Heart-of-the-Machine.github.io/master/icons/GitHub-Mark-28px.png" alt="GitHub"></a>
</p>
</center>

Heart of the Machine is a ModFest 1.16 entry.

Heart of the Machine adds a whole new dimension of abandoned machinery to explore. Maybe you can uncover the secrets of
this dimension and make use of them somehow.

<center>
<h3 align="center">This mod depends on the <a href="https://modrinth.com/mod/qsl">Quilt Standard Libraries</a> and <a href="https://modrinth.com/mod/qkl">Quilt Kotlin Libraries</a>.</h3>
<p align="center">
<a href="https://modrinth.com/mod/qsl"><img src="https://raw.githubusercontent.com/Heart-of-the-Machine/Heart-of-the-Machine.github.io/master/icons/qsl-icon-rounded-64x64.png" alt="Quilt Standard Libraries"></a>
<a href="https://modrinth.com/mod/qkl"><img src="https://raw.githubusercontent.com/Heart-of-the-Machine/Heart-of-the-Machine.github.io/master/icons/qkl-icon-rounded-64x64.png" alt="Quilt Kotlin Libraries"></a>
</p>
</center>

## Rewrite

This mod is being re-written, nearly from scratch, for two main reasons. First, the original version of this mod was my
first fabric/quilt ecosystem mod, and my first Minecraft mod in several years, meaning that much of the mod's mechanics
were hacked in, causing issues like deadlocks during world-generation. Second, Minecraft has changed so much since this
mod was last updated, especially in the areas of world-generation, which is a focus of this mod, meaning that much of
the mod's original code is just unusable with modern versions of Minecraft.

Huge thanks to [Misode's Datapack Generators](https://misode.github.io/), as they allowed me to recreate the shape of
Heart of the Machine's caves without having to manually create a new world for every change.

## Screenshots

Here is a screenshot of a previous version of Heart of the Machine:

![Old Heart of the Machine screenshot](https://raw.githubusercontent.com/Heart-of-the-Machine/Heart-of-the-Machine.github.io/master/screenshots/2020-09-20_12.12.07.png)

## Getting to the Nectere Dimension

The Nectere (or Nexus) dimension can be accessed through portals that generate at surface level in the overworld. You
can locate them using the `/locate nectere_portal` command. Beware, the Nectere dimension has some pretty wacky terrain,
and you may want to bring some building blocks just in case you end up spawning in a cliff or on a floating island.

## What is still subject to change?

This mod is still under development. We still have plans for adding a lot more content to Heart of the Machine, but some
existing content will also be changing as well.

Existing content that will likely be changing in the future is:

- We will be adding new biomes to the Nectere dimension. This will likely change how terrain is generated, leaving
  chunk boundaries in generated terrain.
- Portal connectivity will change. We are planning to a biome-based dimension-squashing system where different biomes
  have different coordinate multipliers when connecting to the overworld. We may add a command to regenerate natural
  portal connections if necessary.
- Some biome features are likely to change. This means that some Plassein Growths may change shape and new structures
  may start generating in unexplored terrain.
- Some block textures will change. Many of Heart of the Machine's textures are sill WIP.

## Goals for Heart of the Machine

Heart of the Machine is an ancient-technology themed mod with a progression and functionality much like some magic mods.
After we have gotten most of the Nectere world-generation and portal systems finalized, we are planning to move on to
the technology aspects of the mod.

Some concrete goals we have for Heart of the Machine are:

- We plan to add an aural energy system similar to Thaumcraft's vis. This energy will be used to power organic and
  inorganic machinery.
- We plan to allow players to construct their own portals. This means that players will be able to take full advantage
  the strange way the Nectere dimension connects to other dimensions.
- We plan to have portals that connect the Nectere dimension to dimensions other than just the overworld.

We have other plans for Heart of the Machine, but those plans are less concrete.

## Known Issues

There are some known issues with the mod. Some are being fixed and some are outside the scope of this mod.

- Some textures are broken when using Sodium without Indium. This is caused by Sodium not supporting custom block
  renderers that HotM uses. To fix this issue, please install Indium alongside Sodium.

## License

Heart of the Machine is licensed under the MIT license.
