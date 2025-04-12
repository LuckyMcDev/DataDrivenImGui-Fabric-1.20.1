package de.lucky.datadrivenimgui.util;

import net.minecraft.client.MinecraftClient;

public class CommandUtils {

    public static void runCommand(String command) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null && client.player.networkHandler != null) {
            if (command.startsWith("/")) {
                command = command.substring(1); // Remove the leading '/'
            }
            client.player.networkHandler.sendChatCommand(command);
        } else {
            System.err.println("Cannot run command, player or network handler is null");
        }
    }
}
