File targetDir = new File('target')
if (!targetDir.exists())
    targetDir.mkdir()
new File('target/helloWorld.txt').append('Hello world!')
