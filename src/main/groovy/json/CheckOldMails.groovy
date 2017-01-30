import hudson.model.*
import java.util.regex.*
import groovy.xml.*
import groovy.util.Node
import java.io.*
import java.nio.charset.Charset
// Este script lee la lista de jobs, decide si debe o no intervenir sobre cada uno de ellos
//  y almacena en su propio workspace de repositorio la configuración modificada, es decir:
//  es inocuo en cuanto a la configuración de jenkins.  El resultado debe recogerse desde el
//  workspace del job e importarse a jenkins por algún medio (p. ej. jenkins-cli)
//def resolver = build.buildVariableResolver
//def jobList = resolver.resolve("jobList")
//final home = build.getEnvironment().get("JENKINS_HOME")
//final workspace = build.workspace
final workspace = "C:/Users/dcastro.jimenez/Desktop/Jobs a Modificar/WORKSPACE";
def writeConfig(text, jobName, workspace){
	println "cambiando ${jobName}..."
	new File("${workspace}/${jobName}").mkdirs()
	def destFile = new File("${workspace}/${jobName}/config.xml")
	try {
		destFile.delete()
	} catch (Exception e) {
		println("Nada que borrar");
	}	
	//destFile << text
	Writer writer = new OutputStreamWriter(new FileOutputStream(destFile), Charset.forName("UTF-8"))
	writer.write(text, 0, text.length())
	writer.flush()
}

// Implementar en esta closure la validación de si debe o no debe intervenir el script
def mustChange(xml) {
	// TODO: ¿debe cambiar el xml?
	return true;
}

// Implementar en esta closure la acción a realizar sobre el xml
def change(xml) {
	//TODO: Lógica con xml en caso de ser necesaria.
}

//def jobs = jobList.split("\n")
def jobs = new File("jobs.txt").text.split("\n");
def correos = new File("correos.txt").text.split("\n");
def correosMap = [:];
correos.each { linea ->
	correosMap.put(linea.trim().split(",")[0], linea.trim().split(",")[1]);
}
jobs.each(){ job ->
	boolean changed = false;
	//def configFile = new File("${home}/jobs/${job}/config.xml")
	def configFile = new File("C:/Users/dcastro.jimenez/Desktop/Jobs a Modificar/JOBS_INT/${job.trim()}/config.xml");
	def newConfigDir = new File("${workspace}/${job.trim()}");
	newConfigDir.mkdirs();
	def newConfigFile = new File("${workspace}/${job.trim()}/config.xml");	
	if (configFile.exists()) {
		correosMap.keySet().each {
			println("Sustituyendo " + it);
			newConfigFile.text = configFile.text.replaceAll(it, correosMap.get(it));
		}
	} else {
		println "--> ${configFile} does not exist!"
	}
	println "***********************"
}


