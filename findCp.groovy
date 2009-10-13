def file = args[0]

def base = "/Users/noah/tvly-workspace/ui-assets/"
def assets = "/var/assets/"

def source = "find ${base} -name ${file}".execute().in.text
def target = assets + (source - base)

new File(target).parentFile.mkdirs()

def cmd = "cp ${source} ${target}"
cmd.execute()
println cmd

