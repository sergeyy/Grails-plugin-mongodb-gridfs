package org.iglas.grails.utils

import org.iglas.grails.gridfs.UtilsService
import org.iglas.grails.gridfs.GridfsService

/**
 * Created by IntelliJ IDEA.
 * User: devel
 * Date: 28.02.12
 * Time: 21:04
 * To change this template use File | Settings | File Templates.
 */
class Gridfs {
    public static makeConfig(params){
        def config = new UserConfig(GridfsService.configName).get()
        def newConfig = [controllers:[:]]

        config.controllers.each { key ->
            if(params?."${key}")
            newConfig.controllers[key] = params."${key}"
        }
        newConfig
    }
}
