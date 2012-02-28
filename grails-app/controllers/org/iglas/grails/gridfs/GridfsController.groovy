package org.iglas.grails.gridfs

import com.mongodb.DBObject
import com.mongodb.Mongo
import com.mongodb.DB
import com.mongodb.gridfs.GridFS
import com.mongodb.gridfs.GridFSInputFile
import com.mongodb.BasicDBObject
import com.mongodb.gridfs.GridFSDBFile
//import org.iglas.grails.utils.FilesConfig
import org.iglas.grails.utils.*
import com.mongodb.DBCollection

class GridfsController {

    //messagesource
    def messageSource

    //defaultaction
    def defaultAction = "list"
    def index(){
        redirect(action: "help")
    }
    def list(params){
        GridfsService.list(params)
    }

    def upload(params){

        //request file

        def config = new UserConfig(GridfsService.configName).get(Gridfs.makeConfig(params))
        if(!params?.idparent){
            log.debug "Input params is bad"
            flash.message = messageSource.getMessage("mongodb-gridfs.paramsbad", [params.idparent] as Object[], request.locale)
            redirect controller: config.controllers.errorController, action: config.controllers.errorAction, id: params.id
            return
        }
        def file = request.getFile("file")
        /**************************
         check if file exists
         **************************/
        if (file.size == 0) {
            def msg = messageSource.getMessage("mongodb-gridfs.upload.nofile", null, request.locale)
            log.debug msg
            flash.message = msg
            redirect controller: config.controllers.errorController, action: config.controllers.errorAction, id: params.idparent
            return
        }

        /***********************
         check extensions
         ************************/
        def fileExtension = file.originalFilename.substring(file.originalFilename.lastIndexOf('.')+1)
        if (!config.allowedExtensions[0].equals("*")) {
            if (!config.allowedExtensions.contains(fileExtension)) {
                def msg = messageSource.getMessage("mongodb-gridfs.upload.unauthorizedExtension", [fileExtension, config.allowedExtensions] as Object[], request.locale)
                log.debug msg
                flash.message = msg
                redirect controller: config.controllers.errorController, action: config.controllers.errorAction, id: params.id
                return
            }
        }

        /*********************
         check file size
         **********************/
        if (config.maxSize) { //if maxSize config exists
            def maxSizeInKb = ((int) (config.maxSize/1024))
            if (file.size > config.maxSize) { //if filesize is bigger than allowed
                log.debug "FileUploader plugin received a file bigger than allowed. Max file size is ${maxSizeInKb} kb"
                flash.message = messageSource.getMessage("mongodb-gridfs.upload.fileBiggerThanAllowed", [maxSizeInKb] as Object[], request.locale)
                redirect controller: config.controllers.errorController, action: config.controllers.errorAction, id: params.id
                return            }
        }


        DBObject metadata = new BasicDBObject()
        Mongo mongo = new Mongo(config.db.host)
        DB db  = mongo.getDB(config.db.name)
        DBCollection col = db.getCollection(config.db.collection + ".files")
        col.ensureIndex(new BasicDBObject(config.indexes))
        GridFS gfsFiles = new GridFS(db, config.db.collection )


        String newFileName = (params.idparent + "_" + file.originalFilename).toLowerCase()
        if (params?.parentclass)
        {
            newFileName = params.parentclass + "_" + newFileName
            metadata.put("parentclass",params.parentclass)
        }

        if(!gfsFiles.findOne(newFileName)){
            GridFSInputFile gfsFile = gfsFiles.createFile(file.getInputStream(),newFileName.toLowerCase().replaceAll(/ /,""))
            // gfsFile.setFilename()

            metadata.put("idparent",params.idparent)
            metadata.put("originalFilename",file.originalFilename.toLowerCase())
            metadata.put("fileExtension",fileExtension.toLowerCase())

            if(params?.text)
                metadata.put("text",params?.text)
            if(params["accesspolitic"])
                metadata.put("access",params["accesspolitic"])
            else
                metadata.put("access","public")

            gfsFile.setMetaData(metadata)
            def accessResult = true
            def access

            if (config.accessClass && config.accessMethod){
                access = Class.forName(config.accessClass  ,true,Thread.currentThread().contextClassLoader).newInstance()
                accessResult = access."${config.accessMethod}"(gfsFile,"upload")
            }

            if (accessResult )
            {
                gfsFile.save()
                redirect controller: config.controllers.successController, action: config.controllers.successAction
            }
            else
            {

                log.debug "Access deny upload:" + access?.message
                flash.message = messageSource.getMessage("mongodb-gridfs.get.accessdeny", [access.message] as Object[], request.locale)
                redirect controller: config.accessController, action: config.accessAction
            }

        } else {
            log.debug "Filename for 'idparent'=${file.originalFilename} is busy"
            flash.message = messageSource.getMessage("mongodb-gridfs.upload.nameinbusy", [file.originalFilename] as Object[], request.locale)
            redirect controller: config.controllers.errorController, action: config.controllers.errorAction
        }


    }
    def get(params){
        if(params?.filename){
            def config = new UserConfig(GridfsService.configName).get(Gridfs.makeConfig(params))
            try {
                GridFSDBFile fileForOutput = GridfsService.get(params)

                if(fileForOutput)
                    if(fileForOutput){
                        def accessResult = true
                        if (config.accessClass && config.accessMethod ){
                            def access = Class.forName(config.accessClass  ,true,Thread.currentThread().contextClassLoader).newInstance()
                            accessResult = access."${config.accessMethod}"(fileForOutput,"get")
                        }

                        if (accessResult ){
                            response.outputStream << fileForOutput.getInputStream()
                            response.contentType = fileForOutput.getContentType()
                            return
                        }
                        else
                        {
                            log.debug "Access deny get:" + access.message
                            flash.message = messageSource.getMessage("mongodb-gridfs.get.accessdeny", [access.message] as Object[], request.locale)
                            redirect controller: config.accessController, action: config.accessAction, id: params.id
                        }


                    }
                    else
                    {
                        log.debug "File not found"
                        flash.message = messageSource.getMessage("mongodb-gridfs.get.filenotfound", [params.idparent] as Object[], request.locale)
                        redirect controller: config.controllers.errorController, action: config.controllers.errorAction, id: params.id
                    }
            } catch (e){
                println(e.message)
                def fileForOutput = false
            }

        }else{
            log.debug "Params  has errors"
            flash.message = messageSource.getMessage("mongodb-gridfs.paramsbad", [params.idparent] as Object[], request.locale)
            redirect controller: config.controllers.errorController, action: config.controllers.errorAction, id: params.id
        }

    }
    def remove(params){
        String rmFileName = params.filename
        def config = new UserConfig(GridfsService.configName).get(Gridfs.makeConfig(params))

        GridFSDBFile fileForRemove = GridfsService.get(filename:rmFileName)
        def accessResult = true
        if (config.accessClass && config.accessMethod ){
            def access = Class.forName(config.accessClass  ,true,Thread.currentThread().contextClassLoader).newInstance()
            accessResult = access."${config.accessMethod}"(fileForRemove,"remove")
        }

        if (accessResult)
        {
            GridfsService.remove(filename:rmFileName)
            redirect controller: config.controllers.successRemoveController, action: config.controllers.successRemoveAction
            return
        }
        else
        {
            log.debug "Access deny remove:" + access.message
            flash.message = messageSource.getMessage("mongodb-gridfs.get.accessdeny", [access.message] as Object[], request.locale)
            redirect controller: config.accessController, action: config.accessAction, id: params.id
        }

    }
    def help(){

    }

}
