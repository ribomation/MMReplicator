/**
 * Scans the directory och MMs and provides a list of template MMs to choose.
 * @user jens
 * @date 2010-feb-20 11:07:10
 */

File emDir = new File('.').absoluteFile
File mmDir = new File(emDir, 'config/modules')
def templates = []
mmDir.eachFileMatch(~/.+-template\.jar/) {templates << it}

String titleTxt = 'Choose ManagementModule Template'
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
            h2 titleTxt
            if (templates) {
                ul {
                    templates.each {templFile ->
                        li {
                            a(href: "create.groovy?template=$templFile.name", templFile.name)
                        }
                    }
                }
            } else {
                p("No template MM JAR files found. Please, ensure the file name ends in '-template.jar' and is located in the EM/config/modules directory.")
            }
        }
        include 'footer.html'
    }
}
