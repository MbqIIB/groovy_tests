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
	def correos = new File("correos.txt").text.split("\n");
	def correosMap = [:];
	correos.each { linea ->
		correosMap.put(linea.split(",")[0], linea.split(",")[1]);
	}
	if (xml.publishers != null && xml.publishers.size() > 0) {
		def publishers = xml.publishers[0];
		if (publishers['hudson.plugins.parameterizedtrigger.BuildTrigger'] != null
		&& publishers['hudson.plugins.parameterizedtrigger.BuildTrigger'].size() > 0) {
			def buildTrigger = publishers['hudson.plugins.parameterizedtrigger.BuildTrigger'][0];
			if(buildTrigger['configs'] != null && buildTrigger['configs'] > 0) {
				def configs = buildTrigger['configs'][0];
				if(configs['hudson.plugins.parameterizedtrigger.BuildTriggerConfig'] != null
				&& configs['hudson.plugins.parameterizedtrigger.BuildTriggerConfig'] > 0) {
					def buildTriggerConfig = configs['hudson.plugins.parameterizedtrigger.BuildTriggerConfig'][0];
					if(buildTriggerConfig['configs'] != null && buildTriggerConfig['configs'] > 0) {
						def configs2 = buildTriggerConfig['configs'][0];
						if(configs2['hudson.plugins.parameterizedtrigger.PredefinedBuildParameters'] != null
						&& configs2['hudson.plugins.parameterizedtrigger.PredefinedBuildParameters'] > 0) {
							def predefinedBuildParameters = configs2['hudson.plugins.parameterizedtrigger.PredefinedBuildParameters'][0];
							if(predefinedBuildParameters['properties'] != null && predefinedBuildParameters['properties'] > 0) {
								def properties = predefinedBuildParameters['properties'];
								println(properties.text);
								// Sustituir el contenido por los correos actualizados.	
							}
						}
					}
				}
			}
		}
	}
	println("\n\n");
}

//def jobs = jobList.split("\n")
def jobs = new File("jobs.txt").text.split("\n")
jobs.each(){ job ->
	boolean changed = false;
	//def configFile = new File("${home}/jobs/${job}/config.xml")
	def configFile = new File("C:\\Users\\dcastro.jimenez\\Desktop\\Jobs a Modificar\\JOBS_INT\\${job}\\config.xml")
	if (configFile.exists()){
		def xml = new XmlSlurper().parseText(configFile.getText("UTF-8"));
		if (mustChange(xml)) {
			println "Modificando $job ..."
			change(xml);
			// Guardar el job en el ws local
			writeConfig(XmlUtil.serialize(xml), job, workspace);
		}
	}else{
		println "--> ${configFile} exists: ${configFile.exists()}"
	}
	println "***********************"
}