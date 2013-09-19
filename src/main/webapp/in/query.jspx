<?xml version="1.0" encoding="UTF-8" ?> 
<html
  xmlns:jsp="http://java.sun.com/JSP/Page"
  xmlns:c="http://java.sun.com/jsp/jstl/core"
  xmlns:fn="http://java.sun.com/jsp/jstl/functions" >
  <head>
    <jsp:directive.page contentType="text/html;charset=UTF-8"></jsp:directive.page> 
    <jsp:useBean id="payrollService" class="com.purplemagma.qbosimplepayroll.QBOSimplePayroll"></jsp:useBean>
    <jsp:setProperty name="payrollService" property="session" value="${pageContext.session}"></jsp:setProperty>
    <script type="text/javascript" src="https://code.jquery.com/jquery-2.0.3.js"><jsp:text/></script>
    <script type="text/javascript" src="/js/utils.js"><jsp:text/></script>
    <script type="text/javascript" src="/js/json2.js"><jsp:text/></script>
  </head>
  <body bgcolor="white">
      <script type="text/javascript">
      <![CDATA[
        function doQuery() {
          query = $("#sql").val();
          url = "/rest/in/doQuery?maxRows=30&query="+encodeURIComponent(query);
          $("#sqlResult").val('Running...')
          $.get(url, function(data) {
            $("#sqlResult").val(JSON.stringify(data));
          }).fail(function(data) {
            $("#sqlResult").val(data.responseText);
          });
          }
          $(document).ready(function() {
            $("#sqlSubmit").click(doQuery);
          });      
      ]]>
      </script>
      Company Id: ${payrollService.company.realmId}<br/>
      Business Name: ${payrollService.company.businessName}<br/>
      Custom query:<br/><textarea id="sql" cols="80" rows="10"><jsp:text/></textarea>
      <button type="submit" id="sqlSubmit">Submit SQL</button><br/>
      Result: <br/><textarea id="sqlResult" disabled="true" cols="80" rows="20"><jsp:text/></textarea><br/>
  </body>
</html>
