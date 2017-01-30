import es.eci.utils.ZipHelper;

def parentWorkspace = "C:/Users/dcastro.jimenez/Desktop/BORRAR";
def version = "1.7.7.0";

ZipHelper.unzipFile(new File("${parentWorkspace}/OmniStore-Code-${version}.zip"),
					new File("${parentWorkspace}/OmniStore"));


