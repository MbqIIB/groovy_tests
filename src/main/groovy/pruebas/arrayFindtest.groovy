//def texto = "aaa:bbb,ddd:eee,ttt:eee";

def version = "1.2.3.4"

ArrayList array = version.split("\\.");

array.add("0");
array.size();
array.each {
	println it
}