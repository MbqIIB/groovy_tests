import groovy.json.*;

List<String> lista1 = ["aaaa","bbbb","cccc"];
List<String> lista2 = ["11111","22222","3333"];
List<String> lista3 = ["dddd","eeee","fffff","ggggg"];

List<List<String>> lista = [];
lista.add(lista1);
lista.add(lista2);
lista.add(lista3);

def json = JsonOutput.toJson(lista);

//println(json);

JsonSlurper js = new JsonSlurper();
def parsedJson = js.parseText(json);

parsedJson.each{
	println it
}