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
		<footer class="stickyFooter table width100Percent">
		    <div class="tableRow">
		        <div class="tableCell bottomLeftButtons">
		            <button tabindex="53" type="button" class="dark" id="closeTrowser">Cancel</button>
		            <button tabindex="53" type="button" class="dark" style="display: none;">Clear</button>
		            <button tabindex="53" type="button" class="dark" style="display: none;">Revert</button>
                	<button class="button primary" id="sendMessage">Send a message</button>
            </div>
		        <div class="tableCell bottomCenterButtons" data-qbo-bind="hidden: hasOldSalesTaxData">
		            <div class="bottomCenterButton" style="display: none;">Print check</div>
		            <div class="bottomCenterButton" style="display: none;">Print</div>
		            <div class="bottomCenterButton" style="display: none;">Print or Preview</div>
		            <div class="bottomCenterButton" style="display: none;">Reverse</div>
		            <div class="bottomCenterButton" style="">Make recurring</div>
		            <div class="bottomCenterButton" style="display: none;">Customize</div>
		            <div class="bottomCenterButton" style="display: none;">More</div>
		        </div>
		        <div class="tableCell bottomRightButtons rightAligned" data-qbo-bind="hidden: hasOldSalesTaxData">
		            <button tabindex="51" type="button" class="button primary lighter" style="display: none;">Save template</button>
		
		            <span data-qbo-bind="hidden: memorizedTemplate" class="saveButtons">
		                <button tabindex="52" type="button" class="button">Save</button><!--
		                --><button tabindex="52" type="button" class="button primary lighter">Save and new</button><!--
		                --><button tabindex="52" type="button" class="button primary lighter"style="display: none;">Send</button>
		                <button tabindex="52" type="button" class="button"style="display: none;">Undo</button>
		            </span>
		
		        </div>
		    </div>
		</footer>
        </div>
      </div>      
    </div>
    
  </body>
</html>
