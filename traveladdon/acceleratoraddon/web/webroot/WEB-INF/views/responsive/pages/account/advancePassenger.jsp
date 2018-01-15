<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<spring:htmlEscape defaultHtmlEscape="true" />
<div class="panel-heading">
	<h3 class="title title-collapse">
		<spring:theme code="text.page.myaccount.advancedpassengerdetails" text="Advanced Passenger Details" />
	</h3>
	<button class="show-hide-button" type="button" data-toggle="collapse" data-target="#advance-passenger" aria-expanded="true" aria-controls="advance-passenger">
		<spring:theme code="text.page.myaccount.advancedpassengerdetails.button.collapseExpand" text="Collapse / Expand" />
	</button>
</div>
<div class="panel-body collapse in" id="advance-passenger">
	<form:form commandName="apiForm" action="advance-passenger" method="POST">
		<div id="y_apiFormErrors">
			<form:errors path="*" cssClass="alert alert-danger" element="div" />
		</div>
		<fieldset class="fieldset">
			<div class="row">
				<div class="form-group col-xs-12">
					<label for="ap-doctype">
						<spring:theme code="text.page.myaccount.advancedpassengerdetails.documenttype" text="Document Type" />
					</label>
					<form:select path="documentType" class="col-xs-12 col-sm-5 form-control">
						<form:option value="" disabled="true" selected="selected">
							<spring:theme code="text.page.myaccount.advancedpassengerdetails.droplist.default" text="Select" />
						</form:option>
						<form:option value="PASSPORT">
							<spring:theme code="text.page.checkin.api.doctype.passport" text="Passport" />
						</form:option>
						<form:option value="NATIONAL_ID">
							<spring:theme code="text.page.checkin.api.doctype.nationalId" text="National ID Card" />
						</form:option>
					</form:select>
				</div>
			</div>
			<div class="row">
				<div class="form-group col-xs-12 col-sm-2">
					<label for="ap-title">
						<spring:theme code="text.page.myaccount.advancedpassengerdetails.title" text="Title" />
					</label>
					<form:select path="title" class="col-xs-12 form-control">
						<form:option value="" disabled="true" selected="selected">
							<spring:theme code="text.page.default.select" text="Please select" />
						</form:option>
						<form:options items="${titles}" itemValue="code" itemLabel="name" htmlEscape="true"/>
					</form:select>
				</div>
				<div class="form-group col-xs-12 col-sm-5">
					<label for="ap-first-name">
						<spring:theme code="text.page.myaccount.advancedpassengerdetails.firstname" text="First name" />
					</label>
					<form:input path="firstname" type="text" class="col-xs-12 form-control" />
				</div>
				<div class="form-group col-xs-12 col-sm-5">
					<label for="ap-last-name">
						<spring:theme code="text.page.myaccount.advancedpassengerdetails.lastname" text="Last name" />
					</label>
					<form:input path="lastname" type="text" class="col-xs-12 form-control" />
				</div>
			</div>
			<div class="row">
				<div class="form-group col-xs-12 col-sm-12 col-md-4">
					<label for="ap-dob-day">
						<spring:theme code="text.page.myaccount.advancedpassengerdetails.dob" text="Date Of Birth" />
					</label>
					<form:input id="y_dob_datepicker" path="dateOfBirth" class="col-xs-12 form-control datepicker" />
				</div>
				<div class="form-group col-xs-12 col-sm-12 col-md-6">
					<label>Gender</label>
					<label class="radio-inline col-xs-4">
						<form:radiobutton path="gender" value="male" checked="checked" />
						<spring:theme code="text.page.myaccount.advancedpassengerdetails.gender.male" text="Male" />
					</label>
					<label class="radio-inline col-xs-4">
						<form:radiobutton path="gender" value="female" />
						<spring:theme code="text.page.myaccount.advancedpassengerdetails.gender.female" text="Female" />
					</label>
				</div>
			</div>
			<div class="row">
				<div class="form-group col-xs-12 col-sm-12 col-md-4">
					<label for="ap-doc-nimber">
						<spring:theme code="text.page.myaccount.advancedpassengerdetails.documentnumber" text="Document Number" />
					</label>
					<form:input path="documentNumber" class="col-xs-12 form-control" />
				</div>
				<div class="form-group col-xs-12 col-sm-12 col-md-4">
					<label for="ap-exp-day">
						<spring:theme code="text.page.myaccount.advancedpassengerdetails.expirydate" text="Expiry Date" />
					</label>
					<form:input id="y_expirydate_datepicker" path="documentExpiryDate" class="col-xs-12 form-control datepicker" />
				</div>
				<div class="form-group col-xs-12 col-sm-12 col-md-4">
					<label for="ap-cit">
						<spring:theme code="text.page.myaccount.advancedpassengerdetails.nationality" text="Nationality" />
					</label>
					<form:select path="nationality" class="col-xs-12 form-control">
						<form:option value="" disabled="true" selected="selected">
							<spring:theme code="text.page.myaccount.advancedpassengerdetails.droplist.default" text="Select" />
						</form:option>
						<form:options items="${nationalities}" itemValue="isocode" itemLabel="name" htmlEscape="true"/>
					</form:select>
				</div>
			</div>
			<div class="row">
				<div class="form-group col-xs-12 col-sm-12 col-md-5">
					<label for="ap-issue">
						<spring:theme code="text.page.myaccount.advancedpassengerdetails.countryofissue" text="Country of Issue" />
					</label>
					<form:select path="countryOfIssue" class="col-xs-12 form-control">
						<form:option value="" disabled="true" selected="selected">
							<spring:theme code="text.page.myaccount.advancedpassengerdetails.droplist.default" text="Select" />
						</form:option>
						<form:options items="${countries}" itemValue="isocode" itemLabel="name" htmlEscape="true"/>
					</form:select>
				</div>
				<div class="form-group col-xs-6 col-sm-6 col-md-3 col-md-offset-1 bottom-align-parent">
					<button class="btn btn-primary btn-block bottom-align">
						<spring:theme code="text.page.myaccount.advancedpassengerdetails.save.btn" text="Save" />
					</button>
				</div>
				<div class="form-group col-xs-6 col-sm-6 col-md-3 bottom-align-parent">
					<button type="button" class="btn btn-default btn-block bottom-align backToHome">
						<spring:theme code="text.page.myaccount.advancedpassengerdetails.cancel.btn" text="Cancel" />
					</button>
				</div>
			</div>
		</fieldset>
	</form:form>
</div>
