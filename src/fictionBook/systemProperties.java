package fictionBook;

public class systemProperties {

  public systemProperties() {
    javaVersion = System.getProperty("java.version");
    javaVendor = System.getProperty("java.vendor");
    javaVendorUrl = System.getProperty("java.vendor.url");
    javaHome = System.getProperty("java.home");
    javaVmSpecificationVendor = System.getProperty("java.vm.specification.version");
    javaVmSpecificationVendor = System.getProperty("java.vm.specification.vendor");
    javaVmSpecificationName = System.getProperty("java.vm.specification.name");
    javaVmVersion = System.getProperty("java.vm.version");
    javaVmVendor = System.getProperty("java.vm.vendor");
    javaVmName = System.getProperty("java.vm.name");
    javaSpecificationVersion = System.getProperty("java.specification.version");
    javaSpecificationVendor = System.getProperty("java.specification.vendor");
    javaSpecificationName = System.getProperty("java.specification.name");
    javaClassVersion = System.getProperty("java.class.version");
    javaClassPath = System.getProperty("java.class.path");
    javaLibraryPath = System.getProperty("java.library.path");
    javaIoTmpdir = System.getProperty("java.io.tmpdir");
    javaCompiler = System.getProperty("java.compiler");
    javaExtDirs = System.getProperty("java.ext.dirs");
    osName = System.getProperty("os.name");
    osArch = System.getProperty("os.arch");
    osVersion = System.getProperty("os.version");
    fileSeparator = System.getProperty("file.separator");
    pathSeparator = System.getProperty("path.separator");
    lineSeparator = System.getProperty("line.separator");
    userName = System.getProperty("user.name");
    userHome = System.getProperty("user.home");
    userDir = System.getProperty("user.dir");
  }

  public void printSystemProperties(){
    System.out.println("Version of the Java Runtime Environment: " + javaVersion);
    System.out.println("Name of the Java Runtime Environment vendor: " + javaVendor);
    System.out.println("The URL of the vendor: " + javaVendorUrl);
    System.out.println("Java installation directory: " + javaHome);
    System.out.println("Specification version of JVM: " + javaVmSpecificationVersion);
    System.out.println("Specification vendor of JVM: " + javaVmSpecificationVendor);
    System.out.println("JVM specification name: " + javaVmSpecificationName);
    System.out.println("JVM implementation version: " + javaVmVersion);
    System.out.println("JVM implementation vendor: " + javaVmVendor);
    System.out.println("JVM  implementation name: " + javaVmName);
    System.out.println("Name of the specification version JRE: " + javaSpecificationVersion);
    System.out.println("JRE specification vendor: " + javaSpecificationVendor);
    System.out.println("JRE specification name: " + javaSpecificationName);
    System.out.println("Java class format version number: " + javaClassVersion);
    System.out.println("Java class path: " + javaClassPath);
    System.out.println("Java library path: " +  javaLibraryPath);
    System.out.println("Temp dir: " + javaIoTmpdir);
    System.out.println("Name of the JIT compiler: " + javaCompiler);
    System.out.println("Extension directory: " + javaExtDirs);
    System.out.println("OS Name: " + osName);
    System.out.println("OS Architecture: " + osArch);
    System.out.println("OS Version: " + osVersion);
    System.out.println("File separator: " + fileSeparator);
    System.out.println("Path separator: " + pathSeparator);
    System.out.println("User name: " + userName);
    System.out.println("User home: " + userHome);
    System.out.println("User dir (current directory): " + userDir);
  }

  public String getJavaVersion(){return javaVersion;}
  public String getJavaVendor(){return javaVendor;}
  public String getJavaVendorUrl(){return javaVendorUrl;}
  public String getJavaHome(){return javaHome;}
  public String getJavaVmSpecificationVersion(){return javaVmSpecificationVersion;}
  public String getJavaVmSpecificationVendor(){return javaVmSpecificationVendor;}
  public String getJavaVmSpecificationName(){return javaVmSpecificationName;}
  public String getJavaVmVersion(){return javaVmVersion;}
  public String getJavaVmVendor(){return javaVmVendor;}
  public String getJavaVmName(){return javaVmName;}
  public String getJavaSpecificationVersion(){return javaSpecificationVersion;}
  public String getJavaSpecificationVendor(){return javaSpecificationVendor;}
  public String getJavaSpecificationName(){return javaSpecificationName;}
  public String getJavaClassVersion(){return javaClassVersion;}
  public String getJavaClassPath(){return javaClassPath;}
  public String getJavaLibraryPath(){return javaLibraryPath;}
  public String getJavaIoTmpdir(){return javaIoTmpdir;}
  public String getJavaCompiler(){return javaCompiler;}
  public String getJavaExtDirs(){return javaExtDirs;}
  public String getOsName(){return osName;}
  public String getOsArch(){return osArch;}
  public String getOsVersion(){return osVersion;}
  public String getFileSeparator(){return fileSeparator;}
  public String getPathSeparator(){return pathSeparator;}
  public String getLineSeparator(){return lineSeparator;}
  public String getUserName(){return userName;}
  public String getUserHome(){return userHome;}
  public String getUserDir(){return userDir;}

  private String javaVersion, javaVendor, javaVendorUrl, javaHome,
          javaVmSpecificationVersion, javaVmSpecificationVendor,
          javaVmSpecificationName, javaVmVersion, javaVmVendor, javaVmName,
          javaSpecificationVersion, javaSpecificationVendor, javaSpecificationName,
          javaClassVersion, javaClassPath, javaLibraryPath, javaIoTmpdir, javaCompiler,
          javaExtDirs, osName, osArch, osVersion, fileSeparator, pathSeparator, 
          lineSeparator, userName, userHome, userDir;
}
