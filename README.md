# HPET

![logo](https://i.imgur.com/ViZiV7i.png)

## How to configure HPET?

First of all, you should have 3 files, config.yml, messages.yml, pets.yml
You can change general settings from config.yml, also notice that /pet reload command sometimes could not reload this file
You can configure plugin messages from the messages.yml file, you can translate every message in your language, or just make the messages look better, if you have troubles with YAML files I recommend using a YAML parser, here it is one https://codebeautify.org/yaml-parser-online
You can setup your own pets or edit the existing ones in pets.yml file, everything about the pet is written there

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
