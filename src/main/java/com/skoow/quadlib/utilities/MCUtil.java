package com.skoow.quadlib.utilities;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.skoow.quadlib.utilities.func.Cons;
import com.skoow.quadlib.utilities.struct.Seq;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.nbt.*;
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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
    public static Object parse(Tag obj) {
        if(obj instanceof ByteTag b) return b.getAsByte();
        if(obj instanceof ShortTag s) return s.getAsShort();
        if(obj instanceof IntTag i) return i.getAsInt();
        if(obj instanceof LongTag l) return l.getAsLong();
        if(obj instanceof FloatTag f) return f.getAsFloat();
        if(obj instanceof DoubleTag d) return d.getAsDouble();
        if(obj instanceof ByteArrayTag b) return b.getAsByteArray();
        if(obj instanceof StringTag s) return s.getAsString();
        if(obj instanceof ListTag l) {
            List<Object> list = new ArrayList<>();
            for (Tag tag : l)
                list.add(parse(tag));
            return list;
        }
        if(obj instanceof CompoundTag nbt) {
            if(nbt.contains("PARSER_CHECK")) {
                String type = nbt.getString("PARSER_CHECK");
                if(type.equals("boolean")) return nbt.getBoolean("value");
            }
            Map<String,Object> map = new LinkedHashMap<>();
            for (String k : nbt.getAllKeys())
                map.put(k,parse(nbt.get(k)));
            return map;
        }
        try {
            return TagParser.parseTag(obj.toString());
        } catch (CommandSyntaxException e) {
            return null;
        }
    }
    public static Tag parse(Object obj) {
        if(obj instanceof Byte b) return ByteTag.valueOf(b);
        else if(obj instanceof Short s) return ShortTag.valueOf(s);
        else if(obj instanceof Integer i) return IntTag.valueOf(i);
        else if(obj instanceof Long l) return LongTag.valueOf(l);
        else if(obj instanceof Float f) return FloatTag.valueOf(f);
        else if(obj instanceof Double d) return DoubleTag.valueOf(d);
        else if(obj instanceof byte[] bs) return new ByteArrayTag(bs);
        else if(obj instanceof String str) return StringTag.valueOf(str);
        else if(obj instanceof Boolean bool) {
            CompoundTag wrap = new CompoundTag();
            wrap.putString("PARSER_CHECK","boolean");
            wrap.putBoolean("value",bool);
            return wrap;
        }
        else if(obj instanceof List<?> l) {
            ListTag tag = new ListTag();
            for (Object o : l)
                tag.add(parse(o));
            return tag;
        }
        else if (obj instanceof Object[] os){
            ListTag tag = new ListTag();
            for (Object o : os)
                tag.add(parse(o));
            return tag;
        }
        else if (obj instanceof Map<?,?> map) {
            CompoundTag tag = new CompoundTag();
            map.forEach((k,v) -> tag.put(String.valueOf(k),parse(v)));
            return tag;
        }
        return EndTag.INSTANCE;
    }
    public static void put(CompoundTag nbt, String key, Object obj) {
        nbt.put(key,parse(obj));
    }
}
