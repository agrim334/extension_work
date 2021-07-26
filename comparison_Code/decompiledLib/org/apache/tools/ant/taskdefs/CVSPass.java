package org.apache.tools.ant.taskdefs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.util.FileUtils;

public class CVSPass
  extends Task
{
  private String cvsRoot = null;
  private File passFile = null;
  private String password = null;
  private final char[] shifts = { '\000', '\001', '\002', '\003', '\004', '\005', '\006', '\007', '\b', '\t', '\n', '\013', '\f', '\r', '\016', '\017', '\020', '\021', '\022', '\023', '\024', '\025', '\026', '\027', '\030', '\031', '\032', '\033', '\034', '\035', '\036', '\037', 'r', 'x', '5', 'O', '`', 'm', 'H', 'l', 'F', '@', 'L', 'C', 't', 'J', 'D', 'W', 'o', '4', 'K', 'w', '1', '"', 'R', 'Q', '_', 'A', 'p', 'V', 'v', 'n', 'z', 'i', ')', '9', 'S', '+', '.', 'f', '(', 'Y', '&', 'g', '-', '2', '*', '{', '[', '#', '}', '7', '6', 'B', '|', '~', ';', '/', '\\', 'G', 's', 'N', 'X', 'k', 'j', '8', '$', 'y', 'u', 'h', 'e', 'd', 'E', 'I', 'c', '?', '^', ']', '\'', '%', '=', '0', ':', 'q', ' ', 'Z', ',', 'b', '<', '3', '!', 'a', '>', 'M', 'T', 'P', 'U', 'ß', 'á', 'Ø', '»', '¦', 'å', '½', 'Þ', '¼', '', 'ù', '', 'È', '¸', '', 'ø', '¾', 'Ç', 'ª', 'µ', 'Ì', '', 'è', 'Ú', '·', 'ÿ', 'ê', 'Ü', '÷', 'Õ', 'Ë', 'â', 'Á', '®', '¬', 'ä', 'ü', 'Ù', 'É', '', 'æ', 'Å', 'Ó', '', 'î', '¡', '³', ' ', 'Ô', 'Ï', 'Ý', 'þ', '­', 'Ê', '', 'à', '', '', 'Ä', 'Í', '', '', '', '', 'ö', 'À', '', 'ô', 'ï', '¹', '¨', '×', '', '', '¥', '´', '', '', 'º', 'Ö', '°', 'ã', 'ç', 'Û', '©', '¯', '', 'Î', 'Æ', '', '¤', '', 'Ò', '', '±', '', '', '¶', '', '', 'Ð', '¢', '', '§', 'Ñ', '', 'ñ', '', 'û', 'í', 'ì', '«', 'Ã', 'ó', 'é', 'ý', 'ð', 'Â', 'ú', '¿', '', '', '', 'õ', 'ë', '£', 'ò', '²', '' };
  
  public CVSPass()
  {
    passFile = new File(System.getProperty("cygwin.user.home", 
      System.getProperty("user.home")) + File.separatorChar + ".cvspass");
  }
  
  public final void execute()
    throws BuildException
  {
    if (cvsRoot == null) {
      throw new BuildException("cvsroot is required");
    }
    if (password == null) {
      throw new BuildException("password is required");
    }
    log("cvsRoot: " + cvsRoot, 4);
    log("password: " + password, 4);
    log("passFile: " + passFile, 4);
    
    BufferedReader reader = null;
    BufferedWriter writer = null;
    try
    {
      StringBuilder buf = new StringBuilder();
      if (passFile.exists())
      {
        reader = new BufferedReader(new FileReader(passFile));
        
        String line = null;
        while ((line = reader.readLine()) != null) {
          if (!line.startsWith(cvsRoot)) {
            buf.append(line).append(System.lineSeparator());
          }
        }
      }
      String pwdfile = buf.toString() + cvsRoot + " A" + mangle(password);
      
      log("Writing -> " + pwdfile, 4);
      
      writer = new BufferedWriter(new FileWriter(passFile));
      
      writer.write(pwdfile);
      writer.newLine();
    }
    catch (IOException e)
    {
      throw new BuildException(e);
    }
    finally
    {
      FileUtils.close(reader);
      FileUtils.close(writer);
    }
  }
  
  private final String mangle(String password)
  {
    StringBuilder buf = new StringBuilder();
    for (char ch : password.toCharArray()) {
      buf.append(shifts[ch]);
    }
    return buf.toString();
  }
  
  public void setCvsroot(String cvsRoot)
  {
    this.cvsRoot = cvsRoot;
  }
  
  public void setPassfile(File passFile)
  {
    this.passFile = passFile;
  }
  
  public void setPassword(String password)
  {
    this.password = password;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.CVSPass
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */