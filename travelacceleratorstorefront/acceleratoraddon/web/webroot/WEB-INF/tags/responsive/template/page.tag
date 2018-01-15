<%@ tag body-content="scriptless" trimDirectiveWhitespaces="true"%>
<%@ attribute name="pageTitle" required="false" rtexprvalue="true"%>
<%@ attribute name="pageCss" required="false" fragment="true"%>
<%@ attribute name="pageScripts" required="false" fragment="true"%>
<%@ attribute name="hideHeaderLinks" required="false"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>
<%@ taglib prefix="header" tagdir="/WEB-INF/tags/addons/travelacceleratorstorefront/responsive/common/header"%>
<%@ taglib prefix="footer" tagdir="/WEB-INF/tags/addons/travelacceleratorstorefront/responsive/common/footer"%>
<%@ taglib prefix="session" tagdir="/WEB-INF/tags/addons/travelacceleratorstorefront/responsive/common/session"%>
<%@ taglib prefix="processing" tagdir="/WEB-INF/tags/addons/travelacceleratorstorefront/responsive/common/processing"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/responsive/common"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<template:master pageTitle="${pageTitle}">
	<jsp:attribute name="pageCss">
		<jsp:invoke fragment="pageCss" />
	</jsp:attribute>
	<jsp:attribute name="pageScripts">
		<jsp:invoke fragment="pageScripts" />
	</jsp:attribute>
	<jsp:body>
		<main class="page-margin" data-currency-iso-code="${fn:escapeXml(currentCurrency.isocode)}">

			<header:header hideHeaderLinks="${hideHeaderLinks}" />
			
				<common:globalMessages />
		        <session:sessionExpirationPreWarningModal />
				<jsp:doBody />
			<processing:processingModal visible="${not empty fareSelection}" />
			<footer:footer />
		</main>

	</jsp:body>
</template:master>
