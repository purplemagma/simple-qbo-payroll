<?xml version="1.0" encoding="UTF-8" ?> 
<html
xmlns:jsp="http://java.sun.com/JSP/Page"
xmlns:c="http://java.sun.com/jsp/jstl/core"
xmlns:fn="http://java.sun.com/jsp/jstl/functions" >
  <head>
    <jsp:directive.page contentType="text/html;charset=UTF-8"></jsp:directive.page> 
    <jsp:useBean id="payrollService" class="com.purplemagma.qbosimplepayroll.QBOSimplePayroll"></jsp:useBean>
    <jsp:setProperty name="payrollService" property="session" value="${pageContext.session}"></jsp:setProperty>
	<link rel="stylesheet" type="text/css" href="https://qa.cdn.qbo.intuit.com/c2/v67.084/scripts/dojo/qbo/harmony/harmony.css"><jsp:text /></link>
	<link rel="stylesheet" type="text/css" href="https://qa.cdn.qbo.intuit.com/c2/v67.084/scripts/dojo/qbo/harmony/svg.css"><jsp:text /></link>
    <script type="text/javascript" src="../js/utils.js"><jsp:text /></script>
    <script type="text/javascript" src="../js/json2.js"><jsp:text /></script>
	<script type="text/javascript" src="../js/dojo/dojo.js"><jsp:text /></script>
  </head>
  <body class="en-us">
  	<script type="text/javascript">
        require([
            "dojo/_base/declare",
        	"dgrid/OnDemandGrid",
        	"dojo/store/JsonRest",
        	"dgrid/Keyboard",
        	"dgrid/Selection",
        	"dgrid/util/mouse",
        	"dojo/domReady!"], 
        function(declare, Grid, JsonRest, Keyboard, Selection) {
        	var CustomGrid = declare([ Grid, Keyboard, Selection ]);
        	var store = new JsonRest({
        	    target: "../rest/in/employees",
        	    idProperty: "Id"
        	  });
            var grid = new CustomGrid({
            	store: store,
            	columns: [
            	   {label: "Given Name", field: "givenName", sortable: false},
            	   {label: "Family Name", field: "familyName"}
            	],
                selectionMode: "single",
                maxRowsPerPage: 500,
                bufferRows: 500,
            }, "grid");
            grid.startup();
        });
    </script>
    <div class="page-content">
	    <div class="divContent">
	    	<div>
				<div class="floatRight">
	                <div class="topButtonBar">
	                	<button type="button" class="button">Edit</button>
	                </div>
	            </div>
	        </div>
			<div class="tallBreakDiv"></div>
			<div class="listScroller" style="height: 210px;">
    	       <div id="grid" class="qboDataGrid"></div>
	    	</div>
    	</div>
    </div>
  </body>
</html>
