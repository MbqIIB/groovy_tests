package xml;

import xml.PomXmlOperations;
import java.io.File;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathConstants;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import groovy.xml.*;
import xml.XmlUtils;

//import com.ibm.icu.text.CharsetDetector;
//import com.ibm.icu.text.CharsetMatch;
//import java.nio.charset.UnsupportedCharsetException


/*********************************************************************/
def pomFile  = 	new File("C:/jenkins/workspace/test2/pom.xml");
def pomFile2 = 	new File("C:/jenkins/workspace/test2/pom2.xml");
def parentWorkspaceDir = new File("C:/jenkins/workspace/test2/PruebaRelease-App-1");

/*********************************************************************/


/****** TESTS ********/
PomXmlOperations.createVersionFile(parentWorkspaceDir);


//def unzipTestsZip(zipPath, destination) {
//	def ant = new AntBuilder();
//	ant.unzip(  src: zipPath,
//	dest: destination,
//	overwrite:"true" )
//}