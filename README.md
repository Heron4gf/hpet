# HPET Documentation

![logo](https://i.imgur.com/Y0hEWEY.jpeg)

## License
Here you can consult HPET [Licence](https://github.com/Heron4gf/hpet/blob/main/LICENSE)
<br>
## Support
Join Discord to receive fast support post sale or plugin information pre sale! [Discord](https://discord.gg/PX7nGZtshD)
<br>
## Install HPET
You can install HPET following these steps:
- Download and install [ProtocolLib](https://www.spigotmc.org/resources/protocollib.1997/), accordingly to your Minecraft server version
- Download HPET plugin from [SpigotMC](https://www.spigotmc.org/resources/⭕%EF%B8%8F1-8-1-20-x⭕%EF%B8%8Fhpet✏%EF%B8%8Fpacket-based-pets-and-cosmetics-⭕%EF%B8%8F.93891/)
- Upload the HPET.jar file into your server's plugins folder
- Restart your server
- Configure HPET!


## Configure HPET

### Create a Pet
Your Pets must be configured in pets.yml file. And enabled via config. A Pet must have an **id**, a **displayname**, a **description** and at least **one skin**<br>
This is an example of a valid base Pet:

```
bat:
  displayname: "&aBat"
  description:
    - ""
    - "&7I can fly!"
  skins:
    - "PAPER:31" # this is using custom model data skin
```

Pets can also have **other attributes**, which are the following:<br><br>
**y**, to configure pet relative y location from player's location, default is 1.<br>
**yaw**, to configure the relative rotation of the pet armorstand, added to player's yaw and yawCalibration, default is 0.<br>
**abilities**, a list of String defined abilities, see [Abilities](#Abilities) for reference.<br>
**animation**, decide the type of animation the pet should have, by default is GLIDE, valid animation types are: ```GLIDE, BOUNCE, GLITCH, NONE, SLOW_GLIDE, WALK, SIDE, FOLLOW```.<br>
**distance**, used to calculate the radius of the pet location relative to the player, default is 1.<br>
**price**, requires [Vault](https://www.spigotmc.org/resources/vault.34315/) to be installed. Allows users to unlock Pets using economy. By default pets are not buyable.<br>
**level**, allows to setup a level type and value, which allows the Pet to level up based on certain factors. See [Pet levelling](#Pet-levelling).<br>
**particle**, only for 1.9+, allows to set the default Pet particle which can be enabled by levelling or by command. By default it is SNOWBALL Here is a list of valid [Particle types in 1.20](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Particle.html).<br>
**group**, since HPET 4.3+, Players can select multiple pets at same time but only one for each group. This allows to group pets by behaviour, notice that this does not have any graphic/aesthetic effect and should not be confused with GUI groups. Default group is "default".<br>
**visible**, boolean (true|false), defines the default Pet visibility. By default it is true.<br>
**balloon**, boolean (true|false), attach the pet to the player with a lead, so it looks like you're holding a balloon<br>
**balloon_height**, allows to adjust the height of lead

### Create a Cosmetic
Your Cosmetics can be placed in separate YAML files into cosmetics folder and enabled via config.yml. A Cosmetic must have an **id**, a **displayname**, a **wear** ([Valid Equipment Slots](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/inventory/EquipmentSlot.html)), a **description** and an **item**<br>
This is an example of a valid base Cosmetic:

```
ballcap:
  displayname: "&9Ballcap"
  wear: HEAD
  description:
    - ""
  item: "LEATHER_HORSE_ARMOR:10102"
```

Cosmetics can also have **other attributes**, which are the following:<br><br>
**color**, to configure the default Cosmetic color in hex, only applies to Leather armor types.<br>
**price**, requires [Vault](https://www.spigotmc.org/resources/vault.34315/) to be installed. Allows users to unlock Pets using economy. By default pets are not buyable.<br>

**Importing Cosmetics from ItemsAdder**<br>
You can also import cosmetics from an ItemsAdder namespace using the following option:<br>
**import_itemsadder:** <namespace><br>
this option can be put once in each yml file that defines cosmetics. The cosmetics **id**, **description**, and **item** will be defined by default from the ItemsAdder configuration, to override the default values you just need to configure the cosmetic values you want to change. **wear** is set by default to **HEAD** if the item is an ItemsAdder HAT, else it gets set as **OFF_HAND** item<br>
**Notice:** importing doesn't mean enabling. To enable the cosmetics you will be still required to put them in enabledPets, even if you imported from ItemsAdder<br>

### Pet levelling
Pets can have levels, to enable levelling you have to enable them in config here:
```
level:
  max: 100 # set the maximum Pet level
  enable: true # enable level based features
```

Levels go from 1 to the specified level and can be displayed on Pet nametag using the **%level%** placeholder.<br>
Levels allow players to unlock the following visual features of their pet:
- Level > 2, Pet trail
- Level > 4, Pet particle (uses the default one)
- Level > 6, Pet glow effect (only in 1.9+)

Levels can also have impact on abilities. Since HPET 4+ you can setup the minimum level required to unlock a certain ability. See more in [Abilities](#Abilities)<br>

### Abilities
Pets can have abilities which are defined as Strings with arguments separated by ":", All abilities follow this format: <ABILITY TYPE>:<ARGUMENTS>. Valid ability types are: <br>

Required arguments: (n) = number, (t) = text<br>
```ADD_HEALTH:(n), TITLE:(t), CONSOLE_LOG:(t), FAKE_LOCATION, ADD_FOOD:(n), SHOOT_ENDERPEARL, SUBTITLE:(t), SHOOT_ARROW, SHOOT_SNOWBALL, DAMAGE:(n), HEAL:(n), POISON_NEAR, PLAYER_PARTICLE:(particle):(count), LAUNCH, CURE:(n), NO_KNOCKBACK, DISARM_OPPONENT, PET_PARTICLE:(particle):(count), DISARM_SELF, INCREASE_DAMAGE, EXP:(n), EXPLOSION:(power):(destroyBlocks):(incendiary), EXTINGUISH, FIREBALL, SET_FIRE:(n), TEMP_FLY:(n), FLY, GOD, TEMP_GOD:(n), FREEZE:(n), INVISIBLE, LIGHNING_ON_PLAYER, LIGHTNING_LOOKING, MESSAGE:(t), PLAYER_COMMAND:(t), CONSOLE_COMMAND:(t), POTION:(potionEffectType):(duration):(amplify), PLAY_SOUND:(sound):(pitch):(volume), PLAY_SOUND_EVERYONE:(sound):(pitch):(volume), PUMPKIN, VELOCITY:(power), SWAP_ITEMS, INVISIBLE_ARMOR, FAKE_ARMOR:(EquipmentSlot):(Material), INVISIBLE_HAND, FAKE_HAND:(Material), NO_FALL_DAMAGE```

**Other arguments (can be added to any Ability):**
Format: name, definition, example usage
- Change of execution, (n)%, SHOOT_ARROW:10%, default: 100%
- Cooldown before execution, (n)m(n)s, HEAL:1m, default: 30s
- Required level, (n)l, EXP:5:5l, default: 1l
- Event listener, (t)e, NO_FALL_DAMAGE:SHIFTe, default: TIME
Valid Ability events: ```SHIFT,WALK,BLOCK_BREAK,BLOCK_PLACE,TIME```

### Pet skins
Pets will change skin every 4 ticks among the ones you defined in the configuration

#### Heads skins
Pet **head** type skins can be decoded into the following formats:

**Base64** or **Player names**, you can use codes from [Minecraft Heads Website](https://minecraft-heads.com), or player names, example usage:
```
skins:
  - "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmMzMTNhOGE1MzE4NjM4OGI5YjVmMDdhOGRhZTg4NThhYTI0YmE4Njk4YzgyZTdlZjdiYTg3NTg4MDlhYWIzNyJ9fX0="
  - "Steve"
```

**HeadDatabase heads**, requires [HeadDatabase](https://www.spigotmc.org/resources/head-database.14280/) to be installed, example usage:
```
skins:
  - "HDB:10123"
```

**HeadDB heads**, requires [HeadDB](https://www.spigotmc.org/resources/headdb.84967/) to be installed, example usage:
```
skins:
  - "HDB:4023"
```

#### Custom Skins
**CustomModelData models**, allows to be compatible with **textured pets/cosmetics**, example usage:
```
skins:
  - "DIAMOND_HOE:3"
```

**Mobs**, **MythicMobs** and **ModelEngine 4**, allows to spawn a Mob, only one skin can be applied, only 1.9+, example usage:
```
skins:
  - "MOB:SHEEP"
```

requires [MythicMobs](https://www.spigotmc.org/resources/⚔-mythicmobs-free-version-►the-1-custom-mob-creator◄.5702/):
```
skins:
  - "MYTHICMOB:Dragon"
```

**Notice:** if you want to use ModelEngine 3, refer to it as MythicMob<br>
requires [ModelEngine](https://mythiccraft.io/index.php?resources/model-engine—ultimate-entity-model-manager-1-19-4-1-20-2.1213/):
```
skins:
  - "MODELENGINE:Dragon"
```

**ItemsAdder items**, allows to be compatible with **textured pets/cosmetics**, example usage:
```
skins:
  - "ITEMSADDER:blue_hat"
```

## Commands and Permissions
Command aliases can be defined in ```config.yml``` in the following section:
```
useAliases: false # enable aliases for /hpet command
alias:
  - mpets # you can insert multiple aliases
```
Default aliases are /hpet, /pet<br>

/hpet - main command, opens gui, pet.command<br>
/hpet select <petname> - select a pet, pet.use.<petname><br>
/hpet remove - remove the current pet, pet.remove<br>
/hpet glow - change glowing status, pet.glow<br>
/hpet trail - change trail status, pet.trail<br>
/hpet update - respawn your pet, pet.update<br>
/hpet particle <particle> - change your pet particle, pet.particle<br>
/hpet color <color hex> - set your cosmetic color, pet.color<br>
/hpet buy <petname> - buy a pet you don't have, pet.see.<petname><br>
/hpet addlevel <petname> <amount> - add pet level, pet.addlevel<br>
/hpet removelevel <petname> <amount> - decrease pet level, pet.removelevel<br>
/hpet setlevel <petname> <amount> - set a pet level, pet.setlevel<br>
/hpet level - shows current pet level, pet.level<br>

(Notice) to show a Pet in GUI a Player will need permission pet.see.<petname>, this allows to choose wether you want to show unlocked pets or not in gui. To show all GUI pets you can assign pet.see.* to players. This permission does **not** change the way pets are visualized while spawned.

## Addons

### How to install addons?
You can simply drag & drop the addon jar file in the addons folder, inside the Pet folder

### Official addons
All official addons are free, avaiable on SpigotMc<br>

- [PetItemAddon](https://www.spigotmc.org/resources/pet-item-addon-hpet.95645/)

## Placeholders: (requires [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/))
%pet_name% - get current pet name<br>
%pet_displayname% - get current pet displayname<br>
%pet_isSelected% - return whether you have a pet or not, true or false<br>
%pet_hasTrail% - return whether you have or not a trail, true or false<br>
%pet_isGlowing% - return whether the pet is glowing or not, true or false<br>
%pet_level% - return the pet level value<br>


## Developer API

**Import Dependency with**<br>

**Maven**
```
	<repositories>
		<repository>
		    <id>jitpack.io</id>
		    <url>https://jitpack.io</url>
		</repository>
	</repositories>
```
```
	<dependency>
	    <groupId>com.github.Heron4gf</groupId>
	    <artifactId>HPET-api</artifactId>
	    <version>4.5</version>
	</dependency>
```
**Gradle**
```
	dependencyResolutionManagement {
		repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
		repositories {
			mavenCentral()
			maven { url 'https://jitpack.io' }
		}
	}
```
```
	dependencies {
	        implementation 'com.github.Heron4gf:HPET-api:4.5'
	}
```
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
