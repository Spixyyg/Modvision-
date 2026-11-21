package com.spixyyg.modvision;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record BrandPayload(String brand) implements CustomPayload {
    public static final CustomPayload.Id<BrandPayload> ID = new CustomPayload.Id<>(Identifier.of("minecraft", "brand"));
    public static final PacketCodec<PacketByteBuf, BrandPayload> CODEC = PacketCodec.of(
        (value, buf) -> buf.writeString(value.brand),
        buf -> new BrandPayload(buf.readString())
    );
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
