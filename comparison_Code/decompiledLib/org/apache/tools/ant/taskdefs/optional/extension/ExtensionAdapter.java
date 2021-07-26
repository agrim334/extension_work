package org.apache.tools.ant.taskdefs.optional.extension;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.DataType;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.util.DeweyDecimal;

public class ExtensionAdapter
  extends DataType
{
  private String extensionName;
  private DeweyDecimal specificationVersion;
  private String specificationVendor;
  private String implementationVendorID;
  private String implementationVendor;
  private DeweyDecimal implementationVersion;
  private String implementationURL;
  
  public void setExtensionName(String extensionName)
  {
    verifyNotAReference();
    this.extensionName = extensionName;
  }
  
  public void setSpecificationVersion(String specificationVersion)
  {
    verifyNotAReference();
    this.specificationVersion = new DeweyDecimal(specificationVersion);
  }
  
  public void setSpecificationVendor(String specificationVendor)
  {
    verifyNotAReference();
    this.specificationVendor = specificationVendor;
  }
  
  public void setImplementationVendorId(String implementationVendorID)
  {
    verifyNotAReference();
    this.implementationVendorID = implementationVendorID;
  }
  
  public void setImplementationVendor(String implementationVendor)
  {
    verifyNotAReference();
    this.implementationVendor = implementationVendor;
  }
  
  public void setImplementationVersion(String implementationVersion)
  {
    verifyNotAReference();
    this.implementationVersion = new DeweyDecimal(implementationVersion);
  }
  
  public void setImplementationUrl(String implementationURL)
  {
    verifyNotAReference();
    this.implementationURL = implementationURL;
  }
  
  public void setRefid(Reference reference)
    throws BuildException
  {
    if ((null != extensionName) || (null != specificationVersion) || (null != specificationVendor) || (null != implementationVersion) || (null != implementationVendorID) || (null != implementationVendor) || (null != implementationURL)) {
      throw tooManyAttributes();
    }
    super.setRefid(reference);
  }
  
  private void verifyNotAReference()
    throws BuildException
  {
    if (isReference()) {
      throw tooManyAttributes();
    }
  }
  
  Extension toExtension()
    throws BuildException
  {
    if (isReference()) {
      return getRef().toExtension();
    }
    dieOnCircularReference();
    if (null == extensionName) {
      throw new BuildException("Extension is missing name.");
    }
    String specificationVersionString = null;
    if (null != specificationVersion) {
      specificationVersionString = specificationVersion.toString();
    }
    String implementationVersionString = null;
    if (null != implementationVersion) {
      implementationVersionString = implementationVersion.toString();
    }
    return new Extension(extensionName, specificationVersionString, specificationVendor, implementationVersionString, implementationVendor, implementationVendorID, implementationURL);
  }
  
  public String toString()
  {
    return "{" + toExtension() + "}";
  }
  
  private ExtensionAdapter getRef()
  {
    return (ExtensionAdapter)getCheckedRef(ExtensionAdapter.class);
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.optional.extension.ExtensionAdapter
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */