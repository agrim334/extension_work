package org.apache.tools.ant.taskdefs.optional.vss;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.taskdefs.LogStreamHandler;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.types.EnumeratedAttribute;
import org.apache.tools.ant.util.FileUtils;

public abstract class MSVSS
  extends Task
  implements MSVSSConstants
{
  private String ssDir = null;
  private String vssLogin = null;
  private String vssPath = null;
  private String serverPath = null;
  private String version = null;
  private String date = null;
  private String label = null;
  private String autoResponse = null;
  private String localPath = null;
  private String comment = null;
  private String fromLabel = null;
  private String toLabel = null;
  private String outputFileName = null;
  private String user = null;
  private String fromDate = null;
  private String toDate = null;
  private String style = null;
  private boolean quiet = false;
  private boolean recursive = false;
  private boolean writable = false;
  private boolean failOnError = true;
  private boolean getLocalCopy = true;
  private int numDays = Integer.MIN_VALUE;
  private DateFormat dateFormat = DateFormat.getDateInstance(3);
  private CurrentModUpdated timestamp = null;
  private WritableFiles writableFiles = null;
  
  abstract Commandline buildCmdLine();
  
  public final void setSsdir(String dir)
  {
    ssDir = FileUtils.translatePath(dir);
  }
  
  public final void setLogin(String vssLogin)
  {
    this.vssLogin = vssLogin;
  }
  
  public final void setVsspath(String vssPath)
  {
    String projectPath;
    String projectPath;
    if (vssPath.startsWith("vss://")) {
      projectPath = vssPath.substring(5);
    } else {
      projectPath = vssPath;
    }
    if (projectPath.startsWith("$")) {
      this.vssPath = projectPath;
    } else {
      this.vssPath = ("$" + projectPath);
    }
  }
  
  public final void setServerpath(String serverPath)
  {
    this.serverPath = serverPath;
  }
  
  public final void setFailOnError(boolean failOnError)
  {
    this.failOnError = failOnError;
  }
  
  public void execute()
    throws BuildException
  {
    Commandline commandLine = buildCmdLine();
    int result = run(commandLine);
    if ((Execute.isFailure(result)) && (getFailOnError()))
    {
      String msg = "Failed executing: " + formatCommandLine(commandLine) + " With a return code of " + result;
      
      throw new BuildException(msg, getLocation());
    }
  }
  
  protected void setInternalComment(String comment)
  {
    this.comment = comment;
  }
  
  protected void setInternalAutoResponse(String autoResponse)
  {
    this.autoResponse = autoResponse;
  }
  
  protected void setInternalDate(String date)
  {
    this.date = date;
  }
  
  protected void setInternalDateFormat(DateFormat dateFormat)
  {
    this.dateFormat = dateFormat;
  }
  
  protected void setInternalFailOnError(boolean failOnError)
  {
    this.failOnError = failOnError;
  }
  
  protected void setInternalFromDate(String fromDate)
  {
    this.fromDate = fromDate;
  }
  
  protected void setInternalFromLabel(String fromLabel)
  {
    this.fromLabel = fromLabel;
  }
  
  protected void setInternalLabel(String label)
  {
    this.label = label;
  }
  
  protected void setInternalLocalPath(String localPath)
  {
    this.localPath = localPath;
  }
  
  protected void setInternalNumDays(int numDays)
  {
    this.numDays = numDays;
  }
  
  protected void setInternalOutputFilename(String outputFileName)
  {
    this.outputFileName = outputFileName;
  }
  
  protected void setInternalQuiet(boolean quiet)
  {
    this.quiet = quiet;
  }
  
  protected void setInternalRecursive(boolean recursive)
  {
    this.recursive = recursive;
  }
  
  protected void setInternalStyle(String style)
  {
    this.style = style;
  }
  
  protected void setInternalToDate(String toDate)
  {
    this.toDate = toDate;
  }
  
  protected void setInternalToLabel(String toLabel)
  {
    this.toLabel = toLabel;
  }
  
  protected void setInternalUser(String user)
  {
    this.user = user;
  }
  
  protected void setInternalVersion(String version)
  {
    this.version = version;
  }
  
  protected void setInternalWritable(boolean writable)
  {
    this.writable = writable;
  }
  
  protected void setInternalFileTimeStamp(CurrentModUpdated timestamp)
  {
    this.timestamp = timestamp;
  }
  
  protected void setInternalWritableFiles(WritableFiles writableFiles)
  {
    this.writableFiles = writableFiles;
  }
  
  protected void setInternalGetLocalCopy(boolean getLocalCopy)
  {
    this.getLocalCopy = getLocalCopy;
  }
  
  protected String getSSCommand()
  {
    if (ssDir == null) {
      return "ss";
    }
    return 
      ssDir + File.separator + "ss";
  }
  
  protected String getVsspath()
  {
    return vssPath;
  }
  
  protected String getQuiet()
  {
    return quiet ? "-O-" : "";
  }
  
  protected String getRecursive()
  {
    return recursive ? "-R" : "";
  }
  
  protected String getWritable()
  {
    return writable ? "-W" : "";
  }
  
  protected String getLabel()
  {
    String shortLabel = "";
    if ((label != null) && (!label.isEmpty())) {
      shortLabel = "-L" + getShortLabel();
    }
    return shortLabel;
  }
  
  private String getShortLabel()
  {
    String shortLabel;
    if ((label != null) && (label.length() > 31))
    {
      String shortLabel = label.substring(0, 30);
      log("Label is longer than 31 characters, truncated to: " + shortLabel, 1);
    }
    else
    {
      shortLabel = label;
    }
    return shortLabel;
  }
  
  protected String getStyle()
  {
    return style != null ? style : "";
  }
  
  protected String getVersionDateLabel()
  {
    String versionDateLabel = "";
    if (version != null)
    {
      versionDateLabel = "-V" + version;
    }
    else if (date != null)
    {
      versionDateLabel = "-Vd" + date;
    }
    else
    {
      String shortLabel = getShortLabel();
      if ((shortLabel != null) && (!shortLabel.isEmpty())) {
        versionDateLabel = "-VL" + shortLabel;
      }
    }
    return versionDateLabel;
  }
  
  protected String getVersion()
  {
    return version != null ? "-V" + version : "";
  }
  
  protected String getLocalpath()
  {
    String lclPath = "";
    if (localPath != null)
    {
      File dir = getProject().resolveFile(localPath);
      if (!dir.exists())
      {
        boolean done = (dir.mkdirs()) || (dir.exists());
        if (!done)
        {
          String msg = "Directory " + localPath + " creation was not successful for an unknown reason";
          
          throw new BuildException(msg, getLocation());
        }
        getProject().log("Created dir: " + dir.getAbsolutePath());
      }
      lclPath = "-GL" + localPath;
    }
    return lclPath;
  }
  
  protected String getComment()
  {
    return comment != null ? "-C" + comment : "-C-";
  }
  
  protected String getAutoresponse()
  {
    if (autoResponse == null) {
      return "-I-";
    }
    if (autoResponse.equalsIgnoreCase("Y")) {
      return "-I-Y";
    }
    if (autoResponse.equalsIgnoreCase("N")) {
      return "-I-N";
    }
    return "-I-";
  }
  
  protected String getLogin()
  {
    return vssLogin != null ? "-Y" + vssLogin : "";
  }
  
  protected String getOutput()
  {
    return outputFileName != null ? "-O" + outputFileName : "";
  }
  
  protected String getUser()
  {
    return user != null ? "-U" + user : "";
  }
  
  protected String getVersionLabel()
  {
    if ((fromLabel == null) && (toLabel == null)) {
      return "";
    }
    if ((fromLabel != null) && (toLabel != null))
    {
      if (fromLabel.length() > 31)
      {
        fromLabel = fromLabel.substring(0, 30);
        log("FromLabel is longer than 31 characters, truncated to: " + fromLabel, 1);
      }
      if (toLabel.length() > 31)
      {
        toLabel = toLabel.substring(0, 30);
        log("ToLabel is longer than 31 characters, truncated to: " + toLabel, 1);
      }
      return "-VL" + toLabel + "~L" + fromLabel;
    }
    if (fromLabel != null)
    {
      if (fromLabel.length() > 31)
      {
        fromLabel = fromLabel.substring(0, 30);
        log("FromLabel is longer than 31 characters, truncated to: " + fromLabel, 1);
      }
      return "-V~L" + fromLabel;
    }
    if (toLabel.length() > 31)
    {
      toLabel = toLabel.substring(0, 30);
      log("ToLabel is longer than 31 characters, truncated to: " + toLabel, 1);
    }
    return "-VL" + toLabel;
  }
  
  protected String getVersionDate()
    throws BuildException
  {
    if ((fromDate == null) && (toDate == null) && (numDays == Integer.MIN_VALUE)) {
      return "";
    }
    if ((fromDate != null) && (toDate != null)) {
      return "-Vd" + toDate + "~d" + fromDate;
    }
    if ((toDate != null) && (numDays != Integer.MIN_VALUE)) {
      try
      {
        return 
          "-Vd" + toDate + "~d" + calcDate(toDate, numDays);
      }
      catch (ParseException ex)
      {
        String msg = "Error parsing date: " + toDate;
        throw new BuildException(msg, getLocation());
      }
    }
    if ((fromDate != null) && (numDays != Integer.MIN_VALUE)) {
      try
      {
        return "-Vd" + calcDate(fromDate, numDays) + "~d" + fromDate;
      }
      catch (ParseException ex)
      {
        String msg = "Error parsing date: " + fromDate;
        throw new BuildException(msg, getLocation());
      }
    }
    return 
      "-Vd" + toDate;
  }
  
  protected String getGetLocalCopy()
  {
    return !getLocalCopy ? "-G-" : "";
  }
  
  private boolean getFailOnError()
  {
    return (!getWritableFiles().equals("skip")) && (failOnError);
  }
  
  public String getFileTimeStamp()
  {
    if (timestamp == null) {
      return "";
    }
    if (timestamp.getValue().equals("modified")) {
      return "-GTM";
    }
    if (timestamp.getValue().equals("updated")) {
      return "-GTU";
    }
    return "-GTC";
  }
  
  public String getWritableFiles()
  {
    if (writableFiles == null) {
      return "";
    }
    if (writableFiles.getValue().equals("replace")) {
      return "-GWR";
    }
    if (writableFiles.getValue().equals("skip"))
    {
      failOnError = false;
      return "-GWS";
    }
    return "";
  }
  
  private int run(Commandline cmd)
  {
    try
    {
      Execute exe = new Execute(new LogStreamHandler(this, 2, 1));
      if (serverPath != null)
      {
        String[] env = exe.getEnvironment();
        if (env == null) {
          env = new String[0];
        }
        String[] newEnv = new String[env.length + 1];
        System.arraycopy(env, 0, newEnv, 0, env.length);
        newEnv[env.length] = ("SSDIR=" + serverPath);
        
        exe.setEnvironment(newEnv);
      }
      exe.setAntRun(getProject());
      exe.setWorkingDirectory(getProject().getBaseDir());
      exe.setCommandline(cmd.getCommandline());
      
      exe.setVMLauncher(false);
      return exe.execute();
    }
    catch (IOException e)
    {
      throw new BuildException(e, getLocation());
    }
  }
  
  private String calcDate(String startDate, int daysToAdd)
    throws ParseException
  {
    Calendar calendar = new GregorianCalendar();
    Date currentDate = dateFormat.parse(startDate);
    calendar.setTime(currentDate);
    calendar.add(5, daysToAdd);
    return dateFormat.format(calendar.getTime());
  }
  
  private String formatCommandLine(Commandline cmd)
  {
    StringBuffer sBuff = new StringBuffer(cmd.toString());
    int indexUser = sBuff.substring(0).indexOf("-Y");
    if (indexUser > 0)
    {
      int indexPass = sBuff.substring(0).indexOf(",", indexUser);
      int indexAfterPass = sBuff.substring(0).indexOf(" ", indexPass);
      for (int i = indexPass + 1; i < indexAfterPass; i++) {
        sBuff.setCharAt(i, '*');
      }
    }
    return sBuff.toString();
  }
  
  public static class CurrentModUpdated
    extends EnumeratedAttribute
  {
    public String[] getValues()
    {
      return new String[] { "current", "modified", "updated" };
    }
  }
  
  public static class WritableFiles
    extends EnumeratedAttribute
  {
    public String[] getValues()
    {
      return new String[] { "replace", "skip", "fail" };
    }
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.optional.vss.MSVSS
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */