<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="/WEB-INF/template/header.jsp"%>

<%@ include file="template/localHeader.jsp"%>

<style type="text/css">
	.console {
		padding: 4px;
		background-color: #DDD;
	}
</style>

<b class="boxHeader">Synchronize with remote spreadsheet</b>
<form method="post" class="box">
	<p>Spreadsheet URL:
		<input name="spreadsheetUrl" value="${spreadsheetUrl}" style="width: 400px" />
		<c:if test="${spreasheetUrlError}">
			<span class="error"><c:out value="${spreasheetUrlError}" /></span>
		</c:if>
	</p>

	<input type="submit" value="Start" />

	<fielset><legend>Output</legend>
		<div class="console"><c:out value="${taskOutput}" /></div>
	</fielset>
</form>

<%@ include file="/WEB-INF/template/footer.jsp"%>