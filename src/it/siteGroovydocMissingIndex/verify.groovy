String log = new File(basedir, 'build.log').text
assert log.contains('Groovydoc report did not generate')
assert log.contains('index.html')
assert log.contains('defaultDocTemplates')
assert new File(basedir, 'target/site/gapidocs/overview-summary.html').isFile()
assert !new File(basedir, 'target/site/gapidocs/index.html').exists()
return true
