<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/responsive/formElement" %>
<%@ taglib prefix="org-common" tagdir="/WEB-INF/tags/addons/travelcommerceorgaddon/responsive/common" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ attribute name="saveUrl" required="true" type="java.lang.String" %>
<%@ attribute name="cancelUrl" required="true" type="java.lang.String" %>
<%@ attribute name="isSelectBoxForPermission" required="false" type="java.lang.Boolean" %>
<%@ attribute name="b2BPermissionForm" required="true" type="de.hybris.platform.travelcommerceorgaddon.forms.B2BPermissionForm" %>
<spring:htmlEscape defaultHtmlEscape="true"/>

<div class="account-section-content row">
    <form:form action="${saveUrl}" id="b2BPermissionForm" commandName="b2BPermissionForm" method="POST">
        <fieldset class="fieldset">
            <legend class="sr-only"><spring:theme code="text.company.managePermissions.create.permission.sronly"
                                                  text="Enter New Permission details"/></legend>
            <div class="clearfix">
                <form:input type="hidden" name="originalCode" path="originalCode" id="originalCode"/>
                <form:input type="hidden" name="permissionType" path="permissionType" id="permissionType"/>
                <form:input type="hidden" name="b2BPermissionTypeData.code" path="b2BPermissionTypeData.code"
                            id="b2BPermissionTypeData.code"/>
                <form:input type="hidden" name="b2BPermissionTypeData.name" path="b2BPermissionTypeData.name"
                            id="b2BPermissionTypeData.name"/>
                <div class="form-group col-sm-6 col-xs-12">
                    <c:choose>
                        <c:when test="${isSelectBoxForPermission}">

                            <formElement:formSelectBox idKey="selectNewPermissionType"
                                                       labelKey="text.company.managePermissions.create.type.label"
                                                       skipBlankMessageKey="text.company.managePermissions.selectBox.permissionType"
                                                       mandatory="true" path="permissionType" selectCSSClass="form-control"
                                                       items="${b2bPermissionTypes}"/>
                        </c:when>
                        <c:otherwise>
                            <label>
                                <spring:theme code="text.company.managePermissions.type.label"/>
                            </label>
                        <span class="label-value">
                                ${fn:escapeXml(b2BPermissionForm.b2BPermissionTypeData.name)}
                        </span>

                        </c:otherwise>
                    </c:choose>
                </div>

                <div class="form-group col-xs-12 col-sm-6">
                    <formElement:formInputBox
                            idKey="text.company.managePermissions.name.label"
                            labelKey="text.company.managePermissions.name.label" path="code"
                            inputCSS="text"/>
                </div>

                <div class="form-group col-xs-12 col-sm-6 nowrap">
                    <formElement:formSelectBox
                            idKey="text.company.managePermissions.unit.label"
                            skipBlankMessageKey="text.company.costCenter.selectBox.unit"
                            labelKey="text.company.managePermissions.unit.label"
                            path="parentUnitName" mandatory="true" selectCSSClass="form-control" items="${b2bUnits}"/>
                </div>

                <c:if test="${b2BPermissionForm.b2BPermissionTypeData.code ne 'B2BBudgetExceededPermission' }">

                    <div class="form-group col-xs-12 col-sm-6 nowrap">
                        <c:if test="${b2BPermissionForm.b2BPermissionTypeData.code eq 'B2BOrderThresholdTimespanPermission'}">

                            <formElement:formSelectBox
                                    idKey="text.company.managePermissions.timespan.label"
                                    skipBlankMessageKey="text.company.managePermissions.selectBox.timespan"
                                    labelKey="text.company.managePermissions.timespan.label"
                                    path="timeSpan" mandatory="true" selectCSSClass="form-control" items="${b2bPeriodRanges}"/>

                        </c:if>
                    </div>

                    <div class="form-group col-xs-12 col-sm-6">
                        <formElement:formInputBox
                                idKey="text.company.managePermissions.value.label"
                                labelKey="text.company.managePermissions.value.label" path="value"
                                inputCSS="text" mandatory="true"/>
                    </div>

                    <div class="form-group col-xs-12 col-sm-6">
                        <formElement:formSelectBox
                                idKey="text.company.managePermissions.currency.label"
                                skipBlankMessageKey="text.company.costCenter.selectBox.currency"
                                labelKey="text.company.managePermissions.currency.label"
                                path="currency" mandatory="true" selectCSSClass="form-control"
                                items="${b2bCostCenterCurrencies}"/>
                    </div>
                </c:if>
            </div>

            <div class="row">
                <div class="form-group col-xs-12 col-sm-6 bottom-align-parent btn-margin">
                    <button type="submit" class="btn btn-primary btn-block bottom-align">
                        <spring:theme code="text.company.managePermissions.edit.saveButton"/>
                    </button>
                </div>
                <div class="form-group col-xs-12 col-sm-6  bottom-align-parent">
                    <org-common:back cancelUrl="${cancelUrl}"
                                     displayTextMsgKey="text.company.managePermissions.edit.cancelButton"/>
                </div>
            </div>
        </fieldset>
    </form:form>
</div>
