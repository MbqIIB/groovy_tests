import es.eci.utils.ParamsHelper;
import es.eci.utils.ComponentVersionHelper;
import java.util.Map;
import java.util.regex.*;

def build = Thread.currentThread().executable;
def resolver = build.buildVariableResolver;

def component = "aQT0 - OmniStore - Desarrollo";

def scmToolsHome = build.getEnvironment(null).get("SCMTOOLS_HOME"); println("scmToolsHome = ${scmToolsHome}")
def user = build.getEnvironment(null).get("userRTC"); println("user = ${user}")
def password = resolver.resolve("pwdRTC"); println("password = ${password}")
def repository = build.getEnvironment(null).get("urlRTC"); println("repository = ${repository}")
def version = resolver.resolve("version"); println("version = ${version}")


// Calcula la versión a partir de las últimas baselines.
def calculaVersion(versionType, scmToolsHome, user, password, repository, component, stream) {
	ComponentVersionHelper componentVersionHelper = new ComponentVersionHelper(scmToolsHome);
	def listaBaselines = componentVersionHelper.getBaselines(component, stream, user, password, repository);
	def listaBaselinesSnapshot = [];
	def listaBaselinesFinal = [];
	listaBaselines.each { String baseline ->
		Matcher finalVersionMatcher = Pattern.compile(/[0-9]{1,10}\.[0-9]{1,10}\.[0-9]{1,10}\.[0-9]{1,10}$/).matcher(baseline.trim());
		Matcher snapshotVersionMatcher = Pattern.compile(/[0-9]{1,10}\.[0-9]{1,10}\.[0-9]{1,10}\.[0-9]{1,10}-SNAPSHOT$/).matcher(baseline.trim());
		if(finalVersionMatcher) {
			listaBaselinesFinal.add(baseline);
		}
		if(snapshotVersionMatcher) {
			listaBaselinesSnapshot.add(baseline);
		}
	}
	def lastVersion;
	if(versionType.equals("DESARROLLO")) {
		if(listaBaselinesSnapshot.size() > 0) {
			lastVersion = listaBaselinesSnapshot.first();
		} else {
			println("No hay baselines con versión Snapshot")
		}
	} else if(versionType.equals("RELEASE")) {
		if(listaBaselinesFinal.size() > 0) {
			lastVersion = listaBaselinesFinal.first();
		} else {
			println("No hay baselines con versión cerrada")
		}
	}
	return lastVersion;
}

/*********** SCRIPT *************/

def directorio;
if(version.contains("SNAPSHOT")) {
	directorio = "ATG_OmniStore_DESARROLLO_aQT0_OmniStore_Desarrollo_deploy";

} else if(version.trim().equals("DESARROLLO")) {
	def stream = "ATG - OmniStore - DESARROLLO";
	version = calculaVersion("DESARROLLO", scmToolsHome, user, password, repository, component, stream);
	directorio = "ATG_OmniStore_DESARROLLO_aQT0_OmniStore_Desarrollo_deploy";

} else if(version.trim().equals("RELEASE")) {
	def stream = "ATG - OmniStore - RELEASE"
	version = calculaVersion("RELEASE", scmToolsHome, user, password, repository, component, stream);
	directorio = "ATG_OmniStore_DESARROLLO_aQT0_OmniStore_Desarrollo_addFix";

} else {
	directorio="ATG_OmniStore_DESARROLLO_aQT0_OmniStore_Desarrollo_addFix";
}

def params = [:];
params.put('versionComponent', version);
params.put('directorio', directorio);
ParamsHelper.addParams(build,params);
