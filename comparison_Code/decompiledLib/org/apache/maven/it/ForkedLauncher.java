package org.apache.maven.it;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.maven.shared.utils.StringUtils;
import org.apache.maven.shared.utils.cli.Arg;
import org.apache.maven.shared.utils.cli.CommandLineException;
import org.apache.maven.shared.utils.cli.CommandLineUtils;
import org.apache.maven.shared.utils.cli.Commandline;
import org.apache.maven.shared.utils.cli.StreamConsumer;
import org.apache.maven.shared.utils.cli.WriterStreamConsumer;
import org.apache.maven.shared.utils.io.FileUtils;

class ForkedLauncher
  implements MavenLauncher
{
  private final String mavenHome;
  private final String executable;
  private final Map<String, String> envVars;
  
  ForkedLauncher(String mavenHome)
  {
    this(mavenHome, Collections.emptyMap(), false);
  }
  
  ForkedLauncher(String mavenHome, Map<String, String> envVars, boolean debugJvm)
  {
    this(mavenHome, envVars, debugJvm, false);
  }
  
  ForkedLauncher(String mavenHome, Map<String, String> envVars, boolean debugJvm, boolean wrapper)
  {
    this.mavenHome = mavenHome;
    this.envVars = envVars;
    if (wrapper)
    {
      StringBuilder script = new StringBuilder();
      if (!isWindows()) {
        script.append("./");
      }
      script.append("mvnw");
      if (debugJvm) {
        script.append("Debug");
      }
      executable = script.toString();
    }
    else
    {
      String script = "mvn" + (debugJvm ? "Debug" : "");
      if (mavenHome != null) {
        executable = new File(mavenHome, "bin/" + script).getPath();
      } else {
        executable = script;
      }
    }
  }
  
  public int run(String[] cliArgs, Properties systemProperties, Map<String, String> envVars, String workingDirectory, File logFile)
    throws IOException, LauncherException
  {
    Commandline cmd = new Commandline();
    
    cmd.setExecutable(executable);
    if (mavenHome != null) {
      cmd.addEnvironment("M2_HOME", mavenHome);
    }
    if (envVars != null) {
      for (localObject1 = envVars.entrySet().iterator(); ((Iterator)localObject1).hasNext();)
      {
        Map.Entry<String, String> envVar = (Map.Entry)((Iterator)localObject1).next();
        
        cmd.addEnvironment((String)envVar.getKey(), (String)envVar.getValue());
      }
    }
    if ((envVars == null) || (envVars.get("JAVA_HOME") == null)) {
      cmd.addEnvironment("JAVA_HOME", System.getProperty("java.home"));
    }
    cmd.addEnvironment("MAVEN_TERMINATE_CMD", "on");
    
    cmd.setWorkingDirectory(workingDirectory);
    for (Object localObject1 = systemProperties.keySet().iterator(); ((Iterator)localObject1).hasNext();)
    {
      o = ((Iterator)localObject1).next();
      
      key = (String)o;
      String value = systemProperties.getProperty(key);
      cmd.createArg().setValue("-D" + key + "=" + value);
    }
    localObject1 = cliArgs;Object o = localObject1.length;
    String cliArg;
    for (String key = 0; key < o; key++)
    {
      cliArg = localObject1[key];
      
      cmd.createArg().setValue(cliArg);
    }
    Object logWriter = new FileWriter(logFile);
    
    StreamConsumer out = new WriterStreamConsumer((Writer)logWriter);
    
    StreamConsumer err = new WriterStreamConsumer((Writer)logWriter);
    try
    {
      return CommandLineUtils.executeCommandLine(cmd, out, err);
    }
    catch (CommandLineException e)
    {
      throw new LauncherException("Failed to run Maven: " + e.getMessage() + "\n" + cmd, e);
    }
    finally
    {
      ((Writer)logWriter).close();
    }
  }
  
  public int run(String[] cliArgs, Properties systemProperties, String workingDirectory, File logFile)
    throws IOException, LauncherException
  {
    return run(cliArgs, systemProperties, envVars, workingDirectory, logFile);
  }
  
  public String getMavenVersion()
    throws IOException, LauncherException
  {
    try
    {
      logFile = File.createTempFile("maven", "log");
    }
    catch (IOException e)
    {
      File logFile;
      throw new LauncherException("Error creating temp file", e);
    }
    File logFile;
    Map<String, String> envVars = Collections.singletonMap("MAVEN_OPTS", "-Demma.rt.control=false");
    run(new String[] { "--version" }, new Properties(), envVars, null, logFile);
    
    List<String> logLines = FileUtils.loadFile(logFile);
    
    logFile.delete();
    
    String version = extractMavenVersion(logLines);
    if (version == null) {
      throw new LauncherException("Illegal Maven output: String 'Maven' not found in the following output:\n" + StringUtils.join(logLines.iterator(), "\n"));
    }
    return version;
  }
  
  static String extractMavenVersion(List<String> logLines)
  {
    String version = null;
    
    Pattern mavenVersion = Pattern.compile("(?i).*Maven.*? ([0-9]\\.\\S*).*");
    for (Iterator<String> it = logLines.iterator(); (version == null) && (it.hasNext());)
    {
      String line = (String)it.next();
      
      Matcher m = mavenVersion.matcher(line);
      if (m.matches()) {
        version = m.group(1);
      }
    }
    return version;
  }
  
  private static boolean isWindows()
  {
    String osName = System.getProperty("os.name").toLowerCase(Locale.US);
    
    return osName.indexOf("windows") > -1;
  }
}

/* Location:
 * Qualified Name:     org.apache.maven.it.ForkedLauncher
 * Java Class Version: 7 (51.0)
 * JD-Core Version:    0.7.1
 */