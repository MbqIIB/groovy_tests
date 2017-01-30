import es.eci.utils.NexusHelper;
import es.eci.utils.pom.MavenCoordinates;

def theGroupId = "es.eci.release.prueba";
def theArtifactId = "lib1";
def theVersion = "1.0.93.0-SNAPSHOT";

NexusHelper nxHelper = new NexusHelper("http://nexus.elcorteingles.pre");
MavenCoordinates coord = new MavenCoordinates(theGroupId, theArtifactId, theVersion);
coord.setPackaging("jar");

String version_maven = nxHelper.resolveSnapshot(coord);
nxHelper.initLogger { println it };

println("version_maven: \"${version_maven}\"");