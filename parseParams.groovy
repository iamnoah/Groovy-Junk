def decode = {
    it.replaceAll(/%([0-9A-Fa-f]{2,2})/) { s, code ->
      Integer.parseInt(code,16) as char
    }
}
def parse = {
    [:] + it.split(/\?/,2)[1].split(/\&/)*.split(/=/,2).sort { a,b ->
       a[0].compareTo(b[0])
    }.collect { it.collect(decode) as MapEntry }
}

def diff = { mapA, mapB, nameA = 'a', nameB = 'b' ->
    [:] + (mapA.findAll { k,v->
      mapB[k] != v 
    } + mapB.findAll { k,v->
      mapA[k] != v
    })*.key.collect { k->
      [k,[(nameA):mapA[k],(nameB):mapB[k]]] as MapEntry
    }
}

