def scmToolsHome = "C:/OpenDevECI/scmtools/eclipses";

def scmToolsHomeDir = new File(scmToolsHome);
if(!scmToolsHomeDir.exists()) {
	throw new FileNotFoundException("No existe el directorio scmToolsHome indicado -> \"${scmToolsHome}\"");	
}