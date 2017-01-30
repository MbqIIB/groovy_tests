import groovy.xml.*;

import java.nio.charset.*;

import groovy.util.CharsetToolkit;
import groovy.util.slurpersupport.GPathResult

import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.Transformer
import javax.xml.transform.TransformerFactory
import javax.xml.transform.OutputKeys;
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult
import java.text.SimpleDateFormat
import java.util.Date;

import org.w3c.dom.Document;


def pomFile  = 	new File("C:/jenkins/workspace/test2/pom.xml");
def pomFile2 = 	new File("C:/jenkins/workspace/test2/pom2.xml");
def otro     = 	new File("C:/OpenDevECI/Workspace64/6A2_TPV/pom.xml");

if(pomFile2.exists()) {
	pomFile2.delete();
}


CharsetToolkit toolkit = new CharsetToolkit(pomFile);
Charset guessedCharset = toolkit.getCharset();
println("CHARSET de ${pomFile.getAbsolutePath()}-> " + guessedCharset.name())

//// Con XmlParser
XmlParser parser = new XmlParser();
Node pom = parser.parse(pomFile);

pom.version[0].setValue("NUEVO");

// Con XmlSlurper
//XmlSlurper parser = new XmlSlurper(false, false);
//GPathResult pom = parser.parse(otro);

/**
 * Devuelve el nombre del encoding de un archivo.
 * @param pomFile
 * @return
 */
private String getEncodingName(File pomFile) {
	CharsetToolkit toolkit = new CharsetToolkit(pomFile);
	Charset guessedCharset = toolkit.getCharset();
	println("CHARSET de ${pomFile.getAbsolutePath()}-> " + guessedCharset.name())
	
	String encoding = null;
	if(guessedCharset.name().equals("UTF-8")) {
		encoding = "UTF-8";
	} else if(guessedCharset.name().equals("windows-1252")) {
		encoding = "ISO-8859-1";
	} else {
		throw new Exception("Encoding desconocido para el archivo ${pomFile.getAbsolutePath()}");
	}
	return encoding;
}


transformXml(pom, pomFile);

/**
 * Transforma un archivo XML a otro con el encóding especificado.
 * @param (File) origin
 * @param (File) destination
 * @param (String) encoding
 */
private void transformXml(Node pom, File pomFile) {
	
	String encoding = getEncodingName(pomFile);
	
	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	DocumentBuilder builder = factory.newDocumentBuilder();

	StringWriter stringWriter = new StringWriter()
	XmlNodePrinter nodePrinter = new XmlNodePrinter(new PrintWriter(stringWriter));
	nodePrinter.setPreserveWhitespace(true);
	nodePrinter.print(pom);
	String xmlString = stringWriter.toString()
	InputStream stream = new ByteArrayInputStream(xmlString.getBytes(Charset.forName("UTF-8")))

	Document doc = builder.parse(stream);
	DOMSource domSource = new DOMSource(doc);

	StringWriter sw = new StringWriter();
	OutputStreamWriter char_output = new OutputStreamWriter(
			new FileOutputStream(pomFile.getAbsolutePath()),
			Charset.forName(encoding).newEncoder()
			);
	StreamResult sr = new StreamResult(char_output);

	TransformerFactory tf = TransformerFactory.newInstance();
	Transformer transformer = tf.newTransformer();
	transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
	transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");
	transformer.setOutputProperty(OutputKeys.ENCODING, encoding);
	transformer.transform(domSource, sr);
}



