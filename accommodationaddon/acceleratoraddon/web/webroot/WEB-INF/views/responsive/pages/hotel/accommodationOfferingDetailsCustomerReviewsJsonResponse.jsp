<%@ page trimDirectiveWhitespaces="true" contentType="application/json"%>
<%@ taglib prefix="json" uri="http://www.atg.com/taglibs/json"%>
<%@ taglib prefix="accommodationDetails" tagdir="/WEB-INF/tags/addons/accommodationaddon/responsive/accommodationdetails"%>
<json:object escapeXml="false">
	<json:property name="customerReviewsPagedHtml">
		<accommodationDetails:customerReviewListItem accommodationCustomerReviews="${customerReviewSearchPageData.results}" />
	</json:property>
	<json:property name="hasMoreReviews" value="${customerReviewSearchPageData.pagination.currentPage < customerReviewSearchPageData.pagination.numberOfPages - 1}" />
</json:object>
