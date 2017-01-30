import hudson.model.*;
import java.util.regex.*;
import groovy.xml.*;
import groovy.util.Node;
import java.io.*;
import java.nio.charset.Charset;
/**
 * 	Este script lee la lista de jobs, decide si debe o no intervenir sobre cada uno de ellos
  	y almacena en su propio workspace de repositorio la configuraci�n modificada, es decir:
  	es inocuo en cuanto a la configuraci�n de jenkins.  El resultado debe recogerse desde el
  	workspace del job e importarse a jenkins por alg�n medio (p. ej. jenkins-cli)
	def resolver = build.buildVariableResolver;
	def jobList = resolver.resolve("jobList");
	final home = build.getEnvironment().get("JENKINS_HOME");
	final workspace = build.workspace;
 */
final workspace = "C:/Users/dcastro.jimenez/Desktop/Jobs a Modificar/WORKSPACE";

/**
 * Escribe la nueva configuraci�n en el workspace 
 * @param text
 * @param jobName
 * @param workspace
 * @return
 */
def writeConfig(text, jobName, workspace){
  println "cambiando ${jobName}..."
  new File("${workspace}/${jobName}").mkdirs()
  def destFile = new File("${workspace}/${jobName}/config.xml")
  destFile.delete()
  //destFile << text
  Writer writer = new OutputStreamWriter(new FileOutputStream(destFile), Charset.forName("UTF-8"))
  writer.write(text, 0, text.length())
  writer.flush()
}

// Implementar en esta closure la validaci�n de si debe o no debe intervenir el script
def mustChange(xml) { 
  return true;
}

/**
 * Implementar en esta closure la acci�n a realizar sobre el xml
 * @param workspace
 * @param configText
 * @return
 */
def change(workspace,configText,job) {
	def correosFile = new File("correos.txt");
	def correos = correosFile.text.split("\n");
	def correosMap = [:];
	correos.each { linea ->
		correosMap.put(linea.trim().split(",")[0], linea.trim().split(",")[1]);
	}
	def newConfigText = "";
	correosMap.keySet().each {
		if(configText.contains(it)) {
			println "Sustituyendo en ${job} -> ${it} por " + correosMap.get(it);
			configText = configText.replaceAll(it, correosMap.get(it));
		}
	}
	println("Queda: \n ${configText}\n\n");
	return configText;
}

/**
 * Comprueba que no hay ning�n mail antiguo en ning�n job de la lista.
 * @return
 */
def checkOldMails(workspace) {
	def jobs = new File("jobs.txt").text.split("\n");
	def correosFile = new File("correos.txt");
	def mails = correosFile.text.split("\n");
	jobs.each { jobLine ->
		def jobName = jobLine.trim();
		def configFile = new File("${workspace}/${jobName}/config.xml");
		if(configFile.exists()) {
			mails.each { mail ->
				def oldMail = mail.split(",")[0];
				if(configFile.text.contains(oldMail)) {
					println("WARNING: El job ${jobName} tiene el mail antiguo ${oldMail}")
				}
			}
		} else {
			println "El config.xml \"${workspace}/${jobName}/config.xml\" no existe";
		}
	}
}

/**
 * Cuerpo del script.
 */
def jobs = new File("jobs.txt").text.split("\n");
jobs.each(){ jobTemp ->
  def job = jobTemp.trim();
  boolean changed = false;
  def configFile = new File("C:/Users/dcastro.jimenez/Desktop/Jobs a Modificar/JOBS_INT/${job}/config.xml")
  if (configFile.exists()){
    def configText = configFile.getText("UTF-8");    
    println "Modificando $job ..."
    configText = change(workspace,configText,job);
    // Guardar el job en el ws local
    writeConfig(configText, job, workspace);
    
  }else{
      println "--> ${configFile} exists: ${configFile.exists()}"
  }
  println "***********************"  
}
println("Comprobaci�n de que los jobs est�n limpios de mails antiguos:")
checkOldMails(workspace);
