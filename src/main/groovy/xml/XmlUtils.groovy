package xml;

import groovy.json.JsonSlurper
import java.io.File;
import java.nio.charset.Charset

import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.Transformer
import javax.xml.transform.TransformerFactory
import javax.xml.transform.OutputKeys;
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathConstants;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

public class XmlUtils {

	/**
	 * Transforma un org.w3c.dom.Document en un archivo destino;
	 * @param doc
	 * @param destFile
	 */
	public static void transformXml(Document doc, File destFile) {
		String encoding = getEncodingName(destFile);
		DOMSource domSource = new DOMSource(doc);
		StringWriter sw = new StringWriter();
		OutputStreamWriter char_output = new OutputStreamWriter(
				new FileOutputStream(destFile.getAbsolutePath()),
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

	/**
	 * Transforma un archivo XML a otro con el encóding especificado.
	 * @param pomFile
	 * @return
	 */
	public static String getEncodingName(File pomFile) {
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

	/**
	 * Devuelve un org.w3c.dom.Document con el contenido de un xmlFile parseado.
	 * @param xmlFile
	 * @return org.w3c.dom.Document
	 */
	public static Document parseXml(File xmlFile) {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

		Document doc = dBuilder.parse(xmlFile);
		doc.getDocumentElement().normalize();

		return doc;
	}

	/**
	 * Devuelve un Node de un Document doc según la query de xPath
	 * que se le pase como argumento
	 * @param Document doc
	 * @param String xPathQuery
	 * @return NodeList nodes
	 */
	public static Node xpathNode(Document doc, String xPathQuery) {
		XPath xPath = XPathFactory.newInstance().newXPath();
		Node node = xPath.evaluate("${xPathQuery}", doc.getDocumentElement(), XPathConstants.NODE);
		return node;
	}
	
	/**
	 * Devuelve una lista de nodos Node[] de un Document doc según la query de xPath
	 * que se le pase como argumento
	 * @param Document doc
	 * @param String xPathQuery
	 * @return NodeList nodes
	 */
	public static Node[] xpathNodes(Document doc, String xPathQuery) {
		XPath xPath = XPathFactory.newInstance().newXPath();
		NodeList nodes = xPath.evaluate("${xPathQuery}", doc.getDocumentElement(), XPathConstants.NODESET);
		Node[] nodesArray = nodes != null ? convertToArray(nodes) : [];
		return nodesArray;
	}
	
	/**
	 * Intenta resolver una propiedad contra el elemento <properties/>
	 * de un pom.xml
	 */
	public static String lookupProperty(Document doc, String value) {
		String ret = '';
		Node[] nodePropsArray = xpathGetChildNodes(doc, "/project/properties");
		nodePropsArray.each { Node node ->
			if(node.getNodeName() == value) {
				ret = node.getTextContent();
			}
		}
		return ret;
	}
	
	
	/**
	 * Convert NodelList to ArrayList
	 * @param list
	 * @return
	 */
	public static Node[] convertToArray(NodeList list) {
		int length = list.getLength();
		Node[] copy = new Node[length];		
		for (int n = 0; n < length; ++n) {
			copy[n] = list.item(n);
		}
		return copy;
	}
	
	/**
	 * Intenta resolver los valores posibles de la versión
	 * de un pom a una String. En el peor caso:
	 ...
	 <version>${core-version}</version>
	 ...
	 <properties>
	 <toolkit-version>21.0.0</toolkit-version>
	 <core-version>${toolkit-version}-SNAPSHOT</core-version>
	 </properties>
	 Debería resolver la versión a:
	 21.0.0-SNAPSHOT
	 */	
	public static String solve(Document doc, String s) {
		StringBuilder sb = new StringBuilder();
		List<String> tokens = parse(s);
		for(String token in tokens) {
			if (token.startsWith('${') && token.endsWith('}')) {
				// Inmersión recursiva (resolviendo antes la propiedad)
				sb.append(solve(doc, lookupProperty(doc, token.substring(2, token.length() - 1))));
			}
			else {
				// Caso trivial
				sb.append(token);
			}
		}
		return sb.toString();
	}
	
	/**
	 * Cambia el valor de una propiedad en un pom.xml
	 * @param doc
	 * @param property
	 * @param value
	 */
	public static void setProperty(Document doc, String property, String value) {		
		Node[] nodePropsArray = xpathGetChildNodes(doc, "/project/properties");
		nodePropsArray.each { Node node ->
			if(node.getNodeName() == property) {
				node.setTextContent(value);
			}
		}
	}

	/**
	 * Parsea una cadena en sus componentes, separando propiedades
	 * Por ejemplo:
	 * 'aaa' -> ['aaa']
	 * 'asldkf${pom-variable}B' -> ['asldkf', '${pom-variable}', 'B']
	 * '${pom-variable-1}-${pom-variable2}-${pom-variable3}' -> ['${pom-variable-1}', '-', '${pom-variable-2}', '-', '${pom-variable-3}']
	 * '${pom-variable}-SNAPSHOT' -> ['${pom-variable}', '-SNAPSHOT']
	 */
	public static List<String> parse(String s) {
		List<String> ret = null;
		if (s != null) {
			ret = new LinkedList<String>();
			int counter = 0;
			while (counter < s.length()) {
				int forward = counter;
				if (s.charAt(counter) == '$') {
					// Variable
					while(forward < s.length() && s.charAt(forward) != '}') {
						forward++;
					}
					// Consumir el último '}'
					forward++;
				}
				else {
					while (forward < s.length() && s.charAt(forward) != '$') {
						forward++;
					}
				}
				ret.add(s.substring(counter, forward));
				counter = forward;
			}
		}
		return ret;
	}
	
	/**
	 * Devuelve una lista de Nodes ya limpia y filtrada que sólo sean 
	 * del tipo Node.ELEMENT_NODE
	 * @param doc
	 * @param xPathQuery
	 * @return Node[] nodePropsArray
	 */
	public static Node[] xpathGetChildNodes(Document doc, String xPathQuery) {
		Node[] nodePropsArray = [];
		Node nodeProps = xpathNode(doc, xPathQuery);		
		if(nodeProps != null) {
			nodePropsArray = convertToArray(nodeProps.getChildNodes()).findAll { it.getNodeType() == Node.ELEMENT_NODE };
		}
		return nodePropsArray;
	} 	
	
	/**
	 * Devuelve una lista de Nodes ya limpia y filtrada que sólo sean
	 * del tipo Node.ELEMENT_NODE
	 * @param Document doc
	 * @param Node node
	 * @return Node[] nodePropsArray
	 */
	public static Node[] getChildNodes(Node node) {		
		Node[] nodePropsArray = convertToArray(node.getChildNodes()).findAll { it.getNodeType() == Node.ELEMENT_NODE };
		return nodePropsArray;
	}
	
	/**
	 * Devuelve los artifacts indicados en el artifacts.json
	 * @param dir
	 * @return
	 */
	public static String[] getArtifactsStream(File artifactsFile) {		
		def artifacts = []
		if (artifactsFile.exists()) {			
			def jsonObject = new JsonSlurper().parse(artifactsFile);
			jsonObject.each {
				artifacts.add(it.artifactId);
			}
		}
		return artifacts;
	}

}
