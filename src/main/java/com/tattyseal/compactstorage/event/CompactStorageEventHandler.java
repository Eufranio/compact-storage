package com.tattyseal.compactstorage.event;

import com.tattyseal.compactstorage.ConfigurationHandler;
import com.tattyseal.compactstorage.block.BlockBarrel;
import com.tattyseal.compactstorage.block.BlockChest;

import net.minecraft.block.Block;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.relauncher.Side;

public class CompactStorageEventHandler
{
	@SubscribeEvent
	public void onPlayerInteract(PlayerInteractEvent.RightClickBlock event)
	{
		Block block = event.getEntity().world.getBlockState(event.getPos()).getBlock();

		if(block instanceof BlockChest || block instanceof BlockBarrel)
		{
			event.setUseBlock(Result.ALLOW);
		}
	}

	@SubscribeEvent
	public void connect(PlayerEvent.PlayerLoggedInEvent e)
	{
		if(FMLCommonHandler.instance().getSide().equals(Side.CLIENT) && ConfigurationHandler.newFeatures)
		{
			e.player.sendMessage(new TextComponentString("What's new in CompactStorage 3.0?"));
			e.player.sendMessage(new TextComponentString(" - New barrels and drums!."));
			e.player.sendMessage(new TextComponentString("     - Barrels allow 64 stacks of one item to be stored in them, right click to insert and punch to remove items!"));
			e.player.sendMessage(new TextComponentString("     - Drums allow 32,000mB of storage, these can also be interfaced with pipes etc!"));
			e.player.sendMessage(new TextComponentString("This message will only be displayed once and can be re-enabled in the configuration file."));
			ConfigurationHandler.disableMessage();
		}
	}
}
