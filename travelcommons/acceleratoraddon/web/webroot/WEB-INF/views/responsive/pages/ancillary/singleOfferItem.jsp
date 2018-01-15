<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="ancillary" tagdir="/WEB-INF/tags/addons/traveladdon/responsive/ancillary"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="offerGroupCode" value="${param.offerGroupCode}" />
<c:set var="index" value="${param.index}" />
<c:set var="tabIndex" value="${param.tabIndex}" />
<ancillary:singleOfferItem offerGroupCode="${offerGroupCode}" index="${index}" tabIndex="${tabIndex}" />