package com.skoow.quadlib.utilities.file;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

public class TypeAdapters {
    public static JsonDeserializer<ItemStack> itemStackDeserializer() {
        return (json, typeOfT, context) -> {
            JsonObject object = json.getAsJsonObject();
            ResourceLocation itemLocation = context.deserialize(object.get("item"), ResourceLocation.class);
            CompoundTag nbt = new CompoundTag();
            int itemCount = 1;
            if(object.has("count")) itemCount = context.deserialize(object.get("count"),Integer.class);
            if(object.has("nbt")) {
                String nbtObject = object.get("nbt").toString();
                try {
                    nbt = TagParser.parseTag(nbtObject);
                } catch (CommandSyntaxException e) {
                    throw new RuntimeException(e);
                }

            }
            Item item = ForgeRegistries.ITEMS.getValue(itemLocation);
            ItemStack stack = new ItemStack(item,itemCount);
            stack.setTag(nbt);
            return stack;
        };
    }
    public static JsonDeserializer<ResourceLocation> resourceLocationDeserializer() {
        return (json, typeOfT, context) -> {
            String id = json.getAsString();
            return new ResourceLocation(id);
        };
    }

}
