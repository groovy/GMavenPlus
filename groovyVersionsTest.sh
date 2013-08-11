#!/bin/sh

groovyVersions=(1.7-beta-1 1.7-beta-2 1.7-rc-1 1.7-rc-2 1.7.0 1.7.1 1.7.10 1.7.11 1.7.2 1.7.3 1.7.4 1.7.5 1.7.6 1.7.7 1.7.8 1.7.9 1.8.0-beta-1 1.8.0-beta-2 1.8.0-beta-3 1.8.0-beta-4 1.8.0-rc-1 1.8.0-rc-2 1.8.0-rc-3 1.8.0-rc-4 1.8.0 1.8.1 1.8.2 1.8.3 1.8.4 1.8.5 1.8.6 1.8.7 1.8.8 1.8.9 1.9.0-beta-1 1.9.0-beta-2 1.9.0-beta-3 1.9.0-beta-4 2.0.0-beta-1 2.0.0-beta-2 2.0.0-beta-3 2.0.0-rc-1 2.0.0-rc-2 2.0.0-rc-3 2.0.0-rc-4 2.0.0 2.0.1 2.0.2 2.0.3 2.0.4 2.0.5 2.0.6 2.0.7 2.0.8 2.1.0-beta-1 2.1.0-rc-1 2.1.0-rc-2 2.1.0-rc-3 2.1.0 2.1.1 2.1.2 2.1.3 2.1.4 2.1.5 2.1.6 2.2.0-beta-1 2.2.0-beta-2)
for groovyVersion in ${groovyVersions[@]}; do
    sed -ri "s,<groovyVersion>.+,<groovyVersion>${groovyVersion}</groovyVersion>," pom.xml
    mvn clean install &> /dev/null
    if [[ $? -ne 0 ]]; then
        echo "Groovy ${groovyVersion} caused failures."
    fi
done
git checkout pom.xml
