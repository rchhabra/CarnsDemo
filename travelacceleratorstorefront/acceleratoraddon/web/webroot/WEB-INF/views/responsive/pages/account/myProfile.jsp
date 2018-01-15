<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<spring:htmlEscape defaultHtmlEscape="true" />
<div class="panel-heading">
	<h3 class="title title-collapse">
		<spring:theme code="text.account.personalDetails.title" text="My Personal Details" />
	</h3>
	<button class="show-hide-button" type="button" data-toggle="collapse" data-target="#personal-details" aria-expanded="true" aria-controls="personal-details">
		<spring:theme code="text.account.personalDetails.button.collapseExpand" text="Collapse / Expand" />
	</button>
</div>
<div class="panel-body collapse in" id="personal-details">
	<%-- List group --%>
	<ul class="list-group">
		<li class="list-group-item row">
			<div class="col-xs-12 col-sm-8">${fn:escapeXml(title.name)}&nbsp;${fn:escapeXml(customerData.firstName)}&nbsp;${fn:escapeXml(customerData.lastName)}</div>
			<div class="col-xs-12 col-sm-4">
				<a href="update-profile" class="btn btn-primary btn-block">
					<spring:theme code="text.account.profile.updatePersonalDetails" text="Update personal details" />
				</a>
			</div>
		</li>
		<li class="list-group-item row">
			<div class="col-xs-12 col-sm-4">${fn:escapeXml(customerData.displayUid)}</div>
			<div class="col-xs-12 col-sm-4">
				<a class="btn btn-primary btn-block" href="update-email">
					<spring:theme code="text.account.profile.updateEmail" text="Update email" />
				</a>
			</div>	
			<div class="col-xs-12 col-sm-4">
				<a class="btn btn-primary btn-block" href="update-password">
					<spring:theme code="text.account.profile.changePassword" text="Change password" />
				</a>
			</div>					
		</li>		
	</ul>
</div>
