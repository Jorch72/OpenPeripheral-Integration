package openperipheral.integration.ic2;

import ic2.api.energy.tile.IEnergySource;
import openperipheral.api.IPeripheralAdapter;
import openperipheral.api.LuaMethod;
import openperipheral.api.LuaType;

public class AdapterEnergySource implements IPeripheralAdapter {

	@Override
	public Class<?> getTargetClass() {
		return IEnergySource.class;
	}

	@LuaMethod(onTick = false, description = "Get the EU output", returnType = LuaType.NUMBER)
	public double getOfferedEnergy(IEnergySource source) {
		return source.getOfferedEnergy();
	}

}
