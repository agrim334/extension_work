package org.apache.tools.ant.taskdefs;

import java.io.File;
import java.io.IOException;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.condition.IsSigned;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.resources.FileProvider;
import org.apache.tools.ant.types.resources.FileResource;
import org.apache.tools.ant.util.FileNameMapper;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.util.IdentityMapper;
import org.apache.tools.ant.util.ResourceUtils;

public class SignJar
  extends AbstractJarSignerTask
{
  private static final FileUtils FILE_UTILS = ;
  public static final String ERROR_TODIR_AND_SIGNEDJAR = "'destdir' and 'signedjar' cannot both be set";
  public static final String ERROR_TOO_MANY_MAPPERS = "Too many mappers";
  public static final String ERROR_SIGNEDJAR_AND_PATHS = "You cannot specify the signed JAR when using paths or filesets";
  public static final String ERROR_BAD_MAP = "Cannot map source file to anything sensible: ";
  public static final String ERROR_MAPPER_WITHOUT_DEST = "The destDir attribute is required if a mapper is set";
  public static final String ERROR_NO_ALIAS = "alias attribute must be set";
  public static final String ERROR_NO_STOREPASS = "storepass attribute must be set";
  protected String sigfile;
  protected File signedjar;
  protected boolean internalsf;
  protected boolean sectionsonly;
  private boolean preserveLastModified;
  protected boolean lazy;
  protected File destDir;
  private FileNameMapper mapper;
  protected String tsaurl;
  protected String tsaproxyhost;
  protected String tsaproxyport;
  protected String tsacert;
  private boolean force = false;
  private String sigAlg;
  private String digestAlg;
  private String tsaDigestAlg;
  
  public void setSigfile(String sigfile)
  {
    this.sigfile = sigfile;
  }
  
  public void setSignedjar(File signedjar)
  {
    this.signedjar = signedjar;
  }
  
  public void setInternalsf(boolean internalsf)
  {
    this.internalsf = internalsf;
  }
  
  public void setSectionsonly(boolean sectionsonly)
  {
    this.sectionsonly = sectionsonly;
  }
  
  public void setLazy(boolean lazy)
  {
    this.lazy = lazy;
  }
  
  public void setDestDir(File destDir)
  {
    this.destDir = destDir;
  }
  
  public void add(FileNameMapper newMapper)
  {
    if (mapper != null) {
      throw new BuildException("Too many mappers");
    }
    mapper = newMapper;
  }
  
  public FileNameMapper getMapper()
  {
    return mapper;
  }
  
  public String getTsaurl()
  {
    return tsaurl;
  }
  
  public void setTsaurl(String tsaurl)
  {
    this.tsaurl = tsaurl;
  }
  
  public String getTsaproxyhost()
  {
    return tsaproxyhost;
  }
  
  public void setTsaproxyhost(String tsaproxyhost)
  {
    this.tsaproxyhost = tsaproxyhost;
  }
  
  public String getTsaproxyport()
  {
    return tsaproxyport;
  }
  
  public void setTsaproxyport(String tsaproxyport)
  {
    this.tsaproxyport = tsaproxyport;
  }
  
  public String getTsacert()
  {
    return tsacert;
  }
  
  public void setTsacert(String tsacert)
  {
    this.tsacert = tsacert;
  }
  
  public void setForce(boolean b)
  {
    force = b;
  }
  
  public boolean isForce()
  {
    return force;
  }
  
  public void setSigAlg(String sigAlg)
  {
    this.sigAlg = sigAlg;
  }
  
  public String getSigAlg()
  {
    return sigAlg;
  }
  
  public void setDigestAlg(String digestAlg)
  {
    this.digestAlg = digestAlg;
  }
  
  public String getDigestAlg()
  {
    return digestAlg;
  }
  
  public void setTSADigestAlg(String digestAlg)
  {
    tsaDigestAlg = digestAlg;
  }
  
  public String getTSADigestAlg()
  {
    return tsaDigestAlg;
  }
  
  public void execute()
    throws BuildException
  {
    boolean hasJar = jar != null;
    boolean hasSignedJar = signedjar != null;
    boolean hasDestDir = destDir != null;
    boolean hasMapper = mapper != null;
    if ((!hasJar) && (!hasResources())) {
      throw new BuildException("jar must be set through jar attribute or nested filesets");
    }
    if (null == alias) {
      throw new BuildException("alias attribute must be set");
    }
    if (null == storepass) {
      throw new BuildException("storepass attribute must be set");
    }
    if ((hasDestDir) && (hasSignedJar)) {
      throw new BuildException("'destdir' and 'signedjar' cannot both be set");
    }
    if ((hasResources()) && (hasSignedJar)) {
      throw new BuildException("You cannot specify the signed JAR when using paths or filesets");
    }
    if ((!hasDestDir) && (hasMapper)) {
      throw new BuildException("The destDir attribute is required if a mapper is set");
    }
    beginExecution();
    try
    {
      if ((hasJar) && (hasSignedJar))
      {
        signOneJar(jar, signedjar);
        
        return;
      }
      Path sources = createUnifiedSourcePath();
      
      destMapper = hasMapper ? mapper : new IdentityMapper();
      for (Resource r : sources)
      {
        FileResource fr = ResourceUtils.asFileResource((FileProvider)r.as(FileProvider.class));
        
        File toDir = hasDestDir ? destDir : fr.getBaseDir();
        
        String[] destFilenames = destMapper.mapFileName(fr.getName());
        if ((destFilenames == null) || (destFilenames.length != 1)) {
          throw new BuildException("Cannot map source file to anything sensible: " + fr.getFile());
        }
        File destFile = new File(toDir, destFilenames[0]);
        signOneJar(fr.getFile(), destFile);
      }
    }
    finally
    {
      FileNameMapper destMapper;
      endExecution();
    }
  }
  
  private void signOneJar(File jarSource, File jarTarget)
    throws BuildException
  {
    File targetFile = jarTarget;
    if (targetFile == null) {
      targetFile = jarSource;
    }
    if (isUpToDate(jarSource, targetFile)) {
      return;
    }
    long lastModified = jarSource.lastModified();
    ExecTask cmd = createJarSigner();
    
    setCommonOptions(cmd);
    
    bindToKeystore(cmd);
    if (null != sigfile)
    {
      addValue(cmd, "-sigfile");
      String value = sigfile;
      addValue(cmd, value);
    }
    try
    {
      if (!FILE_UTILS.areSame(jarSource, targetFile))
      {
        addValue(cmd, "-signedjar");
        addValue(cmd, targetFile.getPath());
      }
    }
    catch (IOException ioex)
    {
      throw new BuildException(ioex);
    }
    if (internalsf) {
      addValue(cmd, "-internalsf");
    }
    if (sectionsonly) {
      addValue(cmd, "-sectionsonly");
    }
    if (sigAlg != null)
    {
      addValue(cmd, "-sigalg");
      addValue(cmd, sigAlg);
    }
    if (digestAlg != null)
    {
      addValue(cmd, "-digestalg");
      addValue(cmd, digestAlg);
    }
    addTimestampAuthorityCommands(cmd);
    
    addValue(cmd, jarSource.getPath());
    
    addValue(cmd, alias);
    
    log("Signing JAR: " + jarSource
      .getAbsolutePath() + " to " + targetFile
      
      .getAbsolutePath() + " as " + alias);
    
    cmd.execute();
    if (preserveLastModified) {
      FILE_UTILS.setFileLastModified(targetFile, lastModified);
    }
  }
  
  private void addTimestampAuthorityCommands(ExecTask cmd)
  {
    if (tsaurl != null)
    {
      addValue(cmd, "-tsa");
      addValue(cmd, tsaurl);
    }
    if (tsacert != null)
    {
      addValue(cmd, "-tsacert");
      addValue(cmd, tsacert);
    }
    if (tsaproxyhost != null)
    {
      if ((tsaurl == null) || (tsaurl.startsWith("https"))) {
        addProxyFor(cmd, "https");
      }
      if ((tsaurl == null) || (!tsaurl.startsWith("https"))) {
        addProxyFor(cmd, "http");
      }
    }
    if (tsaDigestAlg != null)
    {
      addValue(cmd, "-tsadigestalg");
      addValue(cmd, tsaDigestAlg);
    }
  }
  
  protected boolean isUpToDate(File jarFile, File signedjarFile)
  {
    if ((isForce()) || (null == jarFile) || (!jarFile.exists())) {
      return false;
    }
    File destFile = signedjarFile;
    if (destFile == null) {
      destFile = jarFile;
    }
    if (jarFile.equals(destFile))
    {
      if (lazy) {
        return isSigned(jarFile);
      }
      return false;
    }
    return FILE_UTILS.isUpToDate(jarFile, destFile);
  }
  
  protected boolean isSigned(File file)
  {
    try
    {
      return IsSigned.isSigned(file, sigfile == null ? alias : sigfile);
    }
    catch (IOException e)
    {
      log(e.toString(), 3);
    }
    return false;
  }
  
  public void setPreserveLastModified(boolean preserveLastModified)
  {
    this.preserveLastModified = preserveLastModified;
  }
  
  private void addProxyFor(ExecTask cmd, String scheme)
  {
    addValue(cmd, "-J-D" + scheme + ".proxyHost=" + tsaproxyhost);
    if (tsaproxyport != null) {
      addValue(cmd, "-J-D" + scheme + ".proxyPort=" + tsaproxyport);
    }
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.SignJar
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */