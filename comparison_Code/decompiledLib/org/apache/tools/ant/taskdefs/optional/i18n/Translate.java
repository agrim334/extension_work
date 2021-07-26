package org.apache.tools.ant.taskdefs.optional.i18n;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.MatchingTask;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.util.LineTokenizer;

public class Translate
  extends MatchingTask
{
  private static final int BUNDLE_SPECIFIED_LANGUAGE_COUNTRY_VARIANT = 0;
  private static final int BUNDLE_SPECIFIED_LANGUAGE_COUNTRY = 1;
  private static final int BUNDLE_SPECIFIED_LANGUAGE = 2;
  private static final int BUNDLE_NOMATCH = 3;
  private static final int BUNDLE_DEFAULT_LANGUAGE_COUNTRY_VARIANT = 4;
  private static final int BUNDLE_DEFAULT_LANGUAGE_COUNTRY = 5;
  private static final int BUNDLE_DEFAULT_LANGUAGE = 6;
  private static final int BUNDLE_MAX_ALTERNATIVES = 7;
  private String bundle;
  private String bundleLanguage;
  private String bundleCountry;
  private String bundleVariant;
  private File toDir;
  private String srcEncoding;
  private String destEncoding;
  private String bundleEncoding;
  private String startToken;
  private String endToken;
  private boolean forceOverwrite;
  private List<FileSet> filesets = new Vector();
  private Map<String, String> resourceMap = new Hashtable();
  private static final FileUtils FILE_UTILS = ;
  private long[] bundleLastModified = new long[7];
  private long srcLastModified;
  private long destLastModified;
  private boolean loaded = false;
  
  public void setBundle(String bundle)
  {
    this.bundle = bundle;
  }
  
  public void setBundleLanguage(String bundleLanguage)
  {
    this.bundleLanguage = bundleLanguage;
  }
  
  public void setBundleCountry(String bundleCountry)
  {
    this.bundleCountry = bundleCountry;
  }
  
  public void setBundleVariant(String bundleVariant)
  {
    this.bundleVariant = bundleVariant;
  }
  
  public void setToDir(File toDir)
  {
    this.toDir = toDir;
  }
  
  public void setStartToken(String startToken)
  {
    this.startToken = startToken;
  }
  
  public void setEndToken(String endToken)
  {
    this.endToken = endToken;
  }
  
  public void setSrcEncoding(String srcEncoding)
  {
    this.srcEncoding = srcEncoding;
  }
  
  public void setDestEncoding(String destEncoding)
  {
    this.destEncoding = destEncoding;
  }
  
  public void setBundleEncoding(String bundleEncoding)
  {
    this.bundleEncoding = bundleEncoding;
  }
  
  public void setForceOverwrite(boolean forceOverwrite)
  {
    this.forceOverwrite = forceOverwrite;
  }
  
  public void addFileset(FileSet set)
  {
    filesets.add(set);
  }
  
  public void execute()
    throws BuildException
  {
    if (bundle == null) {
      throw new BuildException("The bundle attribute must be set.", getLocation());
    }
    if (startToken == null) {
      throw new BuildException("The starttoken attribute must be set.", getLocation());
    }
    if (endToken == null) {
      throw new BuildException("The endtoken attribute must be set.", getLocation());
    }
    if (bundleLanguage == null)
    {
      Locale l = Locale.getDefault();
      bundleLanguage = l.getLanguage();
    }
    if (bundleCountry == null) {
      bundleCountry = Locale.getDefault().getCountry();
    }
    if (bundleVariant == null)
    {
      Locale l = new Locale(bundleLanguage, bundleCountry);
      bundleVariant = l.getVariant();
    }
    if (toDir == null) {
      throw new BuildException("The todir attribute must be set.", getLocation());
    }
    if (!toDir.exists()) {
      toDir.mkdirs();
    } else if (toDir.isFile()) {
      throw new BuildException("%s is not a directory", new Object[] { toDir });
    }
    if (srcEncoding == null) {
      srcEncoding = System.getProperty("file.encoding");
    }
    if (destEncoding == null) {
      destEncoding = srcEncoding;
    }
    if (bundleEncoding == null) {
      bundleEncoding = srcEncoding;
    }
    loadResourceMaps();
    
    translate();
  }
  
  private void loadResourceMaps()
    throws BuildException
  {
    Locale locale = new Locale(bundleLanguage, bundleCountry, bundleVariant);
    
    String language = "_" + locale.getLanguage();
    String country = "_" + locale.getCountry();
    String variant = "_" + locale.getVariant();
    
    processBundle(bundle + language + country + variant, 0, false);
    processBundle(bundle + language + country, 1, false);
    processBundle(bundle + language, 2, false);
    processBundle(bundle, 3, false);
    
    locale = Locale.getDefault();
    
    language = "_" + locale.getLanguage();
    country = "_" + locale.getCountry();
    variant = "_" + locale.getVariant();
    bundleEncoding = System.getProperty("file.encoding");
    
    processBundle(bundle + language + country + variant, 4, false);
    processBundle(bundle + language + country, 5, false);
    processBundle(bundle + language, 6, true);
  }
  
  private void processBundle(String bundleFile, int i, boolean checkLoaded)
    throws BuildException
  {
    File propsFile = getProject().resolveFile(bundleFile + ".properties");
    InputStream ins = null;
    try
    {
      ins = Files.newInputStream(propsFile.toPath(), new OpenOption[0]);
      loaded = true;
      bundleLastModified[i] = propsFile.lastModified();
      log("Using " + propsFile, 4);
      loadResourceMap(ins);
    }
    catch (IOException ioe)
    {
      log(propsFile + " not found.", 4);
      if ((!loaded) && (checkLoaded)) {
        throw new BuildException(ioe.getMessage(), getLocation());
      }
    }
  }
  
  private void loadResourceMap(InputStream ins)
    throws BuildException
  {
    try
    {
      BufferedReader in = new BufferedReader(new InputStreamReader(ins, bundleEncoding));
      try
      {
        String line;
        while ((line = in.readLine()) != null) {
          if ((line.trim().length() > 1) && ('#' != line.charAt(0)) && ('!' != line.charAt(0)))
          {
            int sepIndex = line.indexOf('=');
            if (-1 == sepIndex) {
              sepIndex = line.indexOf(':');
            }
            if (-1 == sepIndex) {
              for (int k = 0; k < line.length(); k++) {
                if (Character.isSpaceChar(line.charAt(k)))
                {
                  sepIndex = k;
                  break;
                }
              }
            }
            if (-1 != sepIndex)
            {
              String key = line.substring(0, sepIndex).trim();
              String value = line.substring(sepIndex + 1).trim();
              while (value.endsWith("\\"))
              {
                value = value.substring(0, value.length() - 1);
                line = in.readLine();
                if (line == null) {
                  break;
                }
                value = value + line.trim();
              }
              if (!key.isEmpty()) {
                resourceMap.putIfAbsent(key, value);
              }
            }
          }
        }
        in.close();
      }
      catch (Throwable localThrowable) {}
      try
      {
        in.close();
      }
      catch (Throwable localThrowable1)
      {
        localThrowable.addSuppressed(localThrowable1);
      }
      throw localThrowable;
    }
    catch (IOException ioe)
    {
      throw new BuildException(ioe.getMessage(), getLocation());
    }
  }
  
  private void translate()
    throws BuildException
  {
    int filesProcessed = 0;
    for (FileSet fs : filesets)
    {
      DirectoryScanner ds = fs.getDirectoryScanner(getProject());
      for (String srcFile : ds.getIncludedFiles()) {
        try
        {
          File dest = FILE_UTILS.resolveFile(toDir, srcFile);
          try
          {
            File destDir = new File(dest.getParent());
            if (!destDir.exists()) {
              destDir.mkdirs();
            }
          }
          catch (Exception e)
          {
            log("Exception occurred while trying to check/create  parent directory.  " + e
              .getMessage(), 4);
          }
          destLastModified = dest.lastModified();
          File src = FILE_UTILS.resolveFile(ds.getBasedir(), srcFile);
          srcLastModified = src.lastModified();
          
          boolean needsWork = (forceOverwrite) || (destLastModified < srcLastModified);
          if (!needsWork) {
            for (int icounter = 0; icounter < 7; icounter++)
            {
              needsWork = destLastModified < bundleLastModified[icounter];
              if (needsWork) {
                break;
              }
            }
          }
          if (needsWork)
          {
            log("Processing " + srcFile, 4);
            translateOneFile(src, dest);
            filesProcessed++;
          }
          else
          {
            log("Skipping " + srcFile + " as destination file is up to date", 3);
          }
        }
        catch (IOException ioe)
        {
          throw new BuildException(ioe.getMessage(), getLocation());
        }
      }
    }
    log("Translation performed on " + filesProcessed + " file(s).", 4);
  }
  
  private void translateOneFile(File src, File dest)
    throws IOException
  {
    BufferedWriter out = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(dest.toPath(), new OpenOption[0]), destEncoding));
    try
    {
      BufferedReader in = new BufferedReader(new InputStreamReader(Files.newInputStream(src.toPath(), new OpenOption[0]), srcEncoding));
      try
      {
        LineTokenizer lineTokenizer = new LineTokenizer();
        lineTokenizer.setIncludeDelims(true);
        String line = lineTokenizer.getToken(in);
        while (line != null)
        {
          int startIndex = line.indexOf(startToken);
          while ((startIndex >= 0) && 
            (startIndex + startToken.length() <= line.length()))
          {
            String replace = null;
            
            int endIndex = line.indexOf(endToken, startIndex + startToken
              .length());
            if (endIndex < 0)
            {
              startIndex++;
            }
            else
            {
              String token = line.substring(startIndex + startToken
                .length(), endIndex);
              
              boolean validToken = true;
              for (int k = 0; (k < token.length()) && (validToken); k++)
              {
                char c = token.charAt(k);
                if ((c == ':') || (c == '=') || 
                  (Character.isSpaceChar(c))) {
                  validToken = false;
                }
              }
              if (!validToken)
              {
                startIndex++;
              }
              else
              {
                if (resourceMap.containsKey(token))
                {
                  replace = (String)resourceMap.get(token);
                }
                else
                {
                  log("Replacement string missing for: " + token, 3);
                  
                  replace = startToken + token + endToken;
                }
                line = line.substring(0, startIndex) + replace + line.substring(endIndex + endToken.length());
                
                startIndex += replace.length();
              }
            }
            startIndex = line.indexOf(startToken, startIndex);
          }
          out.write(line);
          line = lineTokenizer.getToken(in);
        }
        in.close();
      }
      catch (Throwable localThrowable) {}
      try
      {
        in.close();
      }
      catch (Throwable localThrowable1)
      {
        localThrowable.addSuppressed(localThrowable1);
      }
      throw localThrowable;
    }
    catch (Throwable localThrowable2) {}
    out.close(); return;
    try
    {
      out.close();
    }
    catch (Throwable localThrowable3)
    {
      localThrowable2.addSuppressed(localThrowable3);
    }
    throw localThrowable2;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.optional.i18n.Translate
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */