package openperipheral.api;

/**
 *
 * Adapter with custom target class validation logic.
 */
public interface IAdapterWithConstraints extends IAdapter {
	public boolean canApply(Class<?> target);
}
