package org.apache.tools.ant.taskdefs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Hashtable;
import java.util.StringTokenizer;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.util.StringUtils;

@Deprecated
public class KeySubst
  extends Task
{
  private File source = null;
  private File dest = null;
  private String sep = "*";
  private Hashtable<String, String> replacements = new Hashtable();
  
  public void execute()
    throws BuildException
  {
    log("!! KeySubst is deprecated. Use Filter + Copy instead. !!");
    log("Performing Substitutions");
    if ((source == null) || (dest == null))
    {
      log("Source and destinations must not be null");
      return;
    }
    BufferedReader br = null;
    BufferedWriter bw = null;
    try
    {
      br = new BufferedReader(new FileReader(source));
      dest.delete();
      bw = new BufferedWriter(new FileWriter(dest));
      
      String line = null;
      String newline = null;
      line = br.readLine();
      while (line != null)
      {
        if (line.isEmpty())
        {
          bw.newLine();
        }
        else
        {
          newline = replace(line, replacements);
          bw.write(newline);
          bw.newLine();
        }
        line = br.readLine();
      }
      bw.flush();
    }
    catch (IOException ioe)
    {
      log(StringUtils.getStackTrace(ioe), 0);
    }
    finally
    {
      FileUtils.close(bw);
      FileUtils.close(br);
    }
  }
  
  public void setSrc(File s)
  {
    source = s;
  }
  
  public void setDest(File dest)
  {
    this.dest = dest;
  }
  
  public void setSep(String sep)
  {
    this.sep = sep;
  }
  
  public void setKeys(String keys)
  {
    if ((keys != null) && (!keys.isEmpty()))
    {
      StringTokenizer tok = new StringTokenizer(keys, sep, false);
      while (tok.hasMoreTokens())
      {
        String token = tok.nextToken().trim();
        StringTokenizer itok = new StringTokenizer(token, "=", false);
        
        String name = itok.nextToken();
        String value = itok.nextToken();
        replacements.put(name, value);
      }
    }
  }
  
  public static void main(String[] args)
  {
    try
    {
      Hashtable<String, String> hash = new Hashtable();
      hash.put("VERSION", "1.0.3");
      hash.put("b", "ffff");
      System.out.println(replace("$f ${VERSION} f ${b} jj $", hash));
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
  
  public static String replace(String origString, Hashtable<String, String> keys)
    throws BuildException
  {
    StringBuffer finalString = new StringBuffer();
    int index = 0;
    int i = 0;
    String key = null;
    while ((index = origString.indexOf("${", i)) > -1)
    {
      key = origString.substring(index + 2, origString.indexOf("}", index + 3));
      
      finalString.append(origString, i, index);
      if (keys.containsKey(key))
      {
        finalString.append((String)keys.get(key));
      }
      else
      {
        finalString.append("${");
        finalString.append(key);
        finalString.append("}");
      }
      i = index + 3 + key.length();
    }
    finalString.append(origString.substring(i));
    return finalString.toString();
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.KeySubst
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */