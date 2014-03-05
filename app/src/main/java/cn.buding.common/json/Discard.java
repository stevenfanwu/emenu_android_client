package cn.buding.common.json;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * set this annotation to skip reading the field.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Discard {
}
