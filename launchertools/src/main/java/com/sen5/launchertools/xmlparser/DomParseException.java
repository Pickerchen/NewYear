package com.sen5.launchertools.xmlparser;

/**
 * @author aiheng@jd.com
 * @date 2014年11月3日 上午10:18:34
 * @desc Dom解析异常
 */
public class DomParseException extends RuntimeException {

    /**
     * serialVersionUID
     * long
     */
    private static final long serialVersionUID = 4964045225307295010L;

    public DomParseException() {
        super();
    }

    public DomParseException(String message) {
        super(message);
    }

    public DomParseException(String message, Throwable e) {
        super(message, e);
    }

}
