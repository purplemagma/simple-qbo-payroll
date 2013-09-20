<?xml version="1.0" encoding="UTF-8" ?> 
<html
  xmlns:jsp="http://java.sun.com/JSP/Page"
  xmlns:c="http://java.sun.com/jsp/jstl/core"
  xmlns:fn="http://java.sun.com/jsp/jstl/functions" >
  <head>
    <jsp:directive.page contentType="text/html;charset=UTF-8"></jsp:directive.page> 
    <script type="text/javascript" src="https://code.jquery.com/jquery-2.0.3.js"><jsp:text/></script>
    <script type="text/javascript">
      <![CDATA[
      $(document).ready(function() {
        $('#signUp').click(function() {
          window.location = "rest/auth/openid/initialize?isSigningUp=true&realmId=${param.realmId}";
        });
      });
      ]]>
    </script>
  </head>
  <body bgcolor="white">
    <h2>Simple Payroll is really cool</h2>
    <button id="signUp">Sign up Now</button>
  </body>
</html>
