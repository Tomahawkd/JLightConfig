package io.tomahawkd.config;

import io.tomahawkd.config.annotation.HiddenField;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;

public class AbstractConfigDelegate implements ConfigDelegate {

	/**
	 * apply self to config
	 *
	 * @param config config to apply
	 */
	@Override
	public void applyDelegate(Config config) {
		config.addDelegate(this);
	}

	/**
	 * pre config and apply default settings (which may needs calculation)
	 */
	@Override
	public void preConfig() {
	}

	/**
	 * post parsing procedure after all arguments is applied to
	 * correspond fields.
	 */
	@Override
	public void postParsing() {
	}

	/**
	 * Acquire field data. Generally used after {@link Config#getDelegateByString(String)}
	 * This may usually used when you could not access the type of the class.
	 *
	 * @param key field name
	 * @param type field type
	 * @param <T> field class type
	 * @return field data
	 */
	@Override
	@Nullable
	@SuppressWarnings("unchecked")
	public final <T> T getField(String key, Class<T> type) {
		try {
			Field field = this.getClass().getDeclaredField(key);
			field.setAccessible(true);

			// ignoring field with hidden field annotation
			if (field.getAnnotation(HiddenField.class) != null) {
				throw new IllegalArgumentException(
						"Field " + field.getName() + " is not accessible");
			}

			// check type
			if (type.isAssignableFrom(field.getType())) {
				return (T) field.get(this);
			} else {
				throw new IllegalArgumentException(
						"Type " + type.getName() + " is not compatible to " +
								field.getType().getName());
			}
		} catch (IllegalAccessException | NoSuchFieldException | IllegalArgumentException e) {
			throw new IllegalArgumentException(e);
		}
	}
}
