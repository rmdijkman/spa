<html>
<script>
	b=1;
	runner.start("threading.gs");

	function run(){		
		document.getElementById("content").innerHTML = b;
		b = b + 1;
	}
</script>
<body>
	<div id="content">asd</div>
</body>
</html>