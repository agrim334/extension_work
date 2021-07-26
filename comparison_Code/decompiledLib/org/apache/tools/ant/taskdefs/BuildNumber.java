package org.apache.tools.ant.taskdefs;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.util.Properties;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.util.FileUtils;

public class BuildNumber
  extends Task
{
  private static final String DEFAULT_PROPERTY_NAME = "build.number";
  private static final String DEFAULT_FILENAME = "build.number";
  private static final FileUtils FILE_UTILS = ;
  private File myFile;
  
  public void setFile(File file)
  {
    myFile = file;
  }
  
  public void execute()
    throws BuildException
  {
    File savedFile = myFile;
    
    validate();
    
    Properties properties = loadProperties();
    int buildNumber = getBuildNumber(properties);
    
    properties.put("build.number", 
      String.valueOf(buildNumber + 1));
    try
    {
      OutputStream output = Files.newOutputStream(myFile.toPath(), new OpenOption[0]);
      try
      {
        properties.store(output, "Build Number for ANT. Do not edit!");
        if (output == null) {
          break label97;
        }
        output.close();
      }
      catch (Throwable localThrowable)
      {
        if (output == null) {
          break label94;
        }
      }
      try
      {
        output.close();
      }
      catch (Throwable localThrowable1)
      {
        localThrowable.addSuppressed(localThrowable1);
      }
      label94:
      throw localThrowable;
    }
    catch (IOException ioe)
    {
      label97:
      throw new BuildException("Error while writing " + myFile, ioe);
    }
    finally
    {
      myFile = savedFile;
    }
    myFile = savedFile;
    
    getProject().setNewProperty("build.number", 
      String.valueOf(buildNumber));
  }
  
  private int getBuildNumber(Properties properties)
    throws BuildException
  {
    String buildNumber = properties.getProperty("build.number", "0").trim();
    try
    {
      return Integer.parseInt(buildNumber);
    }
    catch (NumberFormatException nfe)
    {
      throw new BuildException(myFile + " contains a non integer build number: " + buildNumber, nfe);
    }
  }
  
  /* Error */
  private Properties loadProperties()
    throws BuildException
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 9	org/apache/tools/ant/taskdefs/BuildNumber:myFile	Ljava/io/File;
    //   4: invokevirtual 38	java/io/File:toPath	()Ljava/nio/file/Path;
    //   7: iconst_0
    //   8: anewarray 44	java/nio/file/OpenOption
    //   11: invokestatic 121	java/nio/file/Files:newInputStream	(Ljava/nio/file/Path;[Ljava/nio/file/OpenOption;)Ljava/io/InputStream;
    //   14: astore_1
    //   15: new 33	java/util/Properties
    //   18: dup
    //   19: invokespecial 125	java/util/Properties:<init>	()V
    //   22: astore_2
    //   23: aload_2
    //   24: aload_1
    //   25: invokevirtual 126	java/util/Properties:load	(Ljava/io/InputStream;)V
    //   28: aload_2
    //   29: astore_3
    //   30: aload_1
    //   31: ifnull +7 -> 38
    //   34: aload_1
    //   35: invokevirtual 130	java/io/InputStream:close	()V
    //   38: aload_3
    //   39: areturn
    //   40: astore_2
    //   41: aload_1
    //   42: ifnull +16 -> 58
    //   45: aload_1
    //   46: invokevirtual 130	java/io/InputStream:close	()V
    //   49: goto +9 -> 58
    //   52: astore_3
    //   53: aload_2
    //   54: aload_3
    //   55: invokevirtual 65	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   58: aload_2
    //   59: athrow
    //   60: astore_1
    //   61: new 71	org/apache/tools/ant/BuildException
    //   64: dup
    //   65: aload_1
    //   66: invokespecial 133	org/apache/tools/ant/BuildException:<init>	(Ljava/lang/Throwable;)V
    //   69: athrow
    // Line number table:
    //   Java source line #127	-> byte code offset #0
    //   Java source line #128	-> byte code offset #15
    //   Java source line #129	-> byte code offset #23
    //   Java source line #130	-> byte code offset #28
    //   Java source line #131	-> byte code offset #30
    //   Java source line #130	-> byte code offset #38
    //   Java source line #127	-> byte code offset #40
    //   Java source line #131	-> byte code offset #60
    //   Java source line #132	-> byte code offset #61
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	70	0	this	BuildNumber
    //   14	32	1	input	java.io.InputStream
    //   60	6	1	ioe	IOException
    //   22	7	2	properties	Properties
    //   40	19	2	localThrowable	Throwable
    //   29	10	3	localProperties1	Properties
    //   52	3	3	localThrowable1	Throwable
    //   52	3	3	localThrowable2	Throwable
    // Exception table:
    //   from	to	target	type
    //   15	30	40	java/lang/Throwable
    //   45	49	52	java/lang/Throwable
    //   0	38	60	java/io/IOException
    //   40	60	60	java/io/IOException
  }
  
  private void validate()
    throws BuildException
  {
    if (null == myFile) {
      myFile = FILE_UTILS.resolveFile(getProject().getBaseDir(), "build.number");
    }
    if (!myFile.exists()) {
      try
      {
        FILE_UTILS.createNewFile(myFile);
      }
      catch (IOException ioe)
      {
        throw new BuildException(myFile + " doesn't exist and new file can't be created.", ioe);
      }
    }
    if (!myFile.canRead()) {
      throw new BuildException("Unable to read from " + myFile + ".");
    }
    if (!myFile.canWrite()) {
      throw new BuildException("Unable to write to " + myFile + ".");
    }
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.BuildNumber
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */