<?xml version="1.0" encoding="UTF-8" ?> 
<html
  xmlns:jsp="http://java.sun.com/JSP/Page"
  xmlns:c="http://java.sun.com/jsp/jstl/core"
  xmlns:fn="http://java.sun.com/jsp/jstl/functions" >
  <head>
    <jsp:directive.page contentType="text/html;charset=UTF-8"></jsp:directive.page> 
    <!-- Just include this wad of javascript in your page. -->
    <script type="text/javascript">
  	//<![CDATA[
        window.addEventListener("message",function(a){if(a.origin.indexOf("intuit.com")>=1&&a.data&&a.data.initXDM)
        {var b=document.createElement("script");b.setAttribute("type","text/javascript");b.innerHTML=a.data.initXDM;
         document.getElementsByTagName("head")[0].appendChild(b)}});
     // ]]>
  	</script>
  </head>
  <body bgcolor="white">
    <script type="text/javascript">
        // QBO will call you back when the channel is ready 
        function qboXDMReady() {
          document.getElementById("scroller").style.height = document.height-150;
          document.getElementById("sendMessage").onclick = function () {
            qboXDM.sendMessageToOtherFrames("a message from trowser");
          };
          document.getElementById("closeTrowser").onclick = function () {
              qboXDM.closeTrowser();
          };
        }
    </script>


    <div class="trowserView unhideTrowser">
      <div class="trowser">
        <div class="universal">
          <header class="table header stretch">
            <div class="tableRow">
              <div class="tableCell">
                <div class="universal pageTitle inlineBlock">Sample trowser</div>
              </div>
            </div>
          </header>
          <div class="scroller" id="scroller" style="margin: 20px; height: 450px;">
            Hello, trowser!<br/>
          </div>
		<footer class="stickyFooter table width100Percent" data-dojo-attach-point="_footer" data-qbo-bind="css:{existingTransaction:existingTransaction}">
		    <div class="tableRow">
		        <div class="tableCell bottomLeftButtons">
		            <button tabindex="53" type="button" class="dark" data-dojo-attach-event="onclick:cancelButtonPressed" id="closeTrowser">Cancel</button>
		            <button tabindex="53" type="button" class="dark" data-dojo-attach-event="onclick:clearForm" data-qbo-bind="visible: showClear" style="display: none;">Clear</button>
		            <button tabindex="53" type="button" class="dark" data-dojo-attach-event="onclick:revertForm" data-qbo-bind="visible: showRevert" style="display: none;">Revert</button>
                	<button class="button primary" id="sendMessage">Send a message</button>
            </div>
		        <div class="tableCell bottomCenterButtons" data-qbo-bind="hidden: hasOldSalesTaxData">
		            <div data-dojo-attach-event="onclick:printCheckButtonPressed" data-qbo-bind="visible:getShowPrintCheckButton" class="bottomCenterButton" style="display: none;">Print check</div>
		            <div data-dojo-attach-event="onclick:printButtonPressed" data-qbo-bind="visible:showStickyFooterPrint" class="bottomCenterButton" data-dojo-attach-point="_printButton" style="display: none;">Print</div>
		            <div data-qbo-bind="visible:showPrintTooltipAnchor" class="bottomCenterButton" data-dojo-attach-point="_stickyFooterPrintAnchor" style="display: none;">Print or Preview</div>
		            <div data-dojo-attach-event="onclick:reverseButtonPressed" data-qbo-bind="visible:showReverse" class="bottomCenterButton" style="display: none;">Reverse</div>
		            <div data-dojo-attach-event="onclick:makeRecurringPressed" data-qbo-bind="visible:showMakeRecur" class="bottomCenterButton" style="">Make recurring</div>
		            <div data-dojo-attach-event="onclick:showCustomizePrintFormsDialog" data-qbo-bind="visible:showCustomize" class="bottomCenterButton" style="display: none;">Customize</div>
		            <div data-dojo-attach-event="onclick:moreTooltipAnchorPressed" data-qbo-bind="visible:showMore" class="bottomCenterButton" data-dojo-attach-point="_stickyFooterMoreAnchor" style="display: none;">More</div>
		        </div>
		        <div class="tableCell bottomRightButtons rightAligned" data-qbo-bind="hidden: hasOldSalesTaxData">
		            <button tabindex="51" type="button" class="button primary lighter" data-dojo-attach-event="onclick:saveTemplateButtonPressed" data-qbo-bind="visible: memorizedTemplate" style="display: none;">Save template</button>
		
		            <span data-qbo-bind="hidden: memorizedTemplate" class="saveButtons">
		                <button tabindex="52" type="button" class="button" data-dojo-attach-event="onclick:saveButtonPrimaryPressed" data-dojo-attach-point="_saveAndCloseButton" data-qbo-bind="hidden: isUndo">Save</button><!--
		                --><button tabindex="52" type="button" class="button primary lighter" data-dojo-attach-event="onclick:saveButtonSecondaryPressed" data-qbo-bind="css: {primary: !showSend}, css: {lighter: !showSend}, hidden: isUndo" data-dojo-attach-point="_saveAndNewButton">Save and new</button><!--
		                --><button tabindex="52" type="button" class="button primary lighter" data-dojo-attach-event="onclick:sendButtonPressed" data-dojo-attach-point="_saveAndSendButton" data-qbo-bind="visible: showSend" style="display: none;">Send</button>
		                <button tabindex="52" type="button" class="button" data-dojo-attach-event="onclick:deleteButtonPressed" data-dojo-attach-point="_undoButton" data-qbo-bind="visible: isUndo" style="display: none;">Undo</button>
		            </span>
		
		        </div>
		    </div>
		</footer>
        </div>
      </div>      
    </div>
    
  </body>
</html>
