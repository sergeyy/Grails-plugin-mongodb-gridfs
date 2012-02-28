package org.iglas.grails.utils


class ExampleAccess {
    public String message
    def check(def file, String action){
        switch(action)  {
            case "get":
                message = "get"
                //You code
                return true
            case "remove":
                message = "remove"
                //You code
                return true
            case "upload":
                message = "upload"
                //You code
                return true
            default:
                return false
        }

    }

}
