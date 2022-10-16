package mhw.uils.exception;

/**
 * xml解析异常
 *
 * @author hongweima
 */
public class XmlParserException extends RuntimeException{
    String message;

    public XmlParserException(String message) {
        super(message);
        this.message = message;
    }
}
