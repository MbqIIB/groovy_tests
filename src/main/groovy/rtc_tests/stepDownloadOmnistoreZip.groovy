import es.eci.utils.NexusHelper;
import es.eci.utils.ZipHelper;

def build = Thread.currentThread().executable;
def resolver = build.buildVariableResolver;

String pathNexus = build.getEnvironment(null).get("MAVEN_REPOSITORY") + "/"; println("pathNexus = ${pathNexus}");
String version = resolver.resolve("version"); println("version = ${version}");
String groupId = resolver.resolve("groupId"); println("groupId = ${groupId}");
String parentWorkspace = resolver.resolve("parentWorkspace"); println("parentWorkspace = ${parentWorkspace}");
String artifactId = "OmniStore-Code";
String extension = "zip";
 	

NexusHelper.downloadLibraries(groupId, artifactId, version, parentWorkspace, extension, pathNexus);

ZipHelper.unzipFile(new File("${parentWorkspace}/OmniStore-Code.zip"), 
					new File("${parentWorkspace}/OmniStore"));

