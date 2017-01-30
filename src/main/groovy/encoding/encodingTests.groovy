import java.io.File;
import java.nio.charset.Charset

def file = new File("C:/OpenDevECI/Workspace64-PRE/6A2_TPV/IntServPdS1/fuentes/pom.xml");

def encodingName = getEncodingNameWithGroovy(file);
 
println("Encoding del archivo \"${file.getCanonicalPath()}\":");
println(encodingName);

public static String getEncodingNameWithGroovy(File pomFile) {
	CharsetToolkit toolkit = new CharsetToolkit(pomFile);
	Charset guessedCharset = toolkit.getCharset();
	println("CHARSET de ${pomFile.getAbsolutePath()}-> " + guessedCharset.name())

	String encoding = null;
	if(guessedCharset.name().equals("UTF-8")) {
		encoding = "UTF-8";
	} else if(guessedCharset.name().equals("windows-1252") 
				|| guessedCharset.name().equals("ISO-8859-1")) {
		encoding = "ISO-8859-1";
	} else {
		throw new Exception("Encoding desconocido para el archivo ${pomFile.getAbsolutePath()}");
	}
	return encoding;
}
