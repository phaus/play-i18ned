play-i18ned
===========

This Module provides support for converting Play! i18n Files into an Excel Sheet and from an Excel Sheet to i18n Files.


## Usage

You need to create the message files first (e.g. conf/messages, conf/messages.de, conf/messages.en)
You may enter some key/value entries to the Files.

The prefered format is:

    # Description
    key=value
    
Add the Module to your dependencies:

    require:
    ...
        - local -> i18ned 0.1
    ...
    repositories:
        - local:
            type: local
            artifact: ${application.path}/../modules/[module]/dist/[module]-[revision].zip
            contains:
              - local -> *

Then install the Module

    play deps --sync
    
## Export (creating Excel File)

    play i18ned:export
    
Your will find the Excel File in __APP_DIR/tmp/i18n.xls__

## Import (Moving Changes from the Excel File into the Messages Files)

    play i18ned:import

## Import with a Prefix

Sometimes you need to export your Messages Files in a different Format.
E.g. every Key needs to have a specific prefix ( prefix.accept=accept ) or you need to export the Files to a specific output Directory.
For that reason there is the 

    play i18ned:pimport

Command. The exact syntax is:

    play i18ned:pimport --prefix <prefix> --output <outputDir>

## Check all Message Files for Missing/Not used Message Keys

    play i18ned:check

This command will scan all the Templates from the Application and will give you an output like this:


    Results for messages:

        NOT USED:

            need.help

        NOT FOUND:

            from.partner
                /yourapp/app/views/Application/index.html
                /yourapp/app/views/Users/show.html
                /yourapp/app/views/Users/show.json



## TODO

* Adding SQL-Export with SQL-Templates
* Adding Support for selective Actions (Im/Export only specific files)
* Import from other i18n Formats