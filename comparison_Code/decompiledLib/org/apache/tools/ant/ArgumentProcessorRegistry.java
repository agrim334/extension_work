package org.apache.tools.ant;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.tools.ant.util.LoaderUtils;

public class ArgumentProcessorRegistry
{
  private static final String DEBUG_ARGUMENT_PROCESSOR_REPOSITORY = "ant.argument-processor-repo.debug";
  private static final boolean DEBUG = "true".equals(System.getProperty("ant.argument-processor-repo.debug"));
  private static final String SERVICE_ID = "META-INF/services/org.apache.tools.ant.ArgumentProcessor";
  private static ArgumentProcessorRegistry instance = new ArgumentProcessorRegistry();
  private List<ArgumentProcessor> processors = new ArrayList();
  
  public static ArgumentProcessorRegistry getInstance()
  {
    return instance;
  }
  
  private ArgumentProcessorRegistry()
  {
    collectArgumentProcessors();
  }
  
  public List<ArgumentProcessor> getProcessors()
  {
    return processors;
  }
  
  private void collectArgumentProcessors()
  {
    try
    {
      ClassLoader classLoader = LoaderUtils.getContextClassLoader();
      if (classLoader != null) {
        for (URL resource : Collections.list(classLoader.getResources("META-INF/services/org.apache.tools.ant.ArgumentProcessor")))
        {
          URLConnection conn = resource.openConnection();
          conn.setUseCaches(false);
          ArgumentProcessor processor = getProcessorByService(conn.getInputStream());
          registerArgumentProcessor(processor);
        }
      }
      InputStream systemResource = ClassLoader.getSystemResourceAsStream("META-INF/services/org.apache.tools.ant.ArgumentProcessor");
      if (systemResource != null)
      {
        ArgumentProcessor processor = getProcessorByService(systemResource);
        registerArgumentProcessor(processor);
      }
    }
    catch (Exception e)
    {
      System.err.println("Unable to load ArgumentProcessor from service META-INF/services/org.apache.tools.ant.ArgumentProcessor (" + e
        .getClass().getName() + ": " + e
        .getMessage() + ")");
      if (DEBUG) {
        e.printStackTrace(System.err);
      }
    }
  }
  
  public void registerArgumentProcessor(String helperClassName)
    throws BuildException
  {
    registerArgumentProcessor(getProcessor(helperClassName));
  }
  
  public void registerArgumentProcessor(Class<? extends ArgumentProcessor> helperClass)
    throws BuildException
  {
    registerArgumentProcessor(getProcessor(helperClass));
  }
  
  private ArgumentProcessor getProcessor(String helperClassName)
  {
    try
    {
      Class<? extends ArgumentProcessor> cl = Class.forName(helperClassName);
      return getProcessor(cl);
    }
    catch (ClassNotFoundException e)
    {
      throw new BuildException("Argument processor class " + helperClassName + " was not found", e);
    }
  }
  
  private ArgumentProcessor getProcessor(Class<? extends ArgumentProcessor> processorClass)
  {
    try
    {
      processor = (ArgumentProcessor)processorClass.getConstructor(new Class[0]).newInstance(new Object[0]);
    }
    catch (Exception e)
    {
      ArgumentProcessor processor;
      throw new BuildException("The argument processor class" + processorClass.getName() + " could not be instantiated with a default constructor", e);
    }
    ArgumentProcessor processor;
    return processor;
  }
  
  public void registerArgumentProcessor(ArgumentProcessor processor)
  {
    if (processor == null) {
      return;
    }
    processors.add(processor);
    if (DEBUG) {
      System.out.println("Argument processor " + processor
        .getClass().getName() + " registered.");
    }
  }
  
  private ArgumentProcessor getProcessorByService(InputStream is)
    throws IOException
  {
    BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
    try
    {
      String processorClassName = rd.readLine();
      if ((processorClassName != null) && (!processorClassName.isEmpty()))
      {
        ArgumentProcessor localArgumentProcessor = getProcessor(processorClassName);
        
        rd.close();return localArgumentProcessor;
      }
      rd.close();
    }
    catch (Throwable localThrowable) {}
    try
    {
      rd.close();
    }
    catch (Throwable localThrowable2)
    {
      localThrowable.addSuppressed(localThrowable2);
    }
    throw localThrowable;
    
    return null;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.ArgumentProcessorRegistry
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */