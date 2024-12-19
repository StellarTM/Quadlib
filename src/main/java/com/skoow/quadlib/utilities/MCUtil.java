package com.skoow.quadlib.utilities;

import com.skoow.quadlib.utilities.func.Cons;
import com.skoow.quadlib.utilities.struct.Seq;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.server.ServerLifecycleHooks;

public class MCUtil {
    public static Minecraft mc() {
        return Minecraft.getInstance();
    }
    public static MinecraftServer server() {
        return ServerLifecycleHooks.getCurrentServer();
    }
    public static ServerLevel overworld() {
        return server().overworld();
    }
    public static LocalPlayer player() {
        return mc().player;
    }
    public static Seq<ServerPlayer> players() {
        return Seq.with(server().getPlayerList().getPlayers());
    }
    public static void player(Cons<ServerPlayer> cons) {
        players().each(cons);
    }
    public static CommandSourceStack cmdSource() {
        return server().createCommandSourceStack();
    }

    public static void clearItem(Player player, ItemStack stack) {
        Item item = stack.getItem();
        int count = stack.getCount();
        Inventory inv = player.getInventory();
        inv.clearOrCountMatchingItems((i) ->
                i.getItem().equals(item), count, player.inventoryMenu.getCraftSlots());
    }
    public static void giveItem(Player player, ItemStack stack) {
        ItemStack outputCopy = stack.copy();
        int k = outputCopy.getCount();
        int i = outputCopy.getMaxStackSize();

        while(k > 0) {
            int l = Math.min(i, k);
            k -= l;
            ItemStack itemstack1 = outputCopy.copy();
            itemstack1.setCount(l);
            boolean flag = player.getInventory().add(itemstack1);
            if (flag && itemstack1.isEmpty()) {
                itemstack1.setCount(1);
                ItemEntity itementity1 = player.drop(itemstack1, false);
                if (itementity1 != null) {
                    itementity1.makeFakeItem();
                }

                player.level().playSound((Player)null, player.getX(), player.getY(), player.getZ(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2F, ((player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);
                player.containerMenu.broadcastChanges();
            } else {
                ItemEntity itementity = player.drop(itemstack1, false);
                if (itementity != null) {
                    itementity.setNoPickUpDelay();
                    itementity.setTarget(player.getUUID());
                }
            }
        }
    }
}
