File targetDir = new File('target')
if (!targetDir.exists())
    targetDir.mkdir()
new File('target/helloWorld2.txt').append('Hello world 2!')
