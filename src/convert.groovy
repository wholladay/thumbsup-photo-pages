@Grab(group = 'org.ccil.cowan.tagsoup', module = 'tagsoup', version = '1.2')
@Grapes(@Grab(group = 'xmlunit', module = 'xmlunit', version = '1.3'))
import groovy.xml.StreamingMarkupBuilder
import groovy.xml.XmlUtil
import org.custommonkey.xmlunit.*

def oldConvertFile = { String fileName ->

    def tagsoupParser = new org.ccil.cowan.tagsoup.Parser()
    def file = new File(fileName)
    def page = new XmlSlurper(tagsoupParser).parseText(file.text)
    def names = ['2002', '2003', '2004', '2005', '2006', '2007', '2008', '2009', '2010', '2011', '2012', '2013', '2014', '2015', '2016', '2017', 'Historic', 'Scouts', 'Webelos']

    println "Converting: ${file.name}..."

    page.body.nav.replaceNode { node ->
        nav() {
            names.collect { name ->
                li() {
                    a(href: "${name}.html", "${name}")
                }
            }
        }
    }

    page.body.header.a.replaceNode {}
    page.body.header.h1.replaceNode {
        h1() {
            a(href: 'index.html', 'Holladay Album')
        }
    }
    page.body.header.h2.replaceNode {
        h2(file.name - '.html')
    }

//    def newHtml = XmlUtil.serialize(new StreamingMarkupBuilder().bind {
    def newHtml = (new StreamingMarkupBuilder().bind {
        namespaces << ["": "http://www.w3.org/1999/xhtml"]
        mkp.yield page
    })
    file.text = newHtml
}

def convertFile = { String fileName ->
    def file = new File(fileName)
    def contents = ''
    def skippingNav = false
    def h1 = '        <h1>Holladay Album</h1>\n'
    def h2 = "        <h2>${file.name - '.html'}</h2>\n"
    def nav = '''
    <nav>
      <li><a href='2002.html'>2002</a></li>
      <li><a href='2003.html'>2003</a></li>
      <li><a href='2004.html'>2004</a></li>
      <li><a href='2005.html'>2005</a></li>
      <li><a href='2006.html'>2006</a></li>
      <li><a href='2007.html'>2007</a></li>
      <li><a href='2008.html'>2008</a></li>
      <li><a href='2009.html'>2009</a></li>
      <li><a href='2010.html'>2010</a></li>
      <li><a href='2011.html'>2011</a></li>
      <li><a href='2012.html'>2012</a></li>
      <li><a href='2013.html'>2013</a></li>
      <li><a href='2014.html'>2014</a></li>
      <li><a href='2015.html'>2015</a></li>
      <li><a href='2016.html'>2016</a></li>
      <li><a href='2017.html'>2017</a></li>
      <li><a href='Historic.html'>Historic</a></li>
      <li><a href='Scouts.html'>Scouts</a></li>
      <li><a href='Webelos.html'>Webelos</a></li>
    </nav>
    '''
    println "converting: ${fileName}"

    file.eachLine { line ->
        if (line.contains('h1')) {
            contents += h1
        } else if (line.contains('h2')) {
            contents += h2
        } else if (line.contains('<nav>')) {
            skippingNav = true
        } else if (skippingNav) {
            if (line.contains('</nav>')) {
                skippingNav = false
                contents += nav
            }
        } else {
            contents += line + '\n'
        }
    }

    file.text = contents
}

def dumpFile = { String fileName ->
    def tagsoupParser = new org.ccil.cowan.tagsoup.Parser()
    def file = new File(fileName)
    def page = new XmlSlurper(tagsoupParser).parseText(file.text)

//    def newHtml = XmlUtil.serialize(new StreamingMarkupBuilder(expandEmptyElements:true).bind {
    def newHtml = (new StreamingMarkupBuilder(expandEmptyElements:true).bind {
        namespaces << ["": "http://www.w3.org/1999/xhtml"]
        mkp.yield page
    })
    println newHtml
}

def fileNames = new FileNameFinder().getFileNames('.', '*.html', 'index*.html')
//def fileNames = ["/Users/wholladay/dev/tmp/2008-2008_12.html"]
fileNames.each { fileName ->
//    dumpFile(fileName)
    convertFile(fileName)
}
