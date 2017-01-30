package json

import groovy.json.JsonSlurper
	
def projectAreas = new File("projectAreas.json");
def resultFile = new File("resultAreas.txt");
resultFile.text = "";

def jsonSlurper = new JsonSlurper();
def object = jsonSlurper.parse(projectAreas);

object.keySet().each {
	def areaName = object[it].name;	
	def rtcUsers = new File("listaUsuarios.txt").text.split("\n");
	def thisUsers = [:];
	rtcUsers.each { rtcUserLine ->
		def rtcUser = rtcUserLine.split("->")[0];
		def rtcUserEmail = rtcUserLine.split("->")[1];
		def usuarios = object[it].users;
		usuarios.keySet().each { user ->			
			if(usuarios[user].name.toLowerCase().trim().equals(rtcUser.toLowerCase().trim())) {
				thisUsers.put(rtcUser,rtcUserEmail);
			}
		}
	}	
	if(thisUsers.size() > 0) {
		resultFile.append("Los usuarios del área \"${areaName}\":\n");
		thisUsers.each { thisUser ->
			resultFile.append("${thisUser.key.trim()} -> ${thisUser.value.trim()}\n");
		}
		resultFile.append("\n");
	}
}