execute as @e[type=minecraft:experience_orb,nbt={Age:0s}] at @s if entity @e[type=item,nbt={Age:0s,Item:{id:"minecraft:spawner"}},distance=..2] run kill @s

execute as @e[type=item,nbt={Item:{id:"minecraft:spawner"}}] run function derec4:item with entity @s Item.components."minecraft:custom_data"

execute as @a[scores={sp_pl=1..}] at @s run function derec4:block/detect

scoreboard players set @a sp_pl 0
