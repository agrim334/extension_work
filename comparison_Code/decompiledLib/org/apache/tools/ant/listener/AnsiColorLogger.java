package org.apache.tools.ant.listener;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import java.util.Properties;
import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.util.FileUtils;

public class AnsiColorLogger
  extends DefaultLogger
{
  private static final int ATTR_DIM = 2;
  private static final int FG_RED = 31;
  private static final int FG_GREEN = 32;
  private static final int FG_BLUE = 34;
  private static final int FG_MAGENTA = 35;
  private static final int FG_CYAN = 36;
  private static final String PREFIX = "\033[";
  private static final String SUFFIX = "m";
  private static final char SEPARATOR = ';';
  private static final String END_COLOR = "\033[m";
  private String errColor = "\033[2;31m";
  private String warnColor = "\033[2;35m";
  private String infoColor = "\033[2;36m";
  private String verboseColor = "\033[2;32m";
  private String debugColor = "\033[2;34m";
  private boolean colorsSet = false;
  
  private void setColors()
  {
    String userColorFile = System.getProperty("ant.logger.defaults");
    String systemColorFile = "/org/apache/tools/ant/listener/defaults.properties";
    
    InputStream in = null;
    try
    {
      Properties prop = new Properties();
      if (userColorFile != null) {
        in = Files.newInputStream(Paths.get(userColorFile, new String[0]), new OpenOption[0]);
      } else {
        in = getClass().getResourceAsStream(systemColorFile);
      }
      if (in != null) {
        prop.load(in);
      }
      String errC = prop.getProperty("AnsiColorLogger.ERROR_COLOR");
      String warn = prop.getProperty("AnsiColorLogger.WARNING_COLOR");
      String info = prop.getProperty("AnsiColorLogger.INFO_COLOR");
      String verbose = prop.getProperty("AnsiColorLogger.VERBOSE_COLOR");
      String debug = prop.getProperty("AnsiColorLogger.DEBUG_COLOR");
      if (errC != null) {
        errColor = ("\033[" + errC + "m");
      }
      if (warn != null) {
        warnColor = ("\033[" + warn + "m");
      }
      if (info != null) {
        infoColor = ("\033[" + info + "m");
      }
      if (verbose != null) {
        verboseColor = ("\033[" + verbose + "m");
      }
      if (debug != null) {
        debugColor = ("\033[" + debug + "m");
      }
    }
    catch (IOException localIOException) {}finally
    {
      FileUtils.close(in);
    }
  }
  
  protected void printMessage(String message, PrintStream stream, int priority)
  {
    if ((message != null) && (stream != null))
    {
      if (!colorsSet)
      {
        setColors();
        colorsSet = true;
      }
      StringBuilder msg = new StringBuilder(message);
      switch (priority)
      {
      case 0: 
        msg.insert(0, errColor);
        msg.append("\033[m");
        break;
      case 1: 
        msg.insert(0, warnColor);
        msg.append("\033[m");
        break;
      case 2: 
        msg.insert(0, infoColor);
        msg.append("\033[m");
        break;
      case 3: 
        msg.insert(0, verboseColor);
        msg.append("\033[m");
        break;
      case 4: 
      default: 
        msg.insert(0, debugColor);
        msg.append("\033[m");
      }
      String strmessage = msg.toString();
      stream.println(strmessage);
    }
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.listener.AnsiColorLogger
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */