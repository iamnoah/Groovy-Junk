class Bin {
    Bin(Bin bin) { this.size = bin.size }
    Bin(Comparable size) { this.size = size }
    final Comparable size    
    final List contents = []
    def getAt(int index) { contents[index] }
    boolean leftShift(item) { 
        (size == null || (contents.sum() ?: 0) + item <= size) && contents.add(item)
    }
    String toString() { "{${size ?: '\u221E'}}->$contents (${contents.sum()})" }
}

def pack( list,Iterable<Bin> bins ) {
    list.sort().reverse().each { item ->        
        bins.find { it << item }
    }
    bins
}

/**
 * The last bin the result holds all the items that didn't fit
 */
def packInto(list,binSizes) {
    pack(list, binSizes.collect { new Bin(it) } + new Bin(null))
}


def packAll(list,size) {
    pack(list,new InfBins(size))
}

class InfBins implements Iterable<Bin> {
    InfBins(Comparable size) { this.size = size }
    Comparable size   
    List bins = []
    int size() {
        bins.size()
    }
    Iterator iterator() {
        def i = 0
        [
            hasNext: {true},
            next: { 
                if(++i > bins.size()) {
                    bins << new Bin(size) 
                }               
                bins[i-1]
            }
        ] as Iterator
    }
    
    String toString() { bins.toString() }
}

def parseList(String s) {
    def list = []
    s.replaceAll(/\s*(\d+(?:\.\d+)?)(?:\s*[xX*]\s*(\d+))?\s*(?:[+, ]|$)/) { str, len, count ->
        list.addAll( [len.toDouble()] * (count?.toInteger() ?: 1) )
    }
    list
}


packIt = { list, bins ->
   packInto(list,bins).with {
        (it.findAll{ it.size && it.contents.sum()} + packAll(it[-1].contents,fill.value).bins)
   }
}

getCutList = { result ->
    result.collect {
        "${it.size} -> ${it.contents.join(' | ')} <${it.size-it.contents.sum()}>"
    }.join('\n')
}

getSummaryText = { result ->
   result.groupBy { it.size }.collect {
        "${it.key} x ${it.value.size()}"
   }.join('\n')
}

def sb = new groovy.swing.SwingBuilder()
calculate = {
    try {
        def result = packIt(parseList(list.text.trim()),parseList(bins.text.trim()))
        summary.text = getSummaryText(result)
        cuts.text = getCutList(result)
    } catch(e) {
        e.printStackTrace(System.out)
    }
}

//*
def frame = sb.frame(title:'Lumber Calculator',size:[400,600],pack:true,show:true,
    defaultCloseOperation: javax.swing.JFrame.EXIT_ON_CLOSE) {
  panel(border: javax.swing.BorderFactory.createEmptyBorder(0,10,10,10)) {
    vbox() {
        hbox() { label('List wood like so: length1*quantity1,len2*quantity2,...') }
        panel() {
            gridLayout(cols:2,rows:3)
            label('Wood you have')
            bins = textField(actionPerformed:calculate)
            
            label('Cuts you need')
            list = textField(actionPerformed:calculate)
            
            label("Length of stock you'll buy")
            fill = spinner(model:spinnerNumberModel(value:96.0d))
        }    
        vstrut()
        hbox() {
            glue()
            button(text:'Calculate',actionPerformed:calculate)
            hstrut()
            hstrut()
        }
 
        hbox() {
            label("Stock Used")
            glue()
        }
        scrollPane() {
            summary = textArea(rows:5)            
        }
        vstrut()       
        hbox() {
            label("Cutting List")
            glue()
        }
        scrollPane() {
            cuts = textArea(rows:5)            
        }
    }    
  }
}
//*/