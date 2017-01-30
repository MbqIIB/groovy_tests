package scm;

import es.eci.utils.ScmCommand;
import es.eci.utils.TmpDir;

class RTCUtils {

	public static String getProjectAreas(rtcUser, rtcPass, rtcUrl, dir) {
		def getProjectAreasCommand = "list projectareas -j";		
		ScmCommand scm = new ScmCommand(ScmCommand.Commands.SCM);
		scm.ejecutarComando(getProjectAreasCommand, rtcUser, rtcPass, rtcUrl, dir);
		def projectAreasJson = scm.getLastResult();
		return projectAreasJson;
	}

	public void getProjectAreaByStream() {

	}

}
