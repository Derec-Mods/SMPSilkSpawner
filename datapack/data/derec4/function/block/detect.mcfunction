
execute positioned ~-1 ~-1 ~-1 if block ~ ~ ~ spawner unless data block ~ ~ ~ SpawnData.entity.id run return run function derec4:spawner/block
execute positioned ~-1 ~-1 ~ ~-0 if block ~ ~ ~ spawner unless data block ~ ~ ~ SpawnData.entity.id run return run function derec4:spawner/block
execute positioned ~-1 ~-1 ~1 if block ~ ~ ~ spawner unless data block ~ ~ ~ SpawnData.entity.id run return run function derec4:spawner/block
execute positioned ~-1 ~ ~-1 if block ~ ~ ~ spawner unless data block ~ ~ ~ SpawnData.entity.id run return run function derec4:spawner/block
execute positioned ~-1 ~ ~ ~ if block ~ ~ ~ spawner unless data block ~ ~ ~ SpawnData.entity.id run return run function derec4:spawner/block
execute positioned ~-1 ~ ~1 if block ~ ~ ~ spawner unless data block ~ ~ ~ SpawnData.entity.id run return run function derec4:spawner/block
execute positioned ~-1 ~1 ~-1 if block ~ ~ ~ spawner unless data block ~ ~ ~ SpawnData.entity.id run return run function derec4:spawner/block
execute positioned ~-1 ~1 ~ ~ if block ~ ~ ~ spawner unless data block ~ ~ ~ SpawnData.entity.id run return run function derec4:spawner/block
execute positioned ~-1 ~1 ~1 if block ~ ~ ~ spawner unless data block ~ ~ ~ SpawnData.entity.id run return run function derec4:spawner/block
execute positioned ~ ~-1 ~-1 if block ~ ~ ~ spawner unless data block ~ ~ ~ SpawnData.entity.id run return run function derec4:spawner/block
execute positioned ~ ~-1 ~ ~ if block ~ ~ ~ spawner unless data block ~ ~ ~ SpawnData.entity.id run return run function derec4:spawner/block
execute positioned ~ ~-1 ~1 if block ~ ~ ~ spawner unless data block ~ ~ ~ SpawnData.entity.id run return run function derec4:spawner/block
execute positioned ~ ~ ~-1 if block ~ ~ ~ spawner unless data block ~ ~ ~ SpawnData.entity.id run return run function derec4:spawner/block
execute positioned ~ ~ ~ if block ~ ~ ~ spawner unless data block ~ ~ ~ SpawnData.entity.id run return run function derec4:spawner/block
execute positioned ~ ~ ~1 if block ~ ~ ~ spawner unless data block ~ ~ ~ SpawnData.entity.id run return run function derec4:spawner/block
execute positioned ~ ~1 ~-1 if block ~ ~ ~ spawner unless data block ~ ~ ~ SpawnData.entity.id run return run function derec4:spawner/block
execute positioned ~ ~1 ~ ~ if block ~ ~ ~ spawner unless data block ~ ~ ~ SpawnData.entity.id run return run function derec4:spawner/block
execute positioned ~ ~1 ~1 if block ~ ~ ~ spawner unless data block ~ ~ ~ SpawnData.entity.id run return run function derec4:spawner/block
execute positioned ~1 ~-1 ~-1 if block ~ ~ ~ spawner unless data block ~ ~ ~ SpawnData.entity.id run return run function derec4:spawner/block
execute positioned ~1 ~-1 ~ ~ if block ~ ~ ~ spawner unless data block ~ ~ ~ SpawnData.entity.id run return run function derec4:spawner/block
execute positioned ~1 ~-1 ~1 if block ~ ~ ~ spawner unless data block ~ ~ ~ SpawnData.entity.id run return run function derec4:spawner/block
execute positioned ~1 ~ ~-1 if block ~ ~ ~ spawner unless data block ~ ~ ~ SpawnData.entity.id run return run function derec4:spawner/block
execute positioned ~1 ~ ~ ~ if block ~ ~ ~ spawner unless data block ~ ~ ~ SpawnData.entity.id run return run function derec4:spawner/block
execute positioned ~1 ~ ~1 if block ~ ~ ~ spawner unless data block ~ ~ ~ SpawnData.entity.id run return run function derec4:spawner/block
execute positioned ~1 ~1 ~-1 if block ~ ~ ~ spawner unless data block ~ ~ ~ SpawnData.entity.id run return run function derec4:spawner/block
execute positioned ~1 ~1 ~ ~ if block ~ ~ ~ spawner unless data block ~ ~ ~ SpawnData.entity.id run return run function derec4:spawner/block
execute positioned ~1 ~1 ~1 if block ~ ~ ~ spawner unless data block ~ ~ ~ SpawnData.entity.id run return run function derec4:spawner/block
$execute as @s run data merge entity @s {Item:{components:{"minecraft:block_entity_data":{SpawnRange: 4s,Delay: 0s, MinSpawnDelay: 200s, SpawnPotentials: [], MaxNearbyEntities: 6s, RequiredPlayerRange: 16s, SpawnCount: 4s, MaxSpawnDelay: 800s,id:"minecraft:mob_spawner",SpawnData:{entity:$(entity)}},"minecraft:custom_name":{"italic":false,"color":"white","text":"Monster Spawner"}}}}

