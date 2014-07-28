package openperipheral.integration.thermalexpansion;

import net.minecraftforge.common.ForgeDirection;
import openperipheral.api.*;
import cofh.api.energy.IEnergyHandler;

public class AdapterEnergyHandler implements IPeripheralAdapter {

	@Override
	public Class<?> getTargetClass() {
		return IEnergyHandler.class;
	}

	@LuaMethod(description = "Get the energy stored in the machine.", returnType = LuaType.NUMBER,
			args = {
					@Arg(name = "slot", type = LuaType.STRING, description = "The direction you are interested in. (north, south, east, west, up or down)")
			})
	public int getEnergyStored(IEnergyHandler tileEntity, ForgeDirection side) {
		return tileEntity.getEnergyStored(side);
	}

	@LuaMethod(description = "Get the max energy stored in the machine.", returnType = LuaType.NUMBER,
			args = {
					@Arg(name = "slot", type = LuaType.STRING, description = "The direction you are interested in. (north, south, east, west, up or down)")
			})
	public int getMaxEnergyStored(IEnergyHandler tileEntity, ForgeDirection side) {
		return tileEntity.getMaxEnergyStored(side);
	}

}
