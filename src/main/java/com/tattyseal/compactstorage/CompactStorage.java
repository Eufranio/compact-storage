package com.tattyseal.compactstorage;

import com.google.common.collect.Lists;
import com.tattyseal.compactstorage.block.BlockChest;
import com.tattyseal.compactstorage.block.BlockChestBuilder;
import com.tattyseal.compactstorage.command.CommandCompactStorage;
import com.tattyseal.compactstorage.compat.ICompat;
import com.tattyseal.compactstorage.creativetabs.CreativeTabCompactStorage;
import com.tattyseal.compactstorage.event.RightClickHandler;
import com.tattyseal.compactstorage.item.ItemBackpack;
import com.tattyseal.compactstorage.item.ItemBlockChest;
import com.tattyseal.compactstorage.network.handler.C01HandlerUpdateBuilder;
import com.tattyseal.compactstorage.network.handler.C02HandlerCraftChest;
import com.tattyseal.compactstorage.network.packet.C01PacketUpdateBuilder;
import com.tattyseal.compactstorage.network.packet.C02PacketCraftChest;
import com.tattyseal.compactstorage.proxy.IProxy;
import com.tattyseal.compactstorage.tileentity.TileEntityChest;
import com.tattyseal.compactstorage.tileentity.TileEntityChestBuilder;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * Created by Toby on 06/11/2014.
 */
@Mod(modid = CompactStorage.ID, name = CompactStorage.NAME, version = CompactStorage.VERSION, guiFactory = "com.tattyseal.compactstorage.client.gui.factory.CompactStorageGuiFactory")
public class CompactStorage
{
    @Mod.Instance(CompactStorage.ID)
    public static CompactStorage instance;
    
    public static List<ICompat> compat;

    @SidedProxy(clientSide = CompactStorage.CLIENT_PROXY, serverSide = CompactStorage.SERVER_PROXY, modId = CompactStorage.ID)
    public static IProxy proxy;

    public static CreativeTabs tabCS;

    public static final Logger logger = LogManager.getLogger("CompactStorage");
    public static boolean deobf;

    public SimpleNetworkWrapper wrapper;
    
    public static final String ID = "compactstorage";
    public static final String NAME = "CompactStorage";
    public static final String VERSION = "2.2";

    public static final String CLIENT_PROXY = "com.tattyseal.compactstorage.proxy.ClientProxy";
    public static final String SERVER_PROXY = "com.tattyseal.compactstorage.proxy.ServerProxy";
    
    public static Block chest;
    public static Block chestBuilder;

    public static ItemBlockChest ibChest;
    
    public static Item backpack;
    
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
    	compat = Lists.newArrayList();

        OreDictionary.registerOre("barsIron", Blocks.IRON_BARS);
        OreDictionary.registerOre("blockChest", Blocks.CHEST);
        OreDictionary.registerOre("itemClay", Items.CLAY_BALL);

        OreDictionary.registerOre("string", Items.STRING);
        OreDictionary.registerOre("wool", Blocks.WOOL);
    	
        tabCS = new CreativeTabCompactStorage();
        
        wrapper = NetworkRegistry.INSTANCE.newSimpleChannel(CompactStorage.ID);
        wrapper.registerMessage(C01HandlerUpdateBuilder.class, C01PacketUpdateBuilder.class, 0, Side.SERVER);
        wrapper.registerMessage(C02HandlerCraftChest.class, C02PacketCraftChest.class, 1, Side.SERVER);

        chest = new BlockChest();
        chest.setRegistryName("compactChest");
        GameRegistry.register(chest);

        ibChest = new ItemBlockChest(chest);
        ibChest.setRegistryName("compactChest");
        GameRegistry.register(ibChest);

        GameRegistry.registerTileEntity(TileEntityChest.class, "tileChest");
        
        chestBuilder = new BlockChestBuilder();
        chestBuilder.setRegistryName("chestBuilder");
        GameRegistry.register(chestBuilder);
        GameRegistry.registerTileEntity(TileEntityChestBuilder.class, "tileChestBuilder");

        ItemBlock ibChestBuilder = new ItemBlock(chestBuilder);
        ibChestBuilder.setRegistryName("chestBuilder");
        ibChestBuilder.setCreativeTab(tabCS);
        GameRegistry.register(ibChestBuilder);


    
        backpack = new ItemBackpack();
        backpack.setRegistryName("backpack");
        GameRegistry.register(backpack);
        
        ConfigurationHandler.configFile = event.getSuggestedConfigurationFile();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
    	for(ICompat icompat : compat)
    	{
    		String modid = icompat.modid();
    		
    		logger.info("Found compatibility for " + modid + " attempting load!");
    		
    		if(Loader.isModLoaded(modid))
    		{
    			try
    			{
    				icompat.registerCompat();
    			}
    			catch(Exception e)
    			{
    				logger.error("Exception " + e.getClass().getName() + " while loading compatibility for " + modid + ".");
    				continue;
    			}
    			
        		logger.info("Loaded compatability for " + modid + ".");
    		}
    		else
    		{
    			logger.warn("Compatability for " + modid + " cannot be loaded as it depends on the mod being installed.");
    		}
    	}

        proxy.registerRenderers();
        MinecraftForge.EVENT_BUS.register(new RightClickHandler());
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        NetworkRegistry.INSTANCE.registerGuiHandler(instance, new GuiHandler());

        GameRegistry.addRecipe(new ItemStack(chestBuilder, 1), "ILI", "ICI", "ILI", 'I', new ItemStack(Items.IRON_INGOT, 1), 'C', new ItemStack(Blocks.CHEST, 1), 'L', new ItemStack(Blocks.LEVER, 1));
        GameRegistry.addRecipe(new ItemStack(chest, 1), "III", "GCG", "III", 'I', new ItemStack(Items.IRON_INGOT, 1), 'G', new ItemStack(Blocks.GLASS_PANE, 1), 'C', new ItemStack(Blocks.CHEST, 1));
        GameRegistry.addRecipe(new ItemStack(backpack, 1), "III", "GCG", "III", 'I', new ItemStack(Blocks.WOOL, 1, OreDictionary.WILDCARD_VALUE), 'G', new ItemStack(Items.STRING, 1), 'C', new ItemStack(Blocks.CHEST, 1));
        ConfigurationHandler.init();
    }
    
    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event)
    {
        event.registerServerCommand(new CommandCompactStorage());
    }
}
