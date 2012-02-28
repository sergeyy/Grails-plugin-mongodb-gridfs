class MongodbGridfsGrailsPlugin {

        // the plugin version
        def version = "0.4.beta"
        // the version or versions of Grails the plugin is designed for
        def grailsVersion = "2.0 > *"
        // the other plugins this plugin depends on
        def dependsOn = [
                'mongodb': "* > 1.0.0.RC3",
                'burning-image': "* > 0.5.0"
        ]
        // resources that are excluded from plugin packaging
        def pluginExcludes = [
                "grails-app/views/error.gsp"
        ]

        // TODO Fill in these fields
        def title = "Mongodb Gridfs Plugin" // Headline display name of the plugin
        def author = "Sergei Yunzhakov"
        def authorEmail = "iglas.sys@@gmail.com"
        def description = '''\
                 The plugin includes can:
                -- listing  files
                -- upload files
                -- download  files
                -- remove  files
                -- grouped files for a parents id
                -- support  multi configure for level  a servletContext,   a user session,  a requests or  params
                -- support  multi database
                -- connect a control access
                -- get of a user dir  icon for  a extension file
                -- makes a thumbnails for images

                Plugins in use:
                -- mongodb
                -- burning-image
                '''

        // URL to the plugin's documentation
        def documentation = "https://github.com/sergeyy/Grails-plugin-mongodb-gridfs/wiki"

        // Extra (optional) plugin metadata

        // License: one of 'APACHE', 'GPL2', 'GPL3'
    def license = "APACHE"

        // Details of company behind the plugin (if there is one)
//    def organization = [ name: "My Company", url: "http://www.my-company.com/" ]

        // Any additional developers beyond the author specified above.
   def developers = [ [ name: "Sergei Yunzhakov", email: "iglas.sys@@gmail.com" ]]

        // Location of the plugin's issue tracker.
//    def issueManagement = [ system: "JIRA", url: "http://jira.grails.org/browse/GPMYPLUGIN" ]

        // Online location of the plugin's browseable source code.
//    def scm = [ url: "http://svn.grails-plugins.codehaus.org/browse/grails-plugins/" ]

        def doWithWebDescriptor = { xml ->
            // TODO Implement additions to web.xml (optional), this event occurs before
        }

        def doWithSpring = {
            // TODO Implement runtime spring config (optional)
        }

        def doWithDynamicMethods = { ctx ->
            // TODO Implement registering dynamic methods to classes (optional)
        }

        def doWithApplicationContext = { applicationContext ->
            // TODO Implement post initialization spring config (optional)
        }

        def onChange = { event ->
            // TODO Implement code that is executed when any artefact that this plugin is
            // watching is modified and reloaded. The event contains: event.source,
            // event.application, event.manager, event.ctx, and event.plugin.
        }

        def onConfigChange = { event ->
            // TODO Implement code that is executed when the project configuration changes.
            // The event is the same as for 'onChange'.
        }

        def onShutdown = { event ->
            // TODO Implement code that is executed when the application shuts down (optional)
        }
    }
