/*
 * Copyright � 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods;

import org.lwjgl.opengl.GL11;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.AxisAlignedBB;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.events.listeners.RenderListener;
import net.wurstclient.features.Feature;
import net.wurstclient.features.HelpPage;
import net.wurstclient.features.Mod;
import net.wurstclient.features.SearchTags;
import net.wurstclient.utils.RenderUtils;

@SearchTags({"mob esp"})
@HelpPage("Mods/MobESP")
@Mod.Bypasses
public final class MobEspMod extends Mod implements RenderListener
{
	private static final AxisAlignedBB MOB_BOX =
		new AxisAlignedBB(-0.5, 0, -0.5, 0.5, 1, 0.5);
	
	public MobEspMod()
	{
		super("MobESP", "Allows you to see mobs through walls.");
	}
	
	@Override
	public Feature[] getSeeAlso()
	{
		return new Feature[]{wurst.mods.playerEspMod, wurst.mods.itemEspMod,
			wurst.mods.prophuntEspMod};
	}
	
	@Override
	public void onEnable()
	{
		wurst.events.add(RenderListener.class, this);
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(RenderListener.class, this);
	}
	
	@Override
	public void onRender(float partialTicks)
	{
		// GL settings
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		GL11.glLineWidth(2);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		
		GL11.glPushMatrix();
		GL11.glTranslated(-mc.getRenderManager().renderPosX,
			-mc.getRenderManager().renderPosY,
			-mc.getRenderManager().renderPosZ);
		
		// draw boxes
		for(Entity entity : WMinecraft.getWorld().loadedEntityList)
		{
			if(!(entity instanceof EntityLiving))
				continue;
			
			if(!wurst.special.targetSpf.invisibleMobs.isChecked()
				&& entity.isInvisible())
				continue;
			
			GL11.glPushMatrix();
			
			// set color
			float factor =
				WMinecraft.getPlayer().getDistanceToEntity(entity) / 20F;
			if(factor > 2)
				factor = 2;
			GL11.glColor4f(2 - factor, factor, 0, 0.5F);
			
			// set position
			GL11.glTranslated(
				entity.prevPosX
					+ (entity.posX - entity.prevPosX) * partialTicks,
				entity.prevPosY
					+ (entity.posY - entity.prevPosY) * partialTicks,
				entity.prevPosZ
					+ (entity.posZ - entity.prevPosZ) * partialTicks);
			
			// set size
			double boxWidth = entity.width + 0.1;
			double boxHeight = entity.height + 0.1;
			GL11.glScaled(boxWidth, boxHeight, boxWidth);
			
			// draw box
			RenderUtils.drawOutlinedBox(MOB_BOX);
			
			GL11.glPopMatrix();
		}
		
		GL11.glPopMatrix();
		
		// GL resets
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_LINE_SMOOTH);
	}
}
