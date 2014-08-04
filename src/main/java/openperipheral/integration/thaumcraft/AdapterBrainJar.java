package openperipheral.integration.thaumcraft;

import openmods.utils.FieldAccessHelpers;
import openmods.utils.ReflectionHelper;
import openperipheral.api.IPeripheralAdapter;
import openperipheral.api.LuaCallable;
import openperipheral.api.LuaType;

public class AdapterBrainJar implements IPeripheralAdapter {
	private static final Class<?> TILE_JAR_BRAIN_CLASS = ReflectionHelper.getClass("thaumcraft.common.tiles.TileJarBrain");

	@Override
	public Class<?> getTargetClass() {
		return TILE_JAR_BRAIN_CLASS;
	}

	@LuaCallable(returnTypes = LuaType.NUMBER, description = "Returns the amount of XP stored in the Brain in a Jar")
	public int getXP(Object target) {
		return FieldAccessHelpers.getIntField(TILE_JAR_BRAIN_CLASS, target, "xp");
	}
}
