# Version 0.1.4
For Minecraft 1.16.1

* Added surface trees to the Thinking Forest biome.
* Added a config for Heart of the Machine called `hotm.yml` in the `config` folder.
* Added config option for which biomes Nectere portals generate in. This config option is a yaml list and is called
  `necterePortalWorldGenBlacklistBiomes`.
* Begin on support for different Nectere biomes having different coordinate multipliers.
  Note: This breakes the `/locate` command in generated chunks.
* Added Wasteland biome.
* Fixed unbreakable crystals.
* Added default config option to automatically make existing worlds use the new biome source for the Nectere dimension.
  This config option is `forceNectereBiomeSource`.
* Added default config option to generate an output portal if a player uses an input portal that is disconnected but
  otherwise in a valid location. This config option is `generateMissingPortals`.
* Made portals play a sound when a player tries to enter them if they are in an invalid or 'dead' location.
