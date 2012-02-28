package org.iglas.grails.gridfs

import com.mongodb.DBObject
import com.mongodb.BasicDBObject
import com.mongodb.Mongo
import com.mongodb.DB
import com.mongodb.gridfs.GridFS
import com.mongodb.gridfs.GridFSFile
import com.mongodb.gridfs.GridFSDBFile
import pl.burningice.plugins.image.BurningImageService
import org.iglas.grails.utils.*

import org.codehaus.groovy.grails.web.context.ServletContextHolder
import org.codehaus.groovy.grails.web.mapping.LinkGenerator
import org.codehaus.groovy.grails.web.taglib.GrailsTag
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.commons.spring.GrailsApplicationContext
import org.codehaus.groovy.grails.web.servlet.GrailsApplicationAttributes
import org.springframework.context.ApplicationContext

class UtilsService {
    // save last query total result
    static Integer total
    LinkGenerator  grailsLinkGenerator
    UtilsService(){
        ApplicationContext appCtx =  ServletContextHolder.getServletContext().getAttribute(GrailsApplicationAttributes.APPLICATION_CONTEXT)
        grailsLinkGenerator = appCtx.getBean("grailsLinkGenerator")
    }
    String getIconForFile(String filename,def params=[:]){
        getIconForFile(GridfsService.get(filename: filename),params)
    }
    public String getIconForFile(GridFSFile file,def params=[:]){
        def config = new UserConfig(GridfsService.configName).get(params)
        String iconDir = config.iconsdir.toLowerCase()
        List imagesType = config.imagestype
        Map thumbConfig = config.thumbconfig

        String prefix  = ""
        if(new File("web-app/").isDirectory())
            prefix  =  "web-app/"

        DBObject metadata = file.getMetaData()
        String idparent = metadata.idparent
        String thumbdir = thumbConfig.publicdir.toLowerCase()
        thumbdir = thumbdir.replaceAll(/\[idparent\]/,idparent)
        new File(prefix + thumbdir).mkdirs()
        String icon  = grailsLinkGenerator.resource(file: config.defaulticon)

        if(metadata?.fileExtension?.toLowerCase() in imagesType){

            if(!new File(prefix + thumbdir +"/"+metadata.originalFilename.toLowerCase()).isFile())
            {
                String tmpfile  = config.tmpdir + "/" + file.filename

                new File(prefix + config.tmpdir).mkdirs()
                GridFSDBFile fileForTmp
                fileForTmp = GridfsService.get(filename: file.filename)

                if(fileForTmp !=  null) {
                    if(!new File(prefix + tmpfile).isFile())
                        fileForTmp.writeTo(prefix + tmpfile)
                    def imageBurning =  new BurningImageService()
                    String filenameForBurn =thumbConfig.x_size +"x" + thumbConfig.y_size + metadata.originalFilename.substring(0,metadata.originalFilename.lastIndexOf('.'))
                    imageBurning.doWith(prefix + tmpfile,prefix + thumbdir)
                            .execute (filenameForBurn.toLowerCase(), {
                        it.scaleAccurate(thumbConfig.x_size, thumbConfig.y_size)
                    })

                }
            }
            icon  = grailsLinkGenerator.resource([dir:  thumbdir , file: thumbConfig.x_size +"x" + thumbConfig.y_size + metadata.originalFilename.toLowerCase()])
        }else {
            def iconFile = getFilePathForExtension(iconDir,metadata.fileExtension.toLowerCase())
            if (iconFile)
                icon = grailsLinkGenerator.resource([dir: iconDir , file: iconFile])
        }
        icon
    }
    static getFilePathForExtension(String dir,String extension){

        def iconConfigFile = new File(dir + "/iconconfig.groovy")
        if(iconConfigFile.isFile()){
            def iconConfig  = new ConfigSlurper().parse(iconConfigFile.toURL())
            if(iconConfig?.iconsOfExtension instanceof Map && iconConfig?.iconsOfExtension[extension]){
                return  iconConfig.iconsOfExtension[extension]
            }
        }
        false
    }
    static deleteIcons(GridFSFile file){
        def config = new UserConfig(GridfsService.configName).get()
        String prefix  = ""
        if(new File("web-app/").isDirectory())
            prefix  =  "web-app/"
        String thumbdir = config.thumbconfig.publicdir.toLowerCase()
        thumbdir = thumbdir.replaceAll(/\[idparent\]/,file.getMetaData().idparent)
        new File(prefix + thumbdir).eachFileMatch( ~".*${file.getMetaData().originalFilename.toLowerCase()}" ) { f ->
            f.delete()
        }
    }
}
