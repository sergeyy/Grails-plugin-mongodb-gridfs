
package org.iglas.grails.utils


import org.codehaus.groovy.grails.commons.ConfigurationHolder
import sun.org.mozilla.javascript.internal.Undefined
import org.codehaus.groovy.grails.web.context.ServletContextHolder
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsWebRequest
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.RequestAttributes

/**
 * Created by IntelliJ IDEA.
 * User: devel
 * Date: 24.02.12
 * Time: 20:18
 * To change this template use File | Settings | File Templates.
 */
class UserConfig {
    public static String defaultNameConfig = "gridfsConfig"
    public String nameConfig
    public  String defaultLevel
    public UserConfig(){
        nameConfig = defaultNameConfig
        preset()
    }
    public UserConfig(String name){
        nameConfig = name
        preset()
    }
    public preset(Boolean reset = false)
    {
        RequestAttributes request = RequestContextHolder.currentRequestAttributes()
        if(!request.getAttribute(nameConfig,1) || reset)
            request.setAttribute(nameConfig,[:],1)
        if(!request.session.getAttribute(nameConfig) || reset)
            request.session.setAttribute(nameConfig,[:])
        if(!ServletContextHolder.getServletContext().getAttribute(nameConfig) || reset)
            ServletContextHolder.getServletContext().setAttribute(nameConfig,ConfigurationHolder.config."${nameConfig}")
    }
    public  get(def params = [:],String level="request"){
        def config = [:]

        RequestAttributes request = RequestContextHolder.currentRequestAttributes()

        switch (level){
            case "request":
                config = merge(ServletContextHolder.getServletContext().getAttribute(nameConfig),
                        request.session.getAttribute(nameConfig))
                config = merge(config,request.getAttribute(nameConfig,1))
                break
            case "session":
                config = merge(ServletContextHolder.getServletContext().getAttribute(nameConfig),
                        request.session.getAttribute(nameConfig))
                break
            case "servlet" :
                config = ServletContextHolder.getServletContext().getAttribute(nameConfig)
                break
            default:
                config = merge(ServletContextHolder.getServletContext().getAttribute(nameConfig),
                        request.session.getAttribute(nameConfig))
                config = merge(config,request.getAttribute(nameConfig,1))
        }
        if(params)
            config =  merge(config,params)
//       println("1" + ServletContextHolder.getServletContext().getAttribute(nameConfig))
//        println("2" + request.session.getAttribute(nameConfig))
 //       println("3" + request.getAttribute(nameConfig,1))
 //       println("4" + config)
        config
    }
    public  set(Map  config  = [:],String level="request"){
        RequestAttributes request = RequestContextHolder.currentRequestAttributes()
        switch (level){
            case "request":
                request.setAttribute(nameConfig,config,1)
                break
            case "session":
                request.session.setAttribute(nameConfig,config)
                break
            case "servlet" :
                if(ServletContextHolder.getServletContext().getAttribute(nameConfig))
                    ServletContextHolder.getServletContext().setAttribute(nameConfig,
                            merge(ServletContextHolder.getServletContext().getAttribute(nameConfig),config))
                else
                    ServletContextHolder.getServletContext().setAttribute(nameConfig,merge(ServletContextHolder.getServletContext().getAttribute(nameConfig),config))
                break
            default:
                request.setAttribute(nameConfig,config,1)
        }

    }

    public static merge(def inConfig, def other) {
        def config

        if(inConfig && other &&
                inConfig instanceof Map &&
                other instanceof Map)
        {

            config = inConfig.clone()

            other.each{String key, entry ->
                def configEntry = config[key]

                if(config?."${key}")
                {

                    if(configEntry instanceof Map && configEntry.size() > 0 && entry instanceof Map) {
                        // recurse
                        config[key] = UserConfig.merge(configEntry, entry)
                    }
                    else {

                        config[key] = entry
                    }
                }
            }
        }else{
            config = inConfig
        }

        config

    }
}
