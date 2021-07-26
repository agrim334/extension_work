package org.apache.tools.ant.taskdefs.optional;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.RegularExpression;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.Substitution;
import org.apache.tools.ant.types.resources.FileProvider;
import org.apache.tools.ant.types.resources.Union;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.util.regexp.Regexp;
import org.apache.tools.ant.util.regexp.RegexpUtil;

public class ReplaceRegExp
  extends Task
{
  private File file;
  private String flags;
  private boolean byline;
  private Union resources;
  private RegularExpression regex;
  private Substitution subs;
  private static final FileUtils FILE_UTILS = ;
  private boolean preserveLastModified = false;
  private String encoding = null;
  
  public ReplaceRegExp()
  {
    file = null;
    flags = "";
    byline = false;
    
    regex = null;
    subs = null;
  }
  
  public void setFile(File file)
  {
    this.file = file;
  }
  
  public void setMatch(String match)
  {
    if (regex != null) {
      throw new BuildException("Only one regular expression is allowed");
    }
    regex = new RegularExpression();
    regex.setPattern(match);
  }
  
  public void setReplace(String replace)
  {
    if (subs != null) {
      throw new BuildException("Only one substitution expression is allowed");
    }
    subs = new Substitution();
    subs.setExpression(replace);
  }
  
  public void setFlags(String flags)
  {
    this.flags = flags;
  }
  
  @Deprecated
  public void setByLine(String byline)
  {
    this.byline = Boolean.parseBoolean(byline);
  }
  
  public void setByLine(boolean byline)
  {
    this.byline = byline;
  }
  
  public void setEncoding(String encoding)
  {
    this.encoding = encoding;
  }
  
  public void addFileset(FileSet set)
  {
    addConfigured(set);
  }
  
  public void addConfigured(ResourceCollection rc)
  {
    if (!rc.isFilesystemOnly()) {
      throw new BuildException("only filesystem resources are supported");
    }
    if (resources == null) {
      resources = new Union();
    }
    resources.add(rc);
  }
  
  public RegularExpression createRegexp()
  {
    if (regex != null) {
      throw new BuildException("Only one regular expression is allowed.");
    }
    regex = new RegularExpression();
    return regex;
  }
  
  public Substitution createSubstitution()
  {
    if (subs != null) {
      throw new BuildException("Only one substitution expression is allowed");
    }
    subs = new Substitution();
    return subs;
  }
  
  public void setPreserveLastModified(boolean b)
  {
    preserveLastModified = b;
  }
  
  protected String doReplace(RegularExpression r, Substitution s, String input, int options)
  {
    String res = input;
    Regexp regexp = r.getRegexp(getProject());
    if (regexp.matches(input, options))
    {
      log("Found match; substituting", 4);
      res = regexp.substitute(input, s.getExpression(getProject()), options);
    }
    return res;
  }
  
  protected void doReplace(File f, int options)
    throws IOException
  {
    File temp = FILE_UTILS.createTempFile(getProject(), "replace", ".txt", null, true, true);
    try
    {
      boolean changes = false;
      
      Charset charset = encoding == null ? Charset.defaultCharset() : Charset.forName(encoding);
      InputStream is = Files.newInputStream(f.toPath(), new OpenOption[0]);
      try
      {
        OutputStream os = Files.newOutputStream(temp.toPath(), new OpenOption[0]);
        try
        {
          Reader r = null;
          Writer w = null;
          try
          {
            r = new InputStreamReader(is, charset);
            w = new OutputStreamWriter(os, charset);
            log("Replacing pattern '" + regex.getPattern(getProject()) + "' with '" + subs
              .getExpression(getProject()) + "' in '" + f
              .getPath() + "'" + (byline ? " by line" : "") + (
              flags.isEmpty() ? "" : new StringBuilder().append(" with flags: '").append(flags).append("'").toString()) + ".", 3);
            if (byline)
            {
              r = new BufferedReader(r);
              w = new BufferedWriter(w);
              
              StringBuilder linebuf = new StringBuilder();
              
              boolean hasCR = false;
              int c;
              do
              {
                c = r.read();
                if (c == 13)
                {
                  if (hasCR)
                  {
                    changes |= replaceAndWrite(linebuf.toString(), w, options);
                    
                    w.write(13);
                    
                    linebuf = new StringBuilder();
                  }
                  else
                  {
                    hasCR = true;
                  }
                }
                else if (c == 10)
                {
                  changes |= replaceAndWrite(linebuf.toString(), w, options);
                  if (hasCR)
                  {
                    w.write(13);
                    hasCR = false;
                  }
                  w.write(10);
                  
                  linebuf = new StringBuilder();
                }
                else
                {
                  if ((hasCR) || (c < 0))
                  {
                    changes |= replaceAndWrite(linebuf.toString(), w, options);
                    if (hasCR)
                    {
                      w.write(13);
                      hasCR = false;
                    }
                    linebuf = new StringBuilder();
                  }
                  if (c >= 0) {
                    linebuf.append((char)c);
                  }
                }
              } while (c >= 0);
            }
            else
            {
              changes = multilineReplace(r, w, options);
            }
          }
          finally
          {
            FileUtils.close(r);
            FileUtils.close(w);
          }
          if (os == null) {
            break label550;
          }
          os.close();
        }
        catch (Throwable localThrowable)
        {
          if (os == null) {
            break label547;
          }
        }
        try
        {
          os.close();
        }
        catch (Throwable localThrowable1)
        {
          localThrowable.addSuppressed(localThrowable1);
        }
        label547:
        throw localThrowable;
      }
      catch (Throwable localThrowable2)
      {
        label550:
        if (is == null) {
          break label587;
        }
      }
      if (is != null)
      {
        is.close();
        break label590;
        try
        {
          is.close();
        }
        catch (Throwable localThrowable3)
        {
          localThrowable2.addSuppressed(localThrowable3);
        }
        label587:
        throw localThrowable2;
      }
      label590:
      if (changes)
      {
        log("File has changed; saving the updated file", 3);
        try
        {
          long origLastModified = f.lastModified();
          FILE_UTILS.rename(temp, f);
          if (preserveLastModified) {
            FILE_UTILS.setFileLastModified(f, origLastModified);
          }
          temp = null;
        }
        catch (IOException e)
        {
          throw new BuildException("Couldn't rename temporary file " + temp, e, getLocation());
        }
      }
      else
      {
        log("No change made", 4);
      }
    }
    finally
    {
      if (temp != null) {
        temp.delete();
      }
    }
  }
  
  public void execute()
    throws BuildException
  {
    if (regex == null) {
      throw new BuildException("No expression to match.");
    }
    if (subs == null) {
      throw new BuildException("Nothing to replace expression with.");
    }
    if ((file != null) && (resources != null)) {
      throw new BuildException("You cannot supply the 'file' attribute and resource collections at the same time.");
    }
    int options = RegexpUtil.asOptions(flags);
    if ((file != null) && (file.exists())) {
      try
      {
        doReplace(file, options);
      }
      catch (IOException e)
      {
        log("An error occurred processing file: '" + file
          .getAbsolutePath() + "': " + e.toString(), 0);
      }
    } else if (file != null) {
      log("The following file is missing: '" + file
        .getAbsolutePath() + "'", 0);
    }
    if (resources != null) {
      for (Resource r : resources)
      {
        File f = ((FileProvider)r.as(FileProvider.class)).getFile();
        if (f.exists()) {
          try
          {
            doReplace(f, options);
          }
          catch (Exception e)
          {
            log("An error occurred processing file: '" + f
              .getAbsolutePath() + "': " + e.toString(), 0);
          }
        } else {
          log("The following file is missing: '" + f
            .getAbsolutePath() + "'", 0);
        }
      }
    }
  }
  
  private boolean multilineReplace(Reader r, Writer w, int options)
    throws IOException
  {
    return replaceAndWrite(FileUtils.safeReadFully(r), w, options);
  }
  
  private boolean replaceAndWrite(String s, Writer w, int options)
    throws IOException
  {
    String res = doReplace(regex, subs, s, options);
    w.write(res);
    return !res.equals(s);
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.optional.ReplaceRegExp
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */