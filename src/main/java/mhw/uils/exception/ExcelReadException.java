package mhw.uils.exception;

/**
 * 读excel异常
 *
 * @author hongweima
 */
public class ExcelReadException extends RuntimeException {
    String message;

    public ExcelReadException(String message) {
        super(message);
        this.message = message;
    }
}
