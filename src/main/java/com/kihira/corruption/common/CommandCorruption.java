package com.kihira.corruption.common;

import com.kihira.corruption.common.corruption.CorruptionRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

public class CommandCorruption extends CommandBase {

    @Override
    public String getCommandName() {
        return "corruption";
    }

    @Override
    public String getCommandUsage(ICommandSender commandSender) {
        return "Pie!";
    }

    @Override
    public void processCommand(ICommandSender commandSender, String[] args) {
        if (args != null ) {
            if (args.length >= 2 && args[0].equals("set")) {
                int corr;
                EntityPlayer player = FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().getPlayerForUsername(args.length >= 3 ? args[2] : commandSender.getCommandSenderName());
                if (player != null) {
                    if (args[1].startsWith("+")) {
                        corr = Integer.valueOf(args[1].substring(1)) + CorruptionDataHelper.getCorruptionForPlayer(player);
                    }
                    else if (args[1].startsWith("-")) {
                        corr = Integer.valueOf(args[1].substring(1)) - CorruptionDataHelper.getCorruptionForPlayer(player);
                    }
                    else {
                        corr = Integer.valueOf(args[1]);
                    }
                    CorruptionDataHelper.setCorruptionForPlayer(player, corr);
                    notifyAdmins(commandSender, "%s has set corruption for %s to %s", commandSender.getCommandSenderName(), player.getCommandSenderName(), corr);
                }
            }
            else if (args.length >= 2 && args[0].equals("effect")) {
                if (CorruptionRegistry.corruptionHashMap.containsKey(args[1])) {
                    EntityPlayer player = commandSender.getEntityWorld().getPlayerEntityByName(commandSender.getCommandSenderName());
                    CorruptionDataHelper.addCorruptionEffectForPlayer(player, args[1]);
                    notifyAdmins(commandSender, "Effect applied!");
                }
            }
            else if (args.length >= 1 && args[0].equals("disable")) {
                EntityPlayer player = commandSender.getEntityWorld().getPlayerEntityByName(args.length >= 2 ? args[1] : commandSender.getCommandSenderName());
                CorruptionDataHelper.setCanBeCorrupted(player, false);
                notifyAdmins(commandSender, "Corruption disabled!");
            }
            else if (args.length >= 1 && args[0].equals("enable")) {
                EntityPlayer player = commandSender.getEntityWorld().getPlayerEntityByName(args.length >= 2 ? args[1] : commandSender.getCommandSenderName());
                CorruptionDataHelper.setCanBeCorrupted(player, true);
                notifyAdmins(commandSender, "Corruption enabled!");
            }
            else if (args.length >= 1 && args[0].equals("get")) {
                EntityPlayer player = commandSender.getEntityWorld().getPlayerEntityByName(args.length >= 2 ? args[1] : commandSender.getCommandSenderName());
                notifyAdmins(commandSender, String.valueOf(CorruptionDataHelper.getCorruptionForPlayer(player)));
            }
            else if (args.length >= 2 && args[0].equals("clear")) {
                EntityPlayer player = commandSender.getEntityWorld().getPlayerEntityByName(args.length >= 2 ? args[1] : commandSender.getCommandSenderName());

                CorruptionDataHelper.removeCorruptionEffectForPlayer(player, args[2]);
                notifyAdmins(commandSender, "Cleared corruption!");
            }
        }
        else throw new CommandException("Not enough args!");
    }
}
