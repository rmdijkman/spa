<html>
<script>
	eventbus.subscribe("pubsub.gs","a");

	function update(vari, valu){
		document.getElementById("content").innerHTML = valu;
	}
</script>
<body>
	<div id="content">asd</div>
</body>
</html>