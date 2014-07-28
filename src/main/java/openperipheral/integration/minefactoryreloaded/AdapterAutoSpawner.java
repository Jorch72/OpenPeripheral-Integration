package openperipheral.integration.minefactoryreloaded;

import openmods.utils.ReflectionHelper;
import openperipheral.api.*;

public class AdapterAutoSpawner implements IPeripheralAdapter {
	private static final Class<?> AUTOSPAWNER_CLASS = ReflectionHelper.getClass(
			"powercrystals.minefactoryreloaded.tile.machine.TileEntityAutoSpawner"
			);

	@Override
	public Class<?> getTargetClass() {
		return AUTOSPAWNER_CLASS;
	}

	@LuaMethod(description = "Get value of spawn exact copy toggle", returnType = LuaType.BOOLEAN)
	public boolean getSpawnExact(Object tileEntityAutoSpawner) {
		return ReflectionHelper.call(tileEntityAutoSpawner, "getSpawnExact");
	}

	@LuaMethod(description = "Set the value of spawn exact copy", returnType = LuaType.VOID,
			args = {
					@Arg(name = "spawnExact", type = LuaType.BOOLEAN, description = "boolean: Spawn Exact Copy?")
			})
	public void setSpawnExact(Object tileEntityAutoSpawner, boolean spawnExact) {
		ReflectionHelper.call(tileEntityAutoSpawner, "setSpawnExact", ReflectionHelper.primitive(spawnExact));
	}

}
