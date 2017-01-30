import es.eci.utils.GlobalVars;
import hudson.model.*;

def build = Thread.currentThread().executable;
def resolver = build.buildVariableResolver;


GlobalVars gVars = new GlobalVars(build);

def rootBuild = gVars.getRootBuild(build);

def buildVariables = rootBuild.getProperties().get("buildVariables");

buildVariables.keySet().each { key ->
	println("Valor para ${key}:");
	println(buildVariables.get(key));
	println("");
}
