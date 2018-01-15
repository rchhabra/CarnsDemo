<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/responsive/common" %>
<%@ taglib prefix="company" tagdir="/WEB-INF/tags/addons/travelcommerceorgaddon/responsive/company" %>
<%@ taglib prefix="org-common" tagdir="/WEB-INF/tags/addons/travelcommerceorgaddon/responsive/common" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<spring:htmlEscape defaultHtmlEscape="true"/>

<c:if test="${empty cancelUrl}">
    <spring:url value="/my-company/organization-management/manage-permissions/view" var="cancelUrl" htmlEscape="false">
        <spring:param name="permissionCode" value="${b2BPermissionForm.originalCode}"/>
    </spring:url>
</c:if>
<c:if test="${empty saveUrl}">
    <spring:url value="/my-company/organization-management/manage-permissions/edit" var="saveUrl" htmlEscape="false">
        <spring:param name="permissionCode" value="${b2BPermissionForm.originalCode}"/>
    </spring:url>
</c:if>
<template:page pageTitle="${pageTitle}">
    <div class="my-company-section container">
        <div class="col-xs-12 col-sm-3 side-to-top">
            <aside id="sidebar">
                <div class="promotions hidden-xs">
                    <cms:pageSlot position="SideContent" var="feature" class="accountPageSideContent">
                        <cms:component component="${feature}" element="section"/>
                    </cms:pageSlot>
                </div>
            </aside>
        </div>
        <div class="col-xs-12 col-sm-9">
            <org-common:headline url="${cancelUrl}" labelKey="text.company.managePermissions.edit.page.title"
                                 backLabelKey="text.company.managePermissions.edit.back"/>
            
            <div class="panel panel-primary panel-list">
                <div class="panel-heading">
                    <h3 class="title"><spring:theme code="text.company.managePermissions.edit.page.title"
                                                    text="Edit Permission"/></h3>
                </div>
                <div class="panel-body">
                    <company:b2bPermissionForm cancelUrl="${cancelUrl}" saveUrl="${saveUrl}"
                                               b2BPermissionForm="${b2BPermissionForm}"/>
                </div>
            </div>
        </div>
    </div>
</template:page>
