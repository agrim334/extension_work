package org.apache.tools.ant.taskdefs.optional.ejb;

import java.io.File;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Path;
import org.xml.sax.SAXException;

public class IPlanetEjbcTask
  extends Task
{
  private File ejbdescriptor;
  private File iasdescriptor;
  private File dest;
  private Path classpath;
  private boolean keepgenerated = false;
  private boolean debug = false;
  private File iashome;
  
  public void setEjbdescriptor(File ejbdescriptor)
  {
    this.ejbdescriptor = ejbdescriptor;
  }
  
  public void setIasdescriptor(File iasdescriptor)
  {
    this.iasdescriptor = iasdescriptor;
  }
  
  public void setDest(File dest)
  {
    this.dest = dest;
  }
  
  public void setClasspath(Path classpath)
  {
    if (this.classpath == null) {
      this.classpath = classpath;
    } else {
      this.classpath.append(classpath);
    }
  }
  
  public Path createClasspath()
  {
    if (classpath == null) {
      classpath = new Path(getProject());
    }
    return classpath.createPath();
  }
  
  public void setKeepgenerated(boolean keepgenerated)
  {
    this.keepgenerated = keepgenerated;
  }
  
  public void setDebug(boolean debug)
  {
    this.debug = debug;
  }
  
  public void setIashome(File iashome)
  {
    this.iashome = iashome;
  }
  
  public void execute()
    throws BuildException
  {
    checkConfiguration();
    
    executeEjbc(getParser());
  }
  
  private void checkConfiguration()
    throws BuildException
  {
    if (ejbdescriptor == null)
    {
      String msg = "The standard EJB descriptor must be specified using the \"ejbdescriptor\" attribute.";
      
      throw new BuildException(msg, getLocation());
    }
    if ((!ejbdescriptor.exists()) || (!ejbdescriptor.isFile()))
    {
      String msg = "The standard EJB descriptor (" + ejbdescriptor + ") was not found or isn't a file.";
      
      throw new BuildException(msg, getLocation());
    }
    if (iasdescriptor == null)
    {
      String msg = "The iAS-speific XML descriptor must be specified using the \"iasdescriptor\" attribute.";
      
      throw new BuildException(msg, getLocation());
    }
    if ((!iasdescriptor.exists()) || (!iasdescriptor.isFile()))
    {
      String msg = "The iAS-specific XML descriptor (" + iasdescriptor + ") was not found or isn't a file.";
      
      throw new BuildException(msg, getLocation());
    }
    if (dest == null)
    {
      String msg = "The destination directory must be specified using the \"dest\" attribute.";
      
      throw new BuildException(msg, getLocation());
    }
    if ((!dest.exists()) || (!dest.isDirectory()))
    {
      String msg = "The destination directory (" + dest + ") was not found or isn't a directory.";
      
      throw new BuildException(msg, getLocation());
    }
    if ((iashome != null) && (!iashome.isDirectory()))
    {
      String msg = "If \"iashome\" is specified, it must be a valid directory (it was set to " + iashome + ").";
      
      throw new BuildException(msg, getLocation());
    }
  }
  
  private SAXParser getParser()
    throws BuildException
  {
    try
    {
      SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
      saxParserFactory.setValidating(true);
      return saxParserFactory.newSAXParser();
    }
    catch (SAXException|ParserConfigurationException e)
    {
      throw new BuildException("Unable to create a SAXParser: " + e.getMessage(), e, getLocation());
    }
  }
  
  private void executeEjbc(SAXParser saxParser)
    throws BuildException
  {
    IPlanetEjbc ejbc = new IPlanetEjbc(ejbdescriptor, iasdescriptor, dest, getClasspath().toString(), saxParser);
    
    ejbc.setRetainSource(keepgenerated);
    ejbc.setDebugOutput(debug);
    if (iashome != null) {
      ejbc.setIasHomeDir(iashome);
    }
    try
    {
      ejbc.execute();
    }
    catch (IOException e)
    {
      throw new BuildException("An IOException occurred while trying to read the XML descriptor file: " + e.getMessage(), e, getLocation());
    }
    catch (SAXException e)
    {
      throw new BuildException("A SAXException occurred while trying to read the XML descriptor file: " + e.getMessage(), e, getLocation());
    }
    catch (IPlanetEjbc.EjbcException e)
    {
      throw new BuildException("An exception occurred while trying to run the ejbc utility: " + e.getMessage(), e, getLocation());
    }
  }
  
  private Path getClasspath()
  {
    if (classpath == null) {
      return new Path(getProject()).concatSystemClasspath("last");
    }
    return classpath.concatSystemClasspath("ignore");
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.optional.ejb.IPlanetEjbcTask
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */