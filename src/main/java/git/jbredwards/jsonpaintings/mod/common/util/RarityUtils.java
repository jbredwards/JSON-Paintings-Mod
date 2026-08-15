/*
 * Copyright (c) 2026. jbredwards
 * All rights reserved.
 */

package git.jbredwards.jsonpaintings.mod.common.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.EnumRarity;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.IRarity;
import net.minecraftforge.fml.common.network.ByteBufUtils;

import javax.annotation.Nonnull;
import java.util.Arrays;

/**
 * Utilities for {@link IRarity}.
 * @author jbred
 *
 */
public final class RarityUtils
{
    @Nonnull
    public static IRarity parse(@Nonnull final JsonElement rarity) {
        // built-in rarity value
        if(rarity.isJsonPrimitive()) {
            @Nonnull final String name = rarity.getAsString();
            return Arrays.stream(EnumRarity.values()).filter(enumRarity -> name.equalsIgnoreCase(enumRarity.getName()))
                    .findFirst().orElseThrow(() -> new JsonParseException("Could not get rarity from " + name));
        }

        // custom rarity value
        return of(JsonUtils.getString(JsonUtils.getJsonObject(rarity, "rarity"), "name"), JSONHandler.getFormatColor(rarity.getAsJsonObject()));
    }

    @Nonnull
    public static IRarity of(@Nonnull final String name, @Nonnull final TextFormatting color) {
        return new IRarity() {
            @Nonnull
            @Override
            public TextFormatting getColor() {
                return color;
            }

            @Nonnull
            @Override
            public String getName() {
                return name;
            }
        };
    }

    @Nonnull
    public static IRarity decode(@Nonnull final ByteBuf from) {
        if(from.readBoolean()) return EnumRarity.values()[ByteBufUtils.readVarInt(from, 5)];
        else return of(ByteBufUtils.readUTF8String(from), TextFormatting.values()[ByteBufUtils.readVarInt(from, 5)]);
    }

    public static void encode(@Nonnull final ByteBuf to, @Nonnull final IRarity rarity) {
        if(rarity instanceof EnumRarity) ByteBufUtils.writeVarInt(to.writeBoolean(true), ((EnumRarity)rarity).ordinal(), 5);
        else {
            ByteBufUtils.writeUTF8String(to.writeBoolean(false), rarity.getName());
            ByteBufUtils.writeVarInt(to, rarity.getColor().ordinal(), 5);
        }
    }
}
