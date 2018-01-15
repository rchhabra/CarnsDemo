<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="org-common" tagdir="/WEB-INF/tags/addons/travelcommerceorgaddon/responsive/common"%>
<%@ taglib prefix="company" tagdir="/WEB-INF/tags/addons/travelcommerceorgaddon/responsive/company"%>
<%@ attribute name="users" required="true" type="java.util.List"%>
<%@ attribute name="action" required="true" type="java.lang.String"%>
<%@ attribute name="createUrl" required="true" type="java.lang.String"%>
<%@ attribute name="editUrl" required="true" type="java.lang.String"%>
<%@ attribute name="role" required="true" type="java.lang.String"%>
<spring:htmlEscape defaultHtmlEscape="true" />
<div class="panel panel-primary panel-list account-list">
	<org-common:selectEntityHeadline labelKey="text.company.manage.units.header.${action}" count="${fn:length(users)}" />
	<div class="panel-body collapse in">
		<div class="form account-cards">
			<fieldset class="fieldset">
				<div class="row">
					<div class="account-cards clearfix">
						<c:forEach items="${users}" var="user" varStatus="rows">
							<div class="card col-xs-12 col-sm-6 col-md-4">
								<spring:url value="/my-company/organization-management/manage-units/viewuser/" var="viewUserUrl" htmlEscape="false">
									<spring:param name="unit" value="${unit.uid}" />
									<spring:param name="user" value="${user.uid}" />
								</spring:url>
								<spring:url value="/my-company/organization-management/manage-units/members/deselect" var="removeUserUrl" htmlEscape="false">
									<spring:param name="user" value="${user.uid}" />
									<spring:param name="role" value="${role}" />
								</spring:url>
								<ul id="id-${rows.index}">
									<li>
										<a href="${viewUserUrl}"> ${fn:escapeXml(user.firstName)}&nbsp;${fn:escapeXml(user.lastName)} </a>
									</li>
									<li>${fn:escapeXml(user.uid)}</li>
								</ul>
								<div class="form-group account-cards-actions col-xs-12">
									<a href="#" url="${removeUserUrl}" class="y_removeUserItem" data-rowid="${rows.index}">
										<button class="btn btn-primary btn-block">
											<spring:theme code="text.company.manage.units.remove" />
										</button>
									</a>
								</div>
							</div>
						</c:forEach>
					</div>
				</div>
				<div class="accountActions-link  col-xs-12 col-sm-5 pull-right">
					<a href="${createUrl}&role=${role}">
						<button class="btn btn-primary btn-block">
							<spring:theme code="text.company.manage.units.users.button.createnew" />
						</button>
					</a>
				</div>
				<div class="accountActions-link  col-xs-12 col-sm-5 pull-right">
					<a href="${editUrl}">
						<button class="btn btn-primary btn-block">
							<spring:theme code='text.company.addExisting.button' />
						</button>
					</a>
				</div>
			</fieldset>
		</div>
	</div>
</div>