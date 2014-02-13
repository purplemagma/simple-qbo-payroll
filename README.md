simple-qbo-payroll
==================
<h2>Methods available in the qboXDM Object</h2>
<h3>qboXDM.closeTrowser()</h3>
<p>Use this method to close the trowser.</p>
<h3>qboXDM.destroy()</h3>
<h3>qboXDM.getContext()</h3>
<p>
</p>
<h3>qboXDM.listenforMessages()</h3>
<h3>qboXDM.navigate(url, data)</h3>
<p>
  qboXDM.navigate("xdmtrowser://qbo-simple-payroll/in/trowser.jsp");<br/>
  - This will load the URL in a trowser that supports cross-domain messaging<br/><br/>
  
	qboXDM.navigate("approute://employees");<br/>
  - QBO app route will be loaded on the same page<br/><br/>
  
	qboXDM.navigate("trowser://qbo-simple-payroll/in/trowser.jsp");<br/>
  - This will load the URL on the trowser.
</p>
<h3>qboXDM.origin</h3>
<h3>qboXDMReceiveMessage()</h3>
<p>
  qboXDM.qboXDMReceiveMessage();<br/>
  - Event listener for messages sent to the frame. <br/>
</p>
<h3>qboXDM.sendMessageToOtherFrames(message)</h3>
<p>
  qboXDM.sendMessageToOtherFrames("message");<br/>
  - Send messages to other frames <br/>
</p>
<h3>qboXDM.showDialog(dialogClassName, args, data)</h3>
<h3>qboXDM.showNameListDialog(nameType, nameId)</h3>
<h3>qboXDM.showPageMessage(message, alert)</h3>
<p>
 qboXDM.showPageMessage("Message", true);<br/>
 - This will display an alert at the top of the page with a warning icon. If the second parameter is set to false, then it has a checkmark beside the message.<br/>
</p>
<h3>qboXDM.subscribe(planId, successFn, errorFn)</h3>
<h3>qboXDM.track(method, args)</h3>
<h3>qboXDM.updateAppSubscriptionState(successFn, errorFn)</h3>
