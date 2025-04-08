from hpet import player, pet, Player

if pet.level() > 3:
    player.heal(10) # heal of 10 hearts

player.set_fly(True)

another_player = Player("playername")
enemy = player.attacker()
if enemy is not None:
    enemy.kill()