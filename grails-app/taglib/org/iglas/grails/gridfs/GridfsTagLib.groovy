package org.iglas.grails.gridfs

import org.codehaus.groovy.grails.web.taglib.exceptions.GrailsTagException
import org.iglas.grails.utils.*

class GridfsTagLib {


    static namespace = 'gridfs'

    static Long _byte  = 1
    static Long _kbyte = 1	*	1000
    static Long _mbyte = 1 	* 	1000	*	1024
    static Long _gbyte = 1	*	1000	*	1024	*	1024
    def getIcon = { attrs ->
        Integer x = attrs.x.toInteger()
        Integer y = attrs.y.toInteger()

        out << '<img src="' + new UtilsService().getIconForFile(attrs.filename,[thumbconfig:[x_size:x,y_size:y]])
        if (attrs?.title)
            out << ' title="${attrs?.title}" '
        out << '" />'
    }
    def createLink = { attrs ->
        out << g.createLink(controller: "gridfs",action:"get" ,params: [filename:attrs.filename] )
    }
    def remove  = {  attrs, body ->
        def config = new UserConfig(GridfsService.configName).get(attrs)
        params.errorAction = attrs?.errorAction? attrs.errorAction : config.controllers.errorAction
        params.errorController = attrs?.errorController? attrs.errorController : config.controllers.errorController
        params.filename = attrs.filename
        if(attrs.filename)
            out << g.link([controller: "gridfs", action: "remove", params: params], body)
        else
        {
            def errorMsg =  "'filename'  attribute not found in file-uploader form tag."
            log.error (errorMsg)
            throw new GrailsTagException(errorMsg)
        }
    }
    def download = { attrs, body ->
        def config = new UserConfig(GridfsService.configName).get(attrs)
        params.errorAction = attrs?.errorAction? attrs.errorAction : config.controllers.errorAction
        params.errorController = attrs?.errorController? attrs.errorController : config.controllers.errorController
        params.filename = attrs.filename

        if(attrs.filename)
            out << g.link([controller: "gridfs", action: "get", params: params], body)
        else
        {
            def errorMsg =  "'filename'  attribute not found in file-uploader form tag."
            log.error (errorMsg)
            throw new GrailsTagException(errorMsg)
        }
    }

    def form = { attrs, body ->
        def config = new UserConfig(GridfsService.configName).get()
        //checking required fields
        if (!attrs.idparent) {
            def errorMsg = "'idparent' attribute not found in file-uploader form tag."
            log.error (errorMsg)
            throw new GrailsTagException(errorMsg)
        }
        //idparent
        def idparent = attrs.idparent

        //case success
        def successAction = attrs.successAction? attrs.successAction : config.controllers.successAction
        def successController = attrs.successController? attrs.successController : config.controllers.successController

        //case error
        def errorAction = attrs.errorAction? attrs.errorAction :config.controllers.errorAction
        def errorController =  attrs.errorController? attrs.errorController : config.controllers.errorController

        def tagBody = new StringBuilder()
        tagBody.append(body())
        tagBody.append("<input type='hidden' name='idparent' value='${idparent}' />")
        if(attrs?.parentclass)
            tagBody.append("<input type='hidden' name='parentclass' value='${attrs.parentclass}' />")
        if(attrs?.accesspolitic)
            tagBody.append(g.select([name:"accesspolitic",from:attrs.accesspolitic,value:"public" ]) + "<br />")

        tagBody.append("<input type='hidden' name='errorAction' value='${errorAction}' />")
        tagBody.append("<input type='hidden' name='errorController' value='${errorController}' />")
        tagBody.append("<input type='hidden' name='successAction' value='${successAction}' />")
        tagBody.append("<input type='hidden' name='successController' value='${successController}' />")

        tagBody.append("""
                            <input type='file' name='file' />
                            <input type='submit' name='submit' value='Submit' />
                            """ )

        //form build
        StringBuilder sb = new StringBuilder()
        sb.append g.uploadForm([controller: 'gridfs', action: 'upload'], tagBody)

        out << sb.toString()
    }

    def prettysize = { attrs ->

        if (!attrs.size) {
            def errorMsg = "'size' attribute not found in file-uploader preetysize tag."
            log.error (errorMsg)
            throw new GrailsTagException(errorMsg)
        }

        def size = attrs.size
        def sb = new StringBuilder()
        if (size >= _byte && size < _kbyte) {
            sb.append(size).append("b")
        } else if (size >= _kbyte && size < _mbyte) {
            size = size / _kbyte
            sb.append(size).append("kb")
        } else if (size >= _mbyte && size < _gbyte) {
            size = size / _mbyte
            sb.append(size).append("mb")
        } else if (size >= _gbyte) {
            size = size / _gbyte
            sb.append(size).append("gb")
        }
        out << sb.toString()
    }
}
/*
(0 - 1000) size = bytes
(1000 - 1000*1024) size / 1000 = kbytes
(1000*1024 - 1000*1024*1024) size / (1000 * 1024) = mbytes
(else) size / (1000 * 1024 * 1024) = gbytes
*/


