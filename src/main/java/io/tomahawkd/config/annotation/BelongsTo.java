package io.tomahawkd.config.annotation;

import io.tomahawkd.config.Config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicate the delegate belongs to which config
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface BelongsTo {
	Class<? extends Config> value();
}
