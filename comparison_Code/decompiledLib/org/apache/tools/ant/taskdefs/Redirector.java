package org.apache.tools.ant.taskdefs;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Vector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.filters.util.ChainReaderHelper;
import org.apache.tools.ant.types.FilterChain;
import org.apache.tools.ant.util.ConcatFileInputStream;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.util.KeepAliveOutputStream;
import org.apache.tools.ant.util.LazyFileOutputStream;
import org.apache.tools.ant.util.LeadPipeInputStream;
import org.apache.tools.ant.util.LineOrientedOutputStreamRedirector;
import org.apache.tools.ant.util.OutputStreamFunneler;
import org.apache.tools.ant.util.ReaderInputStream;
import org.apache.tools.ant.util.TeeOutputStream;

public class Redirector
{
  private static final int STREAMPUMPER_WAIT_INTERVAL = 1000;
  private static final String DEFAULT_ENCODING = System.getProperty("file.encoding");
  private File[] input;
  private File[] out;
  private File[] error;
  
  private class PropertyOutputStream
    extends ByteArrayOutputStream
  {
    private final String property;
    private boolean closed = false;
    
    PropertyOutputStream(String property)
    {
      this.property = property;
    }
    
    public void close()
      throws IOException
    {
      synchronized (outMutex)
      {
        if ((!closed) && ((!appendOut) || (!appendProperties)))
        {
          Redirector.this.setPropertyFromBAOS(this, property);
          closed = true;
        }
      }
    }
  }
  
  private boolean logError = false;
  private PropertyOutputStream baos = null;
  private PropertyOutputStream errorBaos = null;
  private String outputProperty;
  private String errorProperty;
  private String inputString;
  private boolean appendOut = false;
  private boolean appendErr = false;
  private boolean alwaysLogOut = false;
  private boolean alwaysLogErr = false;
  private boolean createEmptyFilesOut = true;
  private boolean createEmptyFilesErr = true;
  private final ProjectComponent managingTask;
  private OutputStream outputStream = null;
  private OutputStream errorStream = null;
  private InputStream inputStream = null;
  private PrintStream outPrintStream = null;
  private PrintStream errorPrintStream = null;
  private Vector<FilterChain> outputFilterChains;
  private Vector<FilterChain> errorFilterChains;
  private Vector<FilterChain> inputFilterChains;
  private String outputEncoding = DEFAULT_ENCODING;
  private String errorEncoding = DEFAULT_ENCODING;
  private String inputEncoding = DEFAULT_ENCODING;
  private boolean appendProperties = true;
  private final ThreadGroup threadGroup = new ThreadGroup("redirector");
  private boolean logInputString = true;
  private final Object inMutex = new Object();
  private final Object outMutex = new Object();
  private final Object errMutex = new Object();
  private boolean outputIsBinary = false;
  
  public Redirector(Task managingTask)
  {
    this(managingTask);
  }
  
  public Redirector(ProjectComponent managingTask)
  {
    this.managingTask = managingTask;
  }
  
  public void setInput(File input)
  {
    setInput(new File[] { input == null ? null : input });
  }
  
  public void setInput(File[] input)
  {
    synchronized (inMutex)
    {
      if (input == null) {
        this.input = null;
      } else {
        this.input = ((File[])input.clone());
      }
    }
  }
  
  public void setInputString(String inputString)
  {
    synchronized (inMutex)
    {
      this.inputString = inputString;
    }
  }
  
  public void setLogInputString(boolean logInputString)
  {
    this.logInputString = logInputString;
  }
  
  void setInputStream(InputStream inputStream)
  {
    synchronized (inMutex)
    {
      this.inputStream = inputStream;
    }
  }
  
  public void setOutput(File out)
  {
    setOutput(new File[] { out == null ? null : out });
  }
  
  public void setOutput(File[] out)
  {
    synchronized (outMutex)
    {
      if (out == null) {
        this.out = null;
      } else {
        this.out = ((File[])out.clone());
      }
    }
  }
  
  public void setOutputEncoding(String outputEncoding)
  {
    if (outputEncoding == null) {
      throw new IllegalArgumentException("outputEncoding must not be null");
    }
    synchronized (outMutex)
    {
      this.outputEncoding = outputEncoding;
    }
  }
  
  public void setErrorEncoding(String errorEncoding)
  {
    if (errorEncoding == null) {
      throw new IllegalArgumentException("errorEncoding must not be null");
    }
    synchronized (errMutex)
    {
      this.errorEncoding = errorEncoding;
    }
  }
  
  public void setInputEncoding(String inputEncoding)
  {
    if (inputEncoding == null) {
      throw new IllegalArgumentException("inputEncoding must not be null");
    }
    synchronized (inMutex)
    {
      this.inputEncoding = inputEncoding;
    }
  }
  
  public void setLogError(boolean logError)
  {
    synchronized (errMutex)
    {
      this.logError = logError;
    }
  }
  
  public void setAppendProperties(boolean appendProperties)
  {
    synchronized (outMutex)
    {
      this.appendProperties = appendProperties;
    }
  }
  
  public void setError(File error)
  {
    setError(new File[] { error == null ? null : error });
  }
  
  public void setError(File[] error)
  {
    synchronized (errMutex)
    {
      if (error == null) {
        this.error = null;
      } else {
        this.error = ((File[])error.clone());
      }
    }
  }
  
  public void setOutputProperty(String outputProperty)
  {
    if ((outputProperty == null) || 
      (!outputProperty.equals(this.outputProperty))) {
      synchronized (outMutex)
      {
        this.outputProperty = outputProperty;
        baos = null;
      }
    }
  }
  
  public void setAppend(boolean append)
  {
    synchronized (outMutex)
    {
      appendOut = append;
    }
    synchronized (errMutex)
    {
      appendErr = append;
    }
  }
  
  public void setAlwaysLog(boolean alwaysLog)
  {
    synchronized (outMutex)
    {
      alwaysLogOut = alwaysLog;
    }
    synchronized (errMutex)
    {
      alwaysLogErr = alwaysLog;
    }
  }
  
  public void setCreateEmptyFiles(boolean createEmptyFiles)
  {
    synchronized (outMutex)
    {
      createEmptyFilesOut = createEmptyFiles;
    }
    synchronized (outMutex)
    {
      createEmptyFilesErr = createEmptyFiles;
    }
  }
  
  public void setErrorProperty(String errorProperty)
  {
    synchronized (errMutex)
    {
      if ((errorProperty == null) || 
        (!errorProperty.equals(this.errorProperty)))
      {
        this.errorProperty = errorProperty;
        errorBaos = null;
      }
    }
  }
  
  public void setInputFilterChains(Vector<FilterChain> inputFilterChains)
  {
    synchronized (inMutex)
    {
      this.inputFilterChains = inputFilterChains;
    }
  }
  
  public void setOutputFilterChains(Vector<FilterChain> outputFilterChains)
  {
    synchronized (outMutex)
    {
      this.outputFilterChains = outputFilterChains;
    }
  }
  
  public void setErrorFilterChains(Vector<FilterChain> errorFilterChains)
  {
    synchronized (errMutex)
    {
      this.errorFilterChains = errorFilterChains;
    }
  }
  
  public void setBinaryOutput(boolean b)
  {
    outputIsBinary = b;
  }
  
  private void setPropertyFromBAOS(ByteArrayOutputStream baos, String propertyName)
  {
    BufferedReader in = new BufferedReader(new StringReader(Execute.toString(baos)));
    managingTask.getProject().setNewProperty(propertyName, 
      (String)in.lines().collect(Collectors.joining(System.lineSeparator())));
  }
  
  public void createStreams()
  {
    synchronized (outMutex)
    {
      outStreams();
      if ((alwaysLogOut) || (outputStream == null))
      {
        OutputStream outputLog = new LogOutputStream(managingTask, 2);
        
        outputStream = (outputStream == null ? outputLog : new TeeOutputStream(outputLog, outputStream));
      }
      if (((outputFilterChains != null) && (outputFilterChains.size() > 0)) || 
        (!outputEncoding.equalsIgnoreCase(inputEncoding))) {
        try
        {
          LeadPipeInputStream snk = new LeadPipeInputStream();
          snk.setManagingComponent(managingTask);
          
          InputStream outPumpIn = snk;
          
          Reader reader = new InputStreamReader(outPumpIn, inputEncoding);
          if ((outputFilterChains != null) && 
            (outputFilterChains.size() > 0))
          {
            ChainReaderHelper helper = new ChainReaderHelper();
            helper.setProject(managingTask.getProject());
            helper.setPrimaryReader(reader);
            helper.setFilterChains(outputFilterChains);
            reader = helper.getAssembledReader();
          }
          outPumpIn = new ReaderInputStream(reader, outputEncoding);
          
          Thread t = new Thread(threadGroup, new StreamPumper(outPumpIn, outputStream, true), "output pumper");
          
          t.setPriority(10);
          outputStream = new PipedOutputStream(snk);
          t.start();
        }
        catch (IOException eyeOhEx)
        {
          throw new BuildException("error setting up output stream", eyeOhEx);
        }
      }
    }
    synchronized (errMutex)
    {
      errorStreams();
      if ((alwaysLogErr) || (errorStream == null))
      {
        OutputStream errorLog = new LogOutputStream(managingTask, 1);
        
        errorStream = (errorStream == null ? errorLog : new TeeOutputStream(errorLog, errorStream));
      }
      if (((errorFilterChains != null) && (errorFilterChains.size() > 0)) || 
        (!errorEncoding.equalsIgnoreCase(inputEncoding))) {
        try
        {
          LeadPipeInputStream snk = new LeadPipeInputStream();
          snk.setManagingComponent(managingTask);
          
          InputStream errPumpIn = snk;
          
          Reader reader = new InputStreamReader(errPumpIn, inputEncoding);
          if ((errorFilterChains != null) && 
            (errorFilterChains.size() > 0))
          {
            ChainReaderHelper helper = new ChainReaderHelper();
            helper.setProject(managingTask.getProject());
            helper.setPrimaryReader(reader);
            helper.setFilterChains(errorFilterChains);
            reader = helper.getAssembledReader();
          }
          errPumpIn = new ReaderInputStream(reader, errorEncoding);
          
          Thread t = new Thread(threadGroup, new StreamPumper(errPumpIn, errorStream, true), "error pumper");
          
          t.setPriority(10);
          errorStream = new PipedOutputStream(snk);
          t.start();
        }
        catch (IOException eyeOhEx)
        {
          throw new BuildException("error setting up error stream", eyeOhEx);
        }
      }
    }
    synchronized (inMutex)
    {
      if ((input != null) && (input.length > 0))
      {
        managingTask.log("Redirecting input from file" + (
          input.length == 1 ? "" : "s"), 3);
        try
        {
          inputStream = new ConcatFileInputStream(input);
        }
        catch (IOException eyeOhEx)
        {
          throw new BuildException(eyeOhEx);
        }
        ((ConcatFileInputStream)inputStream).setManagingComponent(managingTask);
      }
      else if (inputString != null)
      {
        StringBuffer buf = new StringBuffer("Using input ");
        if (logInputString) {
          buf.append('"').append(inputString).append('"');
        } else {
          buf.append("string");
        }
        managingTask.log(buf.toString(), 3);
        inputStream = new ByteArrayInputStream(inputString.getBytes());
      }
      if ((inputStream != null) && (inputFilterChains != null) && 
        (inputFilterChains.size() > 0))
      {
        ChainReaderHelper helper = new ChainReaderHelper();
        helper.setProject(managingTask.getProject());
        try
        {
          helper.setPrimaryReader(new InputStreamReader(inputStream, inputEncoding));
        }
        catch (IOException eyeOhEx)
        {
          throw new BuildException("error setting up input stream", eyeOhEx);
        }
        helper.setFilterChains(inputFilterChains);
        
        inputStream = new ReaderInputStream(helper.getAssembledReader(), inputEncoding);
      }
    }
  }
  
  private void outStreams()
  {
    if ((out != null) && (out.length > 0))
    {
      String logHead = "Output " + (appendOut ? "appended" : "redirected") + " to ";
      outputStream = foldFiles(out, logHead, 3, appendOut, createEmptyFilesOut);
    }
    if (outputProperty != null)
    {
      if (baos == null)
      {
        baos = new PropertyOutputStream(outputProperty);
        managingTask.log("Output redirected to property: " + outputProperty, 3);
      }
      OutputStream keepAliveOutput = new KeepAliveOutputStream(baos);
      
      outputStream = (outputStream == null ? keepAliveOutput : new TeeOutputStream(outputStream, keepAliveOutput));
    }
    else
    {
      baos = null;
    }
  }
  
  private void errorStreams()
  {
    if ((error != null) && (error.length > 0))
    {
      String logHead = "Error " + (appendErr ? "appended" : "redirected") + " to ";
      errorStream = foldFiles(error, logHead, 3, appendErr, createEmptyFilesErr);
    }
    else if ((!logError) && (outputStream != null) && (errorProperty == null))
    {
      long funnelTimeout = 0L;
      OutputStreamFunneler funneler = new OutputStreamFunneler(outputStream, 0L);
      try
      {
        outputStream = funneler.getFunnelInstance();
        errorStream = funneler.getFunnelInstance();
        if (!outputIsBinary)
        {
          outputStream = new LineOrientedOutputStreamRedirector(outputStream);
          errorStream = new LineOrientedOutputStreamRedirector(errorStream);
        }
      }
      catch (IOException eyeOhEx)
      {
        throw new BuildException("error splitting output/error streams", eyeOhEx);
      }
    }
    if (errorProperty != null)
    {
      if (errorBaos == null)
      {
        errorBaos = new PropertyOutputStream(errorProperty);
        managingTask.log("Error redirected to property: " + errorProperty, 3);
      }
      OutputStream keepAliveError = new KeepAliveOutputStream(errorBaos);
      
      errorStream = ((error == null) || (error.length == 0) ? keepAliveError : new TeeOutputStream(errorStream, keepAliveError));
    }
    else
    {
      errorBaos = null;
    }
  }
  
  public ExecuteStreamHandler createHandler()
    throws BuildException
  {
    createStreams();
    boolean nonBlockingRead = (input == null) && (inputString == null);
    return new PumpStreamHandler(getOutputStream(), getErrorStream(), 
      getInputStream(), nonBlockingRead);
  }
  
  protected void handleOutput(String output)
  {
    synchronized (outMutex)
    {
      if (outPrintStream == null) {
        outPrintStream = new PrintStream(outputStream);
      }
      outPrintStream.print(output);
    }
  }
  
  protected int handleInput(byte[] buffer, int offset, int length)
    throws IOException
  {
    synchronized (inMutex)
    {
      if (inputStream == null) {
        return managingTask.getProject().defaultInput(buffer, offset, length);
      }
      return inputStream.read(buffer, offset, length);
    }
  }
  
  protected void handleFlush(String output)
  {
    synchronized (outMutex)
    {
      if (outPrintStream == null) {
        outPrintStream = new PrintStream(outputStream);
      }
      outPrintStream.print(output);
      outPrintStream.flush();
    }
  }
  
  protected void handleErrorOutput(String output)
  {
    synchronized (errMutex)
    {
      if (errorPrintStream == null) {
        errorPrintStream = new PrintStream(errorStream);
      }
      errorPrintStream.print(output);
    }
  }
  
  protected void handleErrorFlush(String output)
  {
    synchronized (errMutex)
    {
      if (errorPrintStream == null) {
        errorPrintStream = new PrintStream(errorStream);
      }
      errorPrintStream.print(output);
      errorPrintStream.flush();
    }
  }
  
  /* Error */
  public OutputStream getOutputStream()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 14	org/apache/tools/ant/taskdefs/Redirector:outMutex	Ljava/lang/Object;
    //   4: dup
    //   5: astore_1
    //   6: monitorenter
    //   7: aload_0
    //   8: getfield 52	org/apache/tools/ant/taskdefs/Redirector:outputStream	Ljava/io/OutputStream;
    //   11: aload_1
    //   12: monitorexit
    //   13: areturn
    //   14: astore_2
    //   15: aload_1
    //   16: monitorexit
    //   17: aload_2
    //   18: athrow
    // Line number table:
    //   Java source line #867	-> byte code offset #0
    //   Java source line #868	-> byte code offset #7
    //   Java source line #869	-> byte code offset #14
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	19	0	this	Redirector
    //   5	11	1	Ljava/lang/Object;	Object
    //   14	4	2	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   7	13	14	finally
    //   14	17	14	finally
  }
  
  /* Error */
  public OutputStream getErrorStream()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 100	org/apache/tools/ant/taskdefs/Redirector:errMutex	Ljava/lang/Object;
    //   4: dup
    //   5: astore_1
    //   6: monitorenter
    //   7: aload_0
    //   8: getfield 56	org/apache/tools/ant/taskdefs/Redirector:errorStream	Ljava/io/OutputStream;
    //   11: aload_1
    //   12: monitorexit
    //   13: areturn
    //   14: astore_2
    //   15: aload_1
    //   16: monitorexit
    //   17: aload_2
    //   18: athrow
    // Line number table:
    //   Java source line #879	-> byte code offset #0
    //   Java source line #880	-> byte code offset #7
    //   Java source line #881	-> byte code offset #14
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	19	0	this	Redirector
    //   5	11	1	Ljava/lang/Object;	Object
    //   14	4	2	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   7	13	14	finally
    //   14	17	14	finally
  }
  
  /* Error */
  public InputStream getInputStream()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 97	org/apache/tools/ant/taskdefs/Redirector:inMutex	Ljava/lang/Object;
    //   4: dup
    //   5: astore_1
    //   6: monitorenter
    //   7: aload_0
    //   8: getfield 59	org/apache/tools/ant/taskdefs/Redirector:inputStream	Ljava/io/InputStream;
    //   11: aload_1
    //   12: monitorexit
    //   13: areturn
    //   14: astore_2
    //   15: aload_1
    //   16: monitorexit
    //   17: aload_2
    //   18: athrow
    // Line number table:
    //   Java source line #891	-> byte code offset #0
    //   Java source line #892	-> byte code offset #7
    //   Java source line #893	-> byte code offset #14
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	19	0	this	Redirector
    //   5	11	1	Ljava/lang/Object;	Object
    //   14	4	2	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   7	13	14	finally
    //   14	17	14	finally
  }
  
  public void complete()
    throws IOException
  {
    System.out.flush();
    System.err.flush();
    synchronized (inMutex)
    {
      if (inputStream != null) {
        inputStream.close();
      }
    }
    synchronized (outMutex)
    {
      outputStream.flush();
      outputStream.close();
    }
    synchronized (errMutex)
    {
      errorStream.flush();
      errorStream.close();
    }
    synchronized (this)
    {
      while (threadGroup.activeCount() > 0) {
        try
        {
          managingTask.log("waiting for " + threadGroup.activeCount() + " Threads:", 4);
          
          Thread[] thread = new Thread[threadGroup.activeCount()];
          threadGroup.enumerate(thread);
          for (int i = 0; (i < thread.length) && (thread[i] != null); i++) {
            try
            {
              managingTask.log(thread[i].toString(), 4);
            }
            catch (NullPointerException localNullPointerException1) {}
          }
          wait(1000L);
        }
        catch (InterruptedException eyeEx)
        {
          Thread[] thread = new Thread[threadGroup.activeCount()];
          threadGroup.enumerate(thread);
          for (int i = 0; (i < thread.length) && (thread[i] != null); i++) {
            thread[i].interrupt();
          }
        }
      }
    }
    setProperties();
    synchronized (inMutex)
    {
      inputStream = null;
    }
    synchronized (outMutex)
    {
      outputStream = null;
      outPrintStream = null;
    }
    synchronized (errMutex)
    {
      errorStream = null;
      errorPrintStream = null;
    }
  }
  
  public void setProperties()
  {
    synchronized (outMutex)
    {
      FileUtils.close(baos);
    }
    synchronized (errMutex)
    {
      FileUtils.close(errorBaos);
    }
  }
  
  private OutputStream foldFiles(File[] file, String logHead, int loglevel, boolean append, boolean createEmptyFiles)
  {
    OutputStream result = new LazyFileOutputStream(file[0], append, createEmptyFiles);
    
    managingTask.log(logHead + file[0], loglevel);
    char[] c = new char[logHead.length()];
    Arrays.fill(c, ' ');
    String indent = new String(c);
    for (int i = 1; i < file.length; i++)
    {
      outputStream = new TeeOutputStream(outputStream, new LazyFileOutputStream(file[i], append, createEmptyFiles));
      
      managingTask.log(indent + file[i], loglevel);
    }
    return result;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.Redirector
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */