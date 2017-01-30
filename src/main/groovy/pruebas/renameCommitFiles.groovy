def parentWorkspace = "C:/jenkins/workspace/test2";
def parentWorkspaceDir = new File(parentWorkspace);

def lastCommitFiles = [];
def newCommitFiles = [];

parentWorkspaceDir.eachFile { File file ->
	if(file.getName().endsWith("_lastCommit.txt")) {
		lastCommitFiles.add(file.getName());
	} else if(file.getName().endsWith("_newCommit.txt")) {
		newCommitFiles.add(file.getName());
	}
}

lastCommitFiles.each { String lastCommitFileName ->
	boolean hasNewCommitFile = false;
	def componentName = lastCommitFileName.split("_lastCommit.txt")[0]
	def newCommitFileName = newCommitFiles.find { String newCommitFileName ->
		newCommitFileName.split("_newCommit.txt")[0].equals(componentName);		
	}
	
	if(newCommitFileName != null) {
		File lastCommitFile = new File("${parentWorkspace}/${lastCommitFileName}");
		lastCommitFile.delete();
	
		File newCommitFile = new File("${parentWorkspace}/${newCommitFileName}");
		newCommitFile.renameTo("${parentWorkspace}/${lastCommitFileName}");
	}
}

