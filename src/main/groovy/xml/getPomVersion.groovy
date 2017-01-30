import es.eci.utils.ParamsHelper

def build = Thread.currentThread().executable;
def resolver = build.buildVariableResolver;

def pomFile = new File("${build.workspace}/pom.xml");

def version;
boolean touched = false;
pomFile.eachLine { String line ->
	if(line.trim().startsWith("<version>") && !touched) {
		version = line.split("<version>")[1].split("</version>")[0];
		touched = true;
	}
}

if(version.startsWith("\$")) {
	def versionPropertyName = version.substring(2, version.length() -1);
	pomFile.eachLine { String line ->
		if(line.trim().startsWith("<${versionPropertyName}>")) {
			version = line.split("<${versionPropertyName}>")[1].split("</${versionPropertyName}>")[0];
		}
	}
}

def params = [:];
params.put('pomVersion', version);
ParamsHelper.addParams(build, params);
