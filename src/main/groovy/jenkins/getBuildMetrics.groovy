import hudson.model.*;

def build = Thread.currentThread().executable;
def resolver = build.buildVariableResolver;

def jobName = resolver.resolve("jobName");
def buildNumber = resolver.resolve("buildNumber");

def job = hudson.model.Hudson.instance.getJob(jobName);

if(job != null) {
  job.getProperties().values().each {
	if(it instanceOf hudson.tasks.junit.TestResultAction) {
	  println("hemos encontrado: " + it)
	}
  }
}
