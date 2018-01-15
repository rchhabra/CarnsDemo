<%@ page trimDirectiveWhitespaces="true" contentType="application/json" %>
<%@ taglib prefix="json" uri="http://www.atg.com/taglibs/json" %>

<json:object escapeXml="false">
    <json:property name="valid" value="${isValid}"/>
</json:object>
