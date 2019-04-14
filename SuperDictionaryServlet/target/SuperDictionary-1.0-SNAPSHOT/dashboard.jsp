<%-- 
    Document   : dashboard
    Created on : Apr 6, 2019, 6:13:51 PM
    Author     : shayankhan
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Analytics Dashboard</title>
    </head>
    <body>
        <h2>Usage Statistics</h2>
        <table border="1">
            <tr>
                <th>Metric</th>
                <th>Value</th>
            </tr>
            <tr>
                <td>Most common word</td>
                <td><%=request.getAttribute("topWord")%></td>
            </tr>
            <tr>
                <td>Most common operation</td>
                <td><%=request.getAttribute("topOperation")%></td>
            </tr>
            <tr>
                <td>Average response time</td>
                <td><%=request.getAttribute("avgResponseTime")%>ms</td>
            </tr>
        </table>
        <h2>Logs</h2>
        <p><%=request.getAttribute("logs")%></p>
    </body>
</html>
