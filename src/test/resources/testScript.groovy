new File("target/testFile.txt").withWriter { w ->
    w << "Hello world!"
}
