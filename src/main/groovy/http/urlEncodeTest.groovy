import java.net.URLEncoder;

def clariveService = "";

def nombre_producto = "";
def nombre_subproducto = "";
def nombre_corriente = "";
def nombre_componente = "";
def num_version = "";
def version = "";
def proceso = "";
def id_proceso = "";
def paso = "";
def tipo_resultado = "";
def metrica = "";

def api_key_clarive = "fe80f95d5e4647bc5d06daf33ada1982";

def url ="http://10.252.80.73:3000/rule/raw/SWC01?api_key=fe80f95d5e4647bc5d06daf33ada1982&producto=PRE - Enlace RTC Jenkins y Nexus&subproducto=GIS - Proyecto Prueba Release - DESARROLLO&corriente=DESARROLLO";

println("URL: " + URLEncoder.encode(url, "UTF-8").replaceAll("\\+", "%20"));
println("URL: " + url.replaceAll(" ", "%20"));
