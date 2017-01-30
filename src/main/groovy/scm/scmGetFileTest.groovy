import es.eci.utils.ScmCommand;
import es.eci.utils.TmpDir;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathConstants;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

///////////// Parámetros de búsqueda /////////////////
def stream = "GIS - DVD - DESARROLLO";
def component= "SKSM4101 - Generacion Codigo Reserva";
def fileToView = "pom.xml";
def xpath = "/project/modules/module";
//////////////////////////////////////////////////////

def scm = new ScmCommand(ScmCommand.Commands.SCM);
def rtcUser = "JENKINS_RTC";
def rtcPass = "12345678";
def rtcUrl = "https://rtc.elcorteingles.int:9443/ccm";
def pattern = /.*\[[1-9]+\].*/

// Conseguimos el version id del archivo que queremos descargar.
def command1 = "list remotefiles --show-versionid --depth - -w \"${stream}\" \"${component}\" \"/\"";
def revision = 0;
TmpDir.tmp { File dir ->
	def output1 = scm.ejecutarComando(command1, rtcUser, rtcPass, rtcUrl, dir);	
	output1.eachLine { String line ->
		def matcher = line.trim() =~ pattern;
		if(line.startsWith("/${fileToView}")) {
			println("linea-> ${line}");			
			if(matcher.matches()) {
				println("Partimos la linea para quedarnos con el valor entre corchetes [XXX]")
				revision = line.split('\\[')[1].split('\\]')[0].toInteger();
			} else {
				println("La línea no tiene valor de revisión. Le asignamos 1.");
				revision = 1;
			}			
		}
	}
} 
println("La revision del archivo \"${fileToView}\" es: ${revision}")
if(revision == 0) {
	throw new NumberFormatException("No se ha podido sacar la revision del archivo \"${fileToView}\"");	
}

// scm get file 
// -r https://rtc.elcorteingles.int:9443/ccm 
// -u JENKINS_RTC -P 12345678 
// -c "DIC - Scripts" 
// -f pom.xml 
// -w "WSR - GIS - PlataformaIC - DESARROLLO - X78482CA" 
// 4 "C:/Temp_RTC/pom.xml"

TmpDir.tmp { File dir ->
	def destinationFilePath = dir.getCanonicalPath() + "/" + fileToView;
	def command = "get file -c \"${component}\" -w \"${stream}\" -f \"${fileToView}\" ${revision} ${destinationFilePath}";
	def output = scm.ejecutarComando(command, rtcUser, rtcPass, rtcUrl, dir);
	
	def destinationFile = new File(destinationFilePath);
	
	// Parseamos el archivo y sacamos el "name"
	DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	Document doc = dBuilder.parse(destinationFile);
	doc.getDocumentElement().normalize();
	
	XPath xPath = XPathFactory.newInstance().newXPath();
	Node node = xPath.evaluate("${xpath}", doc.getDocumentElement(), XPathConstants.NODE);
		
	println(destinationFile.text);
	
	println("name -> " + node.getTextContent())
}

