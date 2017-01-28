/*
 * Copyright � 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.features.mods;

import org.lwjgl.opengl.GL11;

import net.minecraft.util.math.BlockPos;
import tk.wurst_client.events.listeners.RenderListener;
import tk.wurst_client.features.Feature;
import tk.wurst_client.utils.RenderUtils;

@Mod.Info(description = "Renders the Nuker animation when you mine a block.",
	name = "Overlay",
	help = "Mods/Overlay")
@Mod.Bypasses
public class OverlayMod extends Mod implements RenderListener
{
	@Override
	public Feature[] getSeeAlso()
	{
		return new Feature[]{wurst.mods.nukerMod};
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
		// check hitResult
		if(mc.objectMouseOver == null)
			return;
		
		// check position
		BlockPos pos = mc.objectMouseOver.getBlockPos();
		if(pos == null)
			return;
		
		// check if hitting block
		if(!mc.playerController.getIsHittingBlock())
			return;
		
		// GL settings
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		GL11.glLineWidth(2);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		
		GL11.glPushMatrix();
		GL11.glTranslated(-mc.getRenderManager().renderPosX,
			-mc.getRenderManager().renderPosY,
			-mc.getRenderManager().renderPosZ);
		
		// set position
		GL11.glTranslated(pos.getX(), pos.getY(), pos.getZ());
		
		// set size
		GL11.glTranslated(0.5, 0.5, 0.5);
		double v = mc.playerController.curBlockDamageMP;
		GL11.glScaled(v, v, v);
		GL11.glTranslated(-0.5, -0.5, -0.5);
		
		// get color
		float red = mc.playerController.curBlockDamageMP * 2F;
		float green = 2 - red;
		
		// draw box
		GL11.glColor4f(red, green, 0, 0.25F);
		RenderUtils.drawSolidBox();
		GL11.glColor4f(red, green, 0, 0.5F);
		RenderUtils.drawOutlinedBox();
		
		GL11.glPopMatrix();
		
		// GL resets
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_LINE_SMOOTH);
	}
}
