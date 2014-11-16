package openperipheral.integration.enderstorage;

import net.minecraft.tileentity.TileEntity;
import openmods.reflection.*;
import openmods.reflection.MethodAccess.Function1;
import openmods.utils.ColorUtils;
import openmods.utils.ColorUtils.ColorMeta;
import openperipheral.api.*;

import com.google.common.base.Preconditions;

public class AdapterFrequencyOwner implements IPeripheralAdapter {
	private final Class<?> CLASS = ReflectionHelper.getClass("codechicken.enderstorage.common.TileFrequencyOwner");

	private final FieldAccess<Integer> FREQ = FieldAccess.create(CLASS, "freq");

	private final Function1<Void, Integer> SET_FREQ = MethodAccess.create(void.class, CLASS, int.class, "setFreq");

	@Override
	public Class<?> getTargetClass() {
		return CLASS;
	}

	@Override
	public String getSourceId() {
		return "ender_frequency";
	}

	@Asynchronous
	@Alias("getColours")
	@LuaCallable(returnTypes = { LuaReturnType.NUMBER, LuaReturnType.NUMBER, LuaReturnType.NUMBER },
			description = "Get the colours active on this chest or tank")
	public IMultiReturn getColors(TileEntity frequencyOwner) {
		int frequency = FREQ.get(frequencyOwner);
		// return a map of the frequency in ComputerCraft colour format
		return MultiReturn.wrap(
				1 << (frequency >> 8 & 0xF),
				1 << (frequency >> 4 & 0xF),
				1 << (frequency >> 0 & 0xF));
	}

	@Asynchronous
	@LuaCallable(returnTypes = { LuaReturnType.STRING, LuaReturnType.STRING, LuaReturnType.STRING },
			description = "Get the colours active on this chest or tank")
	public IMultiReturn getColorNames(TileEntity frequencyOwner) {
		int frequency = FREQ.get(frequencyOwner);
		return MultiReturn.wrap(
				colorToName(frequency >> 8 & 0xF),
				colorToName(frequency >> 4 & 0xF),
				colorToName(frequency >> 0 & 0xF));
	}

	@Alias("setColours")
	@LuaCallable(description = "Set the frequency of this chest or tank")
	public void setColors(TileEntity frequencyOwner,
			@Arg(name = "color_left", description = "The first color") int colorLeft,
			@Arg(name = "color_middle", description = "The second color") int colorMiddle,
			@Arg(name = "color_right", description = "The third color") int colorRight) {
		// transform the ComputerCraft colours (2^n) into the range 0-15 And
		int high = parseComputerCraftColor(colorLeft);
		int med = parseComputerCraftColor(colorMiddle);
		int low = parseComputerCraftColor(colorRight);

		int frequency = ((high & 0xF) << 8) + ((med & 0xF) << 4) + (low & 0xF);
		setFreq(frequencyOwner, frequency);
	}

	@LuaCallable(description = "Set the frequency of this chest or tank")
	public void setColorNames(TileEntity frequencyOwner,
			@Arg(name = "color_left") String colorLeft,
			@Arg(name = "color_middle") String colorMiddle,
			@Arg(name = "color_right") String colorRight) {

		int high = parseColorName(colorLeft);
		int med = parseColorName(colorMiddle);
		int low = parseColorName(colorRight);
		// convert the three colours into a single colour
		int frequency = ((high & 0xF) << 8) + ((med & 0xF) << 4) + (low & 0xF);
		setFreq(frequencyOwner, frequency);
	}

	@Asynchronous
	@LuaCallable(returnTypes = LuaReturnType.NUMBER, description = "Get the frequency of this chest or tank")
	public int getFrequency(TileEntity frequencyOwner) {
		return FREQ.get(frequencyOwner);
	}

	@LuaCallable(description = "Set the frequency of this chest or tank")
	public void setFrequency(TileEntity frequencyOwner,
			@Arg(name = "frequency", description = "A single number that represents all three colours on this chest or tank") int frequency) {
		setFreq(frequencyOwner, frequency);
	}

	private void setFreq(TileEntity frequencyOwner, int frequency) {
		Preconditions.checkElementIndex(frequency, 4096, "frequency");
		SET_FREQ.call(frequencyOwner, frequency);
	}

	private static int parseComputerCraftColor(int bitmask) {
		ColorMeta meta = ColorUtils.bitmaskToColor(bitmask);
		Preconditions.checkNotNull(meta, "Invalid color %sb", Integer.toBinaryString(bitmask));
		return meta.vanillaBlockId;
	}

	private static int parseColorName(String name) {
		ColorMeta meta = ColorUtils.nameToColor(name);
		Preconditions.checkNotNull(meta, "Invalid color name %s", name);
		return meta.vanillaBlockId;
	}

	private static String colorToName(int color) {
		ColorMeta meta = ColorUtils.vanillaBlockToColor(color);
		Preconditions.checkNotNull(meta, "Invalid color id %s", color);
		return meta.name;
	}
}
