<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<!DOCTYPE HTML>
<html>
    <head>
        <meta charset="UTF-8"/>
        <title>Krakow MongoDB User Group</title>
        <script src="http://ajax.googleapis.com/ajax/libs/jquery/1.9.1/jquery.min.js"></script>
        <script src="http://code.highcharts.com/highcharts.js"></script>
        <script src="http://code.highcharts.com/highcharts-more.js"></script>
        <script src="http://code.highcharts.com/modules/exporting.js"></script>
        <script src="http://netdna.bootstrapcdn.com/bootstrap/3.0.0/js/bootstrap.min.js"></script>
        <link rel="stylesheet" href="http://netdna.bootstrapcdn.com/bootstrap/3.0.0/css/bootstrap.min.css">
        <link rel="stylesheet" href="http://netdna.bootstrapcdn.com/bootstrap/3.0.0/css/bootstrap-theme.min.css">
        <link rel="icon" href="http://media.mongodb.org/favicon.ico" type="image/icon">
    </head>

    <body>

    <form class="well form-inline" id="aggForm">
        <input type="text" class="input-small" placeholder="Resource Name" id="resourceName" />
        <input type="text" class="input-small" placeholder="Start Date" id="startDate" />
        <input type="text" class="input-small" placeholder="End Date" id="endDate" />
        <button type="submit" class="btn btn-primary">Get Resources</button>
        <div id="docsNo"></div>
    </form>

    <div id="container" style="width:100%; height:600px;"></div>
	
	<script>
		$(function() {
		    Highcharts.setOptions({
                global: {
                    useUTC: false
                }
            });
			$('#aggForm').submit(function(e) {
				var resource = $('#resourceName').val();
				var startDate = $('#startDate').val();
				var endDate = $('#endDate').val();
				$.get('${pageContext.request.contextPath}/rest/' + resource + '/' + startDate + '/' + endDate, function(documents) {
				    var averages = [];
				    var ranges = [];
					documents.forEach(function(doc) {
					    averages.push([doc.date, doc.avg]);
                        var stdDevHalf = doc.stdDev / 2;
					    ranges.push([doc.date, doc.avg - stdDevHalf, doc.avg + stdDevHalf]);
					});
					$('#docsNo').text('Number of documents: ' + documents.length);
					$('#container').highcharts({
                        title: {
                            text: 'Avg & StdDev'
                        },
                        xAxis: {
                            type: 'datetime',
                            title: {
                                text: 'Date'
                            }
                        },
                        yAxis: {
                            title: {
                                text: null
                            }
                        },
                        tooltip: {
                            crosshairs: true,
                            shared: true
                        },
                        legend: {},
                        series: [{
                            name: 'Average value',
                            data: averages,
                            zIndex: 1,
                            marker: {
                                fillColor: 'white',
                                lineWidth: 2,
                                lineColor: Highcharts.getOptions().colors[0]
                            }
                        }, {
                            name: 'StdDev Range',
                            data: ranges,
                            type: 'arearange',
                            lineWidth: 0,
                            linkedTo: ':previous',
                            color: Highcharts.getOptions().colors[0],
                            fillOpacity: 0.3,
                            zIndex: 0
                        }]
                    });
				});
				return false;
			});
		});
	</script>
  </body>
</html>