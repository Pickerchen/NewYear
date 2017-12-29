package com.sen5.launchertools.xmlparser;

/**
 * Created by yaojiaxu on 2017/1/13 0013.
 * ClassName: BooleanValueConvert
 * Description:
 */

public class BooleanValueConvert implements DomConvert {

    @Override
    public Boolean convert(Object object) {
        String str = (String)object;
        if (Boolean.valueOf(str)) {
            return true;
        } else {
            return false;
        }
    }
}
