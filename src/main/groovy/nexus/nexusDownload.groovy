import es.eci.utils.NexusHelper;
import es.eci.utils.ZipHelper;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import es.eci.utils.commandline.CommandLineHelper;
import es.eci.utils.TmpDir;

def pathNexus = "http://nexus.elcorteingles.int/content/groups/public/"
def version = "1.7.7.0"
def groupId = "es.eci.omnistore"
def parentWorkspace = "C:/Users/dcastro.jimenez/Desktop/BORRAR/Download"
def artifactId = "OmniStore-Code";
def extension = "zip";

//NexusHelper.downloadLibraries(groupId, artifactId, version, parentWorkspace, extension, pathNexus);

def urlOrigen = pathNexus + groupId.replaceAll("\\.", "/") + "/" + artifactId + "/" + version + "/" + "${artifactId}-${version}.${extension}"
def pathDestino = "C:/Users/dcastro.jimenez/Desktop/BORRAR/Download/${artifactId}.${extension}"

wgetCommand(urlOrigen, pathDestino);

/**
 * Descarga mediante wget desde una url indicada.
 * @param urlOrigen
 * @param pathDestino
 * @return returnCode
 */
public int wgetCommand(String urlOrigen, String pathDestino) {
	def returnCode;	
	TmpDir.tmp { File dir ->		
		def command = "wget ${urlOrigen} -O ${pathDestino}";
		println("Ejecutando comando: \"${command}\"")
		CommandLineHelper buildCommandLineHelper = new CommandLineHelper(command);		
		returnCode = buildCommandLineHelper.execute(dir);
	}	
	return returnCode;
}

