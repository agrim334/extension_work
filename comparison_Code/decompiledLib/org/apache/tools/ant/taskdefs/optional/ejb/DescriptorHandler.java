package org.apache.tools.ant.taskdefs.optional.ejb;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Map;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.xml.sax.AttributeList;
import org.xml.sax.HandlerBase;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class DescriptorHandler
  extends HandlerBase
{
  private static final int DEFAULT_HASH_TABLE_SIZE = 10;
  private static final int STATE_LOOKING_EJBJAR = 1;
  private static final int STATE_IN_EJBJAR = 2;
  private static final int STATE_IN_BEANS = 3;
  private static final int STATE_IN_SESSION = 4;
  private static final int STATE_IN_ENTITY = 5;
  private static final int STATE_IN_MESSAGE = 6;
  private Task owningTask;
  private String publicId = null;
  private static final String EJB_REF = "ejb-ref";
  private static final String EJB_LOCAL_REF = "ejb-local-ref";
  private static final String HOME_INTERFACE = "home";
  private static final String REMOTE_INTERFACE = "remote";
  private static final String LOCAL_HOME_INTERFACE = "local-home";
  private static final String LOCAL_INTERFACE = "local";
  private static final String BEAN_CLASS = "ejb-class";
  private static final String PK_CLASS = "prim-key-class";
  private static final String EJB_NAME = "ejb-name";
  private static final String EJB_JAR = "ejb-jar";
  private static final String ENTERPRISE_BEANS = "enterprise-beans";
  private static final String ENTITY_BEAN = "entity";
  private static final String SESSION_BEAN = "session";
  private static final String MESSAGE_BEAN = "message-driven";
  private int parseState = 1;
  protected String currentElement = null;
  protected String currentText = null;
  protected Hashtable<String, File> ejbFiles = null;
  protected String ejbName = null;
  private Map<String, File> fileDTDs = new Hashtable();
  private Map<String, String> resourceDTDs = new Hashtable();
  private boolean inEJBRef = false;
  private Map<String, URL> urlDTDs = new Hashtable();
  private File srcDir;
  
  public DescriptorHandler(Task task, File srcDir)
  {
    owningTask = task;
    this.srcDir = srcDir;
  }
  
  public void registerDTD(String publicId, String location)
  {
    if (location == null) {
      return;
    }
    File fileDTD = new File(location);
    if (!fileDTD.exists()) {
      fileDTD = owningTask.getProject().resolveFile(location);
    }
    if (fileDTD.exists())
    {
      if (publicId != null)
      {
        fileDTDs.put(publicId, fileDTD);
        owningTask.log("Mapped publicId " + publicId + " to file " + fileDTD, 3);
      }
      return;
    }
    if ((getClass().getResource(location) != null) && 
      (publicId != null))
    {
      resourceDTDs.put(publicId, location);
      owningTask.log("Mapped publicId " + publicId + " to resource " + location, 3);
    }
    try
    {
      if (publicId != null)
      {
        URL urldtd = new URL(location);
        urlDTDs.put(publicId, urldtd);
      }
    }
    catch (MalformedURLException localMalformedURLException) {}
  }
  
  public InputSource resolveEntity(String publicId, String systemId)
    throws SAXException
  {
    this.publicId = publicId;
    
    File dtdFile = (File)fileDTDs.get(publicId);
    if (dtdFile != null) {
      try
      {
        owningTask.log("Resolved " + publicId + " to local file " + dtdFile, 3);
        
        return new InputSource(Files.newInputStream(dtdFile.toPath(), new OpenOption[0]));
      }
      catch (IOException localIOException) {}
    }
    String dtdResourceName = (String)resourceDTDs.get(publicId);
    if (dtdResourceName != null)
    {
      InputStream is = getClass().getResourceAsStream(dtdResourceName);
      if (is != null)
      {
        owningTask.log("Resolved " + publicId + " to local resource " + dtdResourceName, 3);
        
        return new InputSource(is);
      }
    }
    URL dtdUrl = (URL)urlDTDs.get(publicId);
    if (dtdUrl != null) {
      try
      {
        InputStream is = dtdUrl.openStream();
        owningTask.log("Resolved " + publicId + " to url " + dtdUrl, 3);
        
        return new InputSource(is);
      }
      catch (IOException localIOException1) {}
    }
    owningTask.log("Could not resolve (publicId: " + publicId + ", systemId: " + systemId + ") to a local entity", 2);
    
    return null;
  }
  
  public Hashtable<String, File> getFiles()
  {
    return ejbFiles == null ? new Hashtable(Collections.emptyMap()) : ejbFiles;
  }
  
  public String getPublicId()
  {
    return publicId;
  }
  
  public String getEjbName()
  {
    return ejbName;
  }
  
  public void startDocument()
    throws SAXException
  {
    ejbFiles = new Hashtable(10, 1.0F);
    currentElement = null;
    inEJBRef = false;
  }
  
  public void startElement(String name, AttributeList attrs)
    throws SAXException
  {
    currentElement = name;
    currentText = "";
    if (("ejb-ref".equals(name)) || ("ejb-local-ref".equals(name))) {
      inEJBRef = true;
    } else if ((parseState == 1) && ("ejb-jar".equals(name))) {
      parseState = 2;
    } else if ((parseState == 2) && ("enterprise-beans".equals(name))) {
      parseState = 3;
    } else if ((parseState == 3) && ("session".equals(name))) {
      parseState = 4;
    } else if ((parseState == 3) && ("entity".equals(name))) {
      parseState = 5;
    } else if ((parseState == 3) && ("message-driven".equals(name))) {
      parseState = 6;
    }
  }
  
  public void endElement(String name)
    throws SAXException
  {
    processElement();
    currentText = "";
    currentElement = "";
    if ((name.equals("ejb-ref")) || (name.equals("ejb-local-ref"))) {
      inEJBRef = false;
    } else if ((parseState == 5) && (name.equals("entity"))) {
      parseState = 3;
    } else if ((parseState == 4) && (name.equals("session"))) {
      parseState = 3;
    } else if ((parseState == 6) && (name.equals("message-driven"))) {
      parseState = 3;
    } else if ((parseState == 3) && (name.equals("enterprise-beans"))) {
      parseState = 2;
    } else if ((parseState == 2) && (name.equals("ejb-jar"))) {
      parseState = 1;
    }
  }
  
  public void characters(char[] ch, int start, int length)
    throws SAXException
  {
    currentText += new String(ch, start, length);
  }
  
  protected void processElement()
  {
    if ((inEJBRef) || ((parseState != 5) && (parseState != 4) && (parseState != 6))) {
      return;
    }
    if (("home".equals(currentElement)) || 
      ("remote".equals(currentElement)) || 
      ("local".equals(currentElement)) || 
      ("local-home".equals(currentElement)) || 
      ("ejb-class".equals(currentElement)) || 
      ("prim-key-class".equals(currentElement)))
    {
      String className = currentText.trim();
      if ((!className.startsWith("java.")) && 
        (!className.startsWith("javax.")))
      {
        className = className.replace('.', File.separatorChar);
        className = className + ".class";
        ejbFiles.put(className, new File(srcDir, className));
      }
    }
    if ((currentElement.equals("ejb-name")) && 
      (ejbName == null)) {
      ejbName = currentText.trim();
    }
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.optional.ejb.DescriptorHandler
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */