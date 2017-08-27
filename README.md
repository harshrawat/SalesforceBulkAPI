# SalesforceBulkAPI
Using Java we can trigger Salesforce Bulk APIs to perform different Import, Export and DML Operations

To perform these actions we have to create one configuration XML file

# Ex :
# for Import : Import.XML
""<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Information>
    <action>Import</action>
    <importInformation>
        <contentType>CSV/XML</contentType>
        <externalIdFieldName>{external Id API Name}</externalIdFieldName>
        <fileLocation>{Location To fetch File}</fileLocation>
        <fileName>{file name}</fileName>
        <objectName>{SObject API Name}</objectName>
        <operation>{DML Operation}</operation>
    </importInformation>
    <userinformation>
        <orgtype>Developer/Production/Sandbox</orgtype>
        <password>{Org Password}</password>
        <securityToken>{Security Token if required}</securityToken>
        <username>{User Name of the org}</username>
    </userinformation>
</Information>""

# for Export : Export.xml
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Information>
    <action>Export</action>
    <exportInformation>
        <contentType>CSV</contentType>
        <fileLocation>{LocationToSaveFile}</fileLocation>
        <fileName>{FileName}</fileName>
        <objectName>{SObject API Name}</objectName>
        <soql>{SOQL}</soql>
    </exportInformation>
    <userinformation>
        <orgtype>Developer/Production/Sandbox</orgtype>
        <password>{Org Password}</password>
        <securityToken>{Security Token if required}</securityToken>
        <username>{User Name of the org}</username>
    </userinformation>
</Information>

After the configuration file we just needed to execute bulk API jar
* Build directory contains .jsr file

# Run following command in CMD

java -jar SalesforceBulkAPI.jar Import.xml
java -jar SalesforceBulkAPI.jar Export.xml
