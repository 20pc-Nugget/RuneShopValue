package com.example;

import com.runeshopvalue.RuneShopValuePlugin;
import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class ExamplePluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(RuneShopValuePlugin.class);
		RuneLite.main(args);
	}
}