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
        bufferRows : 500,
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
                    height: 250
                },
                colors : ["#47a6c0", "#445275", "#CC0E4E"],
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
                    itemMarginTop: 10,
                    itemMarginBottom: 17,
                    itemStyle: {
                        lineHeight: "15px"
                    },
                    symbolWidth: 3,
                    useHTML: true,
                    labelFormatter: function() {
                        this.legendSymbol.element.setAttribute("height", "40");
                        this.legendSymbol.element.setAttribute("y", "3");
                        this.legendSymbol.element.setAttribute("ry", "0");
                        this.legendSymbol.element.setAttribute("rx", "0");
                        return "<div class='fancyMoneyLight marginLeft10'>"+this.formattedY + '</div><div class=\'fancyText upperCase marginLeft10\'>' +  this.name + "</div>";
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
                        size: "17",
                        center: ["50%","45%"]
                    }
                },
                series : [ {
                    data : data,
                    type : "pie",
                    cursor : "pointer",
                    innerSize : "50%",
                    id : "mainSeries"
                }]
            });
    });    
        function qboXDMReady() {
            document.getElementById("payrun").onclick = function () {
                var baseUrl = document.location.origin + document.location.pathname.substr(0,document.location.pathname.lastIndexOf("/"));
                qboXDM.navigate("xdmtrowser://"+baseUrl+"/trowser.jsp");
            };
          }
// ]]>
  </script>
  <div>
    <div class="stage">
      <div class="stage-header">
        <span class="page-title">Employees</span>
          <div>
                  <div class="topButtonBar floatRight">
                    <button type="button" class="button primary" id="payrun">New Pay Run</button>
                  </div>
            </div>
      </div>
      <div class="stage-content">
                <div style="width: 100%; height: 250px">
                    <div class="floatLeft" style="margin-top: 30px; margin-right: 30px">
                        <div class="fancyMoney">$2700</div>
                        <div class="fancySummarySubText upperCase">Total Payroll Cost</div>
                    </div>
          <div id="chart" style="width: 300px; height: 250px" class="floatLeft"><jsp:text/></div>
                </div>
      </div>
    </div>
    <div class="page-content">
      <div class="divContent">
          <div>
                  <div class="topButtonBar floatRight">
                    <button type="button" class="button">Add employee</button>
                  </div>
            </div>
        <div class="tallBreakDiv"><jsp:text/></div>
        <div class="listScroller" style="height: 210px;">
          <div id="grid" class="qboDataGrid"><jsp:text/></div>
        </div>
      </div>
    </div>
  </div>
</body>
</html>
