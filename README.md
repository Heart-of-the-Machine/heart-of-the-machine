# Heart of the Machine

[![Website Icon]][Website] [![Trello Icon]][Trello] [![Github Workflow Status]][Github Workflow] [![Discord Status]][Discord] [![Modrinth Status]][Modrinth] [![CurseForge Status]][CurseForge]

[Website Icon]: https://github.com/Heart-of-the-Machine/Heart-of-the-Machine.github.io/blob/master/icons/Plassein-Machine-Casing-tbg-C4096-20x20.png
[Website]: https://heart-of-the-machine.github.io/
[Trello Icon]: https://github.com/Heart-of-the-Machine/Heart-of-the-Machine.github.io/blob/master/icons/trello-mark-blue-20px.png
[Trello]: https://trello.com/b/LM2DHkuS
[Github Workflow Status]: https://img.shields.io/github/workflow/status/Heart-of-the-Machine/heart-of-the-machine/build?logo=github&style=flat-square
[Github Workflow]: https://github.com/Heart-of-the-Machine/heart-of-the-machine/actions?query=workflow%3Abuild
[Discord Status]: https://img.shields.io/discord/720635296131055697?logo=discord&logoColor=white&style=flat-square
[Discord]: https://discord.gg/hU4us4D
[Modrinth Status]: https://img.shields.io/badge/dynamic/json?color=green&label=modrinth&style=flat-square&query=downloads&url=https%3A%2F%2Fapi.modrinth.com%2Fapi%2Fv1%2Fmod%2Fheart-of-the-machine&logo=data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHZpZXdCb3g9IjAgMCAxMSAxMSIgd2lkdGg9IjE0LjY2NyIgaGVpZ2h0PSIxNC42NjciICB4bWxuczp2PSJodHRwczovL3ZlY3RhLmlvL25hbm8iPjxkZWZzPjxjbGlwUGF0aCBpZD0iQSI+PHBhdGggZD0iTTAgMGgxMXYxMUgweiIvPjwvY2xpcFBhdGg+PC9kZWZzPjxnIGNsaXAtcGF0aD0idXJsKCNBKSI+PHBhdGggZD0iTTEuMzA5IDcuODU3YTQuNjQgNC42NCAwIDAgMS0uNDYxLTEuMDYzSDBDLjU5MSA5LjIwNiAyLjc5NiAxMSA1LjQyMiAxMWMxLjk4MSAwIDMuNzIyLTEuMDIgNC43MTEtMi41NTZoMGwtLjc1LS4zNDVjLS44NTQgMS4yNjEtMi4zMSAyLjA5Mi0zLjk2MSAyLjA5MmE0Ljc4IDQuNzggMCAwIDEtMy4wMDUtMS4wNTVsMS44MDktMS40NzQuOTg0Ljg0NyAxLjkwNS0xLjAwM0w4LjE3NCA1LjgybC0uMzg0LS43ODYtMS4xMTYuNjM1LS41MTYuNjk0LS42MjYuMjM2LS44NzMtLjM4N2gwbC0uMjEzLS45MS4zNTUtLjU2Ljc4Ny0uMzcuODQ1LS45NTktLjcwMi0uNTEtMS44NzQuNzEzLTEuMzYyIDEuNjUxLjY0NSAxLjA5OC0xLjgzMSAxLjQ5MnptOS42MTQtMS40NEE1LjQ0IDUuNDQgMCAwIDAgMTEgNS41QzExIDIuNDY0IDguNTAxIDAgNS40MjIgMCAyLjc5NiAwIC41OTEgMS43OTQgMCA0LjIwNmguODQ4QzEuNDE5IDIuMjQ1IDMuMjUyLjgwOSA1LjQyMi44MDljMi42MjYgMCA0Ljc1OCAyLjEwMiA0Ljc1OCA0LjY5MSAwIC4xOS0uMDEyLjM3Ni0uMDM0LjU2bC43NzcuMzU3aDB6IiBmaWxsLXJ1bGU9ImV2ZW5vZGQiIGZpbGw9IiM1ZGE0MjYiLz48L2c+PC9zdmc+
[Modrinth]: https://modrinth.com/mod/heart-of-the-machine
[CurseForge Status]: https://img.shields.io/badge/dynamic/json?color=orange&label=curseforge&style=flat-square&query=downloadCount&url=https%3A%2F%2Faddons-ecs.forgesvc.net%2Fapi%2Fv2%2Faddon%2F391897&logo=data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHZpZXdCb3g9IjAgMCA0MyAyNCI+PHBhdGggZmlsbD0iI2NjY2NjYyIgZD0iTTExLjMgMGwuNyAzSDBzLjIuOS4zIDFjLjMuNS42IDEuMSAxIDEuNSAxLjkgMi4yIDUuMiAzLjEgNy45IDMuNiAxLjkuNCAzLjguNSA1LjcuNmwyLjIgNS45aDEuMmwuNyAxLjloLTFMMTYuMyAyM0gzM2wtMS43LTUuNWgtMWwuNy0xLjloMS4yczEtNi4xIDQuMS04LjljMy0yLjggNi43LTMuMiA2LjctMy4yVjBIMTEuM3ptMTYuOCAxNS4xYy0uOC41LTEuNy41LTIuMy45LS40LjItLjYuOC0uNi44LS40LS45LS45LTEuMi0xLjUtMS40LS42LS4yLTEuNy0uMS0zLjItMS40LTEtLjktMS4xLTIuMS0xLTIuN3YtLjEtLjJjMC0uMSAwLS4yLjEtLjMuMi0uNi43LTEuMiAxLjctMS42IDAgMC0uNyAxIDAgMiAuNC42IDEuMi45IDEuOS41LjMtLjIuNS0uNi42LS45LjItLjcuMi0xLjQtLjQtMS45LS45LS44LTEuMS0xLjktLjUtMi42IDAgMCAuMi45IDEuMS44LjYgMCAuNi0uMi40LS40LS4xLS4zLTEuNC0yLjIuNS0zLjYgMCAwIDEuMi0uOCAyLjYtLjctLjguMS0xLjcuNi0yIDEuNHYuMWMtLjMuOC0uMSAxLjcuNSAyLjUuNC42LjkgMS4xIDEuMSAxLjktLjMtLjEtLjUgMC0uNy4yLS4yLjItLjMuNi0uMi45LjEuMi4zLjQuNS40SDI3Yy4zLS4xLjUtLjUuNC0uOC4yLjIuMy43LjIgMSAwIC4zLS4yLjYtLjMuOC0uMS4yLS4zLjQtLjQuNi0uMS4yLS4yLjQtLjIuNiAwIC4yIDAgLjUuMS43LjQuNiAxLjIgMCAxLjQtLjUuMy0uNi4yLTEuMy0uMi0xLjkgMCAwIC43LjQgMS4yIDEuOC40IDEuMi0uMyAyLjYtMS4xIDMuMXoiLz48L3N2Zz4K
[CurseForge]: https://www.curseforge.com/minecraft/mc-mods/heart-of-the-machine

Heart of the Machine is a ModFest 1.16 entry.

Heart of the Machine adds a whole new dimension of abandoned machinery to explore. Maybe you can uncover the secrets of
this dimension and make use of them somehow.

## Screenshots
![Heart of the Machine screenshot](https://raw.githubusercontent.com/Heart-of-the-Machine/Heart-of-the-Machine.github.io/master/screenshots/2020-09-20_12.12.07.png)

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
