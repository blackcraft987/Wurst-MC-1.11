/*
 * Copyright � 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods;

import net.minecraft.network.play.client.CPacketPlayer;
import net.wurstclient.compatibility.WConnection;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.events.listeners.UpdateListener;
import net.wurstclient.features.HelpPage;
import net.wurstclient.features.Mod;

@HelpPage("Mods/Headless")
@Mod.Bypasses(ghostMode = false, latestNCP = false, olderNCP = false)
public final class HeadlessMod extends Mod implements UpdateListener
{
	public HeadlessMod()
	{
		super("Headless",
			"While this is active, other people will think you are\n"
				+ "headless. Looks hilarious!");
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
	}
	
	@Override
	public void onUpdate()
	{
		WConnection.sendPacket(
			new CPacketPlayer.Rotation(WMinecraft.getPlayer().rotationYaw, 180F,
				WMinecraft.getPlayer().onGround));
	}
}
