def a = null;
def b = "aaa";

def res = (a != null && a.trim().equals("")) ? a : "false" ;
println(res);

if(a != null && a.trim().equals("")) {
	println(a)
} else {
	println "false"
}
