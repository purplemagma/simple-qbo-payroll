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
          
          document.getElementById("nextStep").onclick = function () {
        	  qboXDM.navigate("xdmtrowser://qbo-simple-payroll/in/wizard_step3.jsp");
          };
          document.getElementById("closeTrowser").onclick = function () {
              qboXDM.closeTrowser();
          };
          document.getElementById("closeTrowserIcon").onclick = function () {
              qboXDM.closeTrowser();
          };
        }
    </script>


    <div class="trowserView unhideTrowser">
      <div class="trowser stretch">
        <div class="body">
        <div class="wizard setupWizard">
			<header class="table header stretch">
        		<div class="tableRow">
            		<div class="title tableCell" id="title">Welcome to QuickBooks!</div>
            		<div accesskey="x" role="button" tabindex="0" class="tableCell element leftDivider close-sprite close" id="closeTrowserIcon"></div>
        		</div>
    		</header>  
  
  
          	<div class="scroller" id="scroller" style="height: 450px;">
          		<div class="mainContainerSetupSteps table">
          			<div class="table width100Percent scrollable">
          				<div class="tableRow content">
							<div class="progressSection">
						   		<div class="stepProgressBar">
						   			<div class="stepNodesBar">
						   				<div class="step">
											<div class="stepOrder">1</div>
											<div class="stepName upperCase">Step 1</div>
										</div>
										<hr class="stepLine" style="width: 604px; height:0; border:1px solid #DDD"/> 
										<div class="step currentStep">
											<div class="stepOrder">2</div>
											<div class="stepName upperCase">Set 2</div>
										</div>
										<hr class="stepLine" style="width: 604px; height:0; border:1px solid #DDD"/> 
										<div class="step ">
											<div class="stepOrder">3</div>
											<div class="stepName upperCase">Step 3</div>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
          	</div>
			<footer class="stickyFooter table width100Percent">
			    <div class="tableRow">
			        <div class="tableCell bottomLeftButtons">
			            <button tabindex="53" type="button" class="dark" id="closeTrowser">Cancel</button>
	            	</div>
			        <div class="tableCell bottomCenterButtons">
			            <div class="bottomCenterButton" style="display: none;">Print check</div>
			            <div class="bottomCenterButton" style="display: none;">Print</div>
			            <div class="bottomCenterButton" style="display: none;">Print or Preview</div>
			            <div class="bottomCenterButton" style="display: none;">Reverse</div>
			            <div class="bottomCenterButton" style="display: none;">Make recurring</div>
			            <div class="bottomCenterButton" style="display: none;">Customize</div>
			            <div class="bottomCenterButton" style="display: none;">More</div>
			        </div>
			        <div class="tableCell bottomRightButtons rightAligned">
			            <button tabindex="51" type="button" class="button primary lighter" id="nextStep">Save and next</button>
			        </div>
			    </div>
			</footer>
        </div>
      </div>      
    </div>
    </div>
  </body>
</html>
