<!DOCTYPE html>
<html>
  <head>
    <style>
      #map {
        width: 745px;
        height: 550px;
      }
    </style>
    <script src="https://maps.googleapis.com/maps/api/js"></script>
    <script>

      var map = null;

      function initialize() {
        var mapCanvas = document.getElementById('map');
        var mapOptions = {
          center: new google.maps.LatLng(51.450095, 5.493081),
          zoom: 15,
          mapTypeId: google.maps.MapTypeId.ROADMAP
        }
        map = new google.maps.Map(mapCanvas, mapOptions);
        title2Marker = {};
        eventBus.subscribe("map.gs","marker");
        console.log("log");
      }

      //Test by giving in the console: marker = {title: 'test', position: {lat: 51.450095, lng: 5.493081}}
      function update(variable, value){
        console.log("log update");
        if (value.position == undefined){
          m = title2Marker[value.title];
          if (m != null){
            m.setMap(null);
          }
          title2Marker[value.title] = null;
        }else{
          m = title2Marker[value.title];
          if (m == null){
            m = new google.maps.Marker({
              position: value.position,
              map: map,
              title: value.title
            });
            title2Marker[value.title] = m;
          }
          m.setPosition(value.position);
        }
      }      

      google.maps.event.addDomListener(window, 'load', initialize);
        </script>
  </head>
  <body>
    <div id="map"></div>
  </body>
</html>