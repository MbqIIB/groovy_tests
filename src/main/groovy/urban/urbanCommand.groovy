import urbanCode.UrbanCodeExecutor

def application = "88888 - Prueba GATES";
def udClientCommand = "C:/OpenDevECI/udclient/udclient" 
def urlUdeploy = "https://udeploy.pre.eci.geci"
def userUdclient = "consola"
def pwdUdclient = "consola"

UrbanCodeExecutor urbExe = new UrbanCodeExecutor(udClientCommand, urlUdeploy, userUdclient, pwdUdclient);

def urbanSnapshot = urbExe.executeCommand("getComponents --application \"${application}\"");