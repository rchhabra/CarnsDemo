<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/responsive/common" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/responsive/formElement" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="org-common" tagdir="/WEB-INF/tags/addons/travelcommerceorgaddon/responsive/common" %>
<spring:htmlEscape defaultHtmlEscape="true"/>

<spring:url value="/my-company/organization-management/manage-permissions" var="cancelUrl" htmlEscape="false"/>

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
            <org-common:headline url="${cancelUrl}" labelKey="text.company.managePermissions.create.permission.title"
                                 backLabelKey="text.company.managePermissions.create.back"/>
            <div class="panel panel-primary panel-list">
                <div class="panel-heading">
                    <h3 class="title"><spring:theme code="text.company.managePermissions.create.permission.panel.title"
                                                    text="New Permission"/></h3>
                </div>
                <div class="panel-body">
                    <div id="addNewPermissionForm">
                        <div class="fieldset">
                            <legend class="sr-only"><spring:theme code="text.company.managePermissions.create.permission.sronly"
                                                                  text="Enter New Permission details"/></legend>
                            <div class="row">
                                <div class="form-group col-sm-6 col-xs-12">
                                    <label>
                                        <spring:theme code="text.company.managePermissions.create.type.label"/>
                                    </label>
                                    <select id="selectNewPermissionType" name="selectNewPermissionType" class="form-control">
                                        <option selected="selected"><spring:theme
                                                code="text.company.managePermissions.selectBox.permissionType"/></option>
                                        <c:forEach items="${b2bPermissionTypes}" var="b2BPermission">
                                            <option value="${fn:escapeXml(b2BPermission.code)}">${fn:escapeXml(b2BPermission.name)}</option>
                                        </c:forEach>
                                    </select>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template:page>
