Just some notes...

|package(s)|group:artifact|notes|
|----------|--------------|-----|
|events             |game:events|contains zircon|
|extensions         |util:extensions|currently java extension, kind of "util" stuff|
|game               |game:core|special zircon impls, will most probably contain amethyst later|
|mapping.dijkstra   |util:pathfinding|might contain more pathfinding later|
|model...           | | |
|.elements          |model:elements|might become more complex with different unit types, corps and name generators|
|.mapgenerators     |model:mapgeneration| |
|.radio             |model:radio|gets its own artifact because of complexity|
|.terrain and .world |model:world|close together so they will get one artifact|
|options            |game:config|More complexity might be hidden behind the simple API (i.e. reading config from files)|
|ui                 |game:application|The only frontend stuff (although not the only artifact using zircon, unfortunately), contains main|

## Folder structure

Every group containing more than one artifact will be an aggregator project.

This means it gets its own folder with every artifact in a subfolder with the artifact's name.

```
game/
    events/
    core/
    config/
    application/
util/
    extensions/
    pathfinding/
model/
    elements/
    mapgeneration/
    radio/
    world/
```

## Dependencies

The group with least dependencies will be `util`, it might be used by `game` and `model`.

`model` should be used by `game` and not need any hexworks dependencies (the radio stuff might need to be broken down).

## New package names

The packages should match their group and artifact. The base package is `de.gleex.pltcmd`. Following should be `group.artifact[.possibly.more]`.

## Tests

Tests run before the new structure: **138**

(tested by running `mvn -U clean package`)