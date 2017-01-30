import rtc.RTCUtils;
import es.eci.utils.ScmCommand;
import es.eci.utils.TmpDir;


RTCUtils ru = new RTCUtils();

TmpDir.tmp { dir ->
	def pa = ru.getProjectArea(
		"GIS - QUVE - INTEGRACION",
		"JENKINS_RTC",
		"12345678",
		"https://rtc.elcorteingles.int:9443/ccm",
		dir
		);
	println("AREA DE PROJECTO: ${pa}")
}

