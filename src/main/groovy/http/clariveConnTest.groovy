import java.io.File;
import java.nio.channels.Channel
import java.nio.channels.Channels

import groovy.io.FileType
import groovy.json.*

def clariveService = "SWC01";

def nombre_producto = "";
def nombre_subproducto = "";
def nombre_corriente = "";
def nombre_componente = "";
def num_version = "";
def version_maven = "";
def proceso = "";
def id_proceso = "";
def paso = "";
def resultado = "";
def metrica = "";
def tipo_corriente = "";

def nakedUrl = "http://10.252.80.73:3000";
def api_key_clarive = "fe80f95d5e4647bc5d06daf33ada1982";

def url = "${nakedUrl}/rule/raw/SWC01?" +
		"api_key=${api_key_clarive}&" +
		"producto=${nombre_producto}&"+
		"subproducto=${nombre_subproducto}&"+
		"corriente=${tipo_corriente}&"+
		"componente=${nombre_componente}&"+
		"version=${num_version}&"+
		"version_maven=${version_maven}&"+
		"proceso=${proceso}&"+
		"id_proceso=${id_proceso}&"+
		"paso=${paso}&"+
		"resultado_paso=${resultado}";
		
def proxy = "proxycorp.geci";
def port = "8080";

def ret = connectToService(url, proxy, port);

println("RESPONSE:");
println(ret.get("responseText"));

println("\nHEADER:")
Map<Object,Object> headerMap = ret.get("headerMap");
headerMap.keySet().each { key ->
	println("${key}: " + headerMap.get(key));
}

println("\nDATE:")
println("FECHA -> " + ret.get("date"));

/**
 * Conexión al servicio indicado por la url.
 * @param url
 * @param eci_proxy_url
 * @param eci_proxy_port
 * @return
 */
private Map<String,Object> connectToService(url, eci_proxy_url, eci_proxy_port) {
	url = url.replaceAll(" ", "%20");
	println("Conectando con Clarive mediante la url:\n${url}");
	URL server = new URL(url);
	Properties systemProperties = System.getProperties();
	systemProperties.setProperty("http.proxyHost","${eci_proxy_url}");
	systemProperties.setProperty("http.proxyPort","${eci_proxy_port}");

	def encoding = "UTF-8";

	HttpURLConnection con = (HttpURLConnection)server.openConnection();
	con.setRequestMethod("POST");
	con.setRequestProperty("Content-Type", "application/json");
	con.setDoOutput(true);
	DataOutputStream wr = new DataOutputStream(con.getOutputStream());
	wr.flush();
	wr.close();

	// Resultado
	int responseCode = 0;
	try {
		responseCode = con.getResponseCode();
	} catch (Exception e) {
		throw new Exception(e);
	} finally {
		println("Response Code : " + responseCode);
	}

	BufferedReader inReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
	String inputLine;
	StringBuffer response = new StringBuffer();

	while ((inputLine = inReader.readLine()) != null) {
		response.append(inputLine);
	}
	inReader.close();


	def responseText = response.toString();	
	def headerMap = con.getHeaderFields();
	def date = con.getHeaderField("Date");
	
	def ret = [:];
	ret.put("responseText", responseText);
	ret.put("headerMap", headerMap);
	ret.put("date", date);
	
	return ret;
}

