package openperipheral.addons.sensors;

import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import openperipheral.addons.Config;
import openperipheral.addons.OpenPeripheralAddons.Blocks;
import openperipheral.addons.OpenPeripheralAddons.Icons;
import openperipheral.addons.utils.CCUtils;
import openperipheral.api.ApiAccess;
import openperipheral.api.IAdapterFactory;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.turtle.*;

public class TurtleUpgradeSensor implements ITurtleUpgrade {

	private static class TurtleSensorEnvironment implements ISensorEnvironment {

		private ITurtleAccess turtle;

		public TurtleSensorEnvironment(ITurtleAccess turtle) {
			this.turtle = turtle;
		}

		@Override
		public boolean isTurtle() {
			return true;
		}

		@Override
		public Vec3 getLocation() {
			return turtle.getVisualPosition(0);
		}

		@Override
		public World getWorld() {
			return turtle.getWorld();
		}

		@Override
		public int getSensorRange() {
			final World world = getWorld();
			return (world.isRaining() && world.isThundering())? Config.sensorRangeInStorm : Config.sensorRange;
		}

		@Override
		public boolean isValid() {
			return CCUtils.isTurtleValid(turtle);
		}

	}

	@Override
	public int getUpgradeID() {
		return 180;
	}

	@Override
	public String getAdjective() {
		return StatCollector.translateToLocal("openperipheral.turtle.sensor.adjective");
	}

	@Override
	public TurtleUpgradeType getType() {
		return TurtleUpgradeType.Peripheral;
	}

	@Override
	public ItemStack getCraftingItem() {
		return new ItemStack(Blocks.sensor);
	}

	@Override
	public IPeripheral createPeripheral(ITurtleAccess turtle, TurtleSide side) {
		return ApiAccess.getApi(IAdapterFactory.class).createPeripheral(new TurtleSensorEnvironment(turtle));
	}

	@Override
	public TurtleCommandResult useTool(ITurtleAccess turtle, TurtleSide side, TurtleVerb verb, int direction) {
		return null;
	}

	@Override
	public Icon getIcon(ITurtleAccess turtle, TurtleSide side) {
		return Icons.sensorTurtle;
	}

	@Override
	public void update(ITurtleAccess turtle, TurtleSide side) {}

}
