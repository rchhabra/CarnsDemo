module.exports = function(grunt) {
  // Project configuration.
  grunt.initConfig({
    pkg: grunt.file.readJSON('package.json'),
    watch: {
        less: {
            files: ['web/webroot/WEB-INF/_ui-src/shared/less/variableMapping.less',
                    'web/webroot/WEB-INF/_ui-src/shared/less/generatedVariables.less',
                    'web/webroot/WEB-INF/_ui-src/**/themes/**/less/variables.less',
                    'web/webroot/WEB-INF/_ui-src/**/themes/**/less/theme-variables.less',
                    'web/webroot/WEB-INF/_ui-src/**/themes/**/less/style.less',
                    'web/webroot/WEB-INF/_ui-src/addons/travelacceleratorstorefront/responsive/less/travelacc.less',
                    'web/webroot/WEB-INF/_ui-src/addons/travelacceleratorstorefront/responsive/less/components.less',
                    'web/webroot/WEB-INF/_ui-src/addons/travelacceleratorstorefront/responsive/less/landing.less',
                    'web/webroot/WEB-INF/_ui-src/addons/travelacceleratorstorefront/responsive/less/booking.less',
                    'web/webroot/WEB-INF/_ui-src/addons/travelacceleratorstorefront/responsive/less/myaccount.less',
                    'web/webroot/WEB-INF/_ui-src/addons/travelacceleratorstorefront/responsive/less/checkout.less',
                    'web/webroot/WEB-INF/_ui-src/addons/travelacceleratorstorefront/responsive/less/staticpages.less',
                    'web/webroot/WEB-INF/_ui-src/responsive/lib/ybase-*/less/*'],
            tasks: ['less'],
        },
        fonts: {
            files: ['web/webroot/_ui/addons/travelacceleratorstorefront/prototype/**/fonts/*'],
            tasks: ['sync:syncfonts'],
        },
        ybasejs: {
            files: ['web/webroot/WEB-INF/_ui-src/responsive/lib/ybase-0.1.0/js/**/*.js'],
            tasks: ['sync:syncybase'],
        },
        jquery: {
            files: ['web/webroot/WEB-INF/_ui-src/responsive/lib/jquery*.js'],
            tasks: ['sync:syncjquery'],
        },
        html: {
            files: ['web/webroot/_ui/addons/travelacceleratorstorefront/prototype/*.html'],
            tasks: ['sync:prototypesynctask'],
            options: {
                // Start a live reload server for html changes
                livereload: 54321,
            },
        },
    },
    less: {
        default: {
            files: [
                {
                    expand: true,
                    cwd: 'web/webroot/',
                    src: 'WEB-INF/_ui_src/responsive/themes/alpha/less/style.less',
                    dest: 'web/webroot/_ui/addons/travelacceleratorstorefront/prototype/assets/css/',
                    ext: '.css'
                }
            ]
        },
        uisrccompile: {
            options: {
                compress: false,
                yuicompress: false,
                optimization: 2,
                sourceMap: true,
                outputSourceFiles: true,
                sourceMapFilename: "web/webroot/_ui/addons/travelacceleratorstorefront/prototype/assets/css/style.css.map",
                sourceMapURL: "style.css.map",
                sourceMapRootpath: "../../../../",
                sourceMapBasepath: "web/webroot",
                livereload: true,
                modifyVars: {
                    'theme-images-url': '"../images"'
                }
            },

            files: [
                {
                    expand: true,
                    flatten: true,
                    cwd: 'web/webroot/',
                    src: 'WEB-INF/_ui-src/responsive/themes/alpha/less/style.less',
                    dest: 'web/webroot/_ui/addons/travelacceleratorstorefront/prototype/assets/css/',
                    ext: '.css',
                }
            ],
        },
        hybris: {
             options: {
                compress: false,
                yuicompress: false,
                optimization: 2,
                sourceMap: true,
                outputSourceFiles: true,
                sourceMapFilename: "web/webroot/_ui/responsive/theme-alpha/css/style.css.map",
                sourceMapURL: "style.css.map",
                sourceMapRootpath: "../../../../",
                sourceMapBasepath: "web/webroot"
            },
            
            files: [
                {
                    expand: true,
                    cwd: 'web/webroot/WEB-INF/_ui-src/',
                    src: '**/themes/**/less/style.less',
                    dest: 'web/webroot/_ui/',
                    ext: '.css',
                    rename:function(dest,src){
                       var nsrc = src.replace(new RegExp("/themes/(.*)/less"),"/theme-$1/css");
                       return dest+nsrc;
                    }
                }
            ]
        }
    },
    
    concat: {
        css: {
            src: 'web/webroot/_ui/addons/travelacceleratorstorefront/prototype/assets/css/**',
            dest: 'web/webroot/_ui/responsive/theme-alpha/css/style.css'
          }
    },

    sync : {
        syncfonts: {
            files: [{
                expand: true,
                cwd: 'web/webroot/WEB-INF/_ui-src/',
                src: '**/themes/**/fonts/*',
                dest: 'web/webroot/_ui/',
                rename:function(dest,src){
                    var nsrc = src.replace(new RegExp("/themes/(.*)"),"/theme-$1");
                    return dest+nsrc;
             }
            }]
        },
        syncybase: {
            files: [{
                cwd: 'web/webroot/WEB-INF/_ui-src/responsive/lib/ybase-0.1.0/js/',
                src: '**/*.js',
                dest: 'web/webroot/_ui/responsive/common/js',
            }]
        },
        syncjquery: {
            files: [{
                cwd: 'web/webroot/WEB-INF/_ui-src/responsive/lib',
                src: 'jquery*.js',
                dest: 'web/webroot/_ui/responsive/common/js',
            }]
        },
        synctheme: {
            files: [{
                cwd: 'web/webroot/WEB-INF/_ui-src/responsive/themes/travelacc/assets',
                src: ['images/**','fonts/**'],
                dest: 'web/webroot/_ui/responsive/theme-alpha',
            },{
                cwd: 'web/webroot/WEB-INF/_ui-src/responsive/themes/travelacc/assets',
                src: 'js/**',
                dest: 'web/webroot/_ui/responsive/common',
            }]
        },
        prototypesyncybase: {
            files: [{
                cwd: 'web/webroot/WEB-INF/_ui-src/responsive/lib/ybase-0.1.0/js/',
                src: '**/*.js',
                dest: 'web/webroot/_ui/responsive/common/js',
            }]
        },
        prototypesyncjquery: {
            files: [{
                cwd: 'web/webroot/WEB-INF/_ui-src/responsive/lib',
                src: 'jquery*.js',
                dest: 'web/webroot/_ui/responsive/common/js',
            }]
        },
        prototypesyncall: {
            files: [{
                cwd: 'web/webroot/WEB-INF/_ui-src/responsive/themes/travelacc',
                src: ['assets/fonts/**','assets/images/**','**.html','assets/js/**'],
                dest: 'web/webroot/prototype/',
            }]
        },
        prototypesynctask: {
            files: [{
                cwd: 'web/webroot/_ui/addons/travelacceleratorstorefront/prototype/**',
                src: '**',
                dest: 'ext-travel/travelacceleratorstorefront/acceleratoraddon/web/webroot/_ui/prototype/',
                verbose: true, // Display log messages when copying files
                // pretend: true, // Don't do any disk operations - just write log. Default: false 
                // updateAndDelete: true, // Remove all files from dest that are not found in src. Default: false
            }]
        }
    },

    serve: {
        options: {
            port: 9000,
             'serve': {
                 'path': 'web/webroot/_ui/addons/travelacceleratorstorefront/prototype'
            }
        }
    }
    
});
 
  // Plugins
  grunt.loadNpmTasks('grunt-contrib-watch');
  grunt.loadNpmTasks('grunt-contrib-less');
  grunt.loadNpmTasks('grunt-contrib-concat');
  grunt.loadNpmTasks('grunt-contrib-uglify');
  grunt.loadNpmTasks('grunt-contrib-cssmin');
  grunt.loadNpmTasks('grunt-sync');
  grunt.loadNpmTasks('grunt-serve');


  // Default task(s).
  grunt.registerTask('default', ['less', 'sync']);

   // Compile LESS in _ui-src task(s).
  grunt.registerTask('uisrccompile', ['less:uisrccompile','serve','watch:less']);

  // Compile LESS in Hybris directory.
  grunt.registerTask('hybris', ['less:hybris','watch:less']);

  // Compile and serve HTML files in Prototype directory.
  //grunt.registerTask('')
  
  // UI Dev task(s).
  grunt.registerTask('prototype', ['sync:prototypesyncall']);
  
  // UI task to Copy Prototype changes across
  grunt.registerTask('html', ['sync:prototypesynctask']);


  // Prototype theme task(s).
  grunt.registerTask('prototypesynctheme', ['less:uisrccompile','concat:css']);

  

};