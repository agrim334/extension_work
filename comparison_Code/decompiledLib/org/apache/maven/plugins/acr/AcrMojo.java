package org.apache.maven.plugins.acr;

import java.io.File;
import java.io.IOException;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.XmlStreamReader;
import org.apache.maven.archiver.MavenArchiveConfiguration;
import org.apache.maven.archiver.MavenArchiver;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.filtering.MavenFileFilter;
import org.apache.maven.shared.filtering.MavenFilteringException;
import org.apache.maven.shared.filtering.MavenResourcesExecution;
import org.apache.maven.shared.utils.io.FileUtils.FilterWrapper;
import org.codehaus.plexus.archiver.Archiver;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.jar.JarArchiver;
import org.codehaus.plexus.archiver.jar.ManifestException;
import org.codehaus.plexus.util.FileUtils;

@Mojo(name="acr", requiresDependencyResolution=ResolutionScope.RUNTIME, threadSafe=true, defaultPhase=LifecyclePhase.PACKAGE)
public class AcrMojo
  extends AbstractMojo
{
  private static final String APP_CLIENT_XML = "META-INF/application-client.xml";
  private static final String[] DEFAULT_INCLUDES = { "**/**" };
  private static final String[] DEFAULT_EXCLUDES = { "META-INF/application-client.xml" };
  @Parameter(defaultValue="${project.build.directory}", required=true, readonly=true)
  private File basedir;
  @Parameter(property="maven.acr.outputDirectory", defaultValue="${project.build.outputDirectory}")
  private File outputDirectory;
  @Parameter(defaultValue="${project.build.finalName}")
  private String jarName;
  @Parameter
  private List<String> excludes;
  @Parameter(defaultValue="${project}", readonly=true, required=true)
  private MavenProject project;
  @Component(role=Archiver.class, hint="jar")
  private JarArchiver jarArchiver;
  @Parameter
  private MavenArchiveConfiguration archive = new MavenArchiveConfiguration();
  @Parameter(property="maven.acr.escapeBackslashesInFilePath", defaultValue="false")
  private boolean escapeBackslashesInFilePath;
  @Parameter(property="maven.acr.escapeString")
  private String escapeString;
  @Parameter(property="maven.acr.filterDeploymentDescriptor", defaultValue="false")
  private boolean filterDeploymentDescriptor;
  @Parameter
  private List<String> filters;
  @Component(role=MavenFileFilter.class, hint="default")
  private MavenFileFilter mavenFileFilter;
  @Parameter(defaultValue="${session}", readonly=true, required=true)
  private MavenSession session;
  
  public void execute()
    throws MojoExecutionException
  {
    if (getLog().isInfoEnabled()) {
      getLog().info("Building JavaEE Application client: " + jarName);
    }
    File jarFile = getAppClientJarFile(basedir, jarName);
    
    MavenArchiver archiver = new MavenArchiver();
    
    archiver.setArchiver(jarArchiver);
    
    archiver.setOutputFile(jarFile);
    try
    {
      String[] mainJarExcludes = DEFAULT_EXCLUDES;
      if ((excludes != null) && (!excludes.isEmpty()))
      {
        excludes.add("META-INF/application-client.xml");
        mainJarExcludes = (String[])excludes.toArray(new String[excludes.size()]);
      }
      if (outputDirectory.exists()) {
        archiver.getArchiver().addDirectory(outputDirectory, DEFAULT_INCLUDES, mainJarExcludes);
      } else {
        getLog().info("JAR will only contain the META-INF/application-client.xml as no content was marked for inclusion");
      }
      File deploymentDescriptor = new File(outputDirectory, "META-INF/application-client.xml");
      if (deploymentDescriptor.exists())
      {
        if (filterDeploymentDescriptor)
        {
          getLog().debug("Filtering deployment descriptor.");
          MavenResourcesExecution mavenResourcesExecution = new MavenResourcesExecution();
          mavenResourcesExecution.setEscapeString(escapeString);
          
          List<FileUtils.FilterWrapper> filterWrappers = mavenFileFilter.getDefaultFilterWrappers(project, filters, escapeBackslashesInFilePath, session, mavenResourcesExecution);
          
          File unfilteredDeploymentDescriptor = new File(outputDirectory, "META-INF/application-client.xml.unfiltered");
          FileUtils.copyFile(deploymentDescriptor, unfilteredDeploymentDescriptor);
          mavenFileFilter.copyFile(unfilteredDeploymentDescriptor, deploymentDescriptor, true, filterWrappers, 
            getEncoding(unfilteredDeploymentDescriptor));
          
          FileUtils.forceDelete(unfilteredDeploymentDescriptor);
        }
        archiver.getArchiver().addFile(deploymentDescriptor, "META-INF/application-client.xml");
      }
      archiver.createArchive(session, project, archive);
    }
    catch (ArchiverException e)
    {
      throw new MojoExecutionException("There was a problem creating the JavaEE Application Client  archive: " + e.getMessage(), e);
    }
    catch (ManifestException e)
    {
      throw new MojoExecutionException("There was a problem reading / creating the manifest for the JavaEE Application Client  archive: " + e.getMessage(), e);
    }
    catch (IOException e)
    {
      throw new MojoExecutionException("There was a I/O problem creating the JavaEE Application Client archive: " + e.getMessage(), e);
    }
    catch (DependencyResolutionRequiredException e)
    {
      throw new MojoExecutionException("There was a problem resolving dependencies while creating the JavaEE Application Client archive: " + e.getMessage(), e);
    }
    catch (MavenFilteringException e)
    {
      throw new MojoExecutionException("There was a problem filtering the deployment descriptor: " + e.getMessage(), e);
    }
    project.getArtifact().setFile(jarFile);
  }
  
  private static File getAppClientJarFile(File basedir, String finalName)
  {
    return new File(basedir, finalName + ".jar");
  }
  
  private String getEncoding(File xmlFile)
    throws IOException
  {
    XmlStreamReader xmlReader = null;
    try
    {
      xmlReader = new XmlStreamReader(xmlFile);
      String encoding = xmlReader.getEncoding();
      xmlReader.close();
      xmlReader = null;
      return encoding;
    }
    finally
    {
      IOUtils.closeQuietly(xmlReader);
    }
  }
}

/* Location:
 * Qualified Name:     org.apache.maven.plugins.acr.AcrMojo
 * Java Class Version: 7 (51.0)
 * JD-Core Version:    0.7.1
 */