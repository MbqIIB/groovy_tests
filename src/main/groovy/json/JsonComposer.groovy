import groovy.json.JsonOutput;
import groovy.json.JsonSlurper;

def descriptorFile = new File("descriptor.txt")

JsonSlurper jsonSlurper = new JsonSlurper();
def jsonObject = jsonSlurper.parseText(descriptorFile.text);
def versions = jsonObject.versions;
versions.add(["tralalalalala":"1.1.1.1.1"]);


def jsonComplete = JsonOutput.toJson(["application":"${jsonObject.application}","description":"${jsonObject.description}","versions":versions]);

println(jsonComplete);