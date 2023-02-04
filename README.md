# HPET

![logo](https://i.imgur.com/ViZiV7i.png)

## How to configure HPET?

First of all, you should have 3 files, config.yml, messages.yml, pets.yml
You can change general settings from config.yml, also notice that /pet reload command sometimes could not reload this file
You can configure plugin messages from the messages.yml file, you can translate every message in your language, or just make the messages look better, if you have troubles with YAML files I recommend using a YAML parser, here it is one https://codebeautify.org/yaml-parser-online
You can setup your own pets or edit the existing ones in pets.yml file, everything about the pet is written there

DEFAULT CONFIGS ARE ON DISCORD!

## Animation Types:
GLIDE, SLOW GLIDE, SIDE, WALK, BOUNCE, NONE

## Commands:
/pet - main command, opens gui
/pet select <petname> - select a pet
/pet remove - remove the current pet
/pet glow - change glowing status
/pet trail - change trail status
/pet update - respawn your pet
/pet particle <particle> - change your pet particle
/pet buy <petname> - buy a pet you don't have
/pet addlevel <petname> <amount> - add pet level
/pet removelevel <petname> <amount> - decrease pet level
/pet setlevel <petname> <amount> - set a pet level
/pet level - shows current pet level

## Permissions:
pet.command - use /pet
pet.trail - use /pet trail
pet.glow - use /pet glow
pet.see.<petname> - see the specified pet in the gui
pet.use.<petname> - select the specified pet in the gui
pet.setlevel - set pet level

## Addons

### How to install addons?
You can simply drag & drop the addon jar file in the addons folder, inside the Pet folder

### Official addons
All official addons are free, avaiable on SpigotMc on my profile page, https://www.spigotmc.org/resources/authors/heron4gf.607598/

- PetAbilitiesAddon
- PetCombatAddon
- PetItemAddon

## How to configure pet skins

### Default
You can use Base64 codes for pet skins, you can find them on https://minecraft-heads.com, else you can use player names as valid pet skins, the plugin will automatically detect the right mode, you can find player names on https://namemc.com

### How do I use CustomModelData models?
Use (material): followed by the CustomModelData code you want to use, example: "DIAMOND_HOE:3"
You can find a list of valid materials on https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Material.html

### How do I use HeadDatabase?
Use HDB: followed by the head code into the skin value you want, example:
"HDB:12345"

###How to use Mobs:
Use MOB: followed by the Mob type, example: "MOB:SHEEP"
Valid Mob types are: https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/entity/EntityType.html
This feature works only in 1.12+

## Where I can get support?
You can join my Discord server, there you can ask questions before buying the plugin and support if you bought it! Here's the invite: https://discord.gg/PX7nGZtshD

## HeadDatabase Pets:
You can use 100k+ heads directly from HeadDatabase! Use HDB: followed by the head code into the skin value you want, example:
"HDB:12345"

## Mob Pets:
You can use Minecraft mobs as Pets!
Use MOB: followed by the Mob type, example: "MOB:SHEEP"
Valid Mob types are: https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/entity/EntityType.html

## CustomModelData Pets:
You can create stunning models with texture packs using latest minecraft features with CustomModelData!
Use (material): followed by the CustomModelData code you want to use, example: "DIAMOND_HOE:3"
You can find a list of valid materials on https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Material.html

## Placeholders: (requires PlaceholderAPI)
%pet_name% - get current pet name
%pet_displayname% - get current pet displayname
%pet_isSelected% - return whether you have a pet or not, true or false
%pet_hasTrail% - return whether you have or not a trail, true or false
%pet_isGlowing% - return whether the pet is glowing or not, true or false
%pet_level% - return the pet level value

## Abilities
Variables: (n) = number, (t) = text
You can add a chance by putting (percentage)% after :, example: TITLE:'hey':10%
You can specify duration by putting (minutes)m(seconds)s after :, example TITLE:'hey':10m3s, or TITLE:hey:10s, by default it is 10seconds
You can specify minimum level required adding 'l' after the number, example: TITLE:'your level is high!':2l

ADD_HEALTH:(n), TITLE:(t), CONSOLE_LOG:(t), FAKE_LOCATION, ADD_FOOD:(n), SHOOT_ENDERPEARL, SUBTITLE:(t), SHOOT_ARROW, SHOOT_SNOWBALL, DAMAGE:(n), HEAL:(n), POISON_NEAR, PLAYER_PARTICLE:(particle):(count), LAUNCH, CURE:(n), NO_KNOCKBACK, DISARM_OPPONENT, PET_PARTICLE:(particle):(count), DISARM_SELF, INCREASE_DAMAGE, EXP:(n), EXPLOSION:(power):(destroyBlocks):(incendiary), EXTINGUISH, FIREBALL, SET_FIRE:(n), TEMP_FLY:(n), FLY, GOD, TEMP_GOD:(n), FREEZE:(n), INVISIBLE, LIGHNING_ON_PLAYER, LIGHTNING_LOOKING, MESSAGE:(t), PLAYER_COMMAND:(t), CONSOLE_COMMAND:(t), POTION:(potionEffectType):(duration):(amplify), PLAY_SOUND:(sound):(pitch):(volume), PLAY_SOUND_EVERYONE:(sound):(pitch):(volume), PUMPKIN, VELOCITY:(power), SWAP_ITEMS, INVISIBLE_ARMOR, FAKE_ARMOR:(EquipmentSlot):(Material), INVISIBLE_HAND, FAKE_HAND:(Material), NO_FALL_DAMAGE


## How do I hook into HPET API?

You can download the PetAPI.jar file here on GitHub, add it to your classpath, then add HPET it into your plugin.yml as dependency
> softdepend: Pet

Get the HPET API:
> Pet.getApi();

Example code:
> if(command.getName().equalsIgnoreCase("petrating")) {
>
>   if(!Pet.getApi().hasUserPet(player)) {
>       return false;
>   }
>   UserPet pet = Pet.getApi().getUserPet(player);
>   if(pet.getType().getName().equals("ghast")) {
>       player.sendMessage("Hey! Your pet is really cool!");
>   } else {
>       player.sendMessage("Your pet is not cool!");
>   }
>   return true;
> }
>
> @EventHandler
> void onPetSelect(PetSelectEvent event) {
>   event.getPlayer().sendMessage("Hello world!");
> }
