package com.mamewo.malarm_test;

import com.jayway.android.robotium.solo.Solo;

public class TestLandscapeUI
	extends TestPortraitUI
{
	@Override
	public void setUp()
			throws Exception
	{
		super.setUp();
		solo_.setActivityOrientation(Solo.LANDSCAPE);
	}
}
