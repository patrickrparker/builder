import java.util.ArrayList;

/**
 * Class to represent a Builder graph vertex.
 * @author Patrick Parker
 * @version 1
 *
 */
public class Vertex 
{
    private String name;
    private String command;
    private ArrayList<String> dependencies;
    
    /**
     * Creates a vertex.
     * @param targetName The name of the target.
     * @param targetCommand The command to compile target.
     * @param targetDependencies A list of target dependencies.
     */
    public Vertex(String targetName, String targetCommand, 
        ArrayList<String> targetDependencies)
    {
        this.name = targetName;
        this.command = targetCommand;
        this.dependencies = targetDependencies;
    }

    /**
     * @return The name of the target.
     */
    public String getName() 
    {
        return name;
    }    
    /**
     * @return The command
     */
    public String getCommand() 
    {
        return command;
    }
    
    /**
     * @return A list of dependencies.
     */
    public ArrayList<String> getDependencies() 
    {
        return dependencies;
    }   
}
