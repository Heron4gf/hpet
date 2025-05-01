package it.heron.hpet.main.commands;

import io.github.jwdeveloper.spigot.commands.api.annotations.FCommand;
import io.github.jwdeveloper.spigot.commands.api.data.events.ArgumentSuggestionEvent;

import it.heron.hpet.api.PetAPI;
import it.heron.hpet.main.PetPlugin;
import it.heron.hpet.modules.messages.MessagesHandler;
import it.heron.hpet.modules.pets.pettypes.PetType;
import it.heron.hpet.modules.pets.userpets.abstracts.UserPet;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;


public final class PetCommand {

    private final PetAPI petAPI;
    private volatile MessagesHandler messagesHandler;

    public PetCommand() {
        this.petAPI = PetPlugin.getApi();
        this.messagesHandler = (MessagesHandler) PetPlugin.getInstance().getModulesHandler().moduleByName("Messages");
    }

    private MessagesHandler getMessagesHandler() {
        MessagesHandler handler = messagesHandler;
        if (handler == null) {
            synchronized(this) {
                handler = messagesHandler;
                if (handler == null) {
                    handler = (MessagesHandler) PetPlugin.getInstance().getModulesHandler().moduleByName("Messages");
                    messagesHandler = handler;
                }
            }
        }
        return handler;
    }

    // --- Helper methods for Suggestions ---

    private Collection<String> getEnabledPetTypeNames() {
        if (petAPI == null) return List.of();
        return petAPI.enabledPetTypes().stream()
                .map(PetType::getName)
                .collect(Collectors.toList());
    }

    private List<String> suggestPetTypes(ArgumentSuggestionEvent event) {
        return new ArrayList<>(getEnabledPetTypeNames());
    }

    private List<String> suggestBuyablePetTypes(ArgumentSuggestionEvent event) {
        // TODO: Implement logic to only suggest pet types the sender doesn't own yet and/or can afford.
        return new ArrayList<>(getEnabledPetTypeNames());
    }

    // --- Helper methods for Command Logic ---

    private void sendColoredMessage(CommandSender sender, String message) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }

    private void sendMessageToSender(CommandSender sender, String messageSubpath, Map<String, String> placeholders) {
        MessagesHandler handler = getMessagesHandler();
        if (handler == null) {
            sendColoredMessage(sender, "&cError: Messages system not available.");
            return;
        }

        if (sender instanceof Player) {
            // MessagesHandler handles default player placeholders ({player}, {pet}, {level})
            messagesHandler.sendMessage((Player) sender, messageSubpath);
        } else {
            // Console sender needs manual placeholder replacement
            String rawMsg = messagesHandler.getRawString(messageSubpath);
            if (rawMsg != null) {
                String consoleMsg = rawMsg.replace("{player}", "Console"); // Default console placeholder
                if (placeholders != null) {
                    for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                        consoleMsg = consoleMsg.replace(entry.getKey(), entry.getValue());
                    }
                }
                sendColoredMessage(sender, consoleMsg);
            } else {
                sendColoredMessage(sender, "&cMessage subpath '" + messageSubpath + "' not found in locale!");
            }
        }
    }

    private Player getTargetPlayer(CommandSender sender, Player targetArg) {
        if (targetArg != null) return targetArg;
        if (sender instanceof Player) return (Player) sender;

        sendMessageToSender(sender, "error.specify_target", null);
        return null;
    }

    private UserPet getUserPet(CommandSender sender, Player target) {
        if (target == null) return null;

        UserPet userPet = petAPI.userPet(target);
        if (userPet == null) {
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("{player}", target.getName());
            sendMessageToSender(sender, "error.no_active_pet", placeholders);
            return null;
        }
        return userPet;
    }


    /*
     * /hpet - main command, opens gui, pet.command
     */
    @FCommand(
        name = "hpet", 
        permission = "pet.command", 
        description = "Main HPET command.", 
        usageMessage = "/hpet [subcommand]"
    )
    public void onHpetCommand(CommandSender sender) {
        sendHelpMessage(sender);
    }

    @FCommand(
        name = "hpet help", 
        permission = "pet.command", 
        description = "Shows help for HPET commands"
    )
    public void onHelpCommand(CommandSender sender) {
        sendHelpMessage(sender);
    }

    private void sendHelpMessage(CommandSender sender) {
        List<String> helpLines = new ArrayList<>();
        helpLines.add("&6&lHPET Commands:");
        helpLines.add("&e/hpet help &7- Shows this help message");
        helpLines.add("&e/hpet select <petType> [player] &7- Select a pet");
        helpLines.add("&e/hpet remove [player] &7- Remove current pet");
        helpLines.add("&e/hpet update [player] &7- Respawn your pet");
        helpLines.add("&e/hpet buy <petType> [player] &7- Buy a pet");
        helpLines.add("&e/hpet addlevel <amount> [player] &7- Add pet levels");
        helpLines.add("&e/hpet removelevel <amount> [player] &7- Remove pet levels");
        helpLines.add("&e/hpet setlevel <level> [player] &7- Set pet level");
        helpLines.add("&e/hpet level [player] &7- Show current pet level");

        helpLines.forEach(line -> sendColoredMessage(sender, line));
    }


    /*
     * /hpet select <petType> [target:Player] - select a pet, pet.use.
     */
    @FCommand(
        pattern = "/hpet select <petType:Text(s:suggestPetTypes)> <target:Player?>",
        permission = "pet.use",
        description = "Selects a pet.",
        usageMessage = "/hpet select <petType> [player]"
    )
    public void selectPetCommand(CommandSender sender, String petType, Player target) {
        Player targetPlayer = getTargetPlayer(sender, target);
        if (targetPlayer == null) return;

        PetType type = petAPI.petType(petType);
        if (type == null) {
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("{petType}", petType);
            sendMessageToSender(sender, "command.hpet.select.error.not_found", placeholders);
            return;
        }

        UserPet selectedPet = petAPI.selectPet(targetPlayer, type);

        if (selectedPet != null) {
            if (sender.equals(targetPlayer)) {
                sendMessageToSender(sender, "command.hpet.select.success.self", null);
            } else {
                Map<String, String> senderPlaceholders = new HashMap<>();
                senderPlaceholders.put("{player}", targetPlayer.getName());
                senderPlaceholders.put("{petType}", type.getName());
                sendMessageToSender(sender, "command.hpet.select.success.other", senderPlaceholders);

                messagesHandler.sendMessage(targetPlayer, "command.hpet.select.success.received");
            }
        } else {
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("{petType}", petType);
            placeholders.put("{player}", targetPlayer.getName());
            sendMessageToSender(sender, "command.hpet.select.error.failed", placeholders);
        }
    }

    /*
     * /hpet remove [target:Player] - remove the current pet, pet.remove
     */
    @FCommand(
        pattern = "/hpet remove <target:Player?>",
        permission = "pet.remove",
        description = "Removes the current pet.",
        usageMessage = "/hpet remove [player]"
    )
    public void removePetCommand(CommandSender sender, Player target) {
        Player targetPlayer = getTargetPlayer(sender, target);
        if (targetPlayer == null) return;

        UserPet userPet = getUserPet(sender, targetPlayer);
        if (userPet == null) return;

        petAPI.removePet(userPet);

        if (sender.equals(targetPlayer)) {
            sendMessageToSender(sender, "command.hpet.remove.success.self", null);
        } else {
            Map<String, String> senderPlaceholders = new HashMap<>();
            senderPlaceholders.put("{player}", targetPlayer.getName());
            sendMessageToSender(sender, "command.hpet.remove.success.other", senderPlaceholders);

            messagesHandler.sendMessage(targetPlayer, "command.hpet.remove.success.received");
        }
    }

    /*
     * /hpet update [target:Player] - respawn your pet, pet.update
     */
    @FCommand(
        pattern = "/hpet update <target:Player?>",
        permission = "pet.update",
        description = "Respawn your current pet.",
        usageMessage = "/hpet update [player]"
    )
    public void updatePetCommand(CommandSender sender, Player target) {
        Player targetPlayer = getTargetPlayer(sender, target);
        if (targetPlayer == null) return;

        UserPet userPet = getUserPet(sender, targetPlayer);
        if (userPet == null) return;

        PetType currentType = userPet.getPetType();
        petAPI.removePet(userPet);
        UserPet newPet = petAPI.selectPet(targetPlayer, currentType);

        if (newPet != null) {
            if (sender.equals(targetPlayer)) {
                sendMessageToSender(sender, "command.hpet.update.success.self", null);
            } else {
                Map<String, String> senderPlaceholders = new HashMap<>();
                senderPlaceholders.put("{player}", targetPlayer.getName());
                sendMessageToSender(sender, "command.hpet.update.success.other", senderPlaceholders);

                messagesHandler.sendMessage(targetPlayer, "command.hpet.update.success.received");
            }
        } else {
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("{player}", targetPlayer.getName());
            sendMessageToSender(sender, "command.hpet.update.error.failed", placeholders);
        }
    }

    /*
     * /hpet buy <petType:Text> [target:Player] - buy a pet you don't have, pet.see.
     */
    @FCommand(
        pattern = "/hpet buy <petType:Text(s:suggestBuyablePetTypes)> <target:Player?>",
        permission = "pet.see",
        description = "Allows a player to buy a pet.",
        usageMessage = "/hpet buy <petType> [player]"
    )
    public void buyPetCommand(CommandSender sender, String petType, Player target) {
        Player targetPlayer = getTargetPlayer(sender, target);
        if (targetPlayer == null) return;

        PetType type = petAPI.petType(petType);
        if (type == null) {
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("{petType}", petType);
            sendMessageToSender(sender, "command.hpet.buy.error.not_found", placeholders);
            return;
        }

        // TODO: Implement logic for buying the pet:
        // 1. Check if the targetPlayer already owns this pet type.
        // 2. Check if the targetPlayer has enough money (requires economy integration).
        // 3. If checks pass, add the pet to the player's owned pets list.
        // 4. Send success/failure messages using messagesHandler.

        Map<String, String> wipPlaceholders = new HashMap<>();
        wipPlaceholders.put("{petType}", petType);
        wipPlaceholders.put("{player}", targetPlayer.getName());
        sendMessageToSender(sender, "command.hpet.buy.wip", wipPlaceholders);

        boolean success = true; // Simulate success/failure based on TODOs above

        if (success) {
            if (sender.equals(targetPlayer)) {
                Map<String, String> selfPlaceholders = new HashMap<>();
                selfPlaceholders.put("{petType}", petType);
                sendMessageToSender(sender, "command.hpet.buy.success.self", selfPlaceholders);
            } else {
                Map<String, String> senderPlaceholders = new HashMap<>();
                senderPlaceholders.put("{petType}", petType);
                senderPlaceholders.put("{player}", targetPlayer.getName());
                sendMessageToSender(sender, "command.hpet.buy.success.other", senderPlaceholders);
                // MessagesHandler buildDictionary should include sender name as {player}
                messagesHandler.sendMessage(targetPlayer, "command.hpet.buy.success.received");
            }
        } else {
            Map<String, String> failedPlaceholders = new HashMap<>();
            failedPlaceholders.put("{petType}", petType);
            failedPlaceholders.put("{player}", targetPlayer.getName());
            sendMessageToSender(sender, "command.hpet.buy.error.failed", failedPlaceholders);
        }
    }

    /*
     * /hpet addlevel <amount:Integer> [target:Player] - add pet level, pet.addlevel
     */
    @FCommand(
        pattern = "/hpet addlevel <amount:Number> <target:Player?>",
        permission = "pet.addlevel",
        description = "Adds levels to a pet.",
        usageMessage = "/hpet addlevel <amount> [player]"
    )
    public void addLevelCommand(CommandSender sender, double amount, Player target) {
        Player targetPlayer = getTargetPlayer(sender, target);
        if (targetPlayer == null) return;

        UserPet userPet = getUserPet(sender, targetPlayer);
        if (userPet == null) return;

        if (amount <= 0) {
            sendMessageToSender(sender, "command.hpet.level.add.error.invalid_amount", null);
            return;
        }

        int currentLevel = userPet.getLevel();
        int newLevel = currentLevel + (int)Math.round(amount);
        userPet.setLevel(newLevel);

        Map<String, String> levelPlaceholders = new HashMap<>();
        levelPlaceholders.put("{amount}", String.valueOf(amount));
        levelPlaceholders.put("{level}", String.valueOf(newLevel));


        if (sender.equals(targetPlayer)) {
            sendMessageToSender(sender, "command.hpet.level.add.success.self", levelPlaceholders);
        } else {
            levelPlaceholders.put("{player}", targetPlayer.getName());
            sendMessageToSender(sender, "command.hpet.level.add.success.other", levelPlaceholders);

            messagesHandler.sendMessage(targetPlayer, "command.hpet.level.add.success.received");
        }
    }


    /*
     * /hpet removelevel <amount:Integer> [target:Player] - decrease pet level, pet.removelevel
     */
    @FCommand(
        pattern = "/hpet removelevel <amount:Number> <target:Player?>",
        permission = "pet.removelevel",
        description = "Decreases a pet's level.",
        usageMessage = "/hpet removelevel <amount> [player]"
    )
    public void removeLevelCommand(CommandSender sender, double amount, Player target) {
        Player targetPlayer = getTargetPlayer(sender, target);
        if (targetPlayer == null) return;

        UserPet userPet = getUserPet(sender, targetPlayer);
        if (userPet == null) return;

        if (amount <= 0) {
            sendMessageToSender(sender, "command.hpet.level.remove.error.invalid_amount", null);
            return;
        }

        int currentLevel = userPet.getLevel();
        int newLevel = Math.max(0, currentLevel - (int)Math.round(amount));
        userPet.setLevel(newLevel);

        Map<String, String> levelPlaceholders = new HashMap<>();
        levelPlaceholders.put("{amount}", String.valueOf(amount));
        levelPlaceholders.put("{level}", String.valueOf(newLevel));

        if (sender.equals(targetPlayer)) {
            sendMessageToSender(sender, "command.hpet.level.remove.success.self", levelPlaceholders);
        } else {
            levelPlaceholders.put("{player}", targetPlayer.getName());
            sendMessageToSender(sender, "command.hpet.level.remove.success.other", levelPlaceholders);

            messagesHandler.sendMessage(targetPlayer, "command.hpet.level.remove.success.received");
        }
    }


    /*
     * /hpet setlevel <level:Integer> [target:Player] - set a pet level, pet.setlevel
     */
    @FCommand(
        pattern = "/hpet setlevel <level:Number> <target:Player?>",
        permission = "pet.setlevel",
        description = "Sets a pet's level.",
        usageMessage = "/hpet setlevel <level> [player]"
    )
    public void setLevelCommand(CommandSender sender, double level, Player target) {
        Player targetPlayer = getTargetPlayer(sender, target);
        if (targetPlayer == null) return;

        UserPet userPet = getUserPet(sender, targetPlayer);
        if (userPet == null) return;

        if (level < 0) {
            sendMessageToSender(sender, "command.hpet.level.set.error.negative_level", null);
            return;
        }

        userPet.setLevel((int)Math.round(level));

        Map<String, String> levelPlaceholders = new HashMap<>();
        levelPlaceholders.put("{level}", String.valueOf(level));

        if (sender.equals(targetPlayer)) {
            sendMessageToSender(sender, "command.hpet.level.set.success.self", levelPlaceholders);
        } else {
            levelPlaceholders.put("{player}", targetPlayer.getName());
            sendMessageToSender(sender, "command.hpet.level.set.success.other", levelPlaceholders);

            messagesHandler.sendMessage(targetPlayer, "command.hpet.level.set.success.received");
        }
    }

    /*
     * /hpet level [target:Player] - shows current pet level, pet.level
     */
    @FCommand(
        pattern = "/hpet level <target:Player?>",
        permission = "pet.level",
        description = "Shows your current pet's level.",
        usageMessage = "/hpet level [player]"
    )
    public void showLevelCommand(CommandSender sender, Player target) {
        Player targetPlayer = getTargetPlayer(sender, target);
        if (targetPlayer == null) return;

        UserPet userPet = getUserPet(sender, targetPlayer);
        if (userPet == null) return;

        int level = userPet.getLevel();

        Map<String, String> levelPlaceholders = new HashMap<>();
        levelPlaceholders.put("{level}", String.valueOf(level));


        if (sender.equals(targetPlayer)) {
            sendMessageToSender(sender, "command.hpet.level.show.self", levelPlaceholders);
        } else {
            levelPlaceholders.put("{player}", targetPlayer.getName());
            sendMessageToSender(sender, "command.hpet.level.show.other", levelPlaceholders);

            messagesHandler.sendMessage(targetPlayer, "command.hpet.level.show.received");
        }
    }
}
