
String titleTxt = 'Help Instructions'
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
            include 'help.html'
        }
        include 'footer.html'
    }
}
