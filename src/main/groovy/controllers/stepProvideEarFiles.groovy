def workspace = params["parentWorkspace"]
def serverList = params["serverList"]
def artifactId = params["artifactId"]
def scriptDir= params["scriptDir"]
def block = params["block"]
def applicationsTexto = params["applications"]
def groupId = params["groupId"]
def version = params["version"]

// Usando "ComponentVersionHelper.getBaselines()" se puede obtener una lista con
// las últimas lineas base de un componente para una corriente dada.

if (applicationsTexto !=null && applicationsTexto !="null" && applicationsTexto.length()>0)
	applicationsTexto = "applications = ${applicationsTexto}"
else
	applicationsTexto = "applications = [:]"
evaluate(applicationsTexto)

def configFile = new File("${workspace}/version.txt")
println "configFile: ${configFile}"
if (version==null || !version.contains(".") || groupId==null || groupId.trim() == "" || groupId == "null") {
	if (configFile.exists()){
		println "Lee version y grupo de version.txt..."
		def config = new ConfigSlurper().parse(configFile.toURL())
		if (groupId==null || groupId.trim() == "" || groupId == "null") {
			groupId=config.groupId
		}
		if (version==null || !version.contains(".")) {
			version=config.version
		}
	}
}
println "--------------groupId : ${groupId}"
println "--------------version: ${version}"

def params2 = new HashMap()
params.keySet().each { key ->
	params2.put(key, params[key])
}
//Comandos a ejecutar
def commands = "cd ${scriptDir};"
applications.each() { key, value ->
	if (key!="sequence")
		commands += "(./provideFiles.sh ${groupId} ${key} ${version} ear &) ;"
}

params2.put("commands",commands)

def servidores = serverList.split(",")
servidores.each() { server ->
	try {
		b = build(params2,"${server}_ExecuteCommands")
	}catch(Exception e){
		if (block=="false"){
			println "pongo SUCCESS ${e}"
			build.getState().setResult(SUCCESS)
		}
	}
}
