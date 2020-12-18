# Heart of the Machine &emsp; [![Website Icon]][Website] [![Trello Icon]][Trello] [![Github Workflow Status]][Github Workflow] [![Discord Status]][Discord] [![CurseForge Status]][CurseForge]

[Website Icon]: https://github.com/Heart-of-the-Machine/Heart-of-the-Machine.github.io/blob/master/icons/Plassein-Machine-Casing-tbg-C4096-20x20.png
[Website]: https://heart-of-the-machine.github.io/
[Trello Icon]: https://github.com/Heart-of-the-Machine/Heart-of-the-Machine.github.io/blob/master/icons/trello-mark-blue-20px.png
[Trello]: https://trello.com/b/LM2DHkuS
[Github Workflow Status]: https://img.shields.io/github/workflow/status/Heart-of-the-Machine/heart-of-the-machine/build?logo=github&style=flat-square
[Github Workflow]: https://github.com/Heart-of-the-Machine/heart-of-the-machine/actions?query=workflow%3Abuild
[Discord Status]: https://img.shields.io/discord/720635296131055697?logo=discord&logoColor=white&style=flat-square
[Discord]: https://discord.gg/hU4us4D
[CurseForge Status]: http://cf.way2muchnoise.eu/391897.svg?badge_style=flat
[CurseForge]: https://www.curseforge.com/minecraft/mc-mods/heart-of-the-machine

Heart of the Machine is a ModFest 1.16 entry.

Heart of the Machine adds a whole new dimension of abandoned machinery to explore. Maybe you can uncover the secrets of
this dimension and make use of them somehow.

## Getting to the Nectere Dimension
The Nectere (or Nexus) dimension can be accessed through portals that generate at surface level in the overworld. You
can locate them using the `/locate nectere_portal` command. Beware, the Nectere dimension has some pretty wacky terrain
and you may want to bring some building blocks just in case you end up spawning in a cliff or on a floating island.

## What is still subject to change?
This mod is still under development. We still have plans for adding a lot more content to Heart of the Machine, but some
existing content will also be changing as well.

Existing content that will likely be changing in the future is:

 * We will be adding new biomes to the Nectere dimension. This will likely change how terrain is generated, leaving
   chunk boundaries in generated terrain.
 * Portal connectivity will change. We are planning to a biome-based dimension-squashing system where different biomes
   have different coordinate multipliers when connecting to the overworld. We may add a command to regenerate natural
   portal connections if necessary.
 * Some biome features are likely to change. This means that some Plassein Growths may change shape and new structures
   may start generating in unexplored terrain.
 * Some block textures will change. Many of Heart of the Machine's textures are sill WIP.

## Goals for Heart of the Machine
Heart of the Machine is an ancient-technology themed mod with a progression and functionality much like some magic mods.
After we have gotten most of the Nectere world-generation and portal systems finalized, we are planning to move on to
the technology aspects of the mod.

Some concrete goals we have for Heart of the Machine are:

 * We plan to add an aural energy system similar to Thaumcraft's vis. This energy will be used to power organic and
   inorganic machinery.
 * We plan to allow players to construct their own portals. This means that players will be able to take full advantage
   the strange way the Nectere dimension connects to other dimensions.
 * We plan to have portals that connect the Nectere dimension to dimensions other than just the overworld.

We have other plans for Heart of the Machine, but those plans are less concrete.

## Known Issues
There are some known issues with the mod. Some are being fixed and some are outside the scope of this mod.

 * ~~Some mods will generate structures in the Nectere dimension when they shouldn't.~~ (Fixed in 0.1.7)
 * ~~Having AE2 installed will cause meteorites to spawn in the Nectere dimension, crashing the server (same as 
   previous issue).~~ (Fixed in 0.1.7)
 * Some textures are broken when using Sodium. This is caused by Sodium not supporting custom block renderers that 
   HotM uses.
 * Updating a world from one Minecraft version to another (e.g. 1.16.3 -> 1.16.4) will cause the world to lose its 
   Nether and End dimensions. This is fixed by the
   [Dimension Update Fixer mod](https://www.curseforge.com/minecraft/mc-mods/dimension-update-fixer).
   This issue is caused by weirdness with minecraft disliking custom chunk-generators, meaning that some data-fixers 
   will throw up on them.

## License
Heart of the Machine is licensed under the MIT license.
