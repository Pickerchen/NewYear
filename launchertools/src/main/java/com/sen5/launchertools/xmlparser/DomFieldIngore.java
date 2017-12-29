package com.sen5.launchertools.xmlparser;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author aiheng@jd.com
 * @date 2014年10月31日 下午5:25:09
 * @desc 此注解用来忽略序列化属性
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DomFieldIngore {
}
