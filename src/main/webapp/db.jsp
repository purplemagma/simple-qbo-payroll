<?xml version="1.0" encoding="UTF-8" ?> 
<html
  xmlns:jsp="http://java.sun.com/JSP/Page"
  xmlns:c="http://java.sun.com/jsp/jstl/core"
  xmlns:fn="http://java.sun.com/jsp/jstl/functions" >
  <head>
    <jsp:useBean id="database" class="com.purplemagma.qbosimplepayroll.Database"></jsp:useBean>
    <jsp:directive.page contentType="text/html;charset=UTF-8"></jsp:directive.page> 
    <script type="text/javascript" src="https://code.jquery.com/jquery-2.0.3.js"><jsp:text/></script>
    <script type="text/javascript" src="js/utils.js"><jsp:text/></script>
    <script type="text/javascript">
      $(document).ready(function() {
        $('#createTablesButton').click(restCall.curry('database/createTables'));
        $('#deleteTablesButton').click(restCall.curry('database/deleteTables'));
        $('#checkTablesButton').click(restCall.curry('database/checkTables'));
      });
    </script>
  </head>
  <body bgcolor="white">
    <div>Status:</div>
    <div id="progress"><jsp:text/></div><br/>
    <div>Extra info:</div>
    <div id="extraInfo"><jsp:text/></div><br/>
    <button id="createTablesButton">Create Tables</button>
    <button id="deleteTablesButton">Delete Tables</button>
    <button id="checkTablesButton">Check Tables</button>
  </body>
</html>
