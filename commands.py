# Here you can create play commands that are specific to the module
import os, os.path
import sys
import getopt
import subprocess

try:
    from play.utils import package_as_war
    PLAY10 = False
except ImportError:
    PLAY10 = True

MODULE = 'i18ned'

COMMANDS = ['i18ned:export', 'i18ned:import', 'i18ned:pimport', 'i18ned:sql', 'i18ned:check']

def execute(**kargs):
    command = kargs.get("command")
    app = kargs.get("app")
    args = kargs.get("args")
    for arg in args:
        print arg
    env = kargs.get("env")

    print "executing command: " + command 
    if command == 'i18ned:export':
        run(app, args, 'Exporter')
    if command == 'i18ned:sql':
        run(app, args, 'SqlExporter')
    if command == 'i18ned:pimport':
        run(app, args, 'PrefixedImporter')
    if command == 'i18ned:import':
        run(app, args, 'Importer')
    if command == 'i18ned:check':
        run(app, args, 'Checker')
def run(app, args, class_name):
    app.check()
    java_cmd = app.java_cmd(['-Xmx64m'], className='play.modules.i18ned.'+class_name, args=args)
    subprocess.call(java_cmd, env=os.environ)

    print
if PLAY10:
    if play_command == 'i18ned:export':
        try:
            print "~ Generating controller and views from entities"
            print "~ "
            check_application()
            load_modules()
            do_classpath()
            try:
                # This is the new style to get the extra arg
                do_java('play.modules.i18ned.Exporter', sys.argv)
            except Exception:
                # For play! < 1.0.3
                do_java('play.modules.i18ned.Exporter')
            subprocess.call(java_cmd, env=os.environ)
            sys.exit(0)
                    
        except getopt.GetoptError, err:
            print "~ Failed to generate scaffold properly..."
            print "~ %s" % str(err)
            print "~ "
            sys.exit(-1)
            
        sys.exit(0)    