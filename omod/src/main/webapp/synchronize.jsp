<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="/WEB-INF/template/header.jsp"%>

<%@ include file="template/localHeader.jsp"%>

<openmrs:require privilege="Manage Locations" otherwise="/login.htm" redirect="/module/kenyamflsync/synchronize.form" />

<style type="text/css">
	#console {
		padding: 4px;
		background-color: #444;
		color: white;
		font-family: monospace;
		font-size: 12px;
		max-height: 200px;
		overflow-y: scroll;
	}
	.console-msg-error {
		color: red;
	}
</style>

<script type="text/javascript">
	var lastMessageId = 0;

	function update_status() {
		jQuery.getJSON(openmrsContextPath + '/module/kenyamflsync/status.form', { sinceMessageId: lastMessageId }, function(data) {
			if (data.busy) {
				jQuery('#start-button').attr('disabled', 'disabled');
			} else {
				jQuery('#start-button').removeAttr('disabled');
			}

			// Append each new message to console
			for (var m = 0; m < data.messages.length; ++m) {
				var message = data.messages[m];
				var timestamp = new Date(message.timestamp).toTimeString();
				var html = '<div ' + (message.error ? 'class="console-msg-error"' : '') + '>' + timestamp + ' ' + message.message + '</div>';

				jQuery('#console').append(html);
				lastMessageId = message.id;
			}

			// Auto-scroll to end of messages
			var consoleElmt = jQuery('#console').get(0);
			consoleElmt.scrollTop = consoleElmt.scrollHeight;

			setTimeout("update_status()", 2000);
		});
	}

	jQuery(function() {
		update_status();
	});
</script>

<b class="boxHeader">Synchronize with remote spreadsheet</b>
<form:form method="post" commandName="options" cssClass="box">
	<table border="0">
		<tr>
			<td>MFL code attribute</td>
			<td>
				<form:select path="attributeType">
					<form:options items="${locationAttributeTypes}" itemValue="locationAttributeTypeId" itemLabel="name" />
				</form:select>
			</td>
		</tr>
		<tr>
			<td>Spreadsheet URL</td>
			<td><form:input path="spreadsheetUrl" cssStyle="width: 400px" /></td>
		</tr>
		<tr>
			<td>&nbsp;</td>
			<td><input type="submit" value="Start" id="start-button" /></td>
	</table>
</form:form>

<br />

<b class="boxHeader">Output</b>
<div class="box">
	<div id="console"></div>
</div>

<%@ include file="/WEB-INF/template/footer.jsp"%>