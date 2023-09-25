import groovy.io.FileType

// Note Groovy 1.7.1 has a bad dependency on Jansi 1.1. You have to manually install it into your local cache for the tests to work. You can download it at http://repo.fusesource.com/nexus/content/groups/public/org/fusesource/jansi/jansi/1.1/.

// Remember to test the console and shell goals. There are currently no integration tests for these.

new File(System.getProperty("user.dir")).eachFileMatch FileType.FILES, ~/groovy-.+\.log/, { it.delete() }
println "Installing plugin..."
quietlyRunCommand "${mvn()} -B -P nonindy -Dmaven.test.skip=true -Dinvoker.skip=true clean install invoker:install"
groovyVersions = ["2.5.23", "3.0.19", "4.0.15", "5.0.0-alpha-2"]
for (groovyVersion in groovyVersions) {
    def groovyMajorVersion = groovyVersion.split("\\.")[0].toInteger()
    System.out.print "Testing Groovy ${groovyVersion}..."
    testLabel = groovyVersion
    os = new FileOutputStream(new File("groovy-${testLabel}.log"))
    profiles = "nonindy"
    properties = "-DgroovyVersion=${groovyVersion} -DgroovyGroupId=${groovyMajorVersion >= 4 ? 'org.apache.groovy' : 'org.codehaus.groovy'}"
    testVersion()
}
quietlyRunCommand "${mvn()} -B clean"

void testVersion() {
    def exitCode = runCommand "${mvn()} --batch-mode --activate-profiles ${profiles} -Dinvoker.streamLogs=true ${properties} invoker:run"
    os.flush()
    os.close()
    if (exitCode != 0) {
        println "  Failed."
    } else {
        println "  Succeeded."
        new File("groovy-${testLabel}.log").delete()
    }
}

def runCommand(def command) {
    def proc = command.execute()
    proc.consumeProcessOutput(os, os)
    proc.waitFor()
}

static def quietlyRunCommand(def command) {
    def proc = command.execute()
    proc.consumeProcessOutput()
    proc.waitFor()
}

static def mvn() {
    if (System.getProperty('os.name').contains('Windows')) {
        try {
            quietlyRunCommand("mvn.bat -v")
            return "mvn.bat"
        } catch (IOException ignored) {
            return "mvn.cmd"
        }
    } else {
        return "mvn"
    }
}
