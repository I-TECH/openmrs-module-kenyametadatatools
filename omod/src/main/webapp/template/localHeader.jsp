<spring:htmlEscape defaultHtmlEscape="true" />
<ul id="menu">
	<li class="first"><a href="${pageContext.request.contextPath}/admin"><spring:message code="admin.title.short" /></a></li>

	<li <c:if test='<%= request.getRequestURI().contains("/synchronize") %>'>class="active"</c:if>>
		<a href="${pageContext.request.contextPath}/module/kenyametadatatools/mflsync/synchronize.form"><spring:message code="kenyametadatatools.mflsync.title" /></a>
	</li>

</ul>
<h2>
	<spring:message code="kenyametadatatools.title" />
</h2>
