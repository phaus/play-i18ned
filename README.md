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

## TODO

* Adding SQL-Export with SQL-Templates
* Adding Support for selective Actions (Im/Export only specific files)
* Import from other i18n Formats