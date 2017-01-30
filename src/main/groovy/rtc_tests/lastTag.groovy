import es.eci.utils.ComponentVersionHelper;
import es.eci.utils.SystemPropertyBuilder;
import java.util.regex.*;

SystemPropertyBuilder propBuilder = new SystemPropertyBuilder();
def propertiesMap = propBuilder.getSystemParameters();

def stream = propertiesMap.get("stream");
def component = propertiesMap.get("component");
def scmToolsHome = "C:/OpenDevECI/scmtools/eclipse";
def user = "JENKINS_RTC";
def password = "12345678";
def repository = "https://rtc.elcorteingles.int:9443/ccm";

Pattern closedVersion = ~/[0-9]\.[0-9]\.[0-9]\.[0-9]$/
Pattern snapshotVersion = ~/[0-9]\.[0-9]\.[0-9]\.[0-9]-SNAPSHOT$/

ComponentVersionHelper componentVersionHelper = new ComponentVersionHelper(scmToolsHome);

List<String> listaBaselines = componentVersionHelper.getBaselines(component, stream, user, password, repository);

List<String> listaBaselinesSnapshot = [];
List<String> listaBaselinesFinal = [];

listaBaselines.each { String baseline ->
	Matcher finalVersionMatcher = Pattern.compile(/[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}$/).matcher(baseline.trim());
	Matcher snapshotVersionMatcher = Pattern.compile(/[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}-SNAPSHOT$/).matcher(baseline.trim());
	if(finalVersionMatcher) {
		listaBaselinesFinal.add(baseline);
	}
	if(snapshotVersionMatcher) {
		listaBaselinesSnapshot.add(baseline);
	}
}

println("Ultima version SNAPSHOT:");
if(listaBaselinesSnapshot.size() > 0) {
	println(listaBaselinesSnapshot.first());
} else {
	println("No hay baselines con versión Snapshot")
}


println("Ultima version CERRADA:");
if(listaBaselinesFinal.size() > 0) {
	println(listaBaselinesFinal.first());
} else {
	println("No hay baselines con versión cerrada")
}


