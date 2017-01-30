@GrabResolver(name='nexuseci', root='http://nexus.elcorteingles.int/content/groups/public/')
@Grab(group='com.ibm.icu', module='icu4j', version='57.1')

import es.eci.utils.versioner.XmlUtils;
import org.w3c.dom.Document

File baseDir = new File("C:/Users/dcastro.jimenez/Desktop/PruebaRelease-App-2");

def treeMap = [:];

baseDir.eachFileRecurse { File file ->
	if(file.getName() == "pom.xml") {
		Properties thisFileProp = new Properties();
		Document doc = XmlUtils.parseXml(file);
		def artifactId = XmlUtils.xpathNode(doc, "/project/artifactId").getTextContent();
		
		def parentNode = XmlUtils.xpathNode(doc, "/project/parent/artifactId");
		def parentArtifactId;
		if(parentNode != null) {
			parentArtifactId = parentNode.getTextContent();
		}
		def thisFilePath = file.getCanonicalPath();
		thisFileProp.put(thisFilePath, parentArtifactId);
		
		
		treeMap.put(artifactId, thisFileProp);
	}
}

println("treemap->\n${treeMap}");

/*********************/

File concretePom = new File("C:/Users/dcastro.jimenez/Desktop/PruebaRelease-App-2/App2-WAR/hijo/pom.xml");

public calculatePath(File pom, Map<String,Properties>treeMap) {
	def doc = XmlUtils.parseXml(pom);
	def parentNode = XmlUtils.xpathNode(doc, "/project/parent/artifactId");
}




