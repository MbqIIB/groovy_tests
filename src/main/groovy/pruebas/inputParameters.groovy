def param1 = System.getProperty("param1");
def param2 = System.getProperty("param2");;

println("El param1 es: ${param1}")
println("El param2 es: ${param2}")

if(param1.equals("aaa")) {
	println "caso1";
} else if(param1.equals("bbb") && param2.equals("ccc")) {
	println "caso2";
} else if(param1.equals("bbb")) {
	println "caso3";
}
