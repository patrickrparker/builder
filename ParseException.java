/**
 * Parse exception.
 *
 * @author TKington
 * @version Mar 20, 2007
 */
public class ParseException extends Exception
{
    private static final long serialVersionUID = 1L;

    /**
     * Creates a ParseException.
     *
     * @param message The message.
     */
    public ParseException(String message) {
        super(message);
    }
}
