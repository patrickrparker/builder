import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;

/**
 * The Builder class.
 * @author Patrick Parker
 * @version 1
 */
public class Builder
{
    private Map<String, Vertex> graph;
    private Map<String, Vertex> subgraph;
    private Queue<String> queue;
    
    /**
     * Creates a Builder.
     * @param makefile the makefile
     * @throws ParseException
     * @throws UnknownTargetException
     * @throws CycleDetectedException
     */
    public Builder(StringBuffer makefile) throws ParseException,
            UnknownTargetException, CycleDetectedException 
    {        
        graph = new HashMap<String, Vertex>();
        subgraph = new HashMap<String, Vertex>();
        queue = new ArrayDeque<String>();
        String[] lines = detectParseError(makefile);
        makeGraph(lines);
        topologicalSort();
    }
    
    /**
     * Does a topological breadth-first sort of the targets 
     * in a queue and looks for cycles.
     * @throws CycleDetectedException
     */
    private void topologicalSort() throws CycleDetectedException
    {
        Map<String, Vertex> forsort = new HashMap<String, Vertex>();
        Set<Entry<String, Vertex>> entrySet = graph.entrySet();        
        for (Entry<String, Vertex> e : entrySet)
        {
            forsort.put(e.getKey(), e.getValue());
        }
        Set<String> keys = graph.keySet();
        for (String s : keys)
        {
            if (forsort.get(s).getDependencies() == null)
            {
                queue.add(forsort.remove(s).getName());
            }
        }        
        while (!forsort.isEmpty())
        {
            boolean vertexRemoved = false;
            Collection<Vertex> verts = forsort.values();
            Iterator<Vertex> itr = verts.iterator();
            while (itr.hasNext())
            {
                Vertex v = itr.next();
                if (queue.containsAll(v.getDependencies()))
                {
                    String target = v.getName();                    
                    queue.add(target);
                    itr.remove();
                    vertexRemoved = true;                    
                }
            }
            if (!vertexRemoved)
            {
                throw new CycleDetectedException();
            }
        }
    }
    
    /**
     * Makes the graph while checking for dependencies
     * that have no target, or duplicate target entries.
     * @param lines An array of Strings on for each line of
     * the makefile.
     * @throws UnknownTargetException
     * @throws ParseException
     */
    private void makeGraph(String[] lines) 
        throws UnknownTargetException, ParseException
    {
        ArrayList<String> masterDepList = new ArrayList<String>();
        for (int i = 0; i < lines.length; i++)
        {
            String[] tokens;
            String line = lines[i];
            if (line.contains("::"))
            {
                tokens = line.split("::");
            }
            else
            {
                tokens = line.split(":");
            }
            if (graph.containsKey(tokens[0]))
            {
                throw new ParseException(
                    "Makefile has duplicate entry for target \""
                    + tokens[0] + "\"");
            }
            if (tokens.length == 2)
            {
                graph.put(tokens[0], 
                    new Vertex(tokens[0], tokens[1], null));
            }
            else
            {
                ArrayList<String> depends = new ArrayList<String>();
                String[] deps = tokens[1].split(" ");
                for (int j = 0; j < deps.length; j++)
                {
                    masterDepList.add(deps[j]);
                    depends.add(deps[j]);                    
                }
                graph.put(tokens[0], 
                    new Vertex(tokens[0], tokens[2], depends));                
            }
            
        }
        for (String s : masterDepList)
        {
            if (!graph.containsKey(s))
            {
                throw new UnknownTargetException(
                    "Makefile needs entry for target \"" + s + "\"");
            }
        }
    }
    
    /**
     * Checks each line for the proper format and 
     * returns the make file as an array of strings,
     * one for each line.
     * @param makefile
     * @return
     * @throws ParseException
     */
    private String[] detectParseError(StringBuffer makefile)
        throws ParseException
    {
        String input = makefile.toString();
        String[] lines = input.split("\n");
        for (int i = 0; i < lines.length; i++)
        {
            
            if (!lines[i].matches("[^:]+::[^:]+") &&
                !lines[i].matches("[^:]+(:[^:]+){2}"))
            {
                throw new ParseException("Parse error at line " + 
                    (i + 1) + " of makefile");
            }
        }
        return lines;
    }
    
    /**
     * Figures out how to make the given target.
     * 
     * @param targetName the target.
     * @return the list of required steps.
     */
    public ArrayList<String> makeTarget(String targetName) 
    {
        if (!queue.contains(targetName))
        {
            return null;
        }      
        ArrayList<String> targetsInOrder = new ArrayList<String>(); 
               
        makeSub(targetName);       
        
        Set<String> keys = subgraph.keySet();
        Iterator<String> itr1 = keys.iterator();
        while (itr1.hasNext())
        {
            String s = itr1.next();
            if (subgraph.get(s).getDependencies() == null)
            {
                targetsInOrder.add(s);
                itr1.remove();
            }
        }        
        while (!subgraph.isEmpty())
        {
            
            Collection<Vertex> verts = subgraph.values();
            Iterator<Vertex> itr = verts.iterator();
            while (itr.hasNext())
            {
                Vertex v = itr.next();
                if (targetsInOrder.containsAll(v.getDependencies()))
                {
                    String s = v.getName();                    
                    targetsInOrder.add(s);
                    itr.remove();                                        
                }
            }
        }
        ArrayList<String> commands = new ArrayList<String>();
        for (String s : targetsInOrder)
        {
            commands.add(graph.get(s).getCommand());
        }
        return commands;
    }
    
    /**
     * Recursive helper to build the subgraph.
     * @param target The target.
     */
    private void makeSub(String target)
    {
        if (graph.get(target).getDependencies() == null)
        {
            subgraph.put(graph.get(target).getName(),
                    graph.get(target));
        }
        else
        {
            subgraph.put(graph.get(target).getName(),
                    graph.get(target));
            ArrayList<String> dep = 
                graph.get(target).getDependencies();
            for (String s : dep)
            {
                makeSub(s);
            }
        }
    }    
}
