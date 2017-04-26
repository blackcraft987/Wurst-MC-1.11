/*
 * Copyright � 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.special_features;

import net.wurstclient.features.SearchTags;
import net.wurstclient.features.Spf;
import net.wurstclient.utils.MiscUtils;

@SearchTags({"change log", "new features", "wurst update"})
@Spf.Info
public final class ChangelogSpf extends Spf
{
	public ChangelogSpf()
	{
		super("Changelog", "Opens the changelog in your browser.");
	}
	
	@Override
	public String getPrimaryAction()
	{
		return "View Changelog";
	}
	
	@Override
	public void doPrimaryAction()
	{
		MiscUtils.openLink("https://www.wurstclient.net/changelog/");
	}
}
