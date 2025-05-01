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

    /**
     * Initializes the PetCommand handler with references to the PetAPI and MessagesHandler modules.
     */
    public PetCommand() {
        this.petAPI = PetPlugin.getApi();
        this.messagesHandler = (MessagesHandler) PetPlugin.getInstance().getModulesHandler().moduleByName("Messages");
    }

    /**
     * Returns the {@code MessagesHandler} instance, initializing it if necessary using thread-safe lazy initialization.
     *
     * @return the {@code MessagesHandler} used for sending localized messages
     */
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

    /**
     * Retrieves the names of all enabled pet types.
     *
     * @return a collection of enabled pet type names, or an empty list if the PetAPI is unavailable
     */

    private Collection<String> getEnabledPetTypeNames() {
        if (petAPI == null) return List.of();
        return petAPI.enabledPetTypes().stream()
                .map(PetType::getName)
                .collect(Collectors.toList());
    }

    /**
     * Returns a list of enabled pet type names for command argument suggestions.
     *
     * @param event the argument suggestion event triggering this suggestion
     * @return a list of enabled pet type names
     */
    private List<String> suggestPetTypes(ArgumentSuggestionEvent event) {
        return new ArrayList<>(getEnabledPetTypeNames());
    }

    /**
     * Returns a list of enabled pet type names for command argument suggestions when buying pets.
     *
     * @param event the argument suggestion event context
     * @return a list of enabled pet type names
     */
    private List<String> suggestBuyablePetTypes(ArgumentSuggestionEvent event) {
        // TODO: Implement logic to only suggest pet types the sender doesn't own yet and/or can afford.
        return new ArrayList<>(getEnabledPetTypeNames());
    }

    /**
     * Sends a message to the specified sender with color codes translated using '&' as the color character.
     *
     * @param sender the recipient of the message
     * @param message the message to send, with color codes using '&'
     */

    private void sendColoredMessage(CommandSender sender, String message) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }

    /**
     * Sends a localized message to the specified sender, replacing placeholders as needed.
     *
     * For players, delegates to the MessagesHandler to handle built-in placeholders. For non-player senders (e.g., console), retrieves the raw message, replaces placeholders (including a default for {player}), and sends the colored message. If the message subpath is not found, notifies the sender of the missing message.
     *
     * @param sender the recipient of the message
     * @param messageSubpath the key or subpath identifying the message in the locale
     * @param placeholders a map of placeholders to replace in the message, or null if none
     */
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

    /**
     * Resolves the target player for a command, defaulting to the sender if they are a player.
     *
     * If no target is specified and the sender is not a player (e.g., console), sends an error message and returns null.
     *
     * @param sender the command sender
     * @param targetArg the specified target player, or null to default to the sender
     * @return the resolved target player, or null if not applicable
     */
    private Player getTargetPlayer(CommandSender sender, Player targetArg) {
        if (targetArg != null) return targetArg;
        if (sender instanceof Player) return (Player) sender;

        sendMessageToSender(sender, "error.specify_target", null);
        return null;
    }

    /**
     * Retrieves the active pet for the specified player, sending an error message to the sender if none is active.
     *
     * @param sender the command sender requesting the pet
     * @param target the player whose active pet is to be retrieved
     * @return the active UserPet of the target player, or null if no active pet exists
     */
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


    /**
     * Handles the main /hpet command, displaying the help message to the sender.
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

    /**
     * Sends a help message to the command sender listing available HPET commands.
     *
     * @param sender the command sender to receive the help message
     */
    @FCommand(
        name = "hpet help", 
        permission = "pet.command", 
        description = "Shows help for HPET commands"
    )
    public void onHelpCommand(CommandSender sender) {
        sendHelpMessage(sender);
    }

    /**
     * Sends a formatted help message to the specified command sender, listing all available pet-related commands and their descriptions.
     *
     * @param sender the recipient of the help message
     */
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


    /**
     * Selects a pet of the specified type for the target player.
     *
     * If the pet type exists, assigns it as the active pet for the target player and sends confirmation messages to both the sender and the target. If the pet type does not exist or selection fails, sends an error message to the sender.
     *
     * @param petType the name of the pet type to select
     * @param target the player to assign the pet to; if null, defaults to the sender if they are a player
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

    /**
     * Removes the current active pet of the specified player.
     *
     * If no target player is provided, removes the sender's pet. Sends confirmation messages to both the sender and the affected player.
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

    /**
     * Respawns the current active pet for the specified player.
     *
     * If the sender is the target player, sends a success message to the sender. If the sender is updating another player's pet, sends success messages to both the sender and the target player. If the pet cannot be respawned, sends an error message to the sender.
     *
     * @param target the player whose pet should be respawned; if null, defaults to the sender if the sender is a player
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

    /**
     * Handles the /hpet buy command, allowing a player to purchase a pet of the specified type for themselves or another player.
     *
     * If the specified pet type does not exist, an error message is sent. The actual purchase logic is not yet implemented; a work-in-progress message is shown instead. Success and failure messages are simulated for demonstration purposes.
     *
     * @param petType the name of the pet type to purchase
     * @param target the player who will receive the pet; if null, defaults to the sender if they are a player
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

    /**
     * Adds the specified number of levels to the target player's active pet.
     *
     * If the amount is not positive, sends an error message to the sender. Sends success messages to both the sender and the target player upon successful level addition.
     *
     * @param amount the number of levels to add; must be positive
     * @param target the player whose pet will receive the added levels; if null, defaults to the sender if they are a player
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


    /**
     * Decreases the active pet's level for the specified player by the given amount.
     *
     * If the resulting level would be negative, it is set to zero. Sends appropriate success or error messages to the sender and target player.
     *
     * @param amount the amount to decrease the pet's level by; must be positive
     * @param target the player whose pet level will be decreased; if null, defaults to the sender if they are a player
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


    /**
     * Sets the active pet's level for the specified player.
     *
     * If the level is negative, sends an error message and does not update the pet. Sends confirmation messages to both the sender and the target player as appropriate.
     *
     * @param level the new level to set for the pet; must be non-negative
     * @param target the player whose pet level will be set; if null, defaults to the sender if they are a player
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

    /**
     * Displays the current level of the target player's active pet.
     *
     * If no target is specified, shows the sender's pet level. Sends appropriate messages to both sender and target.
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
