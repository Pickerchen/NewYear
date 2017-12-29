package com.sen5.launchertools.xmlparser;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author aiheng@jd.com
 * @date 2014年10月31日 下午5:29:21
 * @desc
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DomFieldConvert {

    Class<? extends DomConvert> value();

}
