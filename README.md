# Partner integration guide
## Setup an IPP app
- Navigate to https://developer-stage.intuit.com and create an account
- Proceed to My Apps -> Manage My Apps -> Create New App
- Select QuickBooks API (for external app) or Ecosystem App (for internal, intuit.com app)
- Write down app id, app token, oauth key and oauth secret

## Setup a local app server (Java)
- git clone https://github.com/purplemagma/intuit-qbo-plugin for an internal app or https://github.com/purplemagma/simple-qbo-payroll for an external app
- update app.properties with app token, oauth key and oauth secret. Uncomment base_url and set it to your app url.
- mvn package
- deploy produced war file to your java web server (e.g. apache tomcat). Your server should support https.

## Test QBO integration
- If you have an account, navigate to https://e2e.qbo.intuit.com and login
- If you don't have an account, create one at https://e2e.qbo.intuit.com/qbo3/redir/startuphere?locale=en_US&qbimport=n&bc=USP-TAN
- Navigate to ecosystem page at https://e2e.qbo.intuit.com/app/ecosystem
- Add a new app and update configuration with "sourceUrl" parameter pointing to app on your server, e.g. https://your.server.com/qbo-simple-payroll
- Click "Navigate". You should see your app loaded inside of QBO.
- Use example provided on the ecosystem page to add desired access points and override qbo app routes.
- For additional information about configuration see ConfigurationReference.pdf

## Submit configuration to QBO for server side update
Some features including app activation and region restriction are only available when configuration is uploaded to QBO servers.
Please prepare a configuration block for your app in the following format and submit it to your contact person at QBO.

    "your_app_id": {
        "pluginId": "your_app_id",
        "appToken": "your_app_token",
        "sourceUrl": "https://your.integration.com",
        "regions": [
            "US"
        ],
        "accessPoints": [
            {
                "overrideAppRoute": "addpayroll"
            }
        ],
        "canonicalName": "your_app_canonical_name",
        "allowedOrigins": ["https://your.integration.com"],
        "trowser": true,
        "postActivation": {
            "trowser": false,
            "accessPoints": [
                {
                    "overrideAppRoute": "employees"
                },
                {
                    "attachPoint": "_apSettingsSettingsList",
                    "linkText": "Your Settings",
                    "trowser": true
                },
                {
                    "attachPoint": "_apCreateEmployeesList",
                    "linkText": "Your Employee List Access Point",
                    "position": 0
                }
            ]
        }
    }

# simple-qbo-payroll
## Overview
This sample app provides basic functionality along with openId and oAuth authorization flows integrated. App activation occurs when you receive the oauth token on behalf of the user.

## How to initialize a qboXDM object
Add the following script to your html head element:

    <script type="text/javascript">
    //<![CDATA[
        window.addEventListener("message",function(a){if(a.origin.indexOf("intuit.com")>=1&&a.data&&a.data.initXDM)
        {var b=document.createElement("script");b.setAttribute("type","text/javascript");b.innerHTML=a.data.initXDM;
         document.getElementsByTagName("head")[0].appendChild(b)}});
     // ]]>
    </script>

It listens for a message from QBO that adds additional scripts, initializes qboXDM object and calls qboXDMReady() function that you need to define as well. By the time that function is called you can access a globally defined qboXDM object.

## Methods available in the qboXDM Object
### qboXDM.closeTrowser()
Use this method to close the trowser.

### qboXDM.getContext(function(context){})
This calls provided function with a qbo context object containing the following data:

    {
        "qbo": {
            "sku": {
                "isClassicMigrator": false,
                "isSimpleStartSku": false,
                "id": 7,
                "isGlobalCompany": false,
                "isBasicSku": false,
                "mnemonic": "PLUS",
                "isUsingFY14DTX": true,
                "name": "QuickBooks Online Plus",
                "isPlusSku": true,
                "isAccountantSku": false,
                "isPayrollSku": true,
                "isEligibleForDesktopImport": true
            },
            "isSampleCompany": false,
            "companyName": "testusc11",
            "user": {
                "lastName": "test",
                "phone": "",
                "email": "alexey_povkh@intuit.com",
                "name": "test test",
                "firstName": "test"
            },
            "companyL10nAttribs": {
                "region": "US",
                "currencyDisplayName": "United States Dollar",
                "dateFormatDelimiter": "/",
                "currencySymbolPos": "BEFORE",
                "defaultDateFormat": "MM/dd/yyyy",
                "locale": "en-us",
                "isMulticurrencyAvailable": false,
                "shortDateFormat": "M/d/yy",
                "digitGroupSeparator": ",",
                "isMulticurrencyEnabled": false,
                "printFormDateFormat": "MM/dd/yyyy",
                "decimalSeparator": ".",
                "mediumTimeFormat": "HH:mm:ss",
                "currencyIsoCode": "USD",
                "currencySymbol": "$",
                "digitGroupSize": 3,
                "mediumDateFormat": "M/d/yyyy",
                "shortTimeFormat": "hh:mm a",
                "qboaClientCollaboratorEnabled": true,
                "dateFormatYearIndex": 2,
                "printCheckDateFormat": "M M D D Y Y Y Y",
                "defaultDateTimeFormat": "MM/dd/yyyy hh:mm:ss a",
                "dateFormatDateIndex": 1,
                "dateFormatMonthIndex": 0
            },
            "realmId": "1034669440",
            "v3ServiceBaseUrl": "https://e2e.qbo.intuit.com/qbo11/v3/company/1034669440/",
            "v3NeoServiceBaseUrl": "https://e2e.qbo.intuit.com/qbo11/neoservice/",
            "baseUrl": "https://e2e.qbo.intuit.com/qbo11",
            "trowser": false,
            "activated": true
        },
        "params": {
            "accessPoint": "_apExample",
            "locale": "en-us",
            "version": "72.158"
        }
    }

### qboXDM.navigate(url, data)
*url* is a combination of *protocol* (xdmtrowser:// or approute://) and *path*

	qboXDM.navigate("xdmtrowser://qbo-simple-payroll/in/trowser.jsp");
This will load the *path* which is relative to your domain in a trowser that supports cross-domain messaging. If *data* is specified it will be available in the context object.
  
	qboXDM.navigate("approute://employees");
Navigates to a QBO page specified by *path*

### qboXDM.sendMessageToOtherFrames(message)
	qboXDM.sendMessageToOtherFrames("message");

Send messages to other frames of your application, e.g. from trowser to main frame and vice versa

### qboXDM.showDialog(dialogClassName, data, args)
	qboXDM.showDialog("qbo/lists/name/employee/EmployeeDialogViewController");
 
This example will launch the modal dialog from the employee section. Other options are the following:
- qbo/lists/name/customer/CustomerDialogViewController
- qbo/lists/name/vendor/VendorDialogViewController
- qbo/lists/taxcode/TaxCodeDialogViewController

*data* can be passed to specify the id of the entity, e.g. {id: "Employee Name Id"}

### qboXDM.showPageMessage(message, alert)
	qboXDM.showPageMessage("Message", true);

This will display an alert at the top of the page with a warning icon. If the second parameter is set to false, then it has a checkmark beside the message.

### qboXDM.updateAppSubscriptionState(successFn, errorFn)

External apps only. This updates the QBO UI state after app is activated (applies access points and route overrides from the post activation section of your config)

### qboXDM.subscribe(planId, successFn, errorFn)

Internal apps only. Subscribes(activates) an app and updates QBO UI state.

### qboXDM.track(method, args)

Use this method to call QBO tracking API directly.

### qboXDM.getModel()

Returns parent node model. Model can be used to access state information on the parent node.

### qboXDM.setModelProperty(name, value)

Sets a property on the parent model to a specified value.

### qboXDM.getModelProperty(name)

Gets a property from the parent model.

### qboXDM.updateQuickFillStore(storeId)

Refreshes specified quickfill store data

### qboXDM.emitEvent(eventId, data)

Sends an event to QBO, plugin must register for events it's allowed to send through the configuration (allowedEvents property, array of strings)

### qboXDM.adjustFrameHeight(height)

Sets iframe height to specified value, support pixel and percent options, must be string

### qboXDM.adjustFrameWidth(width)

Sets iframe width to specified value, support pixel and percent options, must be string

### qboXDM.showSpinner(callbackFn)

Shows a spinner, calls callbackFn with a "timeout"  value that equals to amount of time spinner will be shown for. If there is need to show spinner for longer than default timeout, this method has to be called again to reset the timeout.

### qboXDM.hideSpinner()

Hides currently displayed spinner

## Functions available for integration
### qboXDMReceiveMessage(message)
Implement this function to receive messages from QBO as well as from the frames of your application (e.g. from trowser to main frame and vice versa)

### qboXDMReady()
Implement this function, it will be called when qboXDM object becomes available.