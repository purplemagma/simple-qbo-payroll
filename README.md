# simple-qbo-payroll
## Methods available in the qboXDM Object
### qboXDM.closeTrowser()
Use this method to close the trowser.

### qboXDM.getContext()
This returns a *qbo* object with the following data: 
- sku
- isSampleCompany
- companyName
- user
- companyL10nAttribs
- realmId
- v3ServiceBaseUrl
- v3NeoServiceBaseUrl
- baseUrl
- trowser
- activated

### qboXDM.navigate(url, data)
*url* is a combination of *protocol* (xdmtrowser:// or approute://) and *path*

	qboXDM.navigate("xdmtrowser://qbo-simple-payroll/in/trowser.jsp");
This will load the *path* which is relative to your domain in a trowser that supports cross-domain messaging. If *data* is specified it will be available in the context object.
  
	qboXDM.navigate("approute://employees");
Navigates to a QBO page specified by *path*

### qboXDM.sendMessageToOtherFrames(message)
	qboXDM.sendMessageToOtherFrames("message");

Send messages to other frames of your application, e.g. from trowser to main frame and vice versa

### qboXDM.showDialog(dialogClassName, args, data)

### qboXDM.showNameListDialog(nameType, nameId)
	qboXDM.showNameListDialog("employee");
 
This example will launch the modal dialog from the employee section. Other options are the following:
- "customer"
- "vendor"
- "employee"
- "account"
- "item"
- "klass"
- "location"
- "term"
- "paymentmethod"
- "taxcode"

### qboXDM.showPageMessage(message, alert)
	qboXDM.showPageMessage("Message", true);

This will display an alert at the top of the page with a warning icon. If the second parameter is set to false, then it has a checkmark beside the message.

### qboXDM.updateAppSubscriptionState(successFn, errorFn)
### qboXDM.subscribe(planId, successFn, errorFn)
### qboXDM.track(method, args)

## Functions available for integration
### qboXDMReceiveMessage(message)
Implement this function to receive messages from QBO as well as from the frames of your application (e.g. from trowser to main frame and vice versa)