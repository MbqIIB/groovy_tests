import es.eci.utils.NexusHelper;

def groupId = "es.eci.serviciosmulticanal.sksm0005"
def artifactId = "sksm0005"
def version = "1.1.3.0-SNAPSHOT"
def pathDescargaLibrerias = "."
def extension = "ear"
def pathNexus = "http://nexus.elcorteingles.int/content/groups/public/"

NexusHelper.downloadLibraries(groupId, artifactId, version, pathDescargaLibrerias, extension, pathNexus);