def root = new File(args.size() ? args[0] : ".")
def entries = []

def findJars(dir) {
    dir.listFiles().findAll { it.name.endsWith(".jar") }
}

def ps = File.separator

def path = { File file,File base = root ->
    file.canonicalPath - base.canonicalPath.with {
        endsWith(ps) ? it : it + ps
    }
}

entries << [kind:'con',path:"org.eclipse.jdt.launching.JRE_CONTAINER"]

def srcDirs = ["src/groovy","src/java","grails-app/controllers","grails-app/services","grails-app/domain","grails-app/taglib","grails-app/conf","test/unit"]
srcDirs.collect { new File(root,it) }.findAll { it.exists() }.each {
    entries << [kind:'src',path:path(it)]
}

def srcs = srcDirs + new File(root,"plugins").listFiles().findAll { it.directory }.collect { plug ->
    new File(plug,"lib").with {
        if(exists()) {
            findJars(it).each {
                entries << [kind:'lib',path:path(it)]
            }
        }        
    }
    srcDirs.collect { new File(plug,it) }.findAll { it.exists() }.each { 
        entries << [kind:'src',path:path(it)]
    }
    
}.flatten()

def GH = new File(System.getenv().GRAILS_HOME)
findJars(new File(GH,"dist")).each {
    entries << [kind:'var',path:"GRAILS_HOME"+ps+path(it,GH)]
}
findJars(new File(GH,"lib")).each {
    entries << [kind:'var',path:"GRAILS_HOME"+ps+path(it,GH)]
}
findJars(new File(root,"lib")).each {
    entries << [kind:'lib',path:path(it)]
}


entries << [kind:'output',path:"web-app/WEB-INF/classes"]

new File(root,".classpath").withWriter { w-> 
  w << '<?xml version="1.0" encoding="UTF-8"?>\n'
  w << new groovy.xml.StreamingMarkupBuilder().bind {
    classpath {
        entries.each {
            classpathentry(it)
        }        
    }
  }.toString().replaceAll(/'/,/"/).replaceAll(/<classpathentry/,"""
    <classpathentry""")
}