<%--
  Created by IntelliJ IDEA.
  User: devel
  Date: 24.02.12
  Time: 20:02
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main">
    <title>Test</title>
</head>
<body>
<g:if test="${flash.message}">
    <div class="message" role="status">${flash.message}</div>
</g:if>
<pre>

    <big>Adding the setting you're file path/to/app/grails-app/config/Config.groovy</big>

    // host for mongodb
    gridfsConfig.db.host = "localhost"
    // db name
    gridfsConfig.db.name = "myGridfs"
    // collections for files
    gridfsConfig.db.collection = "files"

    // dir for icons
    gridfsConfig.iconsdir = "images/icons"
    // extensions  images for  thumbnail list
    gridfsConfig.imagestype = ["jpg","gif","png","bmp"]
    // allowedExtensions
    gridfsConfig.allowedExtensions = ["jpg","gif","png","bmp","doc","rtf","zip"]
    // tmp dir
    gridfsConfig.tmpdir = "tmp/files"
    // icon default
    gridfsConfig.defaulticon = "images/icons/empty.png"
    // file  iconconfig.groovy in images dir
    // format Extension : filename
    // Example:
    //iconsOfExtension = [
    //        pdf:"pdf.png",
    //        doc:"word.png"]

    //template for dirs  users thumbnails files
    gridfsConfig.thumbconfig = [
    publicdir:"tmp/imagesthumb/[idparent]",
    x_size:128,
    y_size:128
    ]

    // max bites size
    gridfsConfig.maxSize = 2000000

    // default errors controller
    gridfsConfig.controllers.errorController = "gridfs"
    gridfsConfig.controllers.errorAction  = "help"

    // default controller for the success remove
    gridfsConfig.controllers.successRemoveController = "gridfs"
    gridfsConfig.controllers.successRemoveAction = "help"

    // default controller for the success upload
    gridfsConfig.controllers.successController =  "gridfs"
    gridfsConfig.controllers.successAction  =  "help"

    // controller for the access deny
    gridfsConfig.accessController =  "gridfs"
    gridfsConfig.accesssAction  =  "help"

    //  class name  for access control
    gridfsConfig.accessClass  =  "org.iglas.grails.utils.ExampleAccess"
    //  method name  for access control
    gridfsConfig.accessMethod  =  "check"

    gridfsConfig.indexes  = [
    "metadata.idparent":1,
    "metadata.parentclass":1]


    <big>  Exsamle controler:</big>

    class TestController {

    def index() {
    UserConfig gridfsConfig = new org.iglas.grails.utils.UserConfig("gridfsConfig")
    gridfsConfig.set([controllers:[successController: "test",successAction:"index"]])
    def list =  new org.iglas.grails.gridfs.GridfsService().list([idparent: "myid",relative:true,icon:true])
    render(view:"index", model:[listFiles: list ])
        }
    }
    <big> Example gsp:</big>
   <pre>
       <gridfs:form idparent="myid" parentclass="user"
                    errorAction="index" errorController="test"
                    successAction="index" successController="test">
           Text:    <g:textField name="text" />     <br />
           Access: <g:select name="accesspolitic" from="['public','private','hidden']" value="public"  />  <br />
       </gridfs:form >
       <g:each in="${listFiles}">

           name -> ${it.filename} <br />
           text-> ${it.metadata?.text} <br />
           <br />
           formatting size       <br />
           <gridfs:prettysize size="${it.size}" />  <br />
           <br />
           <br />
           get icon file path in list (invoke params icon=true)    <br />
           <img src="${it.iconUrl}" />           <br />
           <br />
           <br />
           get link for download file
           <gridfs:download filename="${it.filename}">${it.metadata.originalFilename}</gridfs:download>    <br />
           <br />
           <br />
           get icon custom size    <br />

           <gridfs:getIcon filename="${it.filename}" title="myicon 150x200" x="150" y="200" />    <br />
           <gridfs:getIcon filename="${it.filename}" title="myicon 50x50" x="50" y="50" />    <br />
           <br />
           <br />
           // get link for file       <br />
           <img src="${gridfs.createLink(filename:it.filename)}" width="200">      <br />
           <br />
           <br />
           <br />
           // get link for remove file         <br />
           <gridfs:remove filename="${it.filename}">remove</gridfs:remove>     <br />
           <br />
           <br />
           //using custom errorControllers action              <br />
           <gridfs:remove filename="${it.filename}"  errorAction="index" errorController="test">remove custom Controler</gridfs:remove>   <br />
           <hr />

       </g:each>

    </pre>
</body>
</html>