File site = new File(basedir, 'child/target/documentation')
assert new File(site, 'gapidocs/index.html').isFile()
assert new File(site, 'testgapidocs/index.html').isFile()
assert new File(site, 'gapidocs/example/MainExample.html').text.contains('Main Groovy documentation marker')
assert new File(site, 'gapidocs/example/JavaExample.html').text.contains('Main Java documentation marker')
assert new File(site, 'testgapidocs/example/TestExample.html').text.contains('Test Groovy documentation marker')
assert new File(site, 'gapidocs/index.html').text.contains('Site API documentation')
assert !new File(site, 'gapidocs/example/TestExample.html').exists()
String reports = new File(site, 'project-reports.html').text
assert reports.contains('href="gapidocs/index.html"')
assert reports.contains('href="testgapidocs/index.html"')
assert reports.contains('Test Groovydoc')
assert !new File(basedir, 'target/documentation/gapidocs').exists()
assert !new File(basedir, 'target/documentation/testgapidocs').exists()
File parentReports = new File(basedir, 'target/documentation/project-reports.html')
if (parentReports.exists()) {
    assert !parentReports.text.contains('gapidocs/index.html')
}
assert !new File(basedir, 'child/target/site/gapidocs').exists()
return true
