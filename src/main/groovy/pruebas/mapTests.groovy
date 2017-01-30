
def mapa = [:];

mapa.put("aaa", "1");
mapa.put("bbb", "2");
mapa.put("ccc", "3");

if(mapa.keySet().contains("bbb")) {
	println "contiene!!"
	println "Value: " + mapa.get("bbb");
} else {
	println "no contiene"
}
println(mapa.keySet().toArray());


