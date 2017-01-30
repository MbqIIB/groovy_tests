package json

import groovy.json.JsonSlurper

def listaUsuarios = new File("listaUsuarios.txt").text.split("\n");
def resultFile = new File("result.txt");

listaUsuarios.each { targetLine ->	
	
	def targetUser = targetLine.split("->")[0];
	
	def projectAreas = new File("projectAreas.json");
	
	def jsonSlurper = new JsonSlurper();
	def object = jsonSlurper.parseText(projectAreas.text);
	
	def userAreas = [];
	
	object.keySet().each {
		def usuarios = object[it].users;
		usuarios.keySet().each { user ->
			if(usuarios[user].name.toLowerCase().trim().equals(targetUser.toLowerCase().trim())) {
				userAreas.add(object[it].name);
			}
		}
	}
	
	resultFile.append("Project Areas del usuario \"${targetUser.trim()}\":\n");
	userAreas.each {
		resultFile.append("${it}\n");
	}
	resultFile.append("\n");
}


