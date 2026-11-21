package com.spixyyg.modvision;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ModVisionMod implements ModInitializer {
    public static Map<UUID, String> brands = new HashMap<>();

    @Override
    public void onInitialize() {
        ServerPlayNetworking.registerGlobalReceiver(BrandPayload.ID, (payload, context) -> {
            brands.put(context.player().getUuid(), payload.brand());
        });
        CommandRegistrationCallback.EVENT.register(this::registerCommands);
    }

    private void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(CommandManager.literal("modvision")
            .requires(source -> source.hasPermissionLevel(2))
            .then(CommandManager.argument("player", EntityArgumentType.player())
            .executes(context -> {
                ServerPlayerEntity target = EntityArgumentType.getPlayer(context, "player");
                ServerCommandSource source = context.getSource();
                String uuid = target.getUuid().toString();
                boolean isBedrock = uuid.startsWith("00000000");
                String platform = isBedrock ? "Bedrock" : "Java";
                String brand = brands.getOrDefault(target.getUuid(), "Unknown - try again in 5 sec");
                boolean flying = target.getAbilities().flying && !target.isCreative() && !target.isSpectator();

                source.sendFeedback(() -> Text.literal("Player: " + target.getName().getString()), false);
                source.sendFeedback(() -> Text.literal("Playing from: " + platform), false);
                source.sendFeedback(() -> Text.literal("App he is using: " + brand), false);
                source.sendFeedback(() -> Text.literal("Flying in survival: " + (flying ? "YES" : "NO")), false);

                if (brand.toLowerCase().contains("meteor") || brand.toLowerCase().contains("wurst") || brand.toLowerCase().contains("baritone")) {
                    source.sendFeedback(() -> Text.literal("Result: HIGH RISK - Cheat app"), false);
                } else if (flying) {
                    source.sendFeedback(() -> Text.literal("Result: HIGH RISK - Flying"), false);
                } else {
                    source.sendFeedback(() -> Text.literal("Result: Looks normal"), false);
                }
                return 1;
            })));
    }
  }
