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
        "subscribedEvents": ["qbo-action-settings-save"],
        "canonicalName": "your_app_canonical_name",
        "allowedOrigins": ["https://your.integration.com"],
        "trowser": true,
        "configurations": {
            "activated": {
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
            },
            "unavailable": {
                "accessPoints": []
            }
        },
        "heartbeat": {
            "url": "https://localhost.intuit.com/java-web-sample/heartbeat.jsp",
            "failoverConfiguration": "unavailable",
            "checkIntervalSeconds": 60
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

 	qboXDM.navigate("xdmtrowser://https://your.allowed.domain.com/qbo-simple-payroll/in/trowser.jsp");
This is similar to the previous but takes a full url, make sure it's included in your plugin's allowedOrigins

	qboXDM.navigate("approute://employees");
Navigates to a QBO page specified by *path*

### qboXDM.sendMessageToOtherFrames(message)
	qboXDM.sendMessageToOtherFrames("message");

Send messages to other frames of your application, e.g. from trowser to main frame and vice versa

### qboXDM.showOkDialog(title, message, options, okBtnFn)
Shows a modal dialog with just an OK button

Arguments: options and okBtnFn are optional
    
    qboXDM.showOkDialog("some title", "You know nothing, Jon Snow!", 
        {"iconClass": "confirmIcon"}, 
        function () {
            console.log("OK button clicked");
        }
    );

### qboXDM.showYesNoDialog(title, message, options, yesBtnFn, noBtnFn)
Shows a modal dialog with a Yes and a No button
Arguments: options, yesBtnFn and noBtnFn are optional
    
    qboXDM.showYesNoDialog("some title", "Are you sure you want to simply walk into Mordor?", 
        {"iconClass": "alertIcon"}, 
        function () {
            console.log("yes button clicked");
        }, 
        function () {
            console.log("no button clicked");
        }
    );

### qboXDM.showOkCancelDialog(title, message, options, okBtnFn, cancelBtnFn)
Shows a modal dialog with an OK and a Cancel button
Arguments: options, okBtnFn and cancelBtnFn are optional
    
    qboXDM.showOkCancelDialog("some title", "Brace yourself. Winter is coming!", 
        {"iconClass": "warningIcon"}, 
        function () {
            console.log("OK button clicked");
        }, function () {
            console.log("Cancel button clicked");
        }
    );

### qboXDM.showContinueCancelDialog(title, message, options, callbackFn)
Shows a modal dialog with a Continue and a Cancel button and optionally shows a checkbox that allows user to choose not to be shown
this dialog again
Arguments: options and callbackFn are optional

callbackFn will be called with an argument object with action param value: continueFn, cancelFn or ignoreFn
e.g. {"action": "continueFn"}
The callback function will get {"action": "continueFn"} when continue button is clicked and 
{"action": "cancelFn"} when cancel button is clicked

Ignore functionality is rarely used. You use it when you want to show a checkbox to end-user to not show this dialog 
(usually a warning) again.
You will have to pass in an options object like: 
    
    { 
        iconClass: "warningIcon", 
        showCheckBox : true, 
        dontShowAgain: "I'm Sauron! I don't want to be see this warning again.", 
        pref: "ignoreMordorWarningPopUp"
    }

Callback with ignoreFn argument will be called when the dialog is invoked again after the end-user has already asked us not to show the dialog
again. 

    qboXDM.showContinueCancelDialog("title", "Are you sure you want to simply walk into Mordor?", 
        { 
            iconClass: "warningIcon", 
            showCheckBox : true, 
            dontShowAgain: "I'm Sauron! I don't want to be see this warning again.", 
            pref: "ignoreMordorWarningPopUp"
        },
        function (obj) {
            switch (obj.action) {
                case "continueFn":
                    console.log("Do something when continue button is clicked");
                    break;
                case "cancelFn":
                    console.log("Do something when cancel button is clicked");
                    break;
                case "ignoreFn":
                    console.log("Do something when dialog show is invoked even after user has explicitly said that he doesn't want to be shows this dialog again.");
                    break;
                default:
                    console.error("Default case should not be ivoked"); 
            }
        }
    );

### qboXDM.showDialogWithCustomButtons(title, message, labels, options, callbackFn)
Shows a modal dialog with buttons as specified in labels array
Arguments: options and callbackFn are optional

callbackFn will be called with an argument object with labelId value: array index of the label in the array

    qboXDM.showDialogWithCustomButtons("Referral sent",
        "When your friend subscribed, you will receive an email about your Amazon gift card", 
        [
            {
                "label": "Refer another friend", 
                "section": -1
            }, {
                "label": "Close",
                "primary": true, 
                "section": 1
            }
        ],
        {
            "iconClass": "confirmIcon"
        },
        function (evt) {
            switch (evt.labelId) {
                case 0:
                    qboXDM.navigate("approute://referrals");
                    break;

                case 1:
                    console.log("Do something with click on close button");
                    break;
            }
        }
    );

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

### publishTopic(topicId, data)

Publishes a message to a topic registered in QBO, plugin must register for topics it's allowed to send to through the configuration (allowedTopics property, array of strings)

### qboXDM.adjustFrameHeight(height)

Sets iframe height to specified value, support pixel and percent options, must be string

### qboXDM.adjustFrameWidth(width)

Sets iframe width to specified value, support pixel and percent options, must be string

### qboXDM.showSpinner(callbackFn)

Shows a spinner, calls callbackFn with a "timeout"  value that equals to amount of time spinner will be shown for. If there is need to show spinner for longer than default timeout, this method has to be called again to reset the timeout.

If you need to show spinner continuously until some event occurs e.g. showing a spinner when doing an xhr and hiding the spinner when the request completes, you can do something like this: 

	function () {
		// url to make xhr to
	    	var url = context.qbo.baseUrl + "/productservice/v1/payroll/" + context.qbo.realmId + "/add";
	
	    	// get XMLHttpRequest object
	    	var xhr = createCORSRequest('POST', url, true);
	
	    	xhr.onload = function() {
	        	// set hideSpinner closure variable to false
	        	hideSpinner = true;
	               
	        	// and also hide spinner immediately
	        	qboXDM.hideSpinner();
	
	        	// show xhr success msg
	    	};
	
	    	// beginning - show spinner before making the request
	    	var hideSpinner = false;
	
	    	var timeOutCallback = function () {                            
	        	if (hideSpinner) {
	            	qboXDM.hideSpinner();
	        	} else {
	            	// if hideSpinner is not set to true by xhr callback handler, then keep on showing the spinner.
	            	qboXDM.showSpinner(showSpinnerCallback);
	        	}
	    	};
	
		    // this callback is called right away by qboXDM.showSpinner with the argument obj containing timeoutInterval.
		    // we set timer with that timeoutInterval to check if we need to continue to show spinner            
		    var showSpinnerCallback = function (obj) {                
	        	// check after the timeoutInterval/2 if we need to continue to show spinner. 
	        	// timeoutInterval/2 rather than timeoutInterval so that we show the spinner continously rather than showing for 5 secs, hiding for a sec and showing again
		        window.setTimeout(timeOutCallback, obj.timeout/2);                            
		    };
	            
	    	qboXDM.showSpinner(showSpinnerCallback);
	    	// end - show spinner before making the request
	
	    	// Make the xhr
	    	xhr.send(JSON.stringify({
	        	"accessPoint": context.params.pc_upsell_ipd_ap
	    	}));
	}

### qboXDM.hideSpinner()

Hides currently displayed spinner

### qboXDM.switchConfiguration(configuration)

Switches plugin to a pre-defined configuration or default if null is passed. If configuration matches heartbeat failover configuration a retry process with default timeout will be triggered.

## Functions available for integration
### qboXDMReceiveMessage(message, successFn, errorFn)
Implement this function to receive messages from QBO as well as from the frames of your application (e.g. from trowser to main frame and vice versa)

#### Available QBO events
You can subscribe to the following events by adding them to the subscribedEvents list in your plugin configuration.
qbo-action-settings-* events are part of company settings integration.

##### qbo-action-settings-dirty_check
Sent when QBO needs to know if model is dirty (i.e. user navigates away or clicks on another settings section)

    if (message.eventName === "qbo-action-settings-dirty_check") {
        var isModelDirty = false,
        timeout = message.data.timeout; // amount of time in ms before QBO assumes there is an error
        ToDo: add logic for checking if model is in dirty state
        successFn({
            result: isModelDirty
        });
    }

##### qbo-action-settings-save
Sent when settings section state needs to be saved.

    if (message.eventName === "qbo-action-settings-save") {
        var timeout = message.data.timeout; // amount of time in ms before QBO assumes there is an error
        // ToDo: attempt to save settings
        // on success call:
        successFn();
        // on handled validation failure call
        //errorFn({handled: true});
        // on error call
        //errorFn({message: "Failed to save your data"});
        // if you need more processing time than timeout, call the following function.
        // this will reset the timeout counter and give you more time to perform saving action
        // you can extend up to 2 times
        // a separate 'qbo-action-settings-save' messages will be sent each time
        //errorFn({extend: true});
    }

##### qbo-action-settings-switch
Sent when settings section changes it's edit state. For example when user rejects changes and navigates away.

    if (message.eventName === "qbo-action-settings-switch") {
        var editMode = message.data.editMode;
        // ToDo: perform some internal logic based on knowing that settings entered/exited edit mode (e.g. show/hide save button)
        // you can change size of your iframe here if necessary by calling qboXDM.adjustFrameHeight(height) function
    }

##### qbo-action-UniversalCrud-save
For integration to the transaction forms (invoice, bill, etc).
Sent before form is saved.

    if (message.eventName === "qbo-action-UniversalCrud-save") {
        if (success) {
            successFn();
        } else {
            errorFn("error message");
        }
    }

##### qbo-action-navigation-switch
Issued when user navigates to the page that is already opened, i.e. clicks on the employees tab when already on the employees page.
Partner's iframe need to reset their content accordingly.

    if (message.eventName === "qbo-action-navigation-switch") {
        if (message.data.path === "employees") {
            document.location.href = message.data.url;
        }
    }

##### qbo-request-page_context
For integration with QBO help system.
Sent when help module requests information about current page.

    if (message.eventName === "qbo-request-page_context") {
        successFn({pageName:"pluginPageName", productName:"pluginProductName", productEdition:"pluginProductEdition"});
    }

### qboXDMReady()
Implement this function, it will be called when qboXDM object becomes available.
