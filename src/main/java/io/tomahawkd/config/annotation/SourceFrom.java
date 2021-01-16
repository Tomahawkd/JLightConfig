package io.tomahawkd.config.annotation;

import io.tomahawkd.config.sources.ConfigSource;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Use SourceFrom to bind source class and config.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface SourceFrom {
	Class<? extends ConfigSource> value();
}
