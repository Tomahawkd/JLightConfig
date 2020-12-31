package io.tomahawkd.config.annotation;

import io.tomahawkd.config.AbstractConfigDelegate;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Hidden field for arg delegate to avoid unintended access.
 * For more information {@link AbstractConfigDelegate#getField(String, Class)}
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface HiddenField {
}
