/**
 * Provides the set of keywords from a MM template file, plus the set of domains
 * and sends it to the substitution phase.
 * @user jens
 * @date 2010-feb-20 12:57:57
 */

import java.util.jar.JarFile

File emDir = new File('.').absoluteFile
File mmDir = new File(emDir, 'config/modules')

String templateFilename = params.template
if (!templateFilename) {
    response.sendError 404, "Template name missing. ${request.requestURL}?template=name-template.jar"
    return
}

File templateFile = new File(mmDir, templateFilename)
if (!templateFile.canRead()) {
    response.sendError 404, "Template MM file '${templateFilename}' not found/readable"
    return
}

// --- Get keywords ---
JarFile mm = new JarFile(templateFile)
InputStream mmIS = mm.getInputStream(mm.getEntry('ManagementModule.xml'))
Set keys = new TreeSet()
mmIS.eachLine {line ->
    (line =~ /@(\w+)@/).each {all, key -> keys.add(key)}
}

// --- Get domains ---
def domainsFile = new File(emDir, 'config/domains.xml')
if (!domainsFile.canRead()) {
    response.sendError 404, "Domains XML file '${domainsFile}' not found/readable"
    return
}
def domainsXML = new XmlSlurper().parse(domainsFile)


String titleTxt = 'Create New ManagementModule'
html.html {
    head {
        title titleTxt
//        style(type: 'text/css', '@import "style.css"')
        link( rel:'stylesheet', href:'style.css')
    }
    body {
        span ''
        include 'header.html'
        div(id: 'content') {
            h1 titleTxt
            div(id: 'message', request.message ?: '')

            div(id: 'form') {
                form(method: 'post', action: 'substitute.groovy') {
                    input(type: 'hidden', name: 'template', value: templateFilename)

                    fieldset {
                        legend 'ManagementModule Name'
                        i 'MM Filename'
                        input(type: 'text', name: 'filename', value: params.filename ?: '', size: 30)
                        span(style: 'font-style:italic;', '.jar')
                    }

                    fieldset {
                        legend 'Target Domain'
                        i 'Choose'
                        select(name: 'domain', size: 1) {
                            option(value: 'none', 'None - SuperDomain')
                            domainsXML.domain.each {d ->
                                option(value: d.@name, "${d.@name} - ${d.@description}")
                            }
                        }
                    }

                    fieldset {
                        legend 'Keywords'
                        i('Based on ' + templateFilename)
                        table {
                            keys.each {key ->
                                tr {
                                    th(key)
                                    td {
                                        input(type: 'text', name: "TOKEN_$key", value: params['TOKEN_' + key] ?: '', size: 35)
                                    }
                                }
                            }

                        }
                    }

                    input(type: 'submit', name: 'action', value: 'CREATE')
                }
            }
        }
        include 'footer.html'
    }
}
