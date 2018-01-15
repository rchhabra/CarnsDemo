<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="ancillary" tagdir="/WEB-INF/tags/addons/traveladdon/responsive/ancillary"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="offerGroupCode" value="${param.offerGroupCode}" />
<c:set var="index" value="${param.index}" />
<c:set var="tabIndex" value="${param.tabIndex}" />
<c:set var="travellerMinOfferGroupQty" value="${param.travellerMinOfferGroupQty}" />
<c:set var="travellerMaxOfferGroupQty" value="${param.travellerMaxOfferGroupQty}" />
<ancillary:multipleOfferItems offerGroupCode="${offerGroupCode}" index="${index}" tabIndex="${tabIndex}" travellerMinOfferGroupQty="${travellerMinOfferGroupQty}" travellerMaxOfferGroupQty="${travellerMaxOfferGroupQty}" />