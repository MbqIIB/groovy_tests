@GrabResolver(name='nexuseci', root='http://nexus.elcorteingles.int/content/groups/public/')
@Grab(group='com.ibm.icu', module='icu4j', version='57.1')

import es.eci.utils.versioner.XmlUtils;
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

//def paramAppName = "aplicacionUrbanCode";
//def paramAppValue = "GIS - Cluster Servicios";
def paramAppName = "instantanea";
def paramAppValue = "";

def paramsMap = ["aplicacionUrbanCode":"GIS - Cluster Servicios",
	"instantanea":""];

//paramsMap.keySet().each { String paramAppName ->
//def paramAppValue = paramsMap.get(paramAppName);

File jobsDir = new File("C:/Users/dcastro.jimenez/Desktop/Jobs a Modificar/JOBS_INT");
jobsDir.eachDir { File jobDir ->
	def configXmlFile = new File(jobDir.getCanonicalPath() + "/config.xml");

	println("############ Añadiendo parmámetro ${paramAppName} a \"${jobDir.getCanonicalPath()}\":");
	def doc = XmlUtils.parseXml(configXmlFile);



	def nodeString = "<?xml version='1.0' encoding='UTF-8'?>\n" +
			"<project>\n" +
			"<hudson.model.StringParameterDefinition>\n" +
			"<name>${paramAppName}</name>\n" +
			"<description></description>\n" +
			"<defaultValue>${paramAppValue}</defaultValue>\n" +
			"</hudson.model.StringParameterDefinition>\n" +
			"</project>";

	def paramFile = new File("paramFile.xml")
	paramFile.text = nodeString;

	Document docParam = XmlUtils.parseXml(paramFile);
	paramFile.delete();

	Node paramNode = XmlUtils.xpathNode(docParam, "/project/hudson.model.StringParameterDefinition");

	Node importedNode = doc.importNode(paramNode, true);

	Node propsNode = XmlUtils.xpathNode(doc, "/project/properties/hudson.model.ParametersDefinitionProperty/parameterDefinitions");

	propsNode.appendChild(importedNode);

	XmlUtils.transformXml(doc, configXmlFile);
	println("#######################################################################\n");

}
//}


