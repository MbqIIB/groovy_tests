package xml;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import xml.XmlUtils;

class PomXmlOperations {

	/**
	 * Crea un archivo 'version.txt' con el contenido de la versión.
	 * @param File dir
	 * @return String version
	 */
	public static String createVersionFile(File dir) {
		def pomMainFile = new File(dir.getAbsolutePath() + "/pom.xml");
		Document doc = XmlUtils.parseXml(pomMainFile);
		def version = XmlUtils.xpathNode(doc, "/project/version").getTextContent();
		def groupId = XmlUtils.xpathNode(doc, "/project/groupId").getTextContent();
		
		def versionFile = new File(dir.getAbsolutePath() + "/version.txt");
		versionFile.text = "";
		versionFile.append("version=${version}");
		versionFile.append("groupId=${groupId}");
	}
	
	/**
	 * Comprueba que la versión de un pom.xml está abierta.
	 * @param File dir
	 * @return
	 */
	public static void checkOpenVersion(File dir) {
		dir.eachFileRecurse { File file ->
			if(file.getName() == "pom.xml") {
				Document doc = XmlUtils.parseXml(file);
				Node node = XmlUtils.xpathNode(doc, "/project/version");
				if(node != null) {
					if(node.getNodeType() == Node.ELEMENT_NODE) {
						if(node.getTextContent() != null) {
							if(!node.getTextContent().trim().endsWith("-SNAPSHOT")) {
								throw new NumberFormatException("[${file.getAbsolutePath()}]: La versión debe acabar en -SNAPSHOT.");
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Si una versión no cumple el formato X.X.X.X o X.X.X.X-SNAPSHOT 
	 * se modifica para que lo cumpla 
	 * @param File dir
	 * @param int index
	 */
	public static void fillVersion(File dir) {
		dir.eachFileRecurse { File file ->
			if(file.getName() == "pom.xml") {
				println("Examinando ${file.getAbsolutePath()}...")
				def doc = XmlUtils.parseXml(file);
				def node = XmlUtils.xpathNode(doc, "/project/version");
				def versionText = node != null ? node.getTextContent() : null;
				if(versionText != null) {
					def isSnapshot = versionText.contains("-SNAPSHOT");
					def numericPart = versionText.split("-SNAPSHOT")[0];
					ArrayList numericPartArray = numericPart.split("\\.");
					// Si la versión tiene menos de 4 dígitos se rellena con ceros.
					if(numericPartArray.size() < 4) {
						def digitsToFill = 4 - numericPartArray.size();
						for(int i=0; i<digitsToFill; i++) {
							numericPartArray.add("0");
						}
						numericPart = numericPartArray[0] + "." + numericPartArray[1] + "." + numericPartArray[2] + "." +numericPartArray[3];
						if(isSnapshot) {
							def finalVersion = numericPart + "-SNAPSHOT";
							node.setTextContent(finalVersion);
							XmlUtils.transformXml(doc, file);
						} else {
							def finalVersion = numericPart;
							node.setTextContent(finalVersion);
							XmlUtils.transformXml(doc, file);
						}
					}
				}
			}
		}
	}

	/**
	 * Cierra las versiones SNAPSHOT de los pom.xml y de sus dependencias
	 * que NO estén en el artifacts.json
	 */
	public static void removeSnapshot(File dir, File artifactsJsonFile) {		
		ArrayList artifacts = XmlUtils.getArtifactsStream(artifactsJsonFile);
		dir.eachFileRecurse { File file ->
			if(file.getName() == "pom.xml") {
				def needRewrite = false;
				//println("Se analiza el pom.xml -> ${file.getAbsolutePath()}")
				def doc = XmlUtils.parseXml(file);
				def node = XmlUtils.xpathNode(doc, "/project/version");

				// Se cierra la versión principal del pom.xml
				def versionText = node != null ? node.getTextContent() : null;
				if(versionText != null) {
					if(versionText.endsWith("-SNAPSHOT")) {
						println("Se cierra la versión principal del pom.xml-> ${file.getAbsolutePath()}");
						def closedVersion = versionText.split("-SNAPSHOT")[0];
						node.setTextContent(closedVersion);
						needRewrite = true;
					}
				}

				// Se cierran las versiones de las dependencias que no estén en artifacts.json
				Node[] depNodes = XmlUtils.xpathGetChildNodes(doc, "/project/dependencies");
				depNodes.each { Node depNode ->
					Node[] depPropertiesNodes = XmlUtils.getChildNodes(depNode);
					def artifactId = '';
					depPropertiesNodes.each { Node depPropNode ->
						if(depPropNode.getNodeName() == "artifactId") {
							artifactId = depPropNode.getTextContent();
						}
					}
					depPropertiesNodes.each { Node depPropNode ->
						if(depPropNode.getNodeName() == "version") {
							def version = depPropNode.getTextContent();
							if(!artifacts.contains(artifactId)) {
								println("Se cierra la dependencia ${artifactId} del pom.xml ${file.getAbsolutePath()}")
								def closedVersion = version.split("-SNAPSHOT")[0];
								depPropNode.setTextContent(closedVersion);
								needRewrite = true;
							}
						}
					}
				}
				if(needRewrite) {
					XmlUtils.transformXml(doc, file);
				}
			}
		}
	}
	
	/**
	 * Incrementa el dígito de versión indicado a los pom.xml
	 * bajo un directorio
	 * @param int index Indice del 1-4 que se desea incrementar.
	 */
	public static void increaseVersion(File dir, int index) {
		dir.eachFileRecurse { File file ->
			if(file.getName() == "pom.xml") {
				def doc = XmlUtils.parseXml(file);
				def node = XmlUtils.xpathNode(doc, "/project/version");
				if(node != null) {	
					def isSnapshot = node.getTextContent().contains("-SNAPSHOT");		
					def version = node.getTextContent().split("-SNAPSHOT")[0];
					ArrayList versionDigits = version.split("\\.");
					def increasedDigit = versionDigits[index - 1].toInteger() + 1;
					versionDigits.set(index - 1, increasedDigit);
					if(isSnapshot) {
						def newVersion = versionDigits[0] + "." + versionDigits[1] + "." + versionDigits[2] + "." + versionDigits[3] + "-SNAPSHOT";
						node.setTextContent(newVersion);
						XmlUtils.transformXml(doc, file);
					} else {
						def newVersion = versionDigits[0] + "." + versionDigits[1] + "." + versionDigits[2] + "." + versionDigits[3];
						node.setTextContent(newVersion);
						XmlUtils.transformXml(doc, file);
					}					
				}				
			}
		}
	}
	
	/**
	 * Comprueba que todas las versiones, tanto las de poms como
	 * las de dependencias, estén cerradas.
	 * @param dir
	 */
	public static void checkClosedVersion(File dir) {
		dir.eachFileRecurse { File file ->
			if(file.getName() == "pom.xml") {
				println("Analizamos ${file.getAbsolutePath()}...")
				def doc = XmlUtils.parseXml(file);
				// Comprobamos que la version del pom.xml esté cerrada.
				def nodeVersion = XmlUtils.xpathNode(doc, "/project/version");
				if(nodeVersion != null) {
					if(nodeVersion.getTextContent().endsWith("-SNAPSHOT")) {
						throw new NumberFormatException("La versión del pom.xml ${file.getAbsolutePath()} no está cerrada.");						
					}
				} 			
				
				// Comprobamos que las versiones de las dependencias estén cerradas.
				Node[] depVersionNodes = XmlUtils.xpathNodes(doc, "/project/dependencies//version");
				depVersionNodes.each { Node depVersionNode ->
					if(depVersionNode.getTextContent().endsWith("-SNAPSHOT")) {						
						throw new NumberFormatException("El pom.xml ${file.getAbsolutePath()} tiene dependencias sin cerrar.");						
					}
				}
			}
		}
	}
	
	/**
	 * Añade -SNAPSHOT a la versión del pom.xml
	 * @param dir
	 */
	public static void addSnapshot(File dir) {
		dir.eachFileRecurse { File file ->
			if(file.getName() == "pom.xml") {
				def doc = XmlUtils.parseXml(file);
				Node versionNode = XmlUtils.xpathNode(doc, "/project/version");
				if(versionNode != null) {
					if(!versionNode.getTextContent().endsWith("-SNAPSHOT")) {
						def newVersion = versionNode.getTextContent() + "-SNAPSHOT";
						versionNode.setTextContent(newVersion);
						XmlUtils.transformXml(doc, file);
					}
				}				
			}
		}
	}
}
















