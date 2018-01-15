<%@ tag language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ attribute name="isPackage" required="false" type="java.lang.Boolean"%>
<spring:htmlEscape defaultHtmlEscape="true" />
<div class="panel-heading">
	<h3 class="title">
		<spring:message code="text.cms.upgradebundle.title" />
	</h3>
</div>
<div class="panel-body">
	<div class="row">
		<div class="col-sm-2 col-xs-4 icon-big">
			<div class="icon-upgrade"></div>
		</div>
		<div class="col-sm-6 col-xs-8">
			<span>
				<spring:message code="text.cms.upgradebundle.description" text="You haven't added this to your booking. Add Now!" />
			</span>
		</div>
		<div class="col-xs-12 col-sm-4">
			<a id="${not empty isPackage && isPackage ? 'y_packageUpgradeBundleOptionsButton' : 'y_upgradeBundleOptionsButton'}" href="#y_panel-upgrade" data-toggle="collapse" data-target="#y_panel-upgrade" aria-expanded="false" aria-controls="y_panel-upgrade" class="btn btn-primary btn-block" data-amend="${fn:escapeXml(amend)}">
                <span id="y_selectUpgradeSpan">
                    <spring:theme code="text.cms.upgradebundle.upgrade.options.button.selectOptions" text="Select Options" />
                </span>
				<span id="y_hideUpgradeSpan" hidden="true">
                    <spring:theme code="text.cms.upgradebundle.upgrade.options.button.hideSelectOptions" text="Hide Seat Options" />
                </span>

			</a>
		</div>
	</div>
	<div class="panel-upgrade row collapse" id="y_panel-upgrade"></div>
</div>
