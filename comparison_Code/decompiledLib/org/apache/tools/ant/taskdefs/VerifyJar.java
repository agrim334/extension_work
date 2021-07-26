package org.apache.tools.ant.taskdefs;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.filters.ChainableReader;
import org.apache.tools.ant.types.FilterChain;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.RedirectorElement;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.resources.FileProvider;

public class VerifyJar
  extends AbstractJarSignerTask
{
  public static final String ERROR_NO_FILE = "Not found :";
  public static final String ERROR_NO_VERIFY = "Failed to verify ";
  private static final String VERIFIED_TEXT = "jar verified.";
  private boolean certificates = false;
  private BufferingOutputFilter outputCache = new BufferingOutputFilter(null);
  private String savedStorePass = null;
  
  public void setCertificates(boolean certificates)
  {
    this.certificates = certificates;
  }
  
  public void execute()
    throws BuildException
  {
    boolean hasJar = jar != null;
    if ((!hasJar) && (!hasResources())) {
      throw new BuildException("jar must be set through jar attribute or nested filesets");
    }
    beginExecution();
    
    RedirectorElement redirector = getRedirector();
    redirector.setAlwaysLog(true);
    FilterChain outputFilterChain = redirector.createOutputFilterChain();
    outputFilterChain.add(outputCache);
    try
    {
      Path sources = createUnifiedSourcePath();
      for (Resource r : sources)
      {
        FileProvider fr = (FileProvider)r.as(FileProvider.class);
        verifyOneJar(fr.getFile());
      }
    }
    finally
    {
      endExecution();
    }
  }
  
  protected void beginExecution()
  {
    if (storepass != null)
    {
      savedStorePass = storepass;
      setStorepass(null);
    }
    super.beginExecution();
  }
  
  protected void endExecution()
  {
    if (savedStorePass != null)
    {
      setStorepass(savedStorePass);
      savedStorePass = null;
    }
    super.endExecution();
  }
  
  private void verifyOneJar(File jar)
  {
    if (!jar.exists()) {
      throw new BuildException("Not found :" + jar);
    }
    ExecTask cmd = createJarSigner();
    
    setCommonOptions(cmd);
    bindToKeystore(cmd);
    if (savedStorePass != null)
    {
      addValue(cmd, "-storepass");
      addValue(cmd, savedStorePass);
    }
    addValue(cmd, "-verify");
    if (certificates) {
      addValue(cmd, "-certs");
    }
    addValue(cmd, jar.getPath());
    if (alias != null) {
      addValue(cmd, alias);
    }
    log("Verifying JAR: " + jar.getAbsolutePath());
    outputCache.clear();
    BuildException ex = null;
    try
    {
      cmd.execute();
    }
    catch (BuildException e)
    {
      ex = e;
    }
    String results = outputCache.toString();
    if (ex != null) {
      if (results.contains("zip file closed")) {
        log("You are running jarsigner against a JVM with a known bug that manifests as an IllegalStateException.", 1);
      } else {
        throw ex;
      }
    }
    if (!results.contains("jar verified.")) {
      throw new BuildException("Failed to verify " + jar);
    }
  }
  
  private static class BufferingOutputFilter
    implements ChainableReader
  {
    private VerifyJar.BufferingOutputFilterReader buffer;
    
    public Reader chain(Reader rdr)
    {
      buffer = new VerifyJar.BufferingOutputFilterReader(rdr);
      return buffer;
    }
    
    public String toString()
    {
      return buffer.toString();
    }
    
    public void clear()
    {
      if (buffer != null) {
        buffer.clear();
      }
    }
  }
  
  private static class BufferingOutputFilterReader
    extends Reader
  {
    private Reader next;
    private StringBuffer buffer = new StringBuffer();
    
    public BufferingOutputFilterReader(Reader next)
    {
      this.next = next;
    }
    
    public int read(char[] cbuf, int off, int len)
      throws IOException
    {
      int result = next.read(cbuf, off, len);
      
      buffer.append(cbuf, off, len);
      
      return result;
    }
    
    public void close()
      throws IOException
    {
      next.close();
    }
    
    public String toString()
    {
      return buffer.toString();
    }
    
    public void clear()
    {
      buffer = new StringBuffer();
    }
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.VerifyJar
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */