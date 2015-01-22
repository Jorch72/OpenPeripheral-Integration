package openperipheral.api;

import java.lang.annotation.*;

/**
 * Used to mark method arguments as receivers of instance of specific variable
 * Available variable names and expected types of argument depend on context. See {@link Constants} for possible values and type.
 *
 *
 * Selecting some values will exclude method from not supporting architectures (e.g. {@link Constants#ARG_COMPUTER} will hide this method from OpenComputers).
 *
 * @see Arg
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Env {
	public String value();
}
