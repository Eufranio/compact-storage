package com.tattyseal.compactstorage.client.gui;

import com.tattyseal.compactstorage.inventory.IChest;
import com.tattyseal.compactstorage.inventory.InventoryBackpack;
import com.tattyseal.compactstorage.util.UsefulFunctions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

import java.io.IOException;

/**
 * Created by Toby on 09/11/2014.
 */
public class GuiChest extends GuiContainer
{
    public World world;
    public EntityPlayer player;
    public BlockPos pos;

    private int invX;
    private int invY;

    public IChest chest;
    
    private KeyBinding[] HOTBAR;
    private int backpackSlot;

    
    public GuiChest(Container container, IChest chest, World world, EntityPlayer player, BlockPos pos)
    {
        super(container);

        this.world = world;
        this.player = player;
        this.pos = pos;

        this.chest = chest;
        
        this.HOTBAR = Minecraft.getMinecraft().gameSettings.keyBindsHotbar;
        
        backpackSlot = -1;
        if(chest instanceof InventoryBackpack)
	    {
        	backpackSlot = player.inventory.currentItem;
	    }

        this.invX = chest.getInvX();
        this.invY = chest.getInvY();

        this.xSize = 7 + (Math.max(9, invX) * 18) + 7;
        this.ySize = 15 + (invY * 18) + 13 + 54 + 4 + 18 + 7;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    public void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
    	super.drawGuiContainerForegroundLayer(mouseX, mouseY);

    	if (chest.hasCustomName()) {
    	    this.fontRenderer.drawString(I18n.format(chest.getName()) + " (" + invX + "x" + invY + ")", 8, 6, 4210752);
        } else {
            this.fontRenderer.drawString("Chest (" + invX + "x" + invY + ")", 8, 6, 4210752);
        }
    	

        this.fontRenderer.drawString("Inventory", 8, 15 + (invY * 18) + 5, 4210752);
    }

    @Override
    public void drawGuiContainerBackgroundLayer(float i, int j, int k)
    {    	
    	GL11.glPushMatrix();
    	
    	GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glColor3f(1, 1, 1);
        
        UsefulFunctions.renderChestBackground(this, guiLeft, guiTop, invX, invY);

        UsefulFunctions.renderSlots(guiLeft + 7 + ((Math.max(9, invX) * 18) / 2) - (invX * 18) / 2, guiTop + 17, invX, invY);
        UsefulFunctions.renderSlots(guiLeft + 7 + ((((Math.max(9, invX)) * 18) / 2) - ((9 * 18) / 2)), guiTop + 17 + (invY * 18) + 13, 9, 3);
        UsefulFunctions.renderSlots(guiLeft + 7 + ((((Math.max(9, invX)) * 18) / 2) - ((9 * 18) / 2)), guiTop + 17 + (invY * 18) + 13 + 54 + 4, 9, 1);

        GL11.glPopMatrix();
    }
    
    @Override
    protected void keyTyped(char c, int id)  throws IOException
    {
    	if (backpackSlot != -1 && HOTBAR[backpackSlot].getKeyCode() == id) 
    	{
    		return;
    	}
    	
    	super.keyTyped(c, id);
    }

    @Override
    public void onGuiClosed()
    {
        super.onGuiClosed();
    }
}
