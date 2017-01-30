import urbanCode.UrbanCodeExecutor;
import urbanCode.UrbanCodeComponentInfoService;

def udClientCommand = "C:/OpenDevECI/udclient/udclient";
def urlUdeploy = "https://udeploy.pre.eci.geci";
def user = "consola";
def password = "consola";

def componentUrban = "PruebaRelease - App 1";

UrbanCodeExecutor urbExe = new UrbanCodeExecutor(udClientCommand, urlUdeploy, user, password);
urbExe.initLogger { println it }

UrbanCodeComponentInfoService urbanInfo = new UrbanCodeComponentInfoService(urbExe);
urbanInfo.initLogger { println it };

def coordinates = urbanInfo.getCoordinates("${componentUrban}");


println("Coordenadas devueltas por Urban: \n${coordinates}");