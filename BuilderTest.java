import java.util.ArrayList;
import junit.framework.TestCase;



/**
 * @author Patrick Parker
 * @version 1
 *
 */
public class BuilderTest extends TestCase 
{
    private Builder testBuild;
    private ArrayList<String> ans1 = new ArrayList<String>();    
    private ArrayList<String> ans2 = new ArrayList<String>();
    private ArrayList<String> ans3 = new ArrayList<String>();
    private ArrayList<String> ans4 = new ArrayList<String>();
    private ArrayList<String> ans5 = new ArrayList<String>();
    private ArrayList<String> ans6 = new ArrayList<String>();
    
    /** 
     * SetUp for tests.
     * @see junit.framework.TestCase#setUp()
     */
    public void setUp()
    {
        StringBuffer makeFile = new StringBuffer(
            "Class1.java::Edit Class1.java\n" +
            "Class1.class:Class1.java:javac Class1.java\n" +
            "Class2.java::Edit Class2.java\n" +
            "Class2.class:Class2.java:javac Class2.java\n" +
            "MyApp.jar:Class1.class Class2.class:jar cvf *.class\n");
        try
        {
            testBuild = new Builder(makeFile);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        ans1.add("Edit Class1.java");
        ans1.add("javac Class1.java");
        ans1.add("Edit Class2.java");
        ans1.add("javac Class2.java");
        ans1.add("jar cvf *.class");
        
        ans2.add("Edit Class1.java");
        ans2.add("Edit Class2.java");
        ans2.add("javac Class1.java");       
        ans2.add("javac Class2.java");
        ans2.add("jar cvf *.class");
       
        ans3.add("Edit Class2.java");
        ans3.add("Edit Class1.java");
        ans3.add("javac Class1.java");       
        ans3.add("javac Class2.java");
        ans3.add("jar cvf *.class");
       
        ans4.add("Edit Class2.java");
        ans4.add("Edit Class1.java");
        ans4.add("javac Class2.java");       
        ans4.add("javac Class1.java");
        ans4.add("jar cvf *.class");
       
        ans5.add("Edit Class2.java");
        ans5.add("javac Class2.java");
        ans5.add("Edit Class1.java");
        ans5.add("javac Class1.java");
        ans5.add("jar cvf *.class");
        
        ans6.add("Edit Class1.java");
        ans6.add("Edit Class2.java");
        ans6.add("javac Class2.java");       
        ans6.add("javac Class1.java");
        ans6.add("jar cvf *.class");
    }
    
    /**
     * Tests the example makefile.
     */
    public void testSimple()
    {
        Boolean flag = false;
        ArrayList<String> output = testBuild.makeTarget("MyApp.jar");
        if (output.equals(ans1) || output.equals(ans2) || 
            output.equals(ans3) || output.equals(ans4) ||
            output.equals(ans5) || output.equals(ans6))
        {
            flag = true;
        }
        assertTrue(flag);
        flag = false;
        output = testBuild.makeTarget("MyApp.jar");
        if (output.equals(ans1) || output.equals(ans2) || 
            output.equals(ans3) || output.equals(ans4) ||
            output.equals(ans5) || output.equals(ans6))
        {
            flag = true;
        }
        assertTrue(flag);
        assertNull(testBuild.makeTarget("invalid"));
        ArrayList<String> expected = new ArrayList<String>();
        expected.add("Edit Class1.java");
        expected.add("javac Class1.java");
        assertEquals(expected, testBuild.makeTarget("Class1.class"));
    }
    
    /**
     * Gives the constructor a makefile with improper format.
     */
    public void testParseError()
    {
        StringBuffer invalid = new StringBuffer("Invalid make file");
        
        try
        {
            new Builder(invalid);
            fail();
        }
        catch (ParseException e)
        {
            // test passed
        } 
        catch (UnknownTargetException e) 
        {
            fail();
        } 
        catch (CycleDetectedException e) 
        {
            fail();
        }
        
    }
    
    /**
     * Tests cycle detection.
     */
    public void testDetectCycle()
    {
        
        try
        {
            new Builder(new StringBuffer(
                "a:b:acommand\n" +
                "b:c:bcommand\n" +
                "c:a:ccommand\n"));
            fail();
        }
        catch (ParseException e)
        {
            fail();
        } 
        catch (UnknownTargetException e) 
        {
            fail();
        } 
        catch (CycleDetectedException e) 
        {
            // test passed
        }
    }
    
    /**
     * Tests cycle detection.
     */
    public void testNoTarget()
    {
        
        try
        {
            new Builder(new StringBuffer(                
                "c:a:ccommand\n"));
            fail();
        }
        catch (ParseException e)
        {
            fail();
        } 
        catch (UnknownTargetException e) 
        {
            // test passed
        } 
        catch (CycleDetectedException e) 
        {
            fail();
        }
    }
    
    /**
     * Constructor should throw ParseException
     * if the make file has a duplicate target.
     */
    public void testDuplicateTarget()
    {
        
        try
        {
            new Builder(new StringBuffer(
                "a:b:acommand\n" +
                "a:b:bcommand\n" +
                "a:b:ccommand\n"));
            fail();
        }
        catch (ParseException e)
        {
            // test passed
        } 
        catch (UnknownTargetException e) 
        {
            fail();
        } 
        catch (CycleDetectedException e) 
        {
            fail();
        }
    }
    
    /**
     * Tests more complicated graphs.
     * @throws ParseException
     * @throws UnknownTargetException
     * @throws CycleDetectedException
     */
    public void testMore() throws 
    ParseException, UnknownTargetException, CycleDetectedException
    {
        Builder builder = new Builder(new StringBuffer(
                "a:b c d:a\n" +
                "b:e:b\n" +
                "c:b e:c\n" +
                "d:c e:d\n" +
                "e:f:e\n" +
                "f::f\n"));
        ArrayList<String> expected = new ArrayList<String>();
        expected.add("f");
        expected.add("e");
        expected.add("b");
        expected.add("c");
        expected.add("d");
        expected.add("a");
        assertEquals(expected, builder.makeTarget("a"));
        
        builder = new Builder(new StringBuffer(
                "a:d b e:a\n" +
                "b:h:b\n" +
                "c:b:c\n" +
                "d:b e:d\n" +
                "e:b h f:e\n" +
                "f:c:f\n" +
                "g:d e a:g\n" +
                "h::h\n" +
                "i:h f e:i\n"));
        expected = new ArrayList<String>();
        expected.add("h");
        expected.add("b");
        expected.add("c");
        expected.add("f");
        expected.add("e");        
        expected.add("d");
        expected.add("a");
        expected.add("g");
        assertEquals(expected, builder.makeTarget("g"));        
        
        
        builder = new Builder(new StringBuffer(
                "a::a\n" +
                "b:a:b\n" +
                "c:b:c\n" +
                "d:c:d\n" +
                "e:d:e\n" +
                "f:e:f\n" +
                "g:f:g\n" +
                "h:g:h\n" +
                "i:h:i\n"));
        expected = new ArrayList<String>();
        expected.add("a");
        expected.add("b");
        expected.add("c");
        expected.add("d");
        expected.add("e");
        expected.add("f");
        expected.add("g");
        expected.add("h");
        assertEquals(expected, builder.makeTarget("h"));
        expected.add("i");
        assertEquals(expected, builder.makeTarget("i"));
    }
    
}
