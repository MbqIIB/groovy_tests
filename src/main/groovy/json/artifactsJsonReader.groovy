import groovy.json.JsonBuilder
import groovy.json.JsonSlurper;

def artifactsJson = new File("artifacts.json");
def index = 5;

JsonSlurper slurper = new JsonSlurper();
def jsonObject = slurper.parse(artifactsJson);
def step = "increaseVersion";

println "stepUpdateArtifactsJson -> fillVersion"
jsonObject.each {
	def versionText =it.version;
	def isSnapshot = versionText.contains("-SNAPSHOT");
	def numericPart = versionText.split("-SNAPSHOT")[0];
	ArrayList numericPartArray = numericPart.split("\\.");
	if(numericPartArray.size() < 4) {
		println("La version de ${it.artifactId} tiene menos de 4 dígitos.");
		def digitsToFill = 4 - numericPartArray.size();
		for(int i=0; i<digitsToFill; i++) {
			numericPartArray.add("0");
		}
		numericPart = numericPartArray[0] + "." + numericPartArray[1] + "." + numericPartArray[2] + "." +numericPartArray[3];
		if(isSnapshot) {
			def finalVersion = numericPart + "-SNAPSHOT";
			it.version = finalVersion;
		} else {
			def finalVersion = numericPart;
			it.version = finalVersion;
		}
	}
}

switch (step) {
	case "increaseVersion":
		println "stepUpdateArtifactsJson -> increaseVersion"
		jsonObject.each {
			def isSnapshot = it.version.contains("-SNAPSHOT");
			def version = it.version.split("-SNAPSHOT")[0];
			println "stepUpdateArtifactsJson -> version: ${it.groupId}:${it.artifactId}:${it.version}"
			ArrayList versionDigits = version.split("\\.");
			println "stepUpdateArtifactsJson -> versionDigits: $versionDigits"
			boolean isHotfix = (index == 5);

			def newVersion;
			if(isSnapshot && !isHotfix) {
				def increasedDigit = versionDigits[index - 1].toInteger() + 1;
				versionDigits.set(index - 1, increasedDigit);
				newVersion = versionDigits[0] + "." + versionDigits[1] + "." + versionDigits[2] + "." + versionDigits[3] + "-SNAPSHOT";
			}
			else if(!isSnapshot && !isHotfix) {
				def increasedDigit = versionDigits[index - 1].toInteger() + 1;
				versionDigits.set(index - 1, increasedDigit);
				newVersion = versionDigits[0] + "." + versionDigits[1] + "." + versionDigits[2] + "." + versionDigits[3];
			}
			else if(!isSnapshot && isHotfix) {
				println("Caso hotfix con versionDigits = ${versionDigits}");
				if(versionDigits[3].split("-").size() == 1) {
					versionDigits.set(3, versionDigits[3] + "-1");
					newVersion = versionDigits[0] + "." + versionDigits[1] + "." + versionDigits[2] + "." + versionDigits[3];
					println("newVersion = ${newVersion}")
				}
				else if(versionDigits[3].split("-").size() == 2) {
					def hotFixDigit = versionDigits[3].split("-")[1].toInteger() + 1;
					versionDigits.set(3, versionDigits[3].split("-")[0] + "-" + hotFixDigit);
					newVersion = versionDigits[0] + "." + versionDigits[1] + "." + versionDigits[2] + "." + versionDigits[3];
					println("newVersion = ${newVersion}")
				}
			}

			it.version = newVersion;
		}
		break;
		
	case "nada":
		println("No hago nada");
}


JsonBuilder builder = new JsonBuilder(jsonObject);
def newArtifactsJson = builder.toString();
artifactsJson.setText(newArtifactsJson);

jsonObject = slurper.parse(artifactsJson);




