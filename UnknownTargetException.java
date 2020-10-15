/**
 * Thrown when trying to build an unknown target.
 *
 * @author TKington
 * @version Mar 20, 2007
 */
public class UnknownTargetException extends Exception
{
    private static final long serialVersionUID = 1L;

    /**
     * Creates an UnknownTargetException.
     *
     * @param target The target.
     */
    public UnknownTargetException(String target) {
        super(target);
    }
}
