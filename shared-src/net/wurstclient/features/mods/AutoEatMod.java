/*
 * Copyright � 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods;

import net.minecraft.block.BlockContainer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.wurstclient.compatibility.WBlock;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.compatibility.WPlayerController;
import net.wurstclient.events.listeners.UpdateListener;
import net.wurstclient.features.Feature;
import net.wurstclient.features.HelpPage;
import net.wurstclient.features.Mod;
import net.wurstclient.features.SearchTags;
import net.wurstclient.features.special_features.YesCheatSpf.BypassLevel;
import net.wurstclient.settings.CheckboxSetting;

@SearchTags({"AutoSoup", "auto eat", "auto soup"})
@HelpPage("Mods/AutoEat")
@Mod.Bypasses
public final class AutoEatMod extends Mod implements UpdateListener
{
	public CheckboxSetting ignoreScreen =
		new CheckboxSetting("Ignore screen", true);
	
	private int oldSlot = -1;
	
	public AutoEatMod()
	{
		super("AutoEat", "Automatically eats food when necessary.");
	}
	
	@Override
	public Feature[] getSeeAlso()
	{
		return new Feature[]{wurst.mods.autoSoupMod};
	}
	
	@Override
	public void initSettings()
	{
		settings.add(ignoreScreen);
	}
	
	@Override
	public void onEnable()
	{
		wurst.events.add(UpdateListener.class, this);
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(UpdateListener.class, this);
		stopIfEating();
	}
	
	@Override
	public void onUpdate()
	{
		if(!shouldEat())
		{
			stopIfEating();
			return;
		}
		
		// search food in hotbar
		int bestSlot = -1;
		float bestSaturation = -1;
		for(int i = 0; i < 9; i++)
		{
			// filter out non-food items
			ItemStack stack =
				WMinecraft.getPlayer().inventory.getStackInSlot(i);
			if(stack == null || !(stack.getItem() instanceof ItemFood))
				continue;
			
			// compare to previously found food
			float saturation =
				((ItemFood)stack.getItem()).getSaturationModifier(stack);
			if(saturation > bestSaturation)
			{
				bestSaturation = saturation;
				bestSlot = i;
			}
		}
		
		// check if any food was found
		if(bestSlot == -1)
		{
			stopIfEating();
			return;
		}
		
		// save old slot
		if(!isEating())
			oldSlot = WMinecraft.getPlayer().inventory.currentItem;
		
		// set slot
		WMinecraft.getPlayer().inventory.currentItem = bestSlot;
		
		// eat food
		mc.gameSettings.keyBindUseItem.pressed = true;
		WPlayerController.processRightClick();
	}
	
	@Override
	public void onYesCheatUpdate(BypassLevel bypassLevel)
	{
		switch(bypassLevel)
		{
			case GHOST_MODE:
			ignoreScreen.lock(() -> false);
			break;
			
			default:
			ignoreScreen.unlock();
			break;
		}
	}
	
	private boolean shouldEat()
	{
		// check hunger
		if(!WMinecraft.getPlayer().canEat(false))
			return false;
		
		// check screen
		if(!ignoreScreen.isChecked() && mc.currentScreen != null)
			return false;
		
		// check for clickable objects
		if(mc.currentScreen == null && mc.objectMouseOver != null)
		{
			// clickable entities
			Entity entity = mc.objectMouseOver.entityHit;
			if(entity instanceof EntityVillager
				|| entity instanceof EntityTameable)
				return false;
			
			// clickable blocks
			if(mc.objectMouseOver.getBlockPos() != null && WBlock.getBlock(
				mc.objectMouseOver.getBlockPos()) instanceof BlockContainer)
				return false;
		}
		
		return true;
	}
	
	public boolean isEating()
	{
		return oldSlot != -1;
	}
	
	private void stopIfEating()
	{
		// check if eating
		if(!isEating())
			return;
		
		// stop eating
		mc.gameSettings.keyBindUseItem.pressed = false;
		
		// reset slot
		WMinecraft.getPlayer().inventory.currentItem = oldSlot;
		oldSlot = -1;
	}
}
