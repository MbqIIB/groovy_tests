import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


def parentWorkspace = "C:/Users/dcastro.jimenez/Desktop/BORRAR";
def version = "1.7.7.0";
def zipPath = "${parentWorkspace}/OmniStore-Code-${version}.zip";
def destination = "${parentWorkspace}/OmniStore";


def ant = new AntBuilder()   // create an antbuilder

ant.unzip(  src: zipPath,
			dest: destination,
			overwrite:"true" )