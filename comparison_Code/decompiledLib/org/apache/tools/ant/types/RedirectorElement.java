package org.apache.tools.ant.types;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import java.util.Vector;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Redirector;
import org.apache.tools.ant.util.FileNameMapper;
import org.apache.tools.ant.util.MergingMapper;

public class RedirectorElement
  extends DataType
{
  private boolean usingInput = false;
  private boolean usingOutput = false;
  private boolean usingError = false;
  private Boolean logError;
  private String outputProperty;
  private String errorProperty;
  private String inputString;
  private Boolean append;
  private Boolean alwaysLog;
  private Boolean createEmptyFiles;
  private Mapper inputMapper;
  private Mapper outputMapper;
  private Mapper errorMapper;
  private Vector<FilterChain> inputFilterChains = new Vector();
  private Vector<FilterChain> outputFilterChains = new Vector();
  private Vector<FilterChain> errorFilterChains = new Vector();
  private String outputEncoding;
  private String errorEncoding;
  private String inputEncoding;
  private Boolean logInputString;
  private boolean outputIsBinary = false;
  
  public void addConfiguredInputMapper(Mapper inputMapper)
  {
    if (isReference()) {
      throw noChildrenAllowed();
    }
    if (this.inputMapper != null)
    {
      if (usingInput) {
        throw new BuildException("attribute \"input\" cannot coexist with a nested <inputmapper>");
      }
      throw new BuildException("Cannot have > 1 <inputmapper>");
    }
    setChecked(false);
    this.inputMapper = inputMapper;
  }
  
  public void addConfiguredOutputMapper(Mapper outputMapper)
  {
    if (isReference()) {
      throw noChildrenAllowed();
    }
    if (this.outputMapper != null)
    {
      if (usingOutput) {
        throw new BuildException("attribute \"output\" cannot coexist with a nested <outputmapper>");
      }
      throw new BuildException("Cannot have > 1 <outputmapper>");
    }
    setChecked(false);
    this.outputMapper = outputMapper;
  }
  
  public void addConfiguredErrorMapper(Mapper errorMapper)
  {
    if (isReference()) {
      throw noChildrenAllowed();
    }
    if (this.errorMapper != null)
    {
      if (usingError) {
        throw new BuildException("attribute \"error\" cannot coexist with a nested <errormapper>");
      }
      throw new BuildException("Cannot have > 1 <errormapper>");
    }
    setChecked(false);
    this.errorMapper = errorMapper;
  }
  
  public void setRefid(Reference r)
    throws BuildException
  {
    if ((usingInput) || (usingOutput) || (usingError) || (inputString != null) || (logError != null) || (append != null) || (createEmptyFiles != null) || (inputEncoding != null) || (outputEncoding != null) || (errorEncoding != null) || (outputProperty != null) || (errorProperty != null) || (logInputString != null)) {
      throw tooManyAttributes();
    }
    super.setRefid(r);
  }
  
  public void setInput(File input)
  {
    if (isReference()) {
      throw tooManyAttributes();
    }
    if (inputString != null) {
      throw new BuildException("The \"input\" and \"inputstring\" attributes cannot both be specified");
    }
    usingInput = true;
    inputMapper = createMergeMapper(input);
  }
  
  public void setInputString(String inputString)
  {
    if (isReference()) {
      throw tooManyAttributes();
    }
    if (usingInput) {
      throw new BuildException("The \"input\" and \"inputstring\" attributes cannot both be specified");
    }
    this.inputString = inputString;
  }
  
  public void setLogInputString(boolean logInputString)
  {
    if (isReference()) {
      throw tooManyAttributes();
    }
    this.logInputString = (logInputString ? Boolean.TRUE : Boolean.FALSE);
  }
  
  public void setOutput(File out)
  {
    if (isReference()) {
      throw tooManyAttributes();
    }
    if (out == null) {
      throw new IllegalArgumentException("output file specified as null");
    }
    usingOutput = true;
    outputMapper = createMergeMapper(out);
  }
  
  public void setOutputEncoding(String outputEncoding)
  {
    if (isReference()) {
      throw tooManyAttributes();
    }
    this.outputEncoding = outputEncoding;
  }
  
  public void setErrorEncoding(String errorEncoding)
  {
    if (isReference()) {
      throw tooManyAttributes();
    }
    this.errorEncoding = errorEncoding;
  }
  
  public void setInputEncoding(String inputEncoding)
  {
    if (isReference()) {
      throw tooManyAttributes();
    }
    this.inputEncoding = inputEncoding;
  }
  
  public void setLogError(boolean logError)
  {
    if (isReference()) {
      throw tooManyAttributes();
    }
    this.logError = (logError ? Boolean.TRUE : Boolean.FALSE);
  }
  
  public void setError(File error)
  {
    if (isReference()) {
      throw tooManyAttributes();
    }
    if (error == null) {
      throw new IllegalArgumentException("error file specified as null");
    }
    usingError = true;
    errorMapper = createMergeMapper(error);
  }
  
  public void setOutputProperty(String outputProperty)
  {
    if (isReference()) {
      throw tooManyAttributes();
    }
    this.outputProperty = outputProperty;
  }
  
  public void setAppend(boolean append)
  {
    if (isReference()) {
      throw tooManyAttributes();
    }
    this.append = (append ? Boolean.TRUE : Boolean.FALSE);
  }
  
  public void setAlwaysLog(boolean alwaysLog)
  {
    if (isReference()) {
      throw tooManyAttributes();
    }
    this.alwaysLog = (alwaysLog ? Boolean.TRUE : Boolean.FALSE);
  }
  
  public void setCreateEmptyFiles(boolean createEmptyFiles)
  {
    if (isReference()) {
      throw tooManyAttributes();
    }
    this.createEmptyFiles = (createEmptyFiles ? Boolean.TRUE : Boolean.FALSE);
  }
  
  public void setErrorProperty(String errorProperty)
  {
    if (isReference()) {
      throw tooManyAttributes();
    }
    this.errorProperty = errorProperty;
  }
  
  public FilterChain createInputFilterChain()
  {
    if (isReference()) {
      throw noChildrenAllowed();
    }
    FilterChain result = new FilterChain();
    result.setProject(getProject());
    inputFilterChains.add(result);
    setChecked(false);
    return result;
  }
  
  public FilterChain createOutputFilterChain()
  {
    if (isReference()) {
      throw noChildrenAllowed();
    }
    FilterChain result = new FilterChain();
    result.setProject(getProject());
    outputFilterChains.add(result);
    setChecked(false);
    return result;
  }
  
  public FilterChain createErrorFilterChain()
  {
    if (isReference()) {
      throw noChildrenAllowed();
    }
    FilterChain result = new FilterChain();
    result.setProject(getProject());
    errorFilterChains.add(result);
    setChecked(false);
    return result;
  }
  
  public void setBinaryOutput(boolean b)
  {
    outputIsBinary = b;
  }
  
  public void configure(Redirector redirector)
  {
    configure(redirector, null);
  }
  
  public void configure(Redirector redirector, String sourcefile)
  {
    if (isReference())
    {
      getRef().configure(redirector, sourcefile);
      return;
    }
    dieOnCircularReference();
    if (alwaysLog != null) {
      redirector.setAlwaysLog(alwaysLog.booleanValue());
    }
    if (logError != null) {
      redirector.setLogError(logError.booleanValue());
    }
    if (append != null) {
      redirector.setAppend(append.booleanValue());
    }
    if (createEmptyFiles != null) {
      redirector.setCreateEmptyFiles(createEmptyFiles.booleanValue());
    }
    if (outputProperty != null) {
      redirector.setOutputProperty(outputProperty);
    }
    if (errorProperty != null) {
      redirector.setErrorProperty(errorProperty);
    }
    if (inputString != null) {
      redirector.setInputString(inputString);
    }
    if (logInputString != null) {
      redirector.setLogInputString(logInputString.booleanValue());
    }
    if (inputMapper != null)
    {
      String[] inputTargets = null;
      try
      {
        inputTargets = inputMapper.getImplementation().mapFileName(sourcefile);
      }
      catch (NullPointerException enPeaEx)
      {
        if (sourcefile != null) {
          throw enPeaEx;
        }
      }
      if ((inputTargets != null) && (inputTargets.length > 0)) {
        redirector.setInput(toFileArray(inputTargets));
      }
    }
    if (outputMapper != null)
    {
      String[] outputTargets = null;
      try
      {
        outputTargets = outputMapper.getImplementation().mapFileName(sourcefile);
      }
      catch (NullPointerException enPeaEx)
      {
        if (sourcefile != null) {
          throw enPeaEx;
        }
      }
      if ((outputTargets != null) && (outputTargets.length > 0)) {
        redirector.setOutput(toFileArray(outputTargets));
      }
    }
    if (errorMapper != null)
    {
      String[] errorTargets = null;
      try
      {
        errorTargets = errorMapper.getImplementation().mapFileName(sourcefile);
      }
      catch (NullPointerException enPeaEx)
      {
        if (sourcefile != null) {
          throw enPeaEx;
        }
      }
      if ((errorTargets != null) && (errorTargets.length > 0)) {
        redirector.setError(toFileArray(errorTargets));
      }
    }
    if (!inputFilterChains.isEmpty()) {
      redirector.setInputFilterChains(inputFilterChains);
    }
    if (!outputFilterChains.isEmpty()) {
      redirector.setOutputFilterChains(outputFilterChains);
    }
    if (!errorFilterChains.isEmpty()) {
      redirector.setErrorFilterChains(errorFilterChains);
    }
    if (inputEncoding != null) {
      redirector.setInputEncoding(inputEncoding);
    }
    if (outputEncoding != null) {
      redirector.setOutputEncoding(outputEncoding);
    }
    if (errorEncoding != null) {
      redirector.setErrorEncoding(errorEncoding);
    }
    redirector.setBinaryOutput(outputIsBinary);
  }
  
  protected Mapper createMergeMapper(File destfile)
  {
    Mapper result = new Mapper(getProject());
    result.setClassname(MergingMapper.class.getName());
    result.setTo(destfile.getAbsolutePath());
    return result;
  }
  
  protected File[] toFileArray(String[] name)
  {
    if (name == null) {
      return null;
    }
    ArrayList<File> list = new ArrayList(name.length);
    for (String n : name) {
      if (n != null) {
        list.add(getProject().resolveFile(n));
      }
    }
    return (File[])list.toArray(new File[list.size()]);
  }
  
  protected void dieOnCircularReference(Stack<Object> stk, Project p)
    throws BuildException
  {
    if (isChecked()) {
      return;
    }
    if (isReference())
    {
      super.dieOnCircularReference(stk, p);
    }
    else
    {
      for (Iterator localIterator1 = Arrays.asList(new Mapper[] { inputMapper, outputMapper, errorMapper }).iterator(); localIterator1.hasNext();)
      {
        m = (Mapper)localIterator1.next();
        if (m != null)
        {
          stk.push(m);
          m.dieOnCircularReference(stk, p);
          stk.pop();
        }
      }
      Mapper m;
      Object filterChainLists = Arrays.asList(new List[] { inputFilterChains, outputFilterChains, errorFilterChains });
      for (List<FilterChain> filterChains : (List)filterChainLists) {
        if (filterChains != null) {
          for (FilterChain fc : filterChains) {
            pushAndInvokeCircularReferenceCheck(fc, stk, p);
          }
        }
      }
      setChecked(true);
    }
  }
  
  private RedirectorElement getRef()
  {
    return (RedirectorElement)getCheckedRef(RedirectorElement.class);
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.types.RedirectorElement
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */