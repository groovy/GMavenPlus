File site = new File(basedir, 'target/site')
assert new File(site, 'gapidocs/index.html').text.contains('Direct API documentation')
assert new File(site, 'testgapidocs/index.html').text.contains('Direct API documentation')
assert new File(site, 'gapidocs/example/MainExample.html').text.contains('Main Groovy documentation marker')
assert new File(site, 'gapidocs/example/JavaExample.html').text.contains('Main Java documentation marker')
assert new File(site, 'testgapidocs/example/TestExample.html').text.contains('Test Groovy documentation marker')
assert !new File(site, 'project-reports.html').exists()
return true
