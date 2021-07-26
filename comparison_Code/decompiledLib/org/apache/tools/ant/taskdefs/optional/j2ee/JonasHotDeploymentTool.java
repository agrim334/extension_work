package org.apache.tools.ant.taskdefs.optional.j2ee;

import java.io.File;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.Java;
import org.apache.tools.ant.types.Commandline.Argument;
import org.apache.tools.ant.types.Path;

public class JonasHotDeploymentTool
  extends GenericHotDeploymentTool
  implements HotDeploymentTool
{
  protected static final String DEFAULT_ORB = "RMI";
  private static final String JONAS_DEPLOY_CLASS_NAME = "org.objectweb.jonas.adm.JonasAdmin";
  private static final String[] VALID_ACTIONS = { "delete", "deploy", "list", "undeploy", "update" };
  private File jonasroot;
  private String orb = null;
  private String davidHost;
  private int davidPort;
  
  public void setDavidhost(String inValue)
  {
    davidHost = inValue;
  }
  
  public void setDavidport(int inValue)
  {
    davidPort = inValue;
  }
  
  public void setJonasroot(File inValue)
  {
    jonasroot = inValue;
  }
  
  public void setOrb(String inValue)
  {
    orb = inValue;
  }
  
  public Path getClasspath()
  {
    Path aClassPath = super.getClasspath();
    if (aClassPath == null) {
      aClassPath = new Path(getTask().getProject());
    }
    if (orb != null)
    {
      String aOrbJar = new File(jonasroot, "lib/" + orb + "_jonas.jar").toString();
      String aConfigDir = new File(jonasroot, "config/").toString();
      Path aJOnASOrbPath = new Path(aClassPath.getProject(), aOrbJar + File.pathSeparator + aConfigDir);
      
      aClassPath.append(aJOnASOrbPath);
    }
    return aClassPath;
  }
  
  public void validateAttributes()
    throws BuildException
  {
    Java java = getJava();
    
    String action = getTask().getAction();
    if (action == null) {
      throw new BuildException("The \"action\" attribute must be set");
    }
    if (!isActionValid()) {
      throw new BuildException("Invalid action \"%s\" passed", new Object[] { action });
    }
    if (getClassName() == null) {
      setClassName("org.objectweb.jonas.adm.JonasAdmin");
    }
    if ((jonasroot == null) || (jonasroot.isDirectory()))
    {
      java.createJvmarg().setValue("-Dinstall.root=" + jonasroot);
      java.createJvmarg().setValue("-Djava.security.policy=" + jonasroot + "/config/java.policy");
      if ("DAVID".equals(orb))
      {
        java.createJvmarg().setValue("-Dorg.omg.CORBA.ORBClass=org.objectweb.david.libs.binding.orbs.iiop.IIOPORB");
        
        java.createJvmarg().setValue("-Dorg.omg.CORBA.ORBSingletonClass=org.objectweb.david.libs.binding.orbs.ORBSingletonClass");
        
        java.createJvmarg().setValue("-Djavax.rmi.CORBA.StubClass=org.objectweb.david.libs.stub_factories.rmi.StubDelegate");
        
        java.createJvmarg().setValue("-Djavax.rmi.CORBA.PortableRemoteObjectClass=org.objectweb.david.libs.binding.rmi.ORBPortableRemoteObjectDelegate");
        
        java.createJvmarg().setValue("-Djavax.rmi.CORBA.UtilClass=org.objectweb.david.libs.helpers.RMIUtilDelegate");
        
        java.createJvmarg().setValue("-Ddavid.CosNaming.default_method=0");
        java.createJvmarg().setValue("-Ddavid.rmi.ValueHandlerClass=com.sun.corba.se.internal.io.ValueHandlerImpl");
        if (davidHost != null) {
          java.createJvmarg().setValue("-Ddavid.CosNaming.default_host=" + davidHost);
        }
        if (davidPort != 0) {
          java.createJvmarg().setValue("-Ddavid.CosNaming.default_port=" + davidPort);
        }
      }
    }
    if (getServer() != null) {
      java.createArg().setLine("-n " + getServer());
    }
    if (("deploy".equals(action)) || 
      ("update".equals(action)) || 
      ("redeploy".equals(action))) {
      java.createArg().setLine("-a " + getTask().getSource());
    } else if ((action.equals("delete")) || (action.equals("undeploy"))) {
      java.createArg().setLine("-r " + getTask().getSource());
    } else if (action.equals("list")) {
      java.createArg().setValue("-l");
    }
  }
  
  protected boolean isActionValid()
  {
    String action = getTask().getAction();
    for (String validAction : VALID_ACTIONS) {
      if (action.equals(validAction)) {
        return true;
      }
    }
    return false;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.optional.j2ee.JonasHotDeploymentTool
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */