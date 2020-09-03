# Version 0.1.5
For Minecraft 1.16.2.

Note: **THIS VERSION CONTAINS BREAKING CHANGES**. This version changes the directory in which Nectere dimension data is
stored.

* Updated the mod to Minecraft 1.16.2.
* Slightly changed the Nectere Portal biome blacklist so that it checks the biome the actual portal block is placed in
  instead of the biome that generates the feature.
* Added a command, `/retrogen_nectere_portal`, to retro-gen the nearest non-Nectere-side portal if it is missing. The
  nearest portal is determined using the same logic as the non-Nectere-side `/locate` command.
* Use ticking BlockEntities to generate Nectere portals in correct locations to avoid Nectere-world corruption during
  overworld generation.
  (Thank you Heaven King for thinking of doing it this way!)
* Changed the Nectere dimension directory to `<world>/dimensions/hotm/nectere`. Note: **THIS IS A BREAKING CHANGE**. In
  order to use existing Nectere chunks in the new dimension location, move everything from within
  `<world>/DIM-nectere/` into `<world>/dimensions/hotm/nectere/`.
