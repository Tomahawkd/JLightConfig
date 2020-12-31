package io.tomahawkd.config;

import io.tomahawkd.config.annotation.HiddenField;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;

public class AbstractConfigDelegate implements ConfigDelegate {

	@Override
	public final void applyDelegate(Config config) {
		config.addDelegate(this);
	}

	@Override
	public void preConfig() {
	}

	@Override
	public void postParsing() {
	}

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