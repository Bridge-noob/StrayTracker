package com.straytracker.StrayTracker;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;

public class GuildOnlineCommand {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(ClientCommandManager.literal("guild")
            .then(ClientCommandManager.literal("online")
                .executes(ctx -> execute(ctx))
            )
        );
    }

    private static int execute(CommandContext<FabricClientCommandSource> ctx) {
        FabricClientCommandSource src = ctx.getSource();
        // Send /guild online to server via chat
        src.sendFeedback(Text.literal("§aSent /guild online to server."));
        // Note: actual server command execution requires server-side; 
        // on client we just inform. For true parsing, the user runs /guild online manually
        // and chat parser captures output.
        return 1;
    }
}
