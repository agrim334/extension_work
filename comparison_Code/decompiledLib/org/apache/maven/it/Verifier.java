package org.apache.maven.it;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.regex.Pattern;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.maven.shared.utils.StringUtils;
import org.apache.maven.shared.utils.cli.CommandLineException;
import org.apache.maven.shared.utils.cli.CommandLineUtils;
import org.apache.maven.shared.utils.cli.Commandline;
import org.apache.maven.shared.utils.cli.StreamConsumer;
import org.apache.maven.shared.utils.cli.WriterStreamConsumer;
import org.apache.maven.shared.utils.io.FileUtils;
import org.junit.Assert;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

public class Verifier
{
  private static final String LOG_FILENAME = "log.txt";
  private static final String[] DEFAULT_CLI_OPTIONS = { "-e", "--batch-mode" };
  private String localRepo;
  private final String basedir;
  private final ByteArrayOutputStream outStream = new ByteArrayOutputStream();
  private final ByteArrayOutputStream errStream = new ByteArrayOutputStream();
  private final String[] defaultCliOptions;
  private PrintStream originalOut;
  private PrintStream originalErr;
  private List<String> cliOptions = new ArrayList();
  private Properties systemProperties = new Properties();
  private Map<String, String> environmentVariables = new HashMap();
  private Properties verifierProperties = new Properties();
  private boolean autoclean = true;
  private String localRepoLayout = "default";
  private boolean debug;
  private Boolean forkJvm;
  private String logFileName = "log.txt";
  private String defaultMavenHome;
  private String defaultClassworldConf;
  private String defaultClasspath;
  private boolean mavenDebug = false;
  private String forkMode;
  private boolean debugJvm = false;
  private static MavenLauncher embeddedLauncher;
  private static final String MARKER = "${artifact:";
  
  public Verifier(String basedir)
    throws VerificationException
  {
    this(basedir, null);
  }
  
  public Verifier(String basedir, boolean debug)
    throws VerificationException
  {
    this(basedir, null, debug);
  }
  
  public Verifier(String basedir, String settingsFile)
    throws VerificationException
  {
    this(basedir, settingsFile, false);
  }
  
  public Verifier(String basedir, String settingsFile, boolean debug)
    throws VerificationException
  {
    this(basedir, settingsFile, debug, DEFAULT_CLI_OPTIONS);
  }
  
  public Verifier(String basedir, String settingsFile, boolean debug, String[] defaultCliOptions)
    throws VerificationException
  {
    this(basedir, settingsFile, debug, null, defaultCliOptions);
  }
  
  public Verifier(String basedir, String settingsFile, boolean debug, boolean forkJvm)
    throws VerificationException
  {
    this(basedir, settingsFile, debug, forkJvm, DEFAULT_CLI_OPTIONS);
  }
  
  public Verifier(String basedir, String settingsFile, boolean debug, boolean forkJvm, String[] defaultCliOptions)
    throws VerificationException
  {
    this(basedir, settingsFile, debug, Boolean.valueOf(forkJvm), defaultCliOptions);
  }
  
  private Verifier(String basedir, String settingsFile, boolean debug, Boolean forkJvm, String[] defaultCliOptions)
    throws VerificationException
  {
    this.basedir = basedir;
    
    this.forkJvm = forkJvm;
    forkMode = System.getProperty("verifier.forkMode");
    if (!debug)
    {
      originalOut = System.out;
      
      originalErr = System.err;
    }
    setDebug(debug);
    
    findLocalRepo(settingsFile);
    findDefaultMavenHome();
    if ((StringUtils.isEmpty(defaultMavenHome)) && (StringUtils.isEmpty(forkMode))) {
      forkMode = "auto";
    }
    this.defaultCliOptions = (defaultCliOptions == null ? new String[0] : (String[])defaultCliOptions.clone());
  }
  
  private void findDefaultMavenHome()
    throws VerificationException
  {
    defaultClasspath = System.getProperty("maven.bootclasspath");
    defaultClassworldConf = System.getProperty("classworlds.conf");
    defaultMavenHome = System.getProperty("maven.home");
    if (defaultMavenHome == null)
    {
      Properties envVars = CommandLineUtils.getSystemEnvVars();
      defaultMavenHome = envVars.getProperty("M2_HOME");
    }
    if (defaultMavenHome == null)
    {
      File f = new File(System.getProperty("user.home"), "m2");
      if (new File(f, "bin/mvn").isFile()) {
        defaultMavenHome = f.getAbsolutePath();
      }
    }
  }
  
  public void setLocalRepo(String localRepo)
  {
    this.localRepo = localRepo;
  }
  
  public void resetStreams()
  {
    if (!debug)
    {
      System.setOut(originalOut);
      
      System.setErr(originalErr);
    }
  }
  
  public void displayStreamBuffers()
  {
    String out = outStream.toString();
    if ((out != null) && (out.trim().length() > 0))
    {
      System.out.println("----- Standard Out -----");
      
      System.out.println(out);
    }
    String err = errStream.toString();
    if ((err != null) && (err.trim().length() > 0))
    {
      System.err.println("----- Standard Error -----");
      
      System.err.println(err);
    }
  }
  
  public void verify(boolean chokeOnErrorOutput)
    throws VerificationException
  {
    List<String> lines = loadFile(getBasedir(), "expected-results.txt", false);
    for (String line : lines) {
      verifyExpectedResult(line);
    }
    if (chokeOnErrorOutput) {
      verifyErrorFreeLog();
    }
  }
  
  public void verifyErrorFreeLog()
    throws VerificationException
  {
    List<String> lines = loadFile(getBasedir(), getLogFileName(), false);
    for (String line : lines) {
      if ((stripAnsi(line).contains("[ERROR]")) && (!isVelocityError(line))) {
        throw new VerificationException("Error in execution: " + line);
      }
    }
  }
  
  private static boolean isVelocityError(String line)
  {
    return (line.contains("VM_global_library.vm")) || ((line.contains("VM #")) && (line.contains("macro")));
  }
  
  public void verifyTextInLog(String text)
    throws VerificationException
  {
    List<String> lines = loadFile(getBasedir(), getLogFileName(), false);
    
    boolean result = false;
    for (String line : lines) {
      if (stripAnsi(line).contains(text))
      {
        result = true;
        break;
      }
    }
    if (!result) {
      throw new VerificationException("Text not found in log: " + text);
    }
  }
  
  public static String stripAnsi(String msg)
  {
    return msg.replaceAll("\033\\[[;\\d]*[ -/]*[@-~]", "");
  }
  
  public Properties loadProperties(String filename)
    throws VerificationException
  {
    Properties properties = new Properties();
    
    File propertiesFile = new File(getBasedir(), filename);
    try
    {
      FileInputStream fis = new FileInputStream(propertiesFile);
      try
      {
        properties.load(fis);
        fis.close();
      }
      catch (Throwable localThrowable) {}
      try
      {
        fis.close();
      }
      catch (Throwable localThrowable1)
      {
        localThrowable.addSuppressed(localThrowable1);
      }
      throw localThrowable;
    }
    catch (IOException e)
    {
      throw new VerificationException("Error reading properties file", e);
    }
    return properties;
  }
  
  public List<String> loadLines(String filename, String encoding)
    throws IOException
  {
    List<String> lines = new ArrayList();
    
    BufferedReader reader = getReader(filename, encoding);
    try
    {
      String line;
      while ((line = reader.readLine()) != null) {
        if (line.length() > 0) {
          lines.add(line);
        }
      }
      if (reader == null) {
        return lines;
      }
      reader.close();
    }
    catch (Throwable localThrowable)
    {
      if (reader == null) {
        break label84;
      }
    }
    try
    {
      reader.close();
    }
    catch (Throwable localThrowable1)
    {
      localThrowable.addSuppressed(localThrowable1);
    }
    label84:
    throw localThrowable;
    return lines;
  }
  
  private BufferedReader getReader(String filename, String encoding)
    throws IOException
  {
    File file = new File(getBasedir(), filename);
    if (StringUtils.isNotEmpty(encoding)) {
      return new BufferedReader(new InputStreamReader(new FileInputStream(file), encoding));
    }
    return new BufferedReader(new FileReader(file));
  }
  
  public List<String> loadFile(String basedir, String filename, boolean hasCommand)
    throws VerificationException
  {
    return loadFile(new File(basedir, filename), hasCommand);
  }
  
  public List<String> loadFile(File file, boolean hasCommand)
    throws VerificationException
  {
    List<String> lines = new ArrayList();
    if (file.exists()) {
      try
      {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        try
        {
          String line = reader.readLine();
          while (line != null)
          {
            line = line.trim();
            if ((!line.startsWith("#")) && (line.length() != 0)) {
              lines.addAll(replaceArtifacts(line, hasCommand));
            }
            line = reader.readLine();
          }
          reader.close();
        }
        catch (Throwable localThrowable) {}
        try
        {
          reader.close();
        }
        catch (Throwable localThrowable1)
        {
          localThrowable.addSuppressed(localThrowable1);
        }
        throw localThrowable;
      }
      catch (FileNotFoundException e)
      {
        throw new VerificationException(e);
      }
      catch (IOException e)
      {
        throw new VerificationException(e);
      }
    }
    return lines;
  }
  
  private List<String> replaceArtifacts(String line, boolean hasCommand)
  {
    int index = line.indexOf("${artifact:");
    if (index >= 0)
    {
      String newLine = line.substring(0, index);
      index = line.indexOf("}", index);
      if (index < 0) {
        throw new IllegalArgumentException("line does not contain ending artifact marker: '" + line + "'");
      }
      String artifact = line.substring(newLine.length() + "${artifact:".length(), index);
      
      newLine = newLine + getArtifactPath(artifact);
      newLine = newLine + line.substring(index + 1);
      
      List<String> l = new ArrayList();
      l.add(newLine);
      
      int endIndex = newLine.lastIndexOf('/');
      
      String command = null;
      String filespec;
      String filespec;
      if (hasCommand)
      {
        int startIndex = newLine.indexOf(' ');
        
        command = newLine.substring(0, startIndex);
        
        filespec = newLine.substring(startIndex + 1, endIndex);
      }
      else
      {
        filespec = newLine;
      }
      File dir = new File(filespec);
      addMetadataToList(dir, hasCommand, l, command);
      addMetadataToList(dir.getParentFile(), hasCommand, l, command);
      
      return l;
    }
    return Collections.singletonList(line);
  }
  
  private static void addMetadataToList(File dir, boolean hasCommand, List<String> l, String command)
  {
    if ((dir.exists()) && (dir.isDirectory()))
    {
      String[] files = dir.list(new FilenameFilter()
      {
        public boolean accept(File dir, String name)
        {
          return (name.startsWith("maven-metadata")) && (name.endsWith(".xml"));
        }
      });
      for (String file : files) {
        if (hasCommand) {
          l.add(command + " " + new File(dir, file).getPath());
        } else {
          l.add(new File(dir, file).getPath());
        }
      }
    }
  }
  
  private String getArtifactPath(String artifact)
  {
    StringTokenizer tok = new StringTokenizer(artifact, ":");
    if (tok.countTokens() != 4) {
      throw new IllegalArgumentException("Artifact must have 4 tokens: '" + artifact + "'");
    }
    String[] a = new String[4];
    for (int i = 0; i < 4; i++) {
      a[i] = tok.nextToken();
    }
    String org = a[0];
    String name = a[1];
    String version = a[2];
    String ext = a[3];
    return getArtifactPath(org, name, version, ext);
  }
  
  public String getArtifactPath(String org, String name, String version, String ext)
  {
    return getArtifactPath(org, name, version, ext, null);
  }
  
  public String getArtifactPath(String gid, String aid, String version, String ext, String classifier)
  {
    if ((classifier != null) && (classifier.length() == 0)) {
      classifier = null;
    }
    if ("maven-plugin".equals(ext)) {
      ext = "jar";
    }
    if ("coreit-artifact".equals(ext))
    {
      ext = "jar";
      classifier = "it";
    }
    if ("test-jar".equals(ext))
    {
      ext = "jar";
      classifier = "tests";
    }
    String repositoryPath;
    if ("legacy".equals(localRepoLayout))
    {
      repositoryPath = gid + "/" + ext + "s/" + aid + "-" + version + "." + ext;
    }
    else if ("default".equals(localRepoLayout))
    {
      String repositoryPath = gid.replace('.', '/');
      repositoryPath = repositoryPath + "/" + aid + "/" + version;
      repositoryPath = repositoryPath + "/" + aid + "-" + version;
      if (classifier != null) {
        repositoryPath = repositoryPath + "-" + classifier;
      }
      repositoryPath = repositoryPath + "." + ext;
    }
    else
    {
      throw new IllegalStateException("Unknown layout: " + localRepoLayout);
    }
    String repositoryPath;
    return localRepo + "/" + repositoryPath;
  }
  
  public List<String> getArtifactFileNameList(String org, String name, String version, String ext)
  {
    List<String> files = new ArrayList();
    String artifactPath = getArtifactPath(org, name, version, ext);
    File dir = new File(artifactPath);
    files.add(artifactPath);
    addMetadataToList(dir, false, files, null);
    addMetadataToList(dir.getParentFile(), false, files, null);
    return files;
  }
  
  public String getArtifactMetadataPath(String gid, String aid, String version)
  {
    return getArtifactMetadataPath(gid, aid, version, "maven-metadata-local.xml");
  }
  
  public String getArtifactMetadataPath(String gid, String aid, String version, String filename)
  {
    StringBuilder buffer = new StringBuilder(256);
    
    buffer.append(localRepo);
    buffer.append('/');
    if ("default".equals(localRepoLayout))
    {
      buffer.append(gid.replace('.', '/'));
      buffer.append('/');
      if (aid != null)
      {
        buffer.append(aid);
        buffer.append('/');
        if (version != null)
        {
          buffer.append(version);
          buffer.append('/');
        }
      }
      buffer.append(filename);
    }
    else
    {
      throw new IllegalStateException("Unsupported repository layout: " + localRepoLayout);
    }
    return buffer.toString();
  }
  
  public String getArtifactMetadataPath(String gid, String aid)
  {
    return getArtifactMetadataPath(gid, aid, null);
  }
  
  public void executeHook(String filename)
    throws VerificationException
  {
    try
    {
      File f = new File(getBasedir(), filename);
      if (!f.exists()) {
        return;
      }
      List<String> lines = loadFile(f, true);
      for (String line1 : lines)
      {
        String line = resolveCommandLineArg(line1);
        
        executeCommand(line);
      }
    }
    catch (VerificationException e)
    {
      throw e;
    }
    catch (Exception e)
    {
      throw new VerificationException(e);
    }
  }
  
  private void executeCommand(String line)
    throws VerificationException
  {
    int index = line.indexOf(" ");
    
    String args = null;
    String cmd;
    if (index >= 0)
    {
      String cmd = line.substring(0, index);
      
      args = line.substring(index + 1);
    }
    else
    {
      cmd = line;
    }
    if ("rm".equals(cmd))
    {
      System.out.println("Removing file: " + args);
      
      File f = new File(args);
      if ((f.exists()) && (!f.delete())) {
        throw new VerificationException("Error removing file - delete failed");
      }
    }
    else if ("rmdir".equals(cmd))
    {
      System.out.println("Removing directory: " + args);
      try
      {
        File f = new File(args);
        
        FileUtils.deleteDirectory(f);
      }
      catch (IOException e)
      {
        throw new VerificationException("Error removing directory - delete failed");
      }
    }
    else if ("svn".equals(cmd))
    {
      launchSubversion(line, getBasedir());
    }
    else
    {
      throw new VerificationException("unknown command: " + cmd);
    }
  }
  
  public static void launchSubversion(String line, String basedir)
    throws VerificationException
  {
    try
    {
      Commandline cli = new Commandline(line);
      
      cli.setWorkingDirectory(basedir);
      
      Writer logWriter = new FileWriter(new File(basedir, "log.txt"));
      try
      {
        StreamConsumer out = new WriterStreamConsumer(logWriter);
        
        StreamConsumer err = new WriterStreamConsumer(logWriter);
        
        System.out.println("Command: " + CommandLineUtils.toString(cli.getCommandline()));
        
        int ret = CommandLineUtils.executeCommandLine(cli, out, err);
        if (ret > 0)
        {
          System.err.println("Exit code: " + ret);
          
          throw new VerificationException();
        }
        logWriter.close();
      }
      catch (Throwable localThrowable) {}
      try
      {
        logWriter.close();
      }
      catch (Throwable localThrowable1)
      {
        localThrowable.addSuppressed(localThrowable1);
      }
      throw localThrowable;
    }
    catch (CommandLineException e)
    {
      throw new VerificationException(e);
    }
    catch (IOException e)
    {
      throw new VerificationException(e);
    }
  }
  
  private static String retrieveLocalRepo(String settingsXmlPath)
    throws VerificationException
  {
    UserModelReader userModelReader = new UserModelReader();
    
    String userHome = System.getProperty("user.home");
    
    String repo = null;
    File userXml;
    File userXml;
    if (settingsXmlPath != null)
    {
      System.out.println("Using settings from " + settingsXmlPath);
      userXml = new File(settingsXmlPath);
    }
    else
    {
      userXml = new File(userHome, ".m2/settings.xml");
    }
    if (userXml.exists())
    {
      userModelReader.parse(userXml);
      
      String localRepository = userModelReader.getLocalRepository();
      if (localRepository != null) {
        repo = new File(localRepository).getAbsolutePath();
      }
    }
    return repo;
  }
  
  public void deleteArtifact(String org, String name, String version, String ext)
    throws IOException
  {
    List<String> files = getArtifactFileNameList(org, name, version, ext);
    for (String fileName : files) {
      FileUtils.forceDelete(new File(fileName));
    }
  }
  
  public void deleteArtifacts(String gid)
    throws IOException
  {
    String path;
    if ("default".equals(localRepoLayout))
    {
      path = gid.replace('.', '/');
    }
    else
    {
      String path;
      if ("legacy".equals(localRepoLayout)) {
        path = gid;
      } else {
        throw new IllegalStateException("Unsupported repository layout: " + localRepoLayout);
      }
    }
    String path;
    FileUtils.deleteDirectory(new File(localRepo, path));
  }
  
  public void deleteArtifacts(String gid, String aid, String version)
    throws IOException
  {
    String path;
    if ("default".equals(localRepoLayout)) {
      path = gid.replace('.', '/') + '/' + aid + '/' + version;
    } else {
      throw new IllegalStateException("Unsupported repository layout: " + localRepoLayout);
    }
    String path;
    FileUtils.deleteDirectory(new File(localRepo, path));
  }
  
  public void deleteDirectory(String path)
    throws IOException
  {
    FileUtils.deleteDirectory(new File(getBasedir(), path));
  }
  
  public void writeFile(String path, String contents)
    throws IOException
  {
    FileUtils.fileWrite(new File(getBasedir(), path).getAbsolutePath(), "UTF-8", contents);
  }
  
  public File filterFile(String srcPath, String dstPath, String fileEncoding, Map<String, String> filterProperties)
    throws IOException
  {
    File srcFile = new File(getBasedir(), srcPath);
    String data = FileUtils.fileRead(srcFile, fileEncoding);
    for (String token : filterProperties.keySet())
    {
      String value = String.valueOf(filterProperties.get(token));
      data = StringUtils.replace(data, token, value);
    }
    File dstFile = new File(getBasedir(), dstPath);
    
    dstFile.getParentFile().mkdirs();
    FileUtils.fileWrite(dstFile.getPath(), fileEncoding, data);
    
    return dstFile;
  }
  
  @Deprecated
  public File filterFile(String srcPath, String dstPath, String fileEncoding, Properties filterProperties)
    throws IOException
  {
    return filterFile(srcPath, dstPath, fileEncoding, filterProperties);
  }
  
  public Properties newDefaultFilterProperties()
  {
    Properties filterProperties = new Properties();
    
    String basedir = new File(getBasedir()).getAbsolutePath();
    filterProperties.put("@basedir@", basedir);
    
    String baseurl = basedir;
    if (!baseurl.startsWith("/")) {
      baseurl = '/' + baseurl;
    }
    baseurl = "file://" + baseurl.replace('\\', '/');
    filterProperties.put("@baseurl@", baseurl);
    
    return filterProperties;
  }
  
  public void assertFilePresent(String file)
  {
    try
    {
      verifyExpectedResult(file, true);
    }
    catch (VerificationException e)
    {
      Assert.fail(e.getMessage());
    }
  }
  
  public void assertFileMatches(String file, String regex)
  {
    assertFilePresent(file);
    try
    {
      String content = FileUtils.fileRead(file);
      if (!Pattern.matches(regex, content)) {
        Assert.fail("Content of " + file + " does not match " + regex);
      }
    }
    catch (IOException e)
    {
      Assert.fail(e.getMessage());
    }
  }
  
  public void assertFileNotPresent(String file)
  {
    try
    {
      verifyExpectedResult(file, false);
    }
    catch (VerificationException e)
    {
      Assert.fail(e.getMessage());
    }
  }
  
  private void verifyArtifactPresence(boolean wanted, String org, String name, String version, String ext)
  {
    List<String> files = getArtifactFileNameList(org, name, version, ext);
    for (String fileName : files) {
      try
      {
        verifyExpectedResult(fileName, wanted);
      }
      catch (VerificationException e)
      {
        Assert.fail(e.getMessage());
      }
    }
  }
  
  public void assertArtifactPresent(String org, String name, String version, String ext)
  {
    verifyArtifactPresence(true, org, name, version, ext);
  }
  
  public void assertArtifactNotPresent(String org, String name, String version, String ext)
  {
    verifyArtifactPresence(false, org, name, version, ext);
  }
  
  private void verifyExpectedResult(String line)
    throws VerificationException
  {
    boolean wanted = true;
    if (line.startsWith("!"))
    {
      line = line.substring(1);
      wanted = false;
    }
    verifyExpectedResult(line, wanted);
  }
  
  private void verifyExpectedResult(String line, boolean wanted)
    throws VerificationException
  {
    if (line.indexOf("!/") > 0)
    {
      String urlString = "jar:file:" + getBasedir() + "/" + line;
      
      InputStream is = null;
      try
      {
        URL url = new URL(urlString);
        
        is = url.openStream();
        if (is == null)
        {
          if (wanted) {
            throw new VerificationException("Expected JAR resource was not found: " + line);
          }
        }
        else if (!wanted) {
          throw new VerificationException("Unwanted JAR resource was found: " + line);
        }
      }
      catch (MalformedURLException e)
      {
        throw new VerificationException("Error looking for JAR resource", e);
      }
      catch (IOException e)
      {
        if (wanted) {
          throw new VerificationException("Error looking for JAR resource: " + line);
        }
      }
      finally
      {
        if (is != null) {
          try
          {
            is.close();
          }
          catch (IOException e)
          {
            System.err.println("WARN: error closing stream: " + e);
          }
        }
      }
    }
    File expectedFile = new File(line);
    if ((!expectedFile.isAbsolute()) && (!expectedFile.getPath().startsWith(File.separator))) {
      expectedFile = new File(getBasedir(), line);
    }
    if (line.indexOf('*') > -1)
    {
      File parent = expectedFile.getParentFile();
      if (!parent.exists())
      {
        if (wanted) {
          throw new VerificationException("Expected file pattern was not found: " + expectedFile.getPath());
        }
      }
      else
      {
        String shortNamePattern = expectedFile.getName().replaceAll("\\*", ".*");
        
        String[] candidates = parent.list();
        
        boolean found = false;
        if (candidates != null) {
          for (String candidate : candidates) {
            if (candidate.matches(shortNamePattern))
            {
              found = true;
              break;
            }
          }
        }
        if ((!found) && (wanted)) {
          throw new VerificationException("Expected file pattern was not found: " + expectedFile.getPath());
        }
        if ((found) && (!wanted)) {
          throw new VerificationException("Unwanted file pattern was found: " + expectedFile.getPath());
        }
      }
    }
    else if (!expectedFile.exists())
    {
      if (wanted) {
        throw new VerificationException("Expected file was not found: " + expectedFile.getPath());
      }
    }
    else if (!wanted)
    {
      throw new VerificationException("Unwanted file was found: " + expectedFile.getPath());
    }
  }
  
  public void executeGoal(String goal)
    throws VerificationException
  {
    executeGoal(goal, environmentVariables);
  }
  
  public void executeGoal(String goal, Map<String, String> envVars)
    throws VerificationException
  {
    executeGoals(Arrays.asList(new String[] { goal }), envVars);
  }
  
  public void executeGoals(List<String> goals)
    throws VerificationException
  {
    executeGoals(goals, environmentVariables);
  }
  
  public String getExecutable()
  {
    String mavenHome = defaultMavenHome;
    if (mavenHome != null) {
      return mavenHome + "/bin/mvn";
    }
    File f = new File(System.getProperty("user.home"), "m2/bin/mvn");
    if (f.exists()) {
      return f.getAbsolutePath();
    }
    return "mvn";
  }
  
  public void executeGoals(List<String> goals, Map<String, String> envVars)
    throws VerificationException
  {
    List<String> allGoals = new ArrayList();
    if (autoclean) {
      allGoals.add("org.apache.maven.plugins:maven-clean-plugin:clean");
    }
    allGoals.addAll(goals);
    
    List<String> args = new ArrayList();
    
    File logFile = new File(getBasedir(), getLogFileName());
    for (Object cliOption : cliOptions)
    {
      String key = String.valueOf(cliOption);
      
      String resolvedArg = resolveCommandLineArg(key);
      try
      {
        args.addAll(Arrays.asList(CommandLineUtils.translateCommandline(resolvedArg)));
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
    }
    Collections.addAll(args, defaultCliOptions);
    if (mavenDebug) {
      args.add("--debug");
    }
    boolean useMavenRepoLocal = Boolean.valueOf(verifierProperties.getProperty("use.mavenRepoLocal", "true")).booleanValue();
    if (useMavenRepoLocal) {
      args.add("-Dmaven.repo.local=" + localRepo);
    }
    args.addAll(allGoals);
    try
    {
      String[] cliArgs = (String[])args.toArray(new String[args.size()]);
      
      MavenLauncher launcher = getMavenLauncher(envVars);
      
      ret = launcher.run(cliArgs, systemProperties, getBasedir(), logFile);
    }
    catch (LauncherException e)
    {
      int ret;
      throw new VerificationException("Failed to execute Maven: " + e.getMessage(), e);
    }
    catch (IOException e)
    {
      throw new VerificationException(e);
    }
    int ret;
    if (ret > 0)
    {
      System.err.println("Exit code: " + ret);
      
      throw new VerificationException("Exit code was non-zero: " + ret + "; command line and log = \n" + new File(defaultMavenHome, "bin/mvn") + " " + StringUtils.join(args.iterator(), " ") + "\n" + getLogContents(logFile));
    }
  }
  
  private MavenLauncher getMavenLauncher(Map<String, String> envVars)
    throws LauncherException
  {
    boolean useWrapper = Files.exists(Paths.get(getBasedir(), new String[] { "mvnw" }), new LinkOption[0]);
    boolean fork;
    boolean fork;
    if (useWrapper)
    {
      fork = true;
    }
    else
    {
      boolean fork;
      if (forkJvm != null)
      {
        fork = forkJvm.booleanValue();
      }
      else if (((envVars.isEmpty()) && ("auto".equalsIgnoreCase(forkMode))) || 
        ("embedded".equalsIgnoreCase(forkMode)))
      {
        boolean fork = false;
        try
        {
          initEmbeddedLauncher();
        }
        catch (Exception e)
        {
          fork = true;
        }
      }
      else
      {
        fork = true;
      }
    }
    if (!fork)
    {
      if (!envVars.isEmpty()) {
        throw new LauncherException("Environment variables are not supported in embedded runtime");
      }
      initEmbeddedLauncher();
      
      return embeddedLauncher;
    }
    return new ForkedLauncher(defaultMavenHome, envVars, debugJvm, useWrapper);
  }
  
  private void initEmbeddedLauncher()
    throws LauncherException
  {
    if (embeddedLauncher == null) {
      if (StringUtils.isEmpty(defaultMavenHome)) {
        embeddedLauncher = Embedded3xLauncher.createFromClasspath();
      } else {
        embeddedLauncher = Embedded3xLauncher.createFromMavenHome(defaultMavenHome, defaultClassworldConf, getClasspath());
      }
    }
  }
  
  private List<URL> getClasspath()
    throws LauncherException
  {
    if (defaultClasspath == null) {
      return null;
    }
    ArrayList<URL> classpath = new ArrayList();
    StringTokenizer st = new StringTokenizer(defaultClasspath, File.pathSeparator);
    while (st.hasMoreTokens()) {
      try
      {
        classpath.add(new File(st.nextToken()).toURI().toURL());
      }
      catch (MalformedURLException e)
      {
        throw new LauncherException("Invalid launcher classpath " + defaultClasspath, e);
      }
    }
    return classpath;
  }
  
  public String getMavenVersion()
    throws VerificationException
  {
    try
    {
      return getMavenLauncher(Collections.emptyMap()).getMavenVersion();
    }
    catch (LauncherException e)
    {
      throw new VerificationException(e);
    }
    catch (IOException e)
    {
      throw new VerificationException(e);
    }
  }
  
  private static String getLogContents(File logFile)
  {
    try
    {
      return FileUtils.fileRead(logFile);
    }
    catch (IOException e)
    {
      return "(Error reading log contents: " + e.getMessage() + ")";
    }
  }
  
  private String resolveCommandLineArg(String key)
  {
    String result = key.replaceAll("\\$\\{basedir\\}", getBasedir());
    if (result.contains("\\\\")) {
      result = result.replaceAll("\\\\", "\\");
    }
    result = result.replaceAll("\\/\\/", "\\/");
    
    return result;
  }
  
  private static List<String> discoverIntegrationTests(String directory)
    throws VerificationException
  {
    try
    {
      ArrayList<String> tests = new ArrayList();
      
      List<File> subTests = FileUtils.getFiles(new File(directory), "**/goals.txt", null);
      for (File testCase : subTests) {
        tests.add(testCase.getParent());
      }
      return tests;
    }
    catch (IOException e)
    {
      throw new VerificationException(directory + " is not a valid test case container", e);
    }
  }
  
  private void displayLogFile()
  {
    System.out.println("Log file contents:");
    try
    {
      BufferedReader reader = new BufferedReader(new FileReader(new File(getBasedir(), getLogFileName())));
      try
      {
        String line = reader.readLine();
        while (line != null)
        {
          System.out.println(line);
          line = reader.readLine();
        }
        reader.close();
      }
      catch (Throwable localThrowable) {}
      try
      {
        reader.close();
      }
      catch (Throwable localThrowable1)
      {
        localThrowable.addSuppressed(localThrowable1);
      }
      throw localThrowable;
    }
    catch (IOException e)
    {
      System.err.println("Error: " + e);
    }
  }
  
  public static void main(String[] args)
    throws VerificationException
  {
    String basedir = System.getProperty("user.dir");
    
    List<String> tests = null;
    
    List<String> argsList = new ArrayList();
    
    String settingsFile = null;
    int index;
    for (int i = 0; i < args.length; i++) {
      if (args[i].startsWith("-D"))
      {
        index = args[i].indexOf("=");
        if (index >= 0) {
          System.setProperty(args[i].substring(2, index), args[i].substring(index + 1));
        } else {
          System.setProperty(args[i].substring(2), "true");
        }
      }
      else if (("-s".equals(args[i])) || ("--settings".equals(args[i])))
      {
        if (i == args.length - 1) {
          throw new IllegalStateException("missing argument to -s");
        }
        i++;
        
        settingsFile = args[i];
      }
      else if (args[i].startsWith("-"))
      {
        System.out.println("skipping unrecognised argument: " + args[i]);
      }
      else
      {
        argsList.add(args[i]);
      }
    }
    NumberFormat fmt;
    if (argsList.size() == 0)
    {
      if (FileUtils.fileExists(basedir + File.separator + "integration-tests.txt")) {
        try
        {
          tests = FileUtils.loadFile(new File(basedir, "integration-tests.txt"));
        }
        catch (IOException e)
        {
          System.err.println("Unable to load integration tests file");
          
          System.err.println(e.getMessage());
          
          System.exit(2);
        }
      } else {
        tests = discoverIntegrationTests(".");
      }
    }
    else
    {
      tests = new ArrayList(argsList.size());
      fmt = new DecimalFormat("0000");
      for (index = argsList.iterator(); index.hasNext();)
      {
        test = (String)index.next();
        if (test.endsWith(",")) {
          test = test.substring(0, test.length() - 1);
        }
        if (StringUtils.isNumeric(test))
        {
          test = "it" + fmt.format(Integer.valueOf(test));
          tests.add(test.trim());
        }
        else if ("it".startsWith(test))
        {
          test = test.trim();
          if (test.length() > 0) {
            tests.add(test);
          }
        }
        else if ((FileUtils.fileExists(test)) && (new File(test).isDirectory()))
        {
          tests.addAll(discoverIntegrationTests(test));
        }
        else
        {
          System.err.println("[WARNING] rejecting " + test + " as an invalid test or test source directory");
        }
      }
    }
    String test;
    if (tests.size() == 0) {
      System.out.println("No tests to run");
    }
    int exitCode = 0;
    
    List<String> failed = new ArrayList();
    for (String test : tests)
    {
      System.out.print(test + "... ");
      
      String dir = basedir + "/" + test;
      if (!new File(dir, "goals.txt").exists())
      {
        System.err.println("Test " + test + " in " + dir + " does not exist");
        
        System.exit(2);
      }
      Verifier verifier = new Verifier(dir);
      verifier.findLocalRepo(settingsFile);
      
      System.out.println("Using default local repository: " + localRepo);
      try
      {
        runIntegrationTest(verifier);
      }
      catch (Throwable e)
      {
        verifier.resetStreams();
        
        System.out.println("FAILED");
        
        verifier.displayStreamBuffers();
        
        System.out.println(">>>>>> Error Stacktrace:");
        e.printStackTrace(System.out);
        System.out.println("<<<<<< Error Stacktrace");
        
        verifier.displayLogFile();
        
        exitCode = 1;
        
        failed.add(test);
      }
    }
    System.out.println(tests.size() - failed.size() + "/" + tests.size() + " passed");
    if (!failed.isEmpty()) {
      System.out.println("Failed tests: " + failed);
    }
    System.exit(exitCode);
  }
  
  private void findLocalRepo(String settingsFile)
    throws VerificationException
  {
    if (localRepo == null) {
      localRepo = System.getProperty("maven.repo.local");
    }
    if (localRepo == null) {
      localRepo = retrieveLocalRepo(settingsFile);
    }
    if (localRepo == null) {
      localRepo = (System.getProperty("user.home") + "/.m2/repository");
    }
    File repoDir = new File(localRepo);
    if (!repoDir.exists()) {
      repoDir.mkdirs();
    }
    localRepo = repoDir.getAbsolutePath();
    
    localRepoLayout = System.getProperty("maven.repo.local.layout", "default");
  }
  
  private static void runIntegrationTest(Verifier verifier)
    throws VerificationException
  {
    verifier.executeHook("prebuild-hook.txt");
    
    Properties properties = verifier.loadProperties("system.properties");
    
    Properties controlProperties = verifier.loadProperties("verifier.properties");
    
    boolean chokeOnErrorOutput = Boolean.valueOf(controlProperties.getProperty("failOnErrorOutput", "true")).booleanValue();
    
    List<String> goals = verifier.loadFile(verifier.getBasedir(), "goals.txt", false);
    
    List<String> cliOptions = verifier.loadFile(verifier.getBasedir(), "cli-options.txt", false);
    
    verifier.setCliOptions(cliOptions);
    
    verifier.setSystemProperties(properties);
    
    verifier.setVerifierProperties(controlProperties);
    
    verifier.executeGoals(goals);
    
    verifier.executeHook("postbuild-hook.txt");
    
    System.out.println("*** Verifying: fail when [ERROR] detected? " + chokeOnErrorOutput + " ***");
    
    verifier.verify(chokeOnErrorOutput);
    
    verifier.resetStreams();
    
    System.out.println("OK");
  }
  
  public void assertArtifactContents(String org, String artifact, String version, String type, String contents)
    throws IOException
  {
    String fileName = getArtifactPath(org, artifact, version, type);
    Assert.assertEquals(contents, FileUtils.fileRead(fileName));
  }
  
  static class UserModelReader
    extends DefaultHandler
  {
    private String localRepository;
    private StringBuffer currentBody = new StringBuffer();
    
    public void parse(File file)
      throws VerificationException
    {
      try
      {
        SAXParserFactory saxFactory = SAXParserFactory.newInstance();
        
        SAXParser parser = saxFactory.newSAXParser();
        
        InputSource is = new InputSource(new FileInputStream(file));
        
        parser.parse(is, this);
      }
      catch (FileNotFoundException e)
      {
        throw new VerificationException("file not found path : " + file.getAbsolutePath(), e);
      }
      catch (IOException e)
      {
        throw new VerificationException(" IOException path : " + file.getAbsolutePath(), e);
      }
      catch (ParserConfigurationException e)
      {
        throw new VerificationException(e);
      }
      catch (SAXException e)
      {
        throw new VerificationException("Parsing exception for file " + file.getAbsolutePath(), e);
      }
    }
    
    public void warning(SAXParseException spe)
    {
      printParseError("Warning", spe);
    }
    
    public void error(SAXParseException spe)
    {
      printParseError("Error", spe);
    }
    
    public void fatalError(SAXParseException spe)
    {
      printParseError("Fatal Error", spe);
    }
    
    private void printParseError(String type, SAXParseException spe)
    {
      System.err.println(type + " [line " + spe
        .getLineNumber() + ", row " + spe.getColumnNumber() + "]: " + spe.getMessage());
    }
    
    public String getLocalRepository()
    {
      return localRepository;
    }
    
    public void characters(char[] ch, int start, int length)
      throws SAXException
    {
      currentBody.append(ch, start, length);
    }
    
    public void endElement(String uri, String localName, String rawName)
      throws SAXException
    {
      if ("localRepository".equals(rawName)) {
        if (notEmpty(currentBody.toString())) {
          localRepository = currentBody.toString().trim();
        } else {
          throw new SAXException("Invalid mavenProfile entry. Missing one or more fields: {localRepository}.");
        }
      }
      currentBody = new StringBuffer();
    }
    
    private boolean notEmpty(String test)
    {
      return (test != null) && (test.trim().length() > 0);
    }
    
    public void reset()
    {
      currentBody = null;
      localRepository = null;
    }
  }
  
  public List<String> getCliOptions()
  {
    return cliOptions;
  }
  
  public void setCliOptions(List<String> cliOptions)
  {
    this.cliOptions = cliOptions;
  }
  
  public void addCliOption(String option)
  {
    cliOptions.add(option);
  }
  
  public Properties getSystemProperties()
  {
    return systemProperties;
  }
  
  public void setSystemProperties(Properties systemProperties)
  {
    this.systemProperties = systemProperties;
  }
  
  public void setSystemProperty(String key, String value)
  {
    if (value != null) {
      systemProperties.setProperty(key, value);
    } else {
      systemProperties.remove(key);
    }
  }
  
  public Map<String, String> getEnvironmentVariables()
  {
    return environmentVariables;
  }
  
  public void setEnvironmentVariables(Map<String, String> environmentVariables)
  {
    this.environmentVariables = environmentVariables;
  }
  
  public void setEnvironmentVariable(String key, String value)
  {
    if (value != null) {
      environmentVariables.put(key, value);
    } else {
      environmentVariables.remove(key);
    }
  }
  
  public Properties getVerifierProperties()
  {
    return verifierProperties;
  }
  
  public void setVerifierProperties(Properties verifierProperties)
  {
    this.verifierProperties = verifierProperties;
  }
  
  public boolean isAutoclean()
  {
    return autoclean;
  }
  
  public void setAutoclean(boolean autoclean)
  {
    this.autoclean = autoclean;
  }
  
  public String getBasedir()
  {
    return basedir;
  }
  
  public String getLogFileName()
  {
    return logFileName;
  }
  
  public void setLogFileName(String logFileName)
  {
    if (StringUtils.isEmpty(logFileName)) {
      throw new IllegalArgumentException("log file name unspecified");
    }
    this.logFileName = logFileName;
  }
  
  public void setDebug(boolean debug)
  {
    this.debug = debug;
    if (!debug)
    {
      System.setOut(new PrintStream(outStream));
      
      System.setErr(new PrintStream(errStream));
    }
  }
  
  public boolean isMavenDebug()
  {
    return mavenDebug;
  }
  
  public void setMavenDebug(boolean mavenDebug)
  {
    this.mavenDebug = mavenDebug;
  }
  
  public void setForkJvm(boolean forkJvm)
  {
    this.forkJvm = Boolean.valueOf(forkJvm);
  }
  
  public boolean isDebugJvm()
  {
    return debugJvm;
  }
  
  public void setDebugJvm(boolean debugJvm)
  {
    this.debugJvm = debugJvm;
  }
  
  public String getLocalRepoLayout()
  {
    return localRepoLayout;
  }
  
  public void setLocalRepoLayout(String localRepoLayout)
  {
    this.localRepoLayout = localRepoLayout;
  }
  
  public String getLocalRepository()
  {
    return localRepo;
  }
}

/* Location:
 * Qualified Name:     org.apache.maven.it.Verifier
 * Java Class Version: 7 (51.0)
 * JD-Core Version:    0.7.1
 */