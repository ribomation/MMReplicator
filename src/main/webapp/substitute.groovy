/**
 * Performs the actual MM substitution work, based on the supplied parameters.
 * This action, should be possible to invoke directly from a curl based shell script.
 * @user jens
 * @date 2010-feb-20 14:10:07
 */

import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.jar.JarEntry
import java.util.jar.Manifest

File emDir = new File('.').absoluteFile
File mmDir = new File(emDir, 'config/modules')
File deployDir = new File(emDir, 'deploy')

//System.out.println 'PARAMS: ' + params
// ------------------------------------------------------------------
// --- Validate params ---
String templateFilename = params.template
if (!templateFilename) {
    response.sendError 404, "Template name missing"
    return
}

File templateFile = new File(mmDir, templateFilename)
if (!templateFile.canRead()) {
    response.sendError 404, "Template MM file '${templateFilename}' not found/readable"
    return
}

String domain = params.domain
if (!domain) {
    response.sendError 404, "Missing domain param"
    return
}

String mmFilename = params.filename
if (!mmFilename) {
    response.sendError 404, "MM filename missing"
    return
}

File mmFile = new File(mmDir, (domain == 'none' ? '' : domain + '/') + mmFilename + '.jar')
if (mmFile.exists()) {
    response.sendError 404, "Target MM file '${mmFile.absolutePath}' already exists"
    return
}

if (params.action != 'CREATE' && params.action != 'GENERATE' ) {
    response.sendError 404, "Missing or invalid action. Got '${params.action}'"
    return
}

// ------------------------------------------------------------------
// --- Collect substitution keywords from params ---
def keywords = [:]
params.each {name, value ->
    def m = (name =~ /^TOKEN_(\w+)/)
    if (m.matches()) keywords[m[0][1]] = value
}

// --- Perform the substitution work ---
JarFile mm = new JarFile(templateFile)
InputStream mmIS = mm.getInputStream(mm.getEntry('ManagementModule.xml'))
StringBuilder mmXML = new StringBuilder()
mmIS.eachLine {line ->
    (line =~ /@(\w+)@/).each {all, key ->
        if (keywords[key]) {
            line = line.replaceAll(/@$key@/, keywords[key])
        }
    }
    mmXML.append(line).append("\n")
}

// ------------------------------------------------------------------
// --- Create the new MM jar file ---
mmFile.parentFile.mkdirs()
if (domain != 'none') {
    deployDir = new File(deployDir, domain)
}
deployDir.mkdirs()
mmFile = new File(deployDir, mmFilename + '.jar')

byte[] xmlData = mmXML.toString()
JarOutputStream mmJar = new JarOutputStream(new FileOutputStream(mmFile), new Manifest())
JarEntry xmlEntry = new JarEntry('ManagementModule.xml')
xmlEntry.size = xmlData.length
mmJar.putNextEntry(xmlEntry)
mmJar.write(xmlData, 0, xmlData.length)
mmJar.close()
String message = mmFile.canonicalPath

if (params.action == 'GENERATE') {
    response.contentType = 'text/plain'
    out << 'Created ' + message
} else {
    String titleTxt = 'ManagementModule Created'
    html.html {
        head {
            title titleTxt
//            style(type: 'text/css', '@import "style.css"')
            link( rel:'stylesheet', href:'style.css')
        }
        body {
            span ''
            include 'header.html'
            div(id: 'content') {
                h1 titleTxt
                p("Deployed MM file")
                p(id: 'message', message)
            }
            include 'footer.html'
        }
    }
}
