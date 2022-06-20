package org.anystub;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface AnySettingsHttp {

    /**
     * override global settings for headers, bodyTrigger, bodyMask
     * ex. global settings define headers: "Context-Type" which requires to include it in every request
     *   in local test you can suppress adding it into stub
     * allHeaders - always overrides global, if not defined in class/method level then it is false
     * if true - overrider global settings
     * if false - enrich global settings
     * @return
     */
    boolean overrideGlobal() default false;

    /**
     * includes all headers in key
     * @return
     */
    boolean allHeaders() default false;

    /**
     * to define headers which will be included in a request key,
     * if not empty then allHeaders ignored
     * @return
     */
    String[] headers() default {};

    /**
     * to define URLs which trigger inclusion a request body in a key.
     * request body includes in a key if any of these strings is a substring of URL
     * example:
     *  '@AnySettingsHttp(bodyTrigger = "random")'
     *  the request to https://gturnquist-quoters.cfapps.io:443/api/randomX will be recorded with request body
     *  the request to https://gturnquist-quoters.cfapps.io:443/api/any will be recorded without request body
     *
     * @return strings which trigger including request body into index
     */
    String[] bodyTrigger() default {};

    /**
     * to define regex expressions which will be replaced by elapses "..." in a request body
     * @return
     * @deprecated since 0.7.0 use
     */
    @Deprecated(since = "0.7.0")
    String[] bodyMask() default {};


}
