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
        $.get('/rest/' + resource + '/' + startDate + '/' + endDate, function(documents) {
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