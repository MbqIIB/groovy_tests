
File dirBase = new File("C:/Users/dcastro.jimenez/Desktop/Jobs a Modificar/JOBS_PRE");

dirBase.eachFileRecurse { File file ->
	if(file.getName().equals("config.xml")) {	
		def fileText = file.text;	
		def filePath = file.getAbsolutePath();
		def fileDir = filePath.split("config.xml")[0];
		println("Replace accents in \"${filePath}\":");
		fileText = fileText.replaceAll("á", "a");
		fileText = fileText.replaceAll("é", "e");
		fileText = fileText.replaceAll("í", "i");
		fileText = fileText.replaceAll("ó", "o");
		fileText = fileText.replaceAll("ú", "u");
		
		fileText = fileText.replaceAll("Á", "A");
		fileText = fileText.replaceAll("E", "E");
		fileText = fileText.replaceAll("Í", "I");
		fileText = fileText.replaceAll("Ó", "O");
		fileText = fileText.replaceAll("Ú", "U");
		
		def newFile = new File("${fileDir}config_new.xml");
		newFile.text = fileText;
		
		file.delete();
		newFile.renameTo(new File("${filePath}"));
	}
}