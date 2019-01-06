// Note Groovy 1.7.1 has a bad dependency on Jansi 1.1. You have to manually install it into your local cache for the tests to work. You can download it at http://repo.fusesource.com/nexus/content/groups/public/org/fusesource/jansi/jansi/1.1/.

// Remember to test the console and shell goals. There are currently no integration tests for these.

new File(System.getProperty("user.dir")).eachFileMatch groovy.io.FileType.FILES, ~/groovy-.+\.log/, { it.delete() }
println "Installing plugin..."
quietlyRunCommand "${mvn()} -B -P nonindy clean install invoker:install"
// TODO: fix joint compilation failures with Groovy 1.9-beta-1 and 1.9-beta-2
groovyVersions = ["1.5.0", "1.5.1", "1.5.2", "1.5.3", "1.5.4", "1.5.5", "1.5.6", "1.5.7", "1.5.8",
                  "1.6-beta-1", "1.6-beta-2", "1.6-RC-1", "1.6-RC-2", "1.6-RC-3", "1.6.0", "1.6.1", "1.6.2", "1.6.3", "1.6.4", "1.6.5", "1.6.6", "1.6.7", "1.6.8", "1.6.9",
                  "1.7-beta-1", "1.7-beta-2", "1.7-rc-1", "1.7-rc-2", "1.7.0", "1.7.1", "1.7.2", "1.7.3", "1.7.4", "1.7.5", "1.7.6", "1.7.7", "1.7.8", "1.7.9", "1.7.10", "1.7.11",
                  "1.8.0-beta-1", "1.8.0-beta-2", "1.8.0-beta-3", "1.8.0-beta-4", "1.8.0-rc-1", "1.8.0-rc-2", "1.8.0-rc-3", "1.8.0-rc-4", "1.8.0", "1.8.1", "1.8.2", "1.8.3", "1.8.4", "1.8.5", "1.8.6", "1.8.7", "1.8.8", "1.8.9",
                  "1.9.0-beta-1", "1.9.0-beta-2", "1.9.0-beta-3", "1.9.0-beta-4",
                  "2.0.0-beta-1", "2.0.0-beta-2", "2.0.0-beta-3", "2.0.0-rc-1", "2.0.0-rc-2", "2.0.0-rc-3", "2.0.0-rc-4", "2.0.0", "2.0.1", "2.0.2", "2.0.3", "2.0.4", "2.0.5", "2.0.6", "2.0.7", "2.0.8",
                  "2.1.0-beta-1", "2.1.0-rc-1", "2.1.0-rc-2", "2.1.0-rc-3", "2.1.0", "2.1.1", "2.1.2", "2.1.3", "2.1.4", "2.1.5", "2.1.6", "2.1.7",
                  "2.2.0-beta-1", "2.2.0-beta-2", "2.2.0-rc-1", "2.2.0-rc-2", "2.2.0-rc-3", "2.2.0", "2.2.1", "2.2.2",
                  "2.3.0-beta-1", "2.3.0-beta-2", "2.3.0-rc-1", "2.3.0-rc-2", "2.3.0-rc-4", "2.3.0", "2.3.1", "2.3.2", "2.3.3", "2.3.4", "2.3.5", "2.3.6", "2.3.7", "2.3.8", "2.3.9", "2.3.10", "2.3.11",
                  "2.4.0-beta-1", "2.4.0-beta-2", "2.4.0-beta-3", "2.4.0-beta-4", "2.4.0-rc-1", "2.4.0-rc-2", "2.4.0", "2.4.1", "2.4.2", "2.4.3", "2.4.4", "2.4.5", "2.4.6", "2.4.7", "2.4.8", "2.4.9", "2.4.10", "2.4.11", "2.4.12", "2.4.13", "2.4.14", "2.4.15",
                  "2.5.0-alpha-1", "2.5.0-beta-1", "2.5.0-beta-2", "2.5.0-beta-3", "2.5.0-rc-1", "2.5.0-rc-2", "2.5.0", "2.5.1", "2.5.2", "2.5.3", "2.5.4", "2.5.5",
                  "2.6.0-alpha-1", "2.6.0-alpha-2", "2.6.0-alpha-3",
                  "3.0.0-alpha-1", "3.0.0-alpha-2", "3.0.0-alpha-3", "3.0.0-alpha-4"]
for (int i = 0; i < groovyVersions.size(); i++) {
    def groovyVersion = groovyVersions[i]
    System.out.print "Testing Groovy ${groovyVersion}..."
    def pom = new File("pom.xml")
    pom.write((pom.text =~ /<groovyVersion>.+/).replaceFirst("<groovyVersion>${groovyVersion}</groovyVersion>"))
    testLabel = groovyVersion
    os = new FileOutputStream(new File("groovy-${testLabel}.log"))
    profile = "${i < groovyVersions.indexOf("2.3.0") ? 'pre2.3-' : ''}nonindy"
    testVersion()
    if (i >= groovyVersions.indexOf("2.0.0-beta-3")) {
        System.out.print "Testing Groovy ${groovyVersion}-indy..."
        testLabel = "${groovyVersion}-indy"
        os = new FileOutputStream(new File("groovy-${testLabel}.log"))
        profile = "${i < groovyVersions.indexOf("2.3.0") ? 'pre2.3-' : ''}indy"
        testVersion()
    }
}
quietlyRunCommand "git checkout pom.xml"
quietlyRunCommand "${mvn()} -B clean"

void testVersion() {
    def exitCode = runCommand "${mvn()} -B -P $profile -Dinvoker.streamLogs=true invoker:run"
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

def quietlyRunCommand(def command) {
    def proc = command.execute()
    proc.consumeProcessOutput()
    proc.waitFor()
}

def mvn() {
    if (System.getProperty('os.name').contains('Windows')) {
        try {
            quietlyRunCommand("mvn.bat -v")
            return "mvn.bat"
        } catch (IOException ioe) {
            return "mvn.cmd"
        }
    } else {
        return "mvn"
    }
}
