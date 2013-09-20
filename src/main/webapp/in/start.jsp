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
    <link rel="stylesheet" type="text/css" href="http://ajax.aspnetcdn.com/ajax/jquery.dataTables/1.9.4/css/jquery.dataTables.css"><jsp:text/></link>
    <script type="text/javascript" charset="utf8" src="http://ajax.aspnetcdn.com/ajax/jquery.dataTables/1.9.4/jquery.dataTables.min.js"><jsp:text/></script>
  </head>
  <body bgcolor="white">
    <script type="text/javascript">
      <![CDATA[
        $(document).ready(function() {
          $('#employeelist').dataTable();
          //$("#payday").click(alert('yo'));
        });      
      ]]>
    </script>
    <h3>Welcome to the QBO Plugin Sample</h3>
    <table id="employeelist">
      <thead>
        <tr>
          <th>Column 1</th>
          <th>Column 2</th>
          <th>etc</th>
        </tr>
      </thead>
      <tbody>
        <tr>
          <td>Row 1 Data 1</td>
          <td>Row 1 Data 2</td>
          <td>etc</td>
        </tr>
        <tr>
          <td>Row 2 Data 1</td>
          <td>Row 2 Data 2</td>
          <td>etc</td>
        </tr>
      </tbody>
    </table>
  </body>
</html>
