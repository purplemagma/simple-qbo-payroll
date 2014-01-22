<?xml version="1.0" encoding="UTF-8" ?>
<html xmlns:jsp="http://java.sun.com/JSP/Page"
  xmlns:c="http://java.sun.com/jsp/jstl/core"
  xmlns:fn="http://java.sun.com/jsp/jstl/functions">
<head>
  <jsp:directive.page contentType="text/html;charset=UTF-8"></jsp:directive.page>
  <jsp:useBean id="payrollService" class="com.purplemagma.qbosimplepayroll.QBOSimplePayroll"></jsp:useBean>
  <jsp:setProperty name="payrollService" property="session" value="${pageContext.session}"></jsp:setProperty>
  <script type="text/javascript" src="../js/utils.js"><jsp:text /></script>
  <script type="text/javascript" src="../js/json2.js"><jsp:text /></script>
  <script type="text/javascript" src="../js/dojo/dojo.js"><jsp:text /></script>
  <script src="../js/highcharts/adapters/standalone-framework.js"><jsp:text/></script>
  <script type="text/javascript" src="../js/highcharts/highcharts.js"><jsp:text /></script>
    <!-- Just include this wad of javascript in your page. -->
    <script type="text/javascript">
    //<![CDATA[
        window.addEventListener("message",function(a){if(a.origin.indexOf("intuit.com")>=1&&a.data&&a.data.initXDM)
        {var b=document.createElement("script");b.setAttribute("type","text/javascript");b.innerHTML=a.data.initXDM;
         document.getElementsByTagName("head")[0].appendChild(b)}});
     // ]]>
    </script>
</head>
<body class="en-us">
  <script type="text/javascript">
//<![CDATA[
    require([ "dojo/_base/declare", "dgrid/OnDemandGrid",
        "dojo/store/JsonRest", "dgrid/Keyboard", "dgrid/Selection",
        "dgrid/util/mouse", "dojo/domReady!" ], function(declare, Grid,
        JsonRest, Keyboard, Selection) {
      var CustomGrid = declare([ Grid, Keyboard, Selection ]);
      var store = new JsonRest({
        target : "../rest/in/employees",
        idProperty : "Id"
      });
      var grid = new CustomGrid({
        store : store,
        columns : [ {
          label : "Given Name",
          field : "GivenName",
          sortable : false
        }, {
          label : "Family Name",
          field : "FamilyName",
          sortable : false
        } ],
        selectionMode : "single",
        maxRowsPerPage : 500,
        bufferRows : 500
      }, "grid");
      grid.startup();

      // Dummy chart
	var data = [
                    {
                        name: "Net Pay",
                        y: 20,
                        formattedY: "$2000"
                    },
                    {
                        name: "Employee Deductions",
                        y: 5,
                        formattedY: "$500"
                    },
                    {
                        name: "Employer Taxes",
                        y: 2,
                        formattedY: "$200"
                    }
                ];

	var chart = new Highcharts.Chart({
	        chart : {
	            renderTo : "chart",
	            backgroundColor : null,
	            plotShadow : false,
	            reflow : false,
	            width: 380,
	            height: 230,
	            marginTop: 0
	        },
	        colors : ["#54b7d2", "#89438d", "#C46AA3"],
	        credits : {
	            enabled : false
	        },
	        title : {
	            text: null
	        },
	        exporting : {
	            enabled : false
	        },
	        legend : {
	            align: "left",
	            verticalAlign: "center",
	            borderWidth: 0,
	            borderRadius: 0,
	            layout: "vertical",
	            padding: 0,
	            title: {
	                text: ""
	            },
	            y: 20,
	            itemMarginTop: 3,
	            itemMarginBottom: 17,
	            itemStyle: {
	                lineHeight: "17px"
	            },
	            symbolWidth: 3,
	            useHTML: true,
	            labelFormatter: function() {
	                this.legendSymbol.element.setAttribute("height", "35");
	                this.legendSymbol.element.setAttribute("y", "3");
	                this.legendSymbol.element.setAttribute("ry", "0");
	                this.legendSymbol.element.setAttribute("rx", "0");
	                return "<div class='fancyMoneyLight marginLeft10'>"+this.formattedY + "</div><div class='fancyText upperCase marginLeft10'>" +  this.name + "</div>";
	            }
	        },
	        tooltip : {
	            formatter: function() {
	                return "<strong>" + this.point.name + "</strong><br/>" + this.point.options.formattedY;
	            }
	        },
	        plotOptions : {
	            pie : {
	                cursor : "pointer",
	                dataLabels : {
	                    enabled : false
	                },
	                borderWidth : 0,
	                showInLegend: true,
	                shadow: false,
	                size: "16",
	                center: ["55%","45%"]
	            }
	        },
	        series : [ {
	            data : data,
	            type : "pie",
	            cursor : "pointer",
	            innerSize : "45%",
	            id : "mainSeries"
	        }]
	    });
    });    
        function qboXDMReady() {
          qboXDM.getContext(function (qboContext) {
            if (qboContext.qbo.trowser === true && qboContext.qbo.activated !== true) {
              // example that shows how to navigate to updated approute after activating the app
              document.getElementById("notActivatedBlock").style.display = "block";
              document.getElementById("activate").onclick = function () {
                qboXDM.updateAppSubscriptionState(function () {
                  qboXDM.closeTrowser();
                  qboXDM.navigate("approute://employees");
                }, function () {
                  console.log("activation failure");
                });
              };
            } else {
              document.getElementById("activatedBlock").style.display = "block";
              document.getElementById("payrun").onclick = function () {
                qboXDM.navigate("xdmtrowser://qbo-simple-payroll/in/trowser.jsp");
              };
              document.getElementById("pullTab").onclick = function(){
                var stage = document.getElementsByClassName("stage-default")[0];
                stage.classList.toggle("stage-close");
              };
            }
          });
        }
        function qboXDMReceiveMessage(message) {
          console.log("Received a message:" + message);
        }
// ]]>
  </script>
  <div class="page-content" id="notActivatedBlock" style="display: none">
    <div class="divContent">
      <div>
        <div class="topButtonBar">
          <button type="button" class="button" id="activate">Activate</button>
        </div>
      </div>
    </div>
  </div>
	<div class="lists employees" id="activatedBlock" style="display: none">
	    <div class="stage stage-default">
	        <div class="stage-header">
	            <span class="page-title">Employees</span>
	        </div>
	        <div class="stage-content">
	            <div class="payroll-stage">
	                <div class="chartContainer">
	                    <div class="leftData">
	                        <div class="fancyMoney">$0.00</div>
	                        <div class="fancySummarySubText upperCase">2013 PAYROLL COST</div>
	                    </div>
	                    <div id="chart" class="chart"><jsp:text/></div>
	                </div>
	                <div class="verticalLineAndText">
	                    <div class="table alignPayrollText">
	                        <div class="tableRow">
	                            <div class="tableCell leftAligned">
	                                <p class="payrollText">Need to pay employees?</p>
	                                <button type="button" class="button primary newNameButton" id="payrun">Turn on Payroll</button>
	                            </div>
	                        </div>
	                    </div>
	                </div>
	            </div>
	        </div>
	        <button class="arrow-sprite pulltab" id="pullTab"></button>
	    </div>
	
	    <div class="tallerBreakDiv"></div>
			<div class="page-content">
        		<div class="divContent employee">
		            <div>
		                <div class="dijitInline dijitTextBox" role="presentation">
		                <input class="dijitReset dijitInputInner" autocomplete="off" type="text" tabindex="0" placeholder="Filter by name" value=""/></div>
		                <input type="checkbox" class="includeInactive"/>
		                <label>Include inactive</label>
		                <div class="floatRight">
		                    <div class="topButtonBar">
			                    <button type="button" class="button primary">New</button>
			                 	<button type="button" class="button" disabled="disabled">Edit</button>
			                 	<button type="button" class="button" disabled="disabled">Delete</button>
			                    <button type="button" class="button" disabled="disabled">Report</button>
			                    <button type="button" class="button" >Print</button>
							</div>
		                </div>
		            </div>

            		<div class="tallBreakDiv"></div>
    			</div>
           		<div class="listScroller" data-dojo-attach-point="_listScroller" style="height: 270px;">
               		<div data-dojo-attach-point="_grid" class="ui-widget dgrid dgrid-grid qboDataGrid" id="dgrid_0" role="grid">
               			<div class="dgrid-header dgrid-header-row ui-widget-header" role="row">
               				<table class="dgrid-row-table" role="presentation" id="dgrid_0-header">
               					<tr>
               						<th class="dgrid-cell dgrid-cell-padding dgrid-column-0 field-_item" role="columnheader" tabindex="0">Name</th>
               						<th class="dgrid-cell dgrid-cell-padding dgrid-column-1 field-phoneNumber" role="columnheader">Phone Number</th>
               						<th class="dgrid-cell dgrid-cell-padding dgrid-column-2 field-email" role="columnheader">Email Address</th>
               					</tr>
               				</table>
               			</div>
               			<div class="dgrid-scroller" tabindex="-1" style="-webkit-user-select: none; margin-top: 34px; margin-bottom: 0px;">
               				<div class="dgrid-content ui-widget-content" tabindex="0">
               				<div role="row" class=" ui-state-default dgrid-row dgrid-row-even ui-state-active" id="dgrid_0-row-82">
               					<table class="dgrid-row-table" role="presentation">
               						<tr>
               							<td class="dgrid-cell dgrid-cell-padding dgrid-column-0 field-_item dgrid-focus" role="gridcell" tabindex="0">
               								<span style="margin-left: 0px">Charles Jim</span>
               							</td>
               							<td class="dgrid-cell dgrid-cell-padding dgrid-column-1 field-phoneNumber" role="gridcell"></td>
               							<td class="dgrid-cell dgrid-cell-padding dgrid-column-2 field-email" role="gridcell"></td>
               						</tr>
               					</table>
               				</div>
               				<div role="row" class=" ui-state-default dgrid-row dgrid-row-odd" id="dgrid_0-row-137">
               					<table class="dgrid-row-table" role="presentation">
               						<tr>
               							<td class="dgrid-cell dgrid-cell-padding dgrid-column-0 field-_item" role="gridcell">
               								<span style="margin-left: 0px">Danica Smith</span>
               							</td>
               							<td class="dgrid-cell dgrid-cell-padding dgrid-column-1 field-phoneNumber" role="gridcell"></td>
               							<td class="dgrid-cell dgrid-cell-padding dgrid-column-2 field-email" role="gridcell"></td>
               						</tr>
               					</table>
               				</div>
               				<div role="row" class=" ui-state-default dgrid-row dgrid-row-even" id="dgrid_0-row-138">
               					<table class="dgrid-row-table" role="presentation">
               						<tr>
               							<td class="dgrid-cell dgrid-cell-padding dgrid-column-0 field-_item" role="gridcell">
               								<span style="margin-left: 0px">Eloi Lewis</span>
               							</td>
               							<td class="dgrid-cell dgrid-cell-padding dgrid-column-1 field-phoneNumber" role="gridcell"></td>
               							<td class="dgrid-cell dgrid-cell-padding dgrid-column-2 field-email" role="gridcell"></td>
               						</tr>
               					</table>
               				</div>
               				<div role="row" class=" ui-state-default dgrid-row dgrid-row-odd" id="dgrid_0-row-185">
               					<table class="dgrid-row-table" role="presentation">
               						<tr>
               							<td class="dgrid-cell dgrid-cell-padding dgrid-column-0 field-_item" role="gridcell">
               								<span style="margin-left: 0px">Jamie Hayward</span>
               							</td>
               							<td class="dgrid-cell dgrid-cell-padding dgrid-column-1 field-phoneNumber" role="gridcell"></td>
               							<td class="dgrid-cell dgrid-cell-padding dgrid-column-2 field-email" role="gridcell"></td>
               						</tr>
               					</table>
               				</div>
               				<div role="row" class=" ui-state-default dgrid-row dgrid-row-even" id="dgrid_0-row-194">
               					<table class="dgrid-row-table" role="presentation">
               						<tr>
               							<td class="dgrid-cell dgrid-cell-padding dgrid-column-0 field-_item" role="gridcell">
               								<span style="margin-left: 0px">Jeff Sage</span>
               							</td>
               							<td class="dgrid-cell dgrid-cell-padding dgrid-column-1 field-phoneNumber" role="gridcell"></td>
               							<td class="dgrid-cell dgrid-cell-padding dgrid-column-2 field-email" role="gridcell"></td>
               						</tr>
               					</table>
               				</div>
               				<div role="row" class=" ui-state-default dgrid-row dgrid-row-odd" id="dgrid_0-row-177">
               					<table class="dgrid-row-table" role="presentation">
               						<tr>
               							<td class="dgrid-cell dgrid-cell-padding dgrid-column-0 field-_item" role="gridcell">
               								<span style="margin-left: 0px">Muhammad Walton</span>
               							</td>
               							<td class="dgrid-cell dgrid-cell-padding dgrid-column-1 field-phoneNumber" role="gridcell"></td>
               							<td class="dgrid-cell dgrid-cell-padding dgrid-column-2 field-email" role="gridcell"></td>
               						</tr>
               					</table>
               				</div>
               			</div>
               		</div>
               		<div class="dgrid-header-scroll dgrid-scrollbar-width ui-widget-header" style="height: 34px;"></div>
               		<div class="dgrid-footer dgrid-footer-hidden"></div>
               	</div>
           	</div>
    	</div>
	</div>
</body>
</html>
