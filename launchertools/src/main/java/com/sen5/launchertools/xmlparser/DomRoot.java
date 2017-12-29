package com.sen5.launchertools.xmlparser;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author aiheng@jd.com
 * @date 2014年10月31日 上午11:46:23
 * @desc 此注解用来控制Root别名
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DomRoot {
    String value();
}
