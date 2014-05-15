package com.kihira.corruption.common.network;

import com.kihira.corruption.Corruption;
import com.kihira.corruption.common.CorruptionDataHelper;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

public class PacketEventHandler {

    private enum Packet {
        CORRUPTION(0);

        private final int id;

        private Packet(int id) {
            this.id = id;
        }

        public int getID() {
            return this.id;
        }
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onClientPacket(FMLNetworkEvent.ClientCustomPacketEvent e) {
        ByteBuf payload = e.packet.payload();
        if (payload.readInt() == Packet.CORRUPTION.getID()) {
            EntityPlayer player = Minecraft.getMinecraft().theWorld.getPlayerEntityByName(ByteBufUtils.readUTF8String(payload));
            if (player != null) {
                int newCorr = payload.readInt();
                int oldCorr = CorruptionDataHelper.getCorruptionForPlayer(player);
                CorruptionDataHelper.setCorruptionForPlayer(player, newCorr);
                Corruption.logger.info(I18n.format("Updated %s corruption to %d", player.getCommandSenderName(), newCorr));

                Random rand = new Random();

                for (int i = oldCorr; i <= newCorr; i++) {
                    EntityClientPlayerMP clientPlayerMP = (EntityClientPlayerMP) player;
                    ((EntityClientPlayerMP) player).getTextureSkin();
                    ThreadDownloadImageData imageData = clientPlayerMP.getTextureSkin();
                    BufferedImage bufferedImage = ObfuscationReflectionHelper.getPrivateValue(ThreadDownloadImageData.class, imageData, "bufferedImage");
                    if (bufferedImage != null) {
                        int x = rand.nextInt(bufferedImage.getWidth());
                        int y = rand.nextInt(bufferedImage.getHeight());
                        //Color color = new Color(bufferedImage.getRGB(x, y));
                        Color color = new Color(1, 1, 1);
                        color.darker();
                        bufferedImage.setRGB(x, y, color.getRGB());
                        imageData.setBufferedImage(bufferedImage);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    @SideOnly(Side.SERVER)
    public void onServerPacket(FMLNetworkEvent.ServerCustomPacketEvent e) {
        ByteBuf payload = e.packet.payload();
        if (payload.readInt() == Packet.CORRUPTION.getID()) {
            Corruption.logger.warn("Received a corruption update server side, this isn't supposed to happen!");
        }
    }

    public static FMLProxyPacket getCorruptionUpdatePacket(String playerName, int newCorruption) {
        ByteBuf byteBuf = Unpooled.buffer();

        byteBuf.writeInt(Packet.CORRUPTION.getID());
        ByteBufUtils.writeUTF8String(byteBuf, playerName);
        byteBuf.writeInt(newCorruption);

        return new FMLProxyPacket(byteBuf, "corruption");
    }

}
