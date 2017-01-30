import groovy.swing.factory.SeparatorFactory;

def dir = new File("C:/OpenDevECI/Workspace64/Groovy_tests/src/main/groovy/files/raiz");

def sep = System.getProperty("file.separator");

dir.eachFileRecurse { File file ->
	if(!file.getCanonicalPath().contains("${sep}target${sep}") && file.getName().endsWith("txt")) {
		println(file.getCanonicalPath());
	}		
}