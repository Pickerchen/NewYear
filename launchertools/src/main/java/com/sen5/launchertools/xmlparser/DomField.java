package com.sen5.launchertools.xmlparser;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author aiheng@jd.com
 * @date 2014年10月31日 上午11:46:46
 * @desc 此注解用来控制属性别名 和 转换别名使用，用于xml2bean和bean2xml里的<对应别名>值的转换
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DomField {
    String value() default "";
}
