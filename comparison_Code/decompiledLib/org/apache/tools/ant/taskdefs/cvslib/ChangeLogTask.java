package org.apache.tools.ant.taskdefs.cvslib;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Vector;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.AbstractCvsTask;
import org.apache.tools.ant.types.FileSet;

public class ChangeLogTask
  extends AbstractCvsTask
{
  private File usersFile;
  private List<CvsUser> cvsUsers = new Vector();
  private File inputDir;
  private File destFile;
  private Date startDate;
  private Date endDate;
  private boolean remote = false;
  private String startTag;
  private String endTag;
  private final List<FileSet> filesets = new Vector();
  
  public void setDir(File inputDir)
  {
    this.inputDir = inputDir;
  }
  
  public void setDestfile(File destFile)
  {
    this.destFile = destFile;
  }
  
  public void setUsersfile(File usersFile)
  {
    this.usersFile = usersFile;
  }
  
  public void addUser(CvsUser user)
  {
    cvsUsers.add(user);
  }
  
  public void setStart(Date start)
  {
    startDate = start;
  }
  
  public void setEnd(Date endDate)
  {
    this.endDate = endDate;
  }
  
  public void setDaysinpast(int days)
  {
    long time = System.currentTimeMillis() - days * 24L * 60L * 60L * 1000L;
    
    setStart(new Date(time));
  }
  
  public void setRemote(boolean remote)
  {
    this.remote = remote;
  }
  
  public void setStartTag(String start)
  {
    startTag = start;
  }
  
  public void setEndTag(String end)
  {
    endTag = end;
  }
  
  public void addFileset(FileSet fileSet)
  {
    filesets.add(fileSet);
  }
  
  public void execute()
    throws BuildException
  {
    File savedDir = inputDir;
    try
    {
      validate();
      Properties userList = new Properties();
      
      loadUserlist(userList);
      for (CvsUser user : cvsUsers)
      {
        user.validate();
        userList.put(user.getUserID(), user.getDisplayname());
      }
      if (!remote)
      {
        setCommand("log");
        if (getTag() != null)
        {
          CvsVersion myCvsVersion = new CvsVersion();
          myCvsVersion.setProject(getProject());
          myCvsVersion.setTaskName("cvsversion");
          myCvsVersion.setCvsRoot(getCvsRoot());
          myCvsVersion.setCvsRsh(getCvsRsh());
          myCvsVersion.setPassfile(getPassFile());
          myCvsVersion.setDest(inputDir);
          myCvsVersion.execute();
          if (myCvsVersion.supportsCvsLogWithSOption()) {
            addCommandArgument("-S");
          }
        }
      }
      else
      {
        setCommand("");
        addCommandArgument("rlog");
        
        addCommandArgument("-S");
        
        addCommandArgument("-N");
      }
      if ((null != startTag) || (null != endTag))
      {
        String startValue = startTag == null ? "" : startTag;
        String endValue = endTag == null ? "" : endTag;
        addCommandArgument("-r" + startValue + "::" + endValue);
      }
      else if (null != startDate)
      {
        outputDate = new SimpleDateFormat("yyyy-MM-dd");
        
        String dateRange = ">=" + ((SimpleDateFormat)outputDate).format(startDate);
        
        addCommandArgument("-d");
        addCommandArgument(dateRange);
      }
      for (Object outputDate = filesets.iterator(); ((Iterator)outputDate).hasNext();)
      {
        FileSet fileSet = (FileSet)((Iterator)outputDate).next();
        
        DirectoryScanner scanner = fileSet.getDirectoryScanner(getProject());
        for (String file : scanner.getIncludedFiles()) {
          addCommandArgument(file);
        }
      }
      ChangeLogParser parser = new ChangeLogParser(remote, getPackage(), getModules());
      RedirectingStreamHandler handler = new RedirectingStreamHandler(parser);
      
      log(getCommand(), 3);
      
      setDest(inputDir);
      setExecuteStreamHandler(handler);
      try
      {
        super.execute();
      }
      finally
      {
        String errors;
        String errors = handler.getErrors();
        if (null != errors) {
          log(errors, 0);
        }
      }
      CVSEntry[] entrySet = parser.getEntrySetAsArray();
      CVSEntry[] filteredEntrySet = filterEntrySet(entrySet);
      
      replaceAuthorIdWithName(userList, filteredEntrySet);
      
      writeChangeLog(filteredEntrySet);
    }
    finally
    {
      inputDir = savedDir;
    }
  }
  
  private void validate()
    throws BuildException
  {
    if (null == inputDir) {
      inputDir = getProject().getBaseDir();
    }
    if (null == destFile) {
      throw new BuildException("Destfile must be set.");
    }
    if (!inputDir.exists()) {
      throw new BuildException("Cannot find base dir %s", new Object[] {inputDir.getAbsolutePath() });
    }
    if ((null != usersFile) && (!usersFile.exists())) {
      throw new BuildException("Cannot find user lookup list %s", new Object[] {usersFile.getAbsolutePath() });
    }
    if (((null != startTag) || (null != endTag)) && ((null != startDate) || (null != endDate))) {
      throw new BuildException("Specify either a tag or date range, not both");
    }
  }
  
  private void loadUserlist(Properties userList)
    throws BuildException
  {
    if (null != usersFile) {
      try
      {
        userList.load(Files.newInputStream(usersFile.toPath(), new OpenOption[0]));
      }
      catch (IOException ioe)
      {
        throw new BuildException(ioe.toString(), ioe);
      }
    }
  }
  
  private CVSEntry[] filterEntrySet(CVSEntry[] entrySet)
  {
    List<CVSEntry> results = new ArrayList();
    for (CVSEntry cvsEntry : entrySet)
    {
      Date date = cvsEntry.getDate();
      if (null != date) {
        if ((null == startDate) || (!startDate.after(date))) {
          if ((null == endDate) || (!endDate.before(date))) {
            results.add(cvsEntry);
          }
        }
      }
    }
    return (CVSEntry[])results.toArray(new CVSEntry[results.size()]);
  }
  
  private void replaceAuthorIdWithName(Properties userList, CVSEntry[] entrySet)
  {
    for (CVSEntry entry : entrySet) {
      if (userList.containsKey(entry.getAuthor())) {
        entry.setAuthor(userList.getProperty(entry.getAuthor()));
      }
    }
  }
  
  private void writeChangeLog(CVSEntry[] entrySet)
    throws BuildException
  {
    try
    {
      PrintWriter writer = new PrintWriter(new OutputStreamWriter(Files.newOutputStream(destFile.toPath(), new OpenOption[0]), StandardCharsets.UTF_8));
      try
      {
        new ChangeLogWriter().printChangeLog(writer, entrySet);
        if (writer.checkError()) {
          throw new IOException("Encountered an error writing changelog");
        }
        writer.close();
      }
      catch (Throwable localThrowable) {}
      try
      {
        writer.close();
      }
      catch (Throwable localThrowable1)
      {
        localThrowable.addSuppressed(localThrowable1);
      }
      throw localThrowable;
    }
    catch (UnsupportedEncodingException uee)
    {
      getProject().log(uee.toString(), 0);
    }
    catch (IOException ioe)
    {
      throw new BuildException(ioe.toString(), ioe);
    }
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.cvslib.ChangeLogTask
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */