//El workflow con rollback está en la línea base 2.2.0

def listaParam = params["lista"]
def listaSkipsParam = params["listaSkips"]

def validateParams(Map params) {
	for (String param: params.keySet()) {
		String value = params[param]
		if (value.contains('${' + param + '}')) {
			throw new Exception("Bucle infinito detectado en $param")
		}
	}
}

validateParams(params)

if (listaParam!=null && listaParam.length()>0){
	def lista = listaParam.split("\n");
	def listaSkips = listaSkipsParam!=null?listaSkipsParam.split(","):[];

	def log = {b, job, direction ->
		println "FIN:    ${new Date()} ---"
		println(b.getBuild().getLog())
	}

	def injectParam = { String valor ->
		if (valor.indexOf("\${")>=0){
			String ret = valor
			boolean continuar = true
			int indice = 0
			while (continuar){
				indice = ret.indexOf("\${", indice)
				if (indice != -1) {
					def remp=params[ret.substring(ret.indexOf("{", indice)+1,ret.indexOf("}", indice))]
					// Detección temprana de bucles
					if (remp == valor) {
						throw new Exception("Bucle infinito detectado en $valor")
					}
					if (remp!=null){
						ret = ret.replace("\${${ret.substring(ret.indexOf("{", indice)+1,ret.indexOf("}", indice))}}",remp)
					}
					else {
						ret = ret.replace("\${${ret.substring(ret.indexOf("{", indice)+1,ret.indexOf("}", indice))}}",'')
					}
				}
				else {
					continuar = false;
				}
			}
			return ret
		}else{
			println "Controller: valor -> ${valor}"
			return valor
		}
	}

	def toParams = { cadena, paramsLocal ->
		if (cadena!=null && cadena.length()>0){
			cadena.split("&").each {param ->
				def t = param.split("=")
				paramsLocal[t[0]] = t.length>1?injectParam(t[1]):""
			}
		}
	}

	def buildSkip = { paramsLocal, job ->
		if (listaSkips.find{skip -> job==skip}!=null){
			println "job: ${job} skipped!!"
		}else{
			def suffix = paramsLocal["suffix"]
			if (suffix!=null && suffix!="" && suffix!="null"){
				job = "${job}${suffix}"
			}
			build( paramsLocal , job)
		}
	}

	def executeJobs = {jobs ->
		def b
		def ok = true
		def nodo = null
		jobs.each() { jobx ->
			println jobx
			if (!jobx.startsWith("#")){
				def paramsLocal = [:]
				paramsLocal.putAll(params)
				def t = jobx.split("\\?")
				def job = t[0]
				if (t.size()>1){
					toParams(t[1],paramsLocal)
				}
				if (ok){
					println "INICIO: ${new Date()} ---"
					// La primera vez
					if (nodo == null && b != null && b.builtOn != null && b.builtOn.nodeName != null && b.builtOn.nodeName != ''){
						nodo = b.builtOn.nodeName
						println "Nodo director: " + nodo
					}
					if (nodo != null) {
						println "************** NODE : " + nodo
						toParams("NODE=" + nodo,paramsLocal)
						toParams("WHERE=" + nodo,paramsLocal)
					}else{
						println "************** NODE no informado"
					}

					if (paramsLocal["block"]=="false"){
						try {
							b = buildSkip( paramsLocal , job)
							build.getState().setResult(SUCCESS)
						}catch(Exception e){
							build.getState().setResult(SUCCESS)
						}
					}else{
						b = buildSkip( paramsLocal , job)
						if (b!=null){
							ok = b.getResult()==SUCCESS
						}
					}
					if (b!=null){
						log(b,job,"EXECUTION")
					}
					println "FIN: ${new Date()} ---"
				}
			}
		}
		return true
	}
	executeJobs(lista)
}else{
	println("Ningún job pasado al Controller!!")
	build.getState().setResult(FAILURE)
}