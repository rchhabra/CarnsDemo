<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/responsive/formElement"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ attribute name="saveUrl" required="true" type="java.lang.String"%>
<%@ attribute name="cancelUrl" required="true" type="java.lang.String"%>
<%@ attribute name="b2BBudgetForm" required="true" type="de.hybris.platform.travelcommerceorgaddon.forms.B2BBudgetForm"%>
<%@ attribute name="addEditLabel" type="java.lang.String"%>
<spring:htmlEscape defaultHtmlEscape="true" />
<spring:theme code="text.store.dateformat.datepicker.selection" text="dd/mm/yyyy" var="dateFormatForDatePicker" />
<spring:theme code="text.store.dateformat.datepicker.selection.hint" text="dd/mm/yyyy" var="dateFormatHint" />
<div class="panel panel-primary panel-list">
	<div class="panel-heading">
		<h3 class="title">
			<spring:theme code="${addEditLabel}"></spring:theme>
		</h3>
	</div>
	<div class="panel-body">
		<form:form cssClass="y_b2bBudgetform" action="${saveUrl}" id="editB2bBudgetform" data-date-format-for-date-picker="${dateFormatForDatePicker}" data-date-format-hint="${dateFormatHint}" commandName="b2BBudgetForm" method="POST">
			<fieldset class="fieldset">
				<legend class="sr-only">Enter New Budget details</legend>
				<form:input type="hidden" name="originalCode" path="originalCode" id="originalCode" />
				<div class="row">
					<div class="form-group col-sm-6 col-xs-12">
						<formElement:formInputBox idKey="text.company.budget.budgetId" labelKey="text.company.budget.budgetId" path="code" inputCSS="text" mandatory="true" />
					</div>
					<div class="form-group col-sm-6 col-xs-12">
						<formElement:formInputBox idKey="text.company.budget.name" labelKey="text.company.budget.name" path="name" inputCSS="text" mandatory="true" />
					</div>
				</div>
				<div class="row">
					<div class="form-group col-sm-6 col-xs-12 nowrap">
						<formElement:formSelectBox idKey="text.company.budget.unit" skipBlankMessageKey="text.company.costCenter.selectBox.unit" labelKey="text.company.costCenter.unit.title" path="parentB2BUnit" mandatory="true" selectCSSClass="form-control" items="${b2bUnits}" />
					</div>
					<div class="form-group col-sm-6 col-xs-12 nowrap">
						<formElement:formInputBox idKey="text.company.budget.amount" labelKey="text.company.budget.amount" path="budget" inputCSS="text" mandatory="true" placeholder="00.00" />
					</div>
				</div>
				<div class="row">
					<div class="form-group col-sm-6 col-xs-12">
						<formElement:formSelectBox idKey="text.company.budget.currency" skipBlankMessageKey="text.company.costCenter.selectBox.currency" labelKey="text.company.costCenter.currency.title" path="currency" mandatory="true" selectCSSClass="form-control" items="${b2bCostCenterCurrencies}" />
					</div>
				</div>
				<div class="row">
					<div class="form-group col-sm-6 col-xs-12 nowrap">
						<formElement:formInputBox idKey="startDate" labelKey="text.company.budget.startDate" path="startDate" inputCSS="col-xs-12 datepicker input-grid form-control y_datePickerStart" mandatory="true" placeholder="text.company.budget.startDate" />
					</div>
					<div class="form-group col-sm-6 col-xs-12 nowrap">
						<formElement:formInputBox idKey="endDate" labelKey="text.company.budget.endDate" path="endDate" inputCSS="col-xs-12 datepicker input-grid form-control y_datePickerEnd" mandatory="true" placeholder="text.company.budget.endDate" />
					</div>
				</div>
				<div class="row">
					<div class="form-group col-xs-12 col-sm-6 bottom-align-parent btn-margin">
						<ycommerce:testId code="Budget_Save_button">
							<button type="submit" class="btn btn-primary btn-block bottom-align" id="SubmitBudget_button">
								<spring:theme code="text.company.budget.saveButton.displayName" />
							</button>
						</ycommerce:testId>
					</div>
					<div class="form-group col-xs-12 col-sm-6  bottom-align-parent">
						<ycommerce:testId code="Budget_Cancel_button">
							<a href="${cancelUrl}" class="btn btn-primary btn-block bottom-align">
								<spring:theme code="text.company.budget.cancelButton.displayName" />
							</a>
						</ycommerce:testId>
					</div>
				</div>
			</fieldset>
		</form:form>
	</div>
</div>