<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="hideHeaderLinks" required="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/responsive/nav"%>
<spring:htmlEscape defaultHtmlEscape="true" />
<cms:pageSlot position="TopHeaderSlot" var="component" element="div" class="container">
	<cms:component component="${component}" />
</cms:pageSlot>
<header class="row header">
	<nav class="navbar navbar-default navbar-fixed-top">
		<div class="user-options-wrapper">
			<div class="container">
				<div class="navbar-brand ">
					<cms:pageSlot position="SiteLogo" var="logo" limit="1">
						<cms:component component="${logo}" />
					</cms:pageSlot>
				</div>

				<div class="navbar-right">
					<ul class="user-options mt-10 flexbox row">
						<c:if test="${uiExperienceOverride}">
							<li>
								<c:url value="/_s/ui-experience?level=" var="backToMobileStoreUrl" />
								<a href="${backToMobileStoreUrl}">
									<spring:theme code="text.backToMobileStore" />
								</a>
							</li>
						</c:if>
						<sec:authorize access="!hasAnyRole('ROLE_ANONYMOUS')">
							<c:set var="maxNumberChars" value="25" />
							<c:if test="${fn:length(user.firstName) gt maxNumberChars}">
								<c:set target="${user}" property="firstName" value="${fn:substring(user.firstName, 0, maxNumberChars)}..." />
							</c:if>
							<li class="y_loggedIn">
								<ycommerce:testId code="header_LoggedUser">
									<spring:theme code="header.welcome" arguments="${user.firstName},${user.lastName}" htmlEscape="true" />
								</ycommerce:testId>
							</li>
							<li class="y_myAccountLink">
								<ycommerce:testId code="header_myAccount">
									<a href="<c:url value="/my-account/profile"/>">
										<ycommerce:testId code="header_LoggedUser">
											<spring:theme code="header.link.account" htmlEscape="true" />
										</ycommerce:testId>
									</a>
								</ycommerce:testId>
							</li>
						</sec:authorize>
						<sec:authorize access="hasAnyRole('ROLE_ANONYMOUS')">
							<li class="y_signButton">
								<ycommerce:testId code="header_Login_link">
									<a href="<c:url value="/login"/>">
										<spring:theme code="header.link.login" />
									</a>
								</ycommerce:testId>
							</li>
						</sec:authorize>
						<sec:authorize access="!hasAnyRole('ROLE_ANONYMOUS')">
							<li class="y_signButton">
								<ycommerce:testId code="header_signOut">
									<a href="<c:url value='/logout'/>">
										<spring:theme code="header.link.logout" />
									</a>
								</ycommerce:testId>
							</li>
						</sec:authorize>
						<cms:pageSlot position="HeaderLinks" var="component">
							<cms:component component="${component}" element="li" />
						</cms:pageSlot>
					</ul>
				</div>
			</div>
		</div>
		<div class="container">
			<div class="navbar-header">
				<button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar-collapse" aria-expanded="false">
					<span class="sr-only"><spring:theme code="sr.header.toggle.navigation" /></span> <span class="icon-bar"></span> <span class="icon-bar"></span> <span class="icon-bar"></span>
				</button>
			</div>
			<div class="collapse navbar-collapse" id="navbar-collapse">
				<nav:topNavigation />
			</div>
		</div>
	</nav>
</header>
