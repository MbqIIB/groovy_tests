
import es.eci.utils.CheckSnapshots;

def application = "GIS - Servicios e Integraciones WMBaaaaaa";
def instantanea = "PGP_02.11.14.00";
def udClientCommand = "C:/OpenDevECI/udclient/udclient";
def urlUdeploy = "https://udeploy.eci.geci";
def userUdclient = "consola";
def pwdUdclient = "consola";

boolean existsUrbanSnapshot = CheckSnapshots.checkUrbanCodeSnapshots(application, instantanea, udClientCommand, urlUdeploy, userUdclient, pwdUdclient);

if(existsUrbanSnapshot) {
	throw new Exception("Ya existe la instantánea \"${instantanea}\" en UrbanCode.");
} else {
	println("La instantánea no existe en UrbanCode");
}
