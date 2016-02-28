<%@ page import="com.google.appengine.api.blobstore.BlobstoreServiceFactory" %>
<%@ page import="com.google.appengine.api.blobstore.BlobstoreService" %>

<%
    BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
%>


<html>
    <head>
        <title>Upload Test</title>
      	<meta http-equiv="refresh" content="5;url=http://code.google.com/p/cityadventure/">
    </head>
    <body>
    Welcome! 
    City
    Adventure
    is 
    under development! <br/>
    Go to <a href="http://code.google.com/p/cityadventure/">project host</a> in 5 seconds... 
    </body>
</html>