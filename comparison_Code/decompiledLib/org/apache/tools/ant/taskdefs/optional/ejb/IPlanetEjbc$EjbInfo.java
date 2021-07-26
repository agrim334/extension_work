package org.apache.tools.ant.taskdefs.optional.ejb;

import java.io.File;
import java.io.PrintStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

class IPlanetEjbc$EjbInfo
{
  private String name;
  private IPlanetEjbc.Classname home;
  private IPlanetEjbc.Classname remote;
  private IPlanetEjbc.Classname implementation;
  private IPlanetEjbc.Classname primaryKey;
  private String beantype = "entity";
  private boolean cmp = false;
  private boolean iiop = false;
  private boolean hasession = false;
  private List<String> cmpDescriptors = new ArrayList();
  
  public IPlanetEjbc$EjbInfo(IPlanetEjbc paramIPlanetEjbc, String name)
  {
    this.name = name;
  }
  
  public String getName()
  {
    if (name == null)
    {
      if (implementation == null) {
        return "[unnamed]";
      }
      return implementation.getClassName();
    }
    return name;
  }
  
  public void setHome(String home)
  {
    setHome(new IPlanetEjbc.Classname(home));
  }
  
  public void setHome(IPlanetEjbc.Classname home)
  {
    this.home = home;
  }
  
  public IPlanetEjbc.Classname getHome()
  {
    return home;
  }
  
  public void setRemote(String remote)
  {
    setRemote(new IPlanetEjbc.Classname(remote));
  }
  
  public void setRemote(IPlanetEjbc.Classname remote)
  {
    this.remote = remote;
  }
  
  public IPlanetEjbc.Classname getRemote()
  {
    return remote;
  }
  
  public void setImplementation(String implementation)
  {
    setImplementation(new IPlanetEjbc.Classname(implementation));
  }
  
  public void setImplementation(IPlanetEjbc.Classname implementation)
  {
    this.implementation = implementation;
  }
  
  public IPlanetEjbc.Classname getImplementation()
  {
    return implementation;
  }
  
  public void setPrimaryKey(String primaryKey)
  {
    setPrimaryKey(new IPlanetEjbc.Classname(primaryKey));
  }
  
  public void setPrimaryKey(IPlanetEjbc.Classname primaryKey)
  {
    this.primaryKey = primaryKey;
  }
  
  public IPlanetEjbc.Classname getPrimaryKey()
  {
    return primaryKey;
  }
  
  public void setBeantype(String beantype)
  {
    this.beantype = beantype.toLowerCase();
  }
  
  public String getBeantype()
  {
    return beantype;
  }
  
  public void setCmp(boolean cmp)
  {
    this.cmp = cmp;
  }
  
  public void setCmp(String cmp)
  {
    setCmp("Container".equals(cmp));
  }
  
  public boolean getCmp()
  {
    return cmp;
  }
  
  public void setIiop(boolean iiop)
  {
    this.iiop = iiop;
  }
  
  public void setIiop(String iiop)
  {
    setIiop(Boolean.parseBoolean(iiop));
  }
  
  public boolean getIiop()
  {
    return iiop;
  }
  
  public void setHasession(boolean hasession)
  {
    this.hasession = hasession;
  }
  
  public void setHasession(String hasession)
  {
    setHasession(Boolean.parseBoolean(hasession));
  }
  
  public boolean getHasession()
  {
    return hasession;
  }
  
  public void addCmpDescriptor(String descriptor)
  {
    cmpDescriptors.add(descriptor);
  }
  
  public List<String> getCmpDescriptors()
  {
    return cmpDescriptors;
  }
  
  private void checkConfiguration(File buildDir)
    throws IPlanetEjbc.EjbcException
  {
    if (home == null) {
      throw new IPlanetEjbc.EjbcException(this$0, "A home interface was not found for the " + name + " EJB.");
    }
    if (remote == null) {
      throw new IPlanetEjbc.EjbcException(this$0, "A remote interface was not found for the " + name + " EJB.");
    }
    if (implementation == null) {
      throw new IPlanetEjbc.EjbcException(this$0, "An EJB implementation class was not found for the " + name + " EJB.");
    }
    if ((!beantype.equals("entity")) && 
      (!beantype.equals("stateless")) && 
      (!beantype.equals("stateful"))) {
      throw new IPlanetEjbc.EjbcException(this$0, "The beantype found (" + beantype + ") isn't valid in the " + name + " EJB.");
    }
    if ((cmp) && (!beantype.equals("entity"))) {
      System.out.println("CMP stubs and skeletons may not be generated for a Session Bean -- the \"cmp\" attribute will be ignoredfor the " + name + " EJB.");
    }
    if ((hasession) && (!beantype.equals("stateful"))) {
      System.out.println("Highly available stubs and skeletons may only be generated for a Stateful Session Bean-- the \"hasession\" attribute will be ignored for the " + name + " EJB.");
    }
    if (!remote.getClassFile(buildDir).exists()) {
      throw new IPlanetEjbc.EjbcException(this$0, "The remote interface " + remote.getQualifiedClassName() + " could not be found.");
    }
    if (!home.getClassFile(buildDir).exists()) {
      throw new IPlanetEjbc.EjbcException(this$0, "The home interface " + home.getQualifiedClassName() + " could not be found.");
    }
    if (!implementation.getClassFile(buildDir).exists()) {
      throw new IPlanetEjbc.EjbcException(this$0, "The EJB implementation class " + implementation.getQualifiedClassName() + " could not be found.");
    }
  }
  
  public boolean mustBeRecompiled(File destDir)
  {
    long sourceModified = sourceClassesModified(destDir);
    
    long destModified = destClassesModified(destDir);
    
    return destModified < sourceModified;
  }
  
  private long sourceClassesModified(File buildDir)
  {
    File remoteFile = remote.getClassFile(buildDir);
    long modified = remoteFile.lastModified();
    if (modified == -1L)
    {
      System.out.println("The class " + remote.getQualifiedClassName() + " couldn't be found on the classpath");
      
      return -1L;
    }
    long latestModified = modified;
    
    File homeFile = home.getClassFile(buildDir);
    modified = homeFile.lastModified();
    if (modified == -1L)
    {
      System.out.println("The class " + home.getQualifiedClassName() + " couldn't be found on the classpath");
      
      return -1L;
    }
    latestModified = Math.max(latestModified, modified);
    File pkFile;
    if (primaryKey != null)
    {
      File pkFile = primaryKey.getClassFile(buildDir);
      modified = pkFile.lastModified();
      if (modified == -1L)
      {
        System.out.println("The class " + primaryKey
          .getQualifiedClassName() + "couldn't be found on the classpath");
        
        return -1L;
      }
      latestModified = Math.max(latestModified, modified);
    }
    else
    {
      pkFile = null;
    }
    File implFile = implementation.getClassFile(buildDir);
    modified = implFile.lastModified();
    if (modified == -1L)
    {
      System.out.println("The class " + implementation
        .getQualifiedClassName() + " couldn't be found on the classpath");
      
      return -1L;
    }
    String pathToFile = remote.getQualifiedClassName();
    pathToFile = pathToFile.replace('.', File.separatorChar) + ".class";
    IPlanetEjbc.access$300(this$0).put(pathToFile, remoteFile);
    
    pathToFile = home.getQualifiedClassName();
    pathToFile = pathToFile.replace('.', File.separatorChar) + ".class";
    IPlanetEjbc.access$300(this$0).put(pathToFile, homeFile);
    
    pathToFile = implementation.getQualifiedClassName();
    pathToFile = pathToFile.replace('.', File.separatorChar) + ".class";
    IPlanetEjbc.access$300(this$0).put(pathToFile, implFile);
    if (pkFile != null)
    {
      pathToFile = primaryKey.getQualifiedClassName();
      pathToFile = pathToFile.replace('.', File.separatorChar) + ".class";
      IPlanetEjbc.access$300(this$0).put(pathToFile, pkFile);
    }
    return latestModified;
  }
  
  private long destClassesModified(File destDir)
  {
    String[] classnames = classesToGenerate();
    long destClassesModified = Instant.now().toEpochMilli();
    boolean allClassesFound = true;
    for (String classname : classnames)
    {
      String pathToClass = classname.replace('.', File.separatorChar) + ".class";
      File classFile = new File(destDir, pathToClass);
      
      IPlanetEjbc.access$300(this$0).put(pathToClass, classFile);
      
      allClassesFound = (allClassesFound) && (classFile.exists());
      if (allClassesFound)
      {
        long fileMod = classFile.lastModified();
        
        destClassesModified = Math.min(destClassesModified, fileMod);
      }
    }
    return allClassesFound ? destClassesModified : -1L;
  }
  
  private String[] classesToGenerate()
  {
    String[] classnames = iiop ? new String[15] : new String[9];
    
    String remotePkg = remote.getPackageName() + ".";
    String remoteClass = remote.getClassName();
    String homePkg = home.getPackageName() + ".";
    String homeClass = home.getClassName();
    String implPkg = implementation.getPackageName() + ".";
    String implFullClass = implementation.getQualifiedWithUnderscores();
    int index = 0;
    
    classnames[(index++)] = (implPkg + "ejb_fac_" + implFullClass);
    classnames[(index++)] = (implPkg + "ejb_home_" + implFullClass);
    classnames[(index++)] = (implPkg + "ejb_skel_" + implFullClass);
    classnames[(index++)] = (remotePkg + "ejb_kcp_skel_" + remoteClass);
    classnames[(index++)] = (homePkg + "ejb_kcp_skel_" + homeClass);
    classnames[(index++)] = (remotePkg + "ejb_kcp_stub_" + remoteClass);
    classnames[(index++)] = (homePkg + "ejb_kcp_stub_" + homeClass);
    classnames[(index++)] = (remotePkg + "ejb_stub_" + remoteClass);
    classnames[(index++)] = (homePkg + "ejb_stub_" + homeClass);
    if (!iiop) {
      return classnames;
    }
    classnames[(index++)] = ("org.omg.stub." + remotePkg + "_" + remoteClass + "_Stub");
    
    classnames[(index++)] = ("org.omg.stub." + homePkg + "_" + homeClass + "_Stub");
    
    classnames[(index++)] = ("org.omg.stub." + remotePkg + "_ejb_RmiCorbaBridge_" + remoteClass + "_Tie");
    
    classnames[(index++)] = ("org.omg.stub." + homePkg + "_ejb_RmiCorbaBridge_" + homeClass + "_Tie");
    
    classnames[(index++)] = (remotePkg + "ejb_RmiCorbaBridge_" + remoteClass);
    
    classnames[(index++)] = (homePkg + "ejb_RmiCorbaBridge_" + homeClass);
    
    return classnames;
  }
  
  public String toString()
  {
    StringBuilder s = new StringBuilder("EJB name: " + name + "\n\r              home:      " + home + "\n\r              remote:    " + remote + "\n\r              impl:      " + implementation + "\n\r              primaryKey: " + primaryKey + "\n\r              beantype:  " + beantype + "\n\r              cmp:       " + cmp + "\n\r              iiop:      " + iiop + "\n\r              hasession: " + hasession);
    for (String cmpDescriptor : cmpDescriptors) {
      s.append("\n\r              CMP Descriptor: ").append(cmpDescriptor);
    }
    return s.toString();
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.optional.ejb.IPlanetEjbc.EjbInfo
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */