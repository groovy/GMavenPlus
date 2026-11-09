File site = new File(basedir, 'target/site')
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
return true
